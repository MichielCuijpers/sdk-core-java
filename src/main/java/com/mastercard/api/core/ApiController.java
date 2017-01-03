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
import com.mastercard.api.core.model.*;
import com.mastercard.api.core.security.Authentication;
import com.mastercard.api.core.security.CryptographyInterceptor;
import org.apache.commons.codec.DecoderException;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiController {

    private static String USER_AGENT = null; // User agent string sent with requests.
    private static String HEADER_SEPARATOR = ";";
    private static String[] SUPPORTED_TLS = new String[] { "TLSv1.1", "TLSv1.2" };
    public  static final String ENVIRONMENT_IDENTIFIER = "#env";

    /**
     */
    public ApiController() {

    }



    /**
     * Append parameter to URL
     *
     * @param s instance of StringBuilder
     * @param stringToAppend e.g. max=10
     * @return StringBuilder
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

    /**
     * This is the method which is used to replace {pathid} in the url with the values in the map.
     * Once the value in the map is used, it is removed.
     *
     * @param url       - url where the values need to be replaced.
     * @param objectMap - map containing the values which can be replace.
     * @return formatted string
     */
    String getPathWithReplacedPath(String url, Map<String, Object> objectMap) throws IllegalStateException {

        String regexToRemovePathParameters = "\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regexToRemovePathParameters);
        Matcher matcher = pattern.matcher(url);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String group = matcher.group(1);
            if (objectMap.containsKey(group)) {
                matcher.appendReplacement(sb, objectMap.remove(group).toString());
            } else {
                throw new IllegalStateException("Error: required parameter='"+group+"' was not found in the RequestMap ");
            }
        }
        matcher.appendTail(sb);


        String tmpResult = sb.length() > 0 ? sb.toString().replaceAll("//", "/").replaceAll("/$", "") : url;

        //arizzini: need to make sure that the correct format is maintained.
        if (!tmpResult.startsWith("/"))
            tmpResult = "/"+tmpResult;

        return tmpResult;
    }

    private URI getURI(OperationConfig operationConfig, OperationMetadata operationMetadata, RequestMap requestObject)
            throws UnsupportedEncodingException, IllegalStateException {
        URI uri;

        //arizzini: if host config or environment config changes betweeen calls
        // we need to update the host
        String host = operationMetadata.getHost();
        try {
            new URL(host);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid URL supplied for host="+host, e);
        }

        String resourcePath = operationConfig.getResourcePath();
        if (resourcePath.contains(ENVIRONMENT_IDENTIFIER)) {
            String context = "";
            if (operationMetadata.getContext() != null) {
                context = operationMetadata.getContext();
            }
            resourcePath = resourcePath.replace(ENVIRONMENT_IDENTIFIER, context);
            //don't worry of //, they will be removed in the getPathWithReplacedPath
        }

        //arizzini: need to replace all the path variables
        String updatedType = getPathWithReplacedPath(resourcePath, requestObject);

        StringBuilder s = new StringBuilder("%s%s");

        List<Object> objectList = new ArrayList<Object>();

        //arizzini: host override, this takes precedence over all other scenarios.
        if (operationMetadata.getHost() == null) {
            //arizzini: removing last slash (/)
            objectList.add(host.replaceAll("/$", ""));
        } else {
            //arizzini: removing last slash (/)
            objectList.add(operationMetadata.getHost().replaceAll("/$", ""));
        }

        //arizzini: removing last slash (/)
        objectList.add(updatedType.replaceAll("/$", ""));

        // Add Query Params
        switch (operationConfig.getAction()) {
            case read:
            case delete:
            case list:
            case query:
                Iterator it = requestObject.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    s = appendToQueryString(s, "%s=%s");
                    objectList.add(urlEncode(entry.getKey().toString()));
                    objectList.add(urlEncode(entry.getValue().toString()));
                }
                break;
        }

        // create and update may have Query and Body parameters as part of the request.
        // Check additionalQueryParametersList
        if (operationConfig.getQueryParams().size() > 0) {
            switch (operationConfig.getAction()) {
                case create:
                case update:
                    // Get the submap of query parameters which also removes the values from objectMap
                    Map<String,Object> queryMap = subMap(requestObject, operationConfig.getQueryParams());
                    Iterator it = queryMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        s = appendToQueryString(s, "%s=%s");
                        objectList.add(urlEncode(entry.getKey().toString()));
                        objectList.add(urlEncode(entry.getValue().toString()));
                    }

                    break;
            }
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

    private HttpRequestBase getRequest(Authentication authentication, OperationConfig operationConfig,
            OperationMetadata operationMetadata, RequestMap requestObject)
            throws InvalidRequestException, MessageSignerException, NoSuchAlgorithmException, InvalidKeyException, CertificateEncodingException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException, IllegalBlockSizeException {



        Map<String,Object> headerMap = subMap(requestObject, operationConfig.getHeaderParams());
        URI uri = getURI(operationConfig, operationMetadata, requestObject);

        //this is what is left from the parameters.
        Map<String, Object> objectMap = new LinkedHashMap<String, Object>(requestObject);
        CryptographyInterceptor interceptor = ApiConfig.getCryptographyInterceptor(uri.getPath());



        HttpRequestBase message = null;

        // Try set default authentication if no authentication provided
        if (authentication == null) {
            if (ApiConfig.getAuthentication() == null) {
                throw new MessageSignerException(
                        "Authentication is null. Set \"ApiConfig.authentication\" or pass an instance of com.mastercard.api.core.security.Authentication to the method call");
            }

            authentication = ApiConfig.getAuthentication();
        }

        String payload = null;

        switch (operationConfig.getAction()) {
        case create:

            if (interceptor != null) {
                objectMap = interceptor.encrypt(objectMap);
            }

            payload = JSONValue.toJSONString(objectMap);
            message = new HttpPost(uri);

            HttpEntity createEntity;
            try {
                createEntity = new StringEntity(payload);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("Unsupported encoding for create action.", e);
            }
            ((HttpPost) message).setEntity(createEntity);

            break;

        case delete:
            payload = "";
            message = new HttpDelete(uri);
            break;

        case update:

            if (interceptor != null) {
                objectMap = interceptor.encrypt(objectMap);
            }

            payload = JSONValue.toJSONString(objectMap);
            message = new HttpPut(uri);

            HttpEntity updateEntity;
            try {
                updateEntity = new StringEntity(payload);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("Unsupported encoding for update action.", e);
            }
            ((HttpPut) message).setEntity(updateEntity);

            break;

        case read:
        case list:
        case query:
            payload = "";
            message = new HttpGet(uri);
            break;
        }

        // Set JSON
        message.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        message.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        // Set other headers
        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
            message.setHeader(entry.getKey(), entry.getValue().toString());
        }

        // Add user agent
        String userAgent = "Java-SDK/" + operationMetadata.getApiVersion();
        if (USER_AGENT != null) {
            userAgent = userAgent + " " + USER_AGENT;
        }
        message.setHeader("User-Agent", userAgent);

        // Sign the request
        authentication
                .sign(uri, HttpMethod.fromAction(operationConfig.getAction()), ContentType.APPLICATION_JSON, payload, message);

        return message;
    }

    public Map<? extends String, ? extends Object> execute(Authentication auth, OperationConfig operationConfig, OperationMetadata operationMetadata, RequestMap requestObject)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, IllegalArgumentException, SystemException {


        CloseableHttpClient httpClient = createHttpClient();

        try {

            HttpRequestBase request = getRequest(auth, operationConfig, operationMetadata, requestObject);

            CryptographyInterceptor interceptor = ApiConfig.getCryptographyInterceptor(
                    request.getURI().getPath());
            int port = request.getURI().getPort();
            String scheme = request.getURI().getScheme();
            HttpHost host = new HttpHost(request.getURI().getHost(), port, scheme);

            ResponseHandler<ApiControllerResponse> responseHandler = createResponseHandler();

            ApiControllerResponse apiResponse = httpClient.execute(host, request, responseHandler);

            if (apiResponse.hasPayload()) {

                Object response = JSONValue.parse(apiResponse.getPayload());

                if (apiResponse.getStatus() < 300) {
                    if (operationConfig.getAction() == Action.list) {

                        Map<String, Object> map = new HashMap<String,Object>();
                        List list = null;

                        //arizzini:  if the response is an object we need to convert this into a map
                        if (response instanceof JSONObject) {
                            list = convertToList((Map<? extends String, ? extends Object>) response);
                        }
                        //arizzini:  if the response is an array we need simply case to a List of Maps.
                        else {
                            list = ((List<Map<? extends String, ? extends Object>>) response);
                        }
                        map.put("list", list);
                        return map;
                    } else {

                        Map<String,Object> map = null;
                        if (response instanceof JSONObject) {
                            map = (Map<String, Object>) response;

                            if (interceptor == null) {
                                return map;
                            } else {
                                return interceptor.decrypt(map);
                            }

                        } else {
                            map = new HashMap<String, Object>();
                            map.put("list", ((List<Map<? extends String, ? extends Object>>) response));
                            return map;
                        }
                    }
                } else {
                    int status = apiResponse.getStatus();

                    if (status == HttpStatus.SC_BAD_REQUEST) {
                        throw new InvalidRequestException((Map<? extends String, ? extends Object>) response);
                    } else if (status == HttpStatus.SC_UNAUTHORIZED) {
                        throw new AuthenticationException((Map<? extends String, ? extends Object>) response);
                    } else if (status == HttpStatus.SC_NOT_FOUND) {
                        throw new ObjectNotFoundException((Map<? extends String, ? extends Object>) response);
                    } else if (status == HttpStatus.SC_FORBIDDEN) {
                        throw new NotAllowedException((Map<? extends String, ? extends Object>) response);
                    } else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                        throw new SystemException((Map<? extends String, ? extends Object>) response);
                    } else {
                        throw new ApiCommunicationException(
                                (Map<? extends String, ? extends Object>) response);
                    }
                }
            }

            return null;

        } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
        } catch (HttpResponseException e) {
            throw new ApiCommunicationException(
                    "Failed to communicate with response code " + String.format("%d", e.getStatusCode()), e);
        } catch (ClientProtocolException e) {
            throw new ApiCommunicationException("HttpClient exception", e);
        } catch (IOException e) {
            throw new ApiCommunicationException("I/O error", e);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (CertificateEncodingException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (DecoderException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (NoSuchProviderException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (InvalidKeyException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (BadPaddingException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (NoSuchPaddingException e) {
            throw new SystemException("Cryptography Error", e);
        } catch (IllegalBlockSizeException e) {
            throw new SystemException("Cryptography Error", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    protected static Map subMap(Map<String,Object> inputMap, List<String> keyList)
    {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
        for (Map.Entry entry : inputMap.entrySet())
        {
            if (keyList.contains(entry.getKey())) {
                resultMap.put(entry.getKey().toString(), entry.getValue());
            }
        }

        //arizzini: removing the values which have been filtered
        inputMap.keySet().removeAll(keyList);

        return resultMap;
    }

    /**
     * Converts an XML Gateway response to a List
     *
     * @param response
     * @return
     */
    private List convertToList(Map<? extends String, ? extends Object> response) {
        List list = new ArrayList();

        if (response.keySet().iterator().hasNext()) {
            String key = response.keySet().iterator().next();
            Map<? extends String, ? extends Object> level1 = response.get(key) instanceof Map ?
                    (Map<? extends String, ? extends Object>) response.get(key) :
                    null;

            if (level1 != null && level1.keySet().iterator().hasNext()) {
                key = level1.keySet().iterator().next();
                list = level1.get(key) instanceof List ? (List) level1.get(key) : new ArrayList<Object>();
            }
        }

        return list;
    }

    private static String[] split(final String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    CloseableHttpClient createHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.useSystemProperties();

        // TLSv1.1 and TLSv1.2 are disabled by default in Java 7, we want to enforce TLSv1.2
        final String[] supportedProtocols = SUPPORTED_TLS;
        final String[] supportedCipherSuites = split(System.getProperty("https.cipherSuites"));

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                (SSLSocketFactory) SSLSocketFactory.getDefault(),
                supportedProtocols,
                supportedCipherSuites,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        httpClientBuilder.setSSLSocketFactory(sslsf);

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

                //arizzini: entity == null when HTTP 200
                if (entity != null) {
                    String payload = EntityUtils.toString(entity);

                    //arizzini: if we have content, we try to parse it
                    if (!payload.isEmpty()) {
                        String responseContentType;
                        Header header = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE);
                        if (header == null) {
                            throw new IllegalStateException("Unknown content type. Missing Content-Type header");
                        } else {
                            if (header.getValue().contains(HEADER_SEPARATOR)) {
                                String parts[] = header.getValue().split(HEADER_SEPARATOR);
                                responseContentType = parts[0];
                            } else {
                                responseContentType = header.getValue();
                            }
                        }

                        if (responseContentType.equals(ContentType.APPLICATION_JSON.getMimeType())) {
                            apiResponse.setPayload(payload);
                        } else {
                            throw new IOException(
                                    "Response was not " + ContentType.APPLICATION_JSON.getMimeType() + ", it was: "
                                            + responseContentType + ". Unable to process payload. " +
                                            "\nResponse: [ " + payload + " + ]");
                        }
                    } else {
                        //arizzini: 200 with no content like a delete.
                        apiResponse.setPayload("");
                    }
                } else {
                    //arizzini: 204 with no content like a delete.
                    apiResponse.setPayload("");
                }

                return apiResponse;

            }
        };
    }

    protected class ApiControllerResponse {
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
