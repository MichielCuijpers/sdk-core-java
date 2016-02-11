/*
 * Copyright 2015 MasterCard International.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * Neither the name of the MasterCard International Incorporated nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF 
 * SUCH DAMAGE.
 *
 */

package com.mastercard.api.core;

import com.mastercard.api.core.exception.*;
import com.mastercard.api.core.security.Authentication;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;


public class ApiController {

//    public static final String X_AUTH_TOKEN = "X-Auth-Token";
    public static String API_BASE_LIVE_URL = Constants.API_BASE_LIVE_URL;
    public static String API_BASE_SANDBOX_URL = Constants.API_BASE_SANDBOX_URL;
//    public static String OAUTH_BASE_URL = Constants.OAUTH_BASE_URL;

    /**
     * User agent string sent with requests.
     */
    public static String USER_AGENT = null;

    private static String HEADER_SEPARATOR = ";";

    private void checkState() throws RuntimeException {
        try {
            new URL(API_BASE_LIVE_URL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid URL supplied for API_BASE_LIVE_URL", e);
        }

        try {
            new URL(API_BASE_SANDBOX_URL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid URL supplied for API_BASE_SANDBOX_URL", e);
        }
    }

    private StringBuilder appendToQueryString(StringBuilder s, String stringToAppend) {
        if (s.indexOf("?") == -1) {
            s.append("?");
        }
        if (s.indexOf("?") != s.length() - 1) {
            s.append("&");
        }
        s.append(stringToAppend);

        return s;
    }

    private String getURLEncodedString(Object stringToEncode) throws UnsupportedEncodingException {
        return URLEncoder.encode(stringToEncode.toString(), "UTF-8");
    }

    private URI getURI(String type, Action action, Map<String, Object> objectMap) throws UnsupportedEncodingException {
        URI uri;

        StringBuilder s = new StringBuilder("%s/%s");

        String baseUrl = API_BASE_LIVE_URL;

        if (ApiConfig.SANDBOX) {
            baseUrl = API_BASE_SANDBOX_URL;
        }

        List<Object> objectList = new ArrayList<Object>();
        objectList.add(baseUrl.replaceAll("/$", ""));
        objectList.add(type.replaceAll("/$", ""));

        switch (action) {
            case create:
                break;
            case show:
            case update:
            case delete:
                if (objectMap.containsKey("id")) {
                    s.append("/%s");
                    objectList.add(getURLEncodedString(objectMap.get("id")));
//                    throw new IllegalStateException("id required for " + action.toString() + "action");
                }
                break;

            case list:
                if (objectMap != null) {
                    if (objectMap.containsKey("max")) {
                        s = appendToQueryString(s, "max=%s");
                        objectList.add(getURLEncodedString(objectMap.get("max")));
                    }

                    if (objectMap.containsKey("offset")) {
                        s = appendToQueryString(s, "offset=%s");
                        objectList.add(getURLEncodedString(objectMap.get("offset")));
                    }

                    if (objectMap.containsKey("sorting")) {
                        if (objectMap.get("sorting") instanceof Map) {
                            Map<String, Object> sorting = (Map<String, Object>) objectMap.get("sorting");
                            Iterator it = sorting.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry entry = (Map.Entry) it.next();
                                s = appendToQueryString(s, "sorting[%s]=%s");
                                objectList.add(getURLEncodedString(entry.getKey().toString()));
                                objectList.add(getURLEncodedString(entry.getValue().toString()));
                            }
                        }
                    }

                    if (objectMap.containsKey("filter")) {
                        if (objectMap.get("filter") instanceof Map) {
                            Map<String, Object> filter = (Map<String, Object>) objectMap.get("filter");
                            Iterator it = filter.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry entry = (Map.Entry) it.next();
                                s = appendToQueryString(s, "filter[%s]=%s");
                                objectList.add(getURLEncodedString(entry.getKey().toString()));
                                objectList.add(getURLEncodedString(entry.getValue().toString()));
                            }
                        }
                    }

                    Iterator it = objectMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        s = appendToQueryString(s, "%s=%s");
                        objectList.add(getURLEncodedString(entry.getKey().toString()));
                        objectList.add(getURLEncodedString(entry.getValue().toString()));
                    }
                }

                break;
        }

        // Use JSON
        s = appendToQueryString(s, "Format=JSON");

        try {
            uri = new URI(String.format(s.toString(), objectList.toArray()));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to build URI", e);
        }

