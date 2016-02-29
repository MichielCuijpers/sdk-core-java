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

    public static String API_BASE_LIVE_URL = Constants.API_BASE_LIVE_URL;
    public static String API_BASE_SANDBOX_URL = Constants.API_BASE_SANDBOX_URL;
    public static String USER_AGENT = null; // User agent string sent with requests.
    private static String HEADER_SEPARATOR = ";";

    private String apiPath;

    /**
     *
     * @param basePath - basePath for the API e.g. /fraud/loststolen/v1
     */
    public ApiController(String basePath) {
        if (basePath == null) {
            throw new IllegalArgumentException("basePath cannot be null");
        }

        String baseUrl = API_BASE_LIVE_URL;

        if (ApiConfig.isSandbox()) {
            baseUrl = API_BASE_SANDBOX_URL;
        }

        this.apiPath = baseUrl + basePath;
    }

    private void checkState() throws RuntimeException {
        try {
            new URL(API_BASE_LIVE_URL);
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid URL supplied for API_BASE_LIVE_URL", e);
        }

        try {
            new URL(API_BASE_SANDBOX_URL);
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid URL supplied for API_BASE_SANDBOX_URL", e);
        }
    }

    /**
     * Append parameter to URL
     *
     * @param s
     * @param stringToAppend    e.g. max=10
     *
     * @return
     */
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

    String urlEncode(Object stringToEncode) throws UnsupportedEncodingException {
        return URLEncoder.encode(stringToEncode.toString(), "UTF-8");
    }

    private URI getURI(String type, Action action, Map<String, Object> objectMap) throws UnsupportedEncodingException {
        URI uri;

        StringBuilder s = new StringBuilder("%s/%s");

        List<Object> objectList = new ArrayList<Object>();
        objectList.add(apiPath.replaceAll("/$", ""));
        objectList.add(type.replaceAll("/$", ""));

        // Handle Id
        switch (action) {
            case read:
            case update:
            case delete:
                if (objectMap.containsKey("id")) {
                    s.append("/%s");
                    objectList.add(urlEncode(objectMap.get("id")));
                    objectMap.remove("id");
                }

                break;
        }

        // Add Query Params
        switch (action) {
            case read:
            case delete:
            case list:
                Iterator it = objectMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    s = appendToQueryString(s, "%s=%s");
                    objectList.add(urlEncode(entry.getKey().toString()));
                    objectList.add(urlEncode(entry.getValue().toString()));
                }

                break;
        }

        // Use JSON
        s = appendToQueryString(s, "Format=JSON");

        try {
            uri = new URI(String.format(s.toString(), objectList.toArray()));
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to build URI", e);
        }

        return uri;
    }

    private HttpRequestBase getRequest(Authentication authentication, URI uri, Action action, Map<String, Object> objectMap, Map<String, String> headerMap)
            throws InvalidRequestException, MessageSignerException {

        HttpRequestBase message = null;

        // Try set default authentication if no authentication provided
        if (authentication == null) {
            if (ApiConfig.getAuthentication() == null) {
                throw new MessageSignerException("Authentication is null. Set \"ApiConfig.authentication\" or pass an instance of com.mastercard.api.core.security.Authentication to the method call");
            }

            authentication = ApiConfig.getAuthentication();
        }

        String payload = null;

        switch (action) {
            case create:
                payload = JSONValue.toJSONString(objectMap);
                message = new HttpPost(uri);

                HttpEntity createEntity;
                try {
                    createEntity = new StringEntity(payload);
                }
                catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Unsupported encoding for create action.", e);
                }
                ((HttpPost) message).setEntity(createEntity);

                break;

            case delete:
                payload = "";
                message = new HttpDelete(uri);
                break;

            case update:
                payload = JSONValue.toJSONString(objectMap);
                message = new HttpPut(uri);

                HttpEntity updateEntity;
                try {
                    updateEntity = new StringEntity(payload);
                }
                catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Unsupported encoding for create action.", e);
                }
                ((HttpPut) message).setEntity(updateEntity);

                break;

            case read:
                payload = "";
                message = new HttpGet(uri);
                break;

            case list:
                payload = "";
                message = new HttpGet(uri);
                break;
        }

        // Set JSON
        message.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        message.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        // Set other headers
        for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
            message.setHeader(headerEntry.getKey(), headerEntry.getValue());
        }

        // Add user agent
        String userAgent = "Java-SDK/" + Constants.VERSION;
        if (USER_AGENT != null) {
            userAgent = userAgent + " " + USER_AGENT;
        }
        message.setHeader("User-Agent", userAgent);

        // Sign the request
        authentication.sign(uri, HttpMethod.fromAction(action), ContentType.APPLICATION_JSON, payload, message);

        return message;
    }

    private Action getAction(String action) throws InvalidRequestException {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }

        Action act;
        try {
            act = Action.valueOf(action);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid action supplied: " + action);
        }

        return act;
    }

    public Map<? extends String, ? extends Object> execute(Authentication auth, String type, String action, Map<String, Object> objectMap)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        checkState();

        Action act = getAction(action);

        URI uri = null;

        if (objectMap == null) {
            objectMap = new LinkedHashMap<String, Object>();
        }

        try {
            uri = getURI(type, act, objectMap);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        int port = uri.getPort();
        String scheme = uri.getScheme();

        HttpHost host = new HttpHost(uri.getHost(), port, scheme);
        CloseableHttpClient httpClient = createHttpClient();

        try {
            Map<String, String> header = new LinkedHashMap<String, String>();
            HttpRequestBase message = null;

            message = getRequest(auth, uri, act, objectMap, header);

            ResponseHandler<ApiControllerResponse> responseHandler = createResponseHandler();

            ApiControllerResponse apiResponse = httpClient.execute(host, message, responseHandler);

            if (apiResponse.hasPayload()) {

                Object response = JSONValue.parse(apiResponse.getPayload());

                if (apiResponse.getStatus() < 300) {
                    if (act == Action.list) {
                        Map<String, Object> map = new HashMap<>();
                        List list = convertToList((Map<? extends String, ? extends Object>) response);

                        map.put("list", list);
                        return map;
                    }
                    else {
                        return (Map<? extends String, ? extends Object>) response;
                    }
                }
                else {
                    int status = apiResponse.getStatus();

                    if (status == HttpStatus.SC_BAD_REQUEST) {
                        throw new InvalidRequestException((Map<? extends String, ? extends Object>) response);
                    }
                    else if (status == HttpStatus.SC_UNAUTHORIZED) {
                        throw new AuthenticationException((Map<? extends String, ? extends Object>) response);
                    }
                    else if (status == HttpStatus.SC_NOT_FOUND) {
                        throw new ObjectNotFoundException((Map<? extends String, ? extends Object>) response);
                    }
                    else if (status == HttpStatus.SC_FORBIDDEN) {
                        throw new NotAllowedException((Map<? extends String, ? extends Object>) response);
                    }
                    else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                        throw new SystemException((Map<? extends String, ? extends Object>) response);
                    }
                    else {
                        throw new ApiCommunicationException((Map<? extends String, ? extends Object>) response);
                    }
                }
            }

            return null;

        }
        catch (HttpResponseException e) {
            throw new ApiCommunicationException("Failed to communicate with response code " + String.format("%d", e.getStatusCode()), e);

        }
        catch (ClientProtocolException e) {
            throw new ApiCommunicationException("HttpClient exception", e);

        }
        catch (IOException e) {
            throw new ApiCommunicationException("I/O error", e);

        }
        finally {
            try {
                httpClient.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Converts an XML Gateway response to a List
     * @param response
     * @return
     */
    private List convertToList(Map<? extends String, ? extends Object> response) {
        List list = new ArrayList();

        if (response.keySet().iterator().hasNext()) {
            String key = response.keySet().iterator().next();
            Map<? extends String, ? extends Object> level1 = response.get(key) instanceof Map ? (Map<? extends String, ? extends Object>) response.get(key) : null;

            if (level1 != null && level1.keySet().iterator().hasNext()) {
                key = level1.keySet().iterator().next();
                list = level1.get(key) instanceof List ? (List) level1.get(key) : new ArrayList<>();
            }
        }

        return list;
    }

    CloseableHttpClient createHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        return httpClientBuilder.build();
    }

    ResponseHandler<ApiControllerResponse> createResponseHandler() {
        return new ResponseHandler<ApiControllerResponse>() {
            public ApiControllerResponse handleResponse(HttpResponse httpResponse) throws IOException {
                ApiControllerResponse apiResponse = new ApiControllerResponse();
                apiResponse.setHttpResponse(httpResponse);

                StatusLine statusLine = httpResponse.getStatusLine();
                apiResponse.setStatus(statusLine.getStatusCode());

                HttpEntity entity = httpResponse.getEntity();
                Header header = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE);

                String payload = null;
                if (entity != null) {
                    payload = EntityUtils.toString(entity);
                }
                else if (204 != statusLine.getStatusCode()) {
                    throw new IOException("Invalid response, there is no content in the response and the status code is " + statusLine.getStatusCode() + ".  Status code should be 204.");
                }

                String responseContentType;

                if (header == null) {
                    throw new IllegalStateException("Unknown content type. Missing Content-Type header");
                }
                else {
                    if (header.getValue().contains(HEADER_SEPARATOR)) {
                        String parts[] = header.getValue().split(HEADER_SEPARATOR);
                        responseContentType = parts[0];
                    }
                    else {
                        responseContentType = header.getValue();
                    }
                }

                if (responseContentType.equals(ContentType.APPLICATION_JSON.getMimeType())) {
                    apiResponse.setPayload(payload);
                }
                else {
                    throw new IOException("Response was not " + ContentType.APPLICATION_JSON.getMimeType() + ", it was: " + responseContentType + ". Unable to process payload.");
                }

                return apiResponse;

            }
        };
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
