package com.mastercard.api.core.mocks

import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.params.HttpParams
import org.apache.http.protocol.HttpContext

/**
 * Created by eamondoyle on 22/02/2016.
 */
class MockHttpClient extends CloseableHttpClient {

    MockHttpResponse mockHttpResponse
    boolean closed = false

    MockHttpClient(MockHttpResponse mockHttpResponse) {
        this.mockHttpResponse = mockHttpResponse
    }

    @Override
    public <T> T execute(final HttpHost target, final HttpRequest request,
                         final ResponseHandler<? extends T> responseHandler) throws IOException,
            ClientProtocolException {
        return responseHandler.handleResponse(mockHttpResponse);
    }

    @Override
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return null
    }

    @Override
    void close() throws IOException {
        closed = true
    }

    @Override
    HttpParams getParams() {
        return null
    }

    @Override
    ClientConnectionManager getConnectionManager() {
        return null
    }
}
