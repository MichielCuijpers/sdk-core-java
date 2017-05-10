package com.mastercard.api.core.mocks

import org.apache.http.Header
import org.apache.http.HeaderIterator
import org.apache.http.HttpEntity
import org.apache.http.HttpHeaders
import org.apache.http.HttpVersion
import org.apache.http.ProtocolVersion
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicStatusLine
import org.apache.http.params.HttpParams

/**
 * Created by eamondoyle on 22/02/2016.
 */
class MockHttpResponse<T extends Object> implements CloseableHttpResponse {

    public static final Map defaultJsonResponse = [a: 1, b: "b", c: true]
    public String contentType = ContentType.APPLICATION_JSON.mimeType

    int status
    String jsonRespone;
    Closure c

    MockHttpResponse(int status, T jsonResponseObject) {
        this.status = status
        parseJson(jsonResponseObject);

    }

    MockHttpResponse(int status, T jsonResponseObject, Closure c) {
        this.status = status
        parseJson(jsonResponseObject);
        this.c = c
    }

    private void parseJson(T jsonResponseObject) {
        if (jsonResponseObject != null) {
            if (jsonResponseObject instanceof String) {
                this.jsonRespone = jsonResponseObject
            } else {
                this.jsonRespone = org.json.simple.JSONValue.toJSONString(jsonResponseObject)
            }

        } else {
            this.jsonRespone = ""
        }
    }

    @Override
    void close() throws IOException {

    }

    @Override
    StatusLine getStatusLine() {
        return new BasicStatusLine(HttpVersion.HTTP_1_1, status, null)
    }

    @Override
    void setStatusLine(StatusLine statusline) {

    }

    @Override
    void setStatusLine(ProtocolVersion ver, int code) {

    }

    @Override
    void setStatusLine(ProtocolVersion ver, int code, String reason) {

    }

    @Override
    void setStatusCode(int code) throws IllegalStateException {

    }

    @Override
    void setReasonPhrase(String reason) throws IllegalStateException {

    }

    @Override
    HttpEntity getEntity() {
        if (c)
            c.call()

        if (jsonRespone == null)
            return null

        byte[] bytes = this.jsonRespone.bytes
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity()
        basicHttpEntity.content = new ByteArrayInputStream(bytes);
        basicHttpEntity.contentLength = bytes.length
        return basicHttpEntity
    }

    @Override
    void setEntity(HttpEntity entity) {

    }

    @Override
    Locale getLocale() {
        return null
    }

    @Override
    void setLocale(Locale loc) {

    }

    @Override
    ProtocolVersion getProtocolVersion() {
        return null
    }

    @Override
    boolean containsHeader(String name) {
        return false
    }

    @Override
    Header[] getHeaders(String name) {
        return new Header[0]
    }

    @Override
    Header getFirstHeader(String name) {
        if (!contentType) {
            return null
        }
        else  if (name == HttpHeaders.CONTENT_TYPE) {
            return new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType)
        }

        return null
    }

    @Override
    Header getLastHeader(String name) {
        return null
    }

    @Override
    Header[] getAllHeaders() {
        return new Header[0]
    }

    @Override
    void addHeader(Header header) {

    }

    @Override
    void addHeader(String name, String value) {

    }

    @Override
    void setHeader(Header header) {

    }

    @Override
    void setHeader(String name, String value) {

    }

    @Override
    void setHeaders(Header[] headers) {

    }

    @Override
    void removeHeader(Header header) {

    }

    @Override
    void removeHeaders(String name) {

    }

    @Override
    HeaderIterator headerIterator() {
        return null
    }

    @Override
    HeaderIterator headerIterator(String name) {
        return null
    }

    @Override
    HttpParams getParams() {
        return null
    }

    @Override
    void setParams(HttpParams params) {

    }
}