        return uri;
    }

    private HttpRequestBase getRequest(Authentication authentication, URI uri, Action action, Map<String, Object> objectMap, Map<String, String> headerMap)
            throws InvalidRequestException, MessageSignerException {

        HttpRequestBase message = null;

        String payload = null;
        switch (action) {
            case create:
                payload = JSONValue.toJSONString(BaseMap.normalize(objectMap));
                message = new HttpPost(uri);
                break;

            case delete:
                payload = "";
                message = new HttpDelete(uri);
                break;

            case update:
                payload = JSONValue.toJSONString(BaseMap.normalize(objectMap));
                message = new HttpPut(uri);
                break;

            case show:
                payload = "";
                message = new HttpGet(uri);
                break;

            case list:
                payload = "";
                message = new HttpGet(uri);
                break;
        }

        switch (action) {
            case create:
                HttpEntity createEntity = null;
                try {
                    createEntity = new StringEntity(payload);
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Unsupported encoding for create action.", e);
                }
                ((HttpPost) message).setEntity(createEntity);
                break;

            case delete:
                break;

            case update:
                HttpEntity updateEntity = null;
                try {
                    updateEntity = new StringEntity(payload);
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Unsupported encoding for create action.", e);
                }
                ((HttpPut) message).setEntity(updateEntity);
                break;

            case show:
            case list:
                break;
        }

        message.setHeader("Accept", "application/json");
        for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
            message.setHeader(headerEntry.getKey(), headerEntry.getValue());
        }

        String userAgent = "Java-SDK/" + Constants.VERSION;
        if (USER_AGENT != null) {
            userAgent = userAgent + " " + USER_AGENT;
        }
        message.setHeader("User-Agent", userAgent);

        authentication.sign(uri, Method.fromAction(action), ContentType.APPLICATION_JSON, payload, message);

        return message;
    }

    private Action getAction(String action) throws InvalidRequestException {
        Action act = Action.valueOf(action);
        if (null == act) {
            throw new IllegalStateException("Invalid action supplied: " + action);
        }

        return act;
    }

    public Map<? extends String, ? extends Object> execute(Authentication auth, String type, String action, Map<String, Object> objectMap)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            ObjectNotFoundException, NotAllowedException, SystemException, MessageSignerException {
        checkState();

        Action act = getAction(action);

        URI uri = null;

        try {
            uri = getURI(type, act, objectMap);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        int port = uri.getPort();
        String scheme = uri.getScheme();
        if (port == PORTS.UNKNOWN.number) {
            port = PORTS.HTTP.number;
            if (scheme.equals(PORTS.HTTPS.name)) {
                port = PORTS.HTTPS.number;
            }
        }

        HttpHost host = new HttpHost(uri.getHost(), port, scheme);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = httpClientBuilder.build();

        try {
            Map<String, String> header = new LinkedHashMap<String, String>();
            HttpRequestBase message = null;

            message = getRequest(auth, uri, act, objectMap, header);

            ResponseHandler<ApiControllerResponse> responseHandler = new ResponseHandler<ApiControllerResponse>() {
                public ApiControllerResponse handleResponse(HttpResponse httpResponse) throws IOException {
                    ApiControllerResponse paymentsApiResponse = new ApiControllerResponse();
                    paymentsApiResponse.setHttpResponse(httpResponse);

                    StatusLine statusLine = httpResponse.getStatusLine();
                    paymentsApiResponse.setStatus(statusLine.getStatusCode());

                    HttpEntity entity = httpResponse.getEntity();
                    Header header = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE);

                    String payload = null;
                    if (entity != null) {
                        payload = EntityUtils.toString(entity);
                    } else if (204 != statusLine.getStatusCode()) {
                        throw new IOException("Invalid response, there is no content in the response and the status code is " + statusLine.getStatusCode() + ".  Status code should be 204.");
                    }

                    String responseContentType;
                    if (null != header) {
                        if (header.getValue().contains(HEADER_SEPARATOR)) {
                            String parts[] = header.getValue().split(HEADER_SEPARATOR);
                            responseContentType = parts[0];
                        } else {
                            responseContentType = header.getValue();
                        }

                        if (responseContentType.equals(ContentType.APPLICATION_JSON.getMimeType())) {
                            paymentsApiResponse.setPayload(payload);
                        } else {
                            throw new IOException("Response was not " + ContentType.APPLICATION_JSON.getMimeType() + ", it was: " + responseContentType + ". Unable to process payload.");
                        }

                        return paymentsApiResponse;

                    }
                    return null;
                }
            };

            ApiControllerResponse paymentsApiResponse = httpClient.execute(host, message, responseHandler);

            if (paymentsApiResponse.hasPayload()) {

                Object response = JSONValue.parse(paymentsApiResponse.getPayload());

                if (response instanceof Map) {
                    if (paymentsApiResponse.getStatus() < 300) {
                        return (Map<? extends String, ? extends Object>) response;

                    } else {
                        int status = paymentsApiResponse.getStatus();

                        if (status == HttpStatus.SC_BAD_REQUEST) {
                            throw new InvalidRequestException(status, (Map<? extends String, ? extends Object>) response);
                        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
                            throw new AuthenticationException(status, (Map<? extends String, ? extends Object>) response);
                        } else if (status == HttpStatus.SC_NOT_FOUND) {
                            throw new ObjectNotFoundException(status, (Map<? extends String, ? extends Object>) response);
                        } else if (status == HttpStatus.SC_METHOD_NOT_ALLOWED) {
                            throw new NotAllowedException(status, (Map<? extends String, ? extends Object>) response);
                        } else if (status < 500) {
                            throw new InvalidRequestException(status, (Map<? extends String, ? extends Object>) response);
                        } else {
                            throw new SystemException(status, (Map<? extends String, ? extends Object>) response);
                        }
                    }
                }
            }

            return null;

        } catch (HttpResponseException e) {
            throw new ApiCommunicationException("Failed to communicate with response code " + String.format("%d", e.getStatusCode()), e);

        } catch (ClientProtocolException e) {
            throw new ApiCommunicationException("HttpClient exception", e);

        } catch (IOException e) {
            throw new ApiCommunicationException("I/O error", e);

        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum PORTS {
        HTTP("http", 80),
        HTTPS("https", 443),
        UNKNOWN("unknown", -1);

        private final String name;
        private final int number;

        PORTS(String name, int number) {
            this.name = name;
            this.number = number;
        }
    }

    public class ApiControllerResponse {
        private HttpResponse httpResponse;
        private String payload;
        private int status;

        public HttpResponse getHttpResponse() {
            return httpResponse;
        }

        public void setHttpResponse(HttpResponse httpResponse) {
            this.httpResponse = httpResponse;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }

        public boolean hasPayload() {
            return payload != null && payload.length() > 0;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
