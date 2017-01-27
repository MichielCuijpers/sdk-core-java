/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.impl.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProxySelector;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.*;

import com.mastercard.api.core.ApiConfig;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.execchain.BackoffStrategyExec;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.impl.execchain.ProtocolExec;
import org.apache.http.impl.execchain.RedirectExec;
import org.apache.http.impl.execchain.RetryExec;
import org.apache.http.impl.execchain.ServiceUnavailableRetryExec;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.TextUtils;
import org.apache.http.util.VersionInfo;

/**
 * Builder for {@link CloseableHttpClient} instances.
 * <p>
 * When a particular component is not explicitly set this class will
 * use its default implementation. System properties will be taken
 * into account when configuring the default implementations when
 * {@link #useSystemProperties()} method is called prior to calling
 * {@link #build()}.
 * </p>
 * <ul>
 *  <li>ssl.TrustManagerFactory.algorithm</li>
 *  <li>javax.net.ssl.trustStoreType</li>
 *  <li>javax.net.ssl.trustStore</li>
 *  <li>javax.net.ssl.trustStoreProvider</li>
 *  <li>javax.net.ssl.trustStorePassword</li>
 *  <li>ssl.KeyManagerFactory.algorithm</li>
 *  <li>javax.net.ssl.keyStoreType</li>
 *  <li>javax.net.ssl.keyStore</li>
 *  <li>javax.net.ssl.keyStoreProvider</li>
 *  <li>javax.net.ssl.keyStorePassword</li>
 *  <li>https.protocols</li>
 *  <li>https.cipherSuites</li>
 *  <li>http.proxyHost</li>
 *  <li>http.proxyPort</li>
 *  <li>http.nonProxyHosts</li>
 *  <li>http.keepAlive</li>
 *  <li>http.maxConnections</li>
 *  <li>http.agent</li>
 * </ul>
 * <p>
 * Please note that some settings used by this class can be mutually
 * exclusive and may not apply when building {@link CloseableHttpClient}
 * instances.
 * </p>
 *
 * @since 4.3
 */
@NotThreadSafe
public class CustomHttpClientBuilder {

    private HttpClientBuilder builder;
    private static String[] SUPPORTED_TLS = new String[] { "TLSv1.1", "TLSv1.2" };


    public static CustomHttpClientBuilder create() {
        return new CustomHttpClientBuilder();
    }

    protected CustomHttpClientBuilder() {
        builder = HttpClientBuilder.create();
    }

//    /**
//     * Assigns {@link HttpRequestExecutor} instance.
//     */
//    public final CustomHttpClientBuilder setRequestExecutor(final HttpRequestExecutor requestExec) {
//        builder.setRequestExecutor(requestExec);
//        return this;
//    }

    /**
     * Assigns {@link javax.net.ssl.HostnameVerifier} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *   org.apache.http.conn.HttpClientConnectionManager)} and the {@link #setSSLSocketFactory(
     *   org.apache.http.conn.socket.LayeredConnectionSocketFactory)} methods.
     * </p>
     *
     *   @since 4.4
     */
    public final CustomHttpClientBuilder setSSLHostnameVerifier(final HostnameVerifier hostnameVerifier) {
        builder.setSSLHostnameVerifier(hostnameVerifier);
        return this;
    }

//    /**
//     * Assigns file containing public suffix matcher. Instances of this class can be created
//     * with {@link org.apache.http.conn.util.PublicSuffixMatcherLoader}.
//     *
//     * @see org.apache.http.conn.util.PublicSuffixMatcher
//     * @see org.apache.http.conn.util.PublicSuffixMatcherLoader
//     *
//     *   @since 4.4
//     */
//    public final CustomHttpClientBuilder setPublicSuffixMatcher(final PublicSuffixMatcher publicSuffixMatcher) {
//        builder.setPublicSuffixMatcher(publicSuffixMatcher);
//        return this;
//    }


//    /**
//     * Assigns {@link SSLContext} instance.
//     * <p>
//     * Please note this value can be overridden by the {@link #setConnectionManager(
//     *   org.apache.http.conn.HttpClientConnectionManager)} and the {@link #setSSLSocketFactory(
//     *   org.apache.http.conn.socket.LayeredConnectionSocketFactory)} methods.
//     * </p>
//     */
//    final CustomHttpClientBuilder setSSLContext(final SSLContext sslContext) {
//        builder.setSSLContext(sslContext);
//        return this;
//    }
//
//    /**
//     * Assigns {@link LayeredConnectionSocketFactory} instance.
//     * <p>
//     * Please note this value can be overridden by the {@link #setConnectionManager(
//     *   org.apache.http.conn.HttpClientConnectionManager)} method.
//     * </p>
//     */
//    final CustomHttpClientBuilder setSSLSocketFactory(
//            final LayeredConnectionSocketFactory sslSocketFactory) {
//        builder.setSSLSocketFactory(sslSocketFactory);
//        return this;
//    }

    /**
     * Assigns maximum total connection value.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *   org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     */
    public final CustomHttpClientBuilder setMaxConnTotal(final int maxConnTotal) {
        builder.setMaxConnTotal(maxConnTotal);
        return this;
    }

    /**
     * Assigns maximum connection per route value.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *   org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     */
    public final CustomHttpClientBuilder setMaxConnPerRoute(final int maxConnPerRoute) {
        builder.setMaxConnPerRoute(maxConnPerRoute);
        return this;
    }

    /**
     * Assigns default {@link SocketConfig}.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *   org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     */
    public final CustomHttpClientBuilder setDefaultSocketConfig(final SocketConfig config) {
        builder.setDefaultSocketConfig(config);
        return this;
    }

    /**
     * Assigns default {@link ConnectionConfig}.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *   org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     */
    public final CustomHttpClientBuilder setDefaultConnectionConfig(final ConnectionConfig config) {
        builder.setDefaultConnectionConfig(config);
        return this;
    }

    /**
     * Sets maximum time to live for persistent connections
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *   org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     *
     * @since 4.4
     */
    public final CustomHttpClientBuilder setConnectionTimeToLive(final long connTimeToLive, final TimeUnit connTimeToLiveTimeUnit) {
        builder.setConnectionTimeToLive(connTimeToLive, connTimeToLiveTimeUnit);
        return this;
    }

    /**
     * Assigns {@link HttpClientConnectionManager} instance.
     */
    public final CustomHttpClientBuilder setConnectionManager(
            final HttpClientConnectionManager connManager) {
        builder.setConnectionManager(connManager);
        return this;
    }

    /**
     * Defines the connection manager is to be shared by multiple
     * client instances.
     * <p>
     * If the connection manager is shared its life-cycle is expected
     * to be managed by the caller and it will not be shut down
     * if the client is closed.
     * </p>
     *
     * @param shared defines whether or not the connection manager can be shared
     *  by multiple clients.
     *
     * @since 4.4
     */
    public final CustomHttpClientBuilder setConnectionManagerShared(
            final boolean shared) {
        builder.setConnectionManagerShared(shared);
        return this;
    }

    /**
     * Assigns {@link ConnectionReuseStrategy} instance.
     */
    public final CustomHttpClientBuilder setConnectionReuseStrategy(
            final ConnectionReuseStrategy reuseStrategy) {
        builder.setConnectionReuseStrategy(reuseStrategy);
        return this;
    }

    /**
     * Assigns {@link ConnectionKeepAliveStrategy} instance.
     */
    public final CustomHttpClientBuilder setKeepAliveStrategy(
            final ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.builder.setKeepAliveStrategy(keepAliveStrategy);
        return this;
    }

//    /**
//     * Assigns {@link AuthenticationStrategy} instance for target
//     * host authentication.
//     */
//    public final CustomHttpClientBuilder setTargetAuthenticationStrategy(
//            final AuthenticationStrategy targetAuthStrategy) {
//        this.builder.setTargetAuthenticationStrategy(targetAuthStrategy);
//        return this;
//    }

    /**
     * Assigns {@link AuthenticationStrategy} instance for proxy
     * authentication.
     */
    public final CustomHttpClientBuilder setProxyAuthenticationStrategy(
            final AuthenticationStrategy proxyAuthStrategy) {
        this.builder.setProxyAuthenticationStrategy( proxyAuthStrategy );
        return this;
    }

//    /**
//     * Assigns {@link UserTokenHandler} instance.
//     * <p>
//     * Please note this value can be overridden by the {@link #disableConnectionState()}
//     * method.
//     * </p>
//     */
//    public final CustomHttpClientBuilder setUserTokenHandler(final UserTokenHandler userTokenHandler) {
//        this.builder.setUserTokenHandler(userTokenHandler);
//        return this;
//    }

    /**
     * Disables connection state tracking.
     */
    public final CustomHttpClientBuilder disableConnectionState() {
        this.builder.disableConnectionState();
        return this;
    }

    /**
     * Assigns {@link SchemePortResolver} instance.
     */
    public final CustomHttpClientBuilder setSchemePortResolver(
            final SchemePortResolver schemePortResolver) {
        this.builder.setSchemePortResolver( schemePortResolver);
        return this;
    }

//    /**
//     * Assigns {@code User-Agent} value.
//     * <p>
//     * Please note this value can be overridden by the {@link #setHttpProcessor(
//     * org.apache.http.protocol.HttpProcessor)} method.
//     * </p>
//     */
//    public final CustomHttpClientBuilder setUserAgent(final String userAgent) {
//        this.builder.setUserAgent(userAgent);
//        return this;
//    }

//    /**
//     * Disables state (cookie) management.
//     * <p>
//     * Please note this value can be overridden by the {@link #setHttpProcessor(
//     * org.apache.http.protocol.HttpProcessor)} method.
//     */
//    final CustomHttpClientBuilder disableCookieManagement() {
//        this.builder.disableCookieManagement();
//        return this;
//    }

    /**
     * Disables automatic content decompression.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     * org.apache.http.protocol.HttpProcessor)} method.
     */
    public final CustomHttpClientBuilder disableContentCompression() {
        this.builder.disableContentCompression();
        return this;
    }

    /**
     * Disables authentication scheme caching.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     * org.apache.http.protocol.HttpProcessor)} method.
     */
    public final CustomHttpClientBuilder disableAuthCaching() {
        this.builder.disableAuthCaching();
        return this;
    }

//    /**
//     * Assigns {@link HttpProcessor} instance.
//     */
//    public final CustomHttpClientBuilder setHttpProcessor(final HttpProcessor httpprocessor) {
//        this.builder.setHttpProcessor(httpprocessor);
//        return this;
//    }

    /**
     * Assigns {@link DnsResolver} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(HttpClientConnectionManager)} method.
     */
    public final CustomHttpClientBuilder setDnsResolver(final DnsResolver dnsResolver) {
        this.builder.setDnsResolver(dnsResolver);
        return this;
    }

    /**
     * Assigns {@link HttpRequestRetryHandler} instance.
     * <p>
     * Please note this value can be overridden by the {@link #disableAutomaticRetries()}
     * method.
     */
    public final CustomHttpClientBuilder setRetryHandler(final HttpRequestRetryHandler retryHandler) {
        this.builder.setRetryHandler(retryHandler);
        return this;
    }

    /**
     * Disables automatic request recovery and re-execution.
     */
    public final CustomHttpClientBuilder disableAutomaticRetries() {
        this.builder.disableAutomaticRetries();
        return this;
    }

    /**
     * Assigns default proxy value.
     * <p>
     * Please note this value can be overridden by the {@link #setRoutePlanner(
     *   org.apache.http.conn.routing.HttpRoutePlanner)} method.
     */
    public final CustomHttpClientBuilder setProxy(final HttpHost proxy) {
        this.builder.setProxy(proxy);
        return this;
    }

    /**
     * Assigns {@link HttpRoutePlanner} instance.
     */
    public final CustomHttpClientBuilder setRoutePlanner(final HttpRoutePlanner routePlanner) {
        this.builder.setRoutePlanner( routePlanner );
        return this;
    }

    /**
     * Assigns {@link RedirectStrategy} instance.
     * <p>
     * Please note this value can be overridden by the {@link #disableRedirectHandling()}
     * method.
     * </p>
     `     */
    public final CustomHttpClientBuilder setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.builder.setRedirectStrategy(redirectStrategy);
        return this;
    }

    /**
     * Disables automatic redirect handling.
     */
    public final CustomHttpClientBuilder disableRedirectHandling() {
        this.builder.disableRedirectHandling();
        return this;
    }

    /**
     * Assigns {@link ConnectionBackoffStrategy} instance.
     */
    public final CustomHttpClientBuilder setConnectionBackoffStrategy(
            final ConnectionBackoffStrategy connectionBackoffStrategy) {
        this.builder.setConnectionBackoffStrategy(connectionBackoffStrategy);
        return this;
    }

    /**
     * Assigns {@link BackoffManager} instance.
     */
    public final CustomHttpClientBuilder setBackoffManager(final BackoffManager backoffManager) {
        this.builder.setBackoffManager(backoffManager);
        return this;
    }

    /**
     * Assigns {@link ServiceUnavailableRetryStrategy} instance.
     */
    public final CustomHttpClientBuilder setServiceUnavailableRetryStrategy(
            final ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
        this.builder.setServiceUnavailableRetryStrategy( serviceUnavailStrategy );
        return this;
    }

//    /**
//     * Assigns default {@link CookieStore} instance which will be used for
//     * request execution if not explicitly set in the client execution context.
//     */
//    final CustomHttpClientBuilder setDefaultCookieStore(final CookieStore cookieStore) {
//        this.builder.setDefaultCookieStore( cookieStore );
//        return this;
//    }

//    /**
//     * Assigns default {@link CredentialsProvider} instance which will be used
//     * for request execution if not explicitly set in the client execution
//     * context.
//     */
//    public final CustomHttpClientBuilder setDefaultCredentialsProvider(
//            final CredentialsProvider credentialsProvider) {
//        this.builder.setDefaultCredentialsProvider ( credentialsProvider);
//        return this;
//    }

//    /**
//     * Assigns default {@link org.apache.http.auth.AuthScheme} registry which will
//     * be used for request execution if not explicitly set in the client execution
//     * context.
//     */
//    public final CustomHttpClientBuilder setDefaultAuthSchemeRegistry(
//            final Lookup<AuthSchemeProvider> authSchemeRegistry) {
//        this.builder.setDefaultAuthSchemeRegistry (authSchemeRegistry);
//        return this;
//    }

//    /**
//     * Assigns default {@link org.apache.http.cookie.CookieSpec} registry which will
//     * be used for request execution if not explicitly set in the client execution
//     * context.
//     *
//     * @see org.apache.http.impl.client.CookieSpecRegistries
//     *
//     */
//    public final CustomHttpClientBuilder setDefaultCookieSpecRegistry(
//            final Lookup<CookieSpecProvider> cookieSpecRegistry) {
//        this.builder.setDefaultCookieSpecRegistry (cookieSpecRegistry);
//        return this;
//    }


//    /**
//     * Assigns a map of {@link org.apache.http.client.entity.InputStreamFactory}s
//     * to be used for automatic content decompression.
//     */
//    public final CustomHttpClientBuilder setContentDecoderRegistry(
//            final Map<String, InputStreamFactory> contentDecoderMap) {
//        this.builder.setContentDecoderRegistry( contentDecoderMap);
//        return this;
//    }

//    /**
//     * Assigns default {@link RequestConfig} instance which will be used
//     * for request execution if not explicitly set in the client execution
//     * context.
//     */
//    public final CustomHttpClientBuilder setDefaultRequestConfig(final RequestConfig config) {
//        this.builder.setDefaultRequestConfig(config);
//        return this;
//    }
//
//    /**
//     * Use system properties when creating and configuring default
//     * implementations.
//     */
//    public final CustomHttpClientBuilder useSystemProperties() {
//        this.builder.useSystemProperties();
//        return this;
//    }

    /**
     * Makes this instance of HttpClient proactively evict expired connections from the
     * connection pool using a background thread.
     * <p>
     * One MUST explicitly close HttpClient with {@link CloseableHttpClient#close()} in order
     * to stop and release the background thread.
     * <p>
     * Please note this method has no effect if the instance of HttpClient is configuted to
     * use a shared connection manager.
     * <p>
     * Please note this method may not be used when the instance of HttpClient is created
     * inside an EJB container.
     *
     * @see #setConnectionManagerShared(boolean)
     * @see org.apache.http.conn.HttpClientConnectionManager#closeExpiredConnections()
     *
     * @since 4.4
     */
    public final CustomHttpClientBuilder evictExpiredConnections() {
        this.builder.evictExpiredConnections();
        return this;
    }

    /**
     * Makes this instance of HttpClient proactively evict idle connections from the
     * connection pool using a background thread.
     * <p>
     * One MUST explicitly close HttpClient with {@link CloseableHttpClient#close()} in order
     * to stop and release the background thread.
     * <p>
     * Please note this method has no effect if the instance of HttpClient is configuted to
     * use a shared connection manager.
     * <p>
     * Please note this method may not be used when the instance of HttpClient is created
     * inside an EJB container.
     *
     * @see #setConnectionManagerShared(boolean)
     * @see org.apache.http.conn.HttpClientConnectionManager#closeExpiredConnections()
     *
     * @param maxIdleTime maximum time persistent connections can stay idle while kept alive
     * in the connection pool. Connections whose inactivity period exceeds this value will
     * get closed and evicted from the pool.
     * @param maxIdleTimeUnit time unit for the above parameter.
     *
     * @deprecated (4.5) use {@link #evictIdleConnections(long, TimeUnit)}
     *
     * @since 4.4
     */
    @Deprecated
    public final CustomHttpClientBuilder evictIdleConnections(final Long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        return evictIdleConnections(maxIdleTime.longValue(), maxIdleTimeUnit);
    }

    /**
     * Makes this instance of HttpClient proactively evict idle connections from the
     * connection pool using a background thread.
     * <p>
     * One MUST explicitly close HttpClient with {@link CloseableHttpClient#close()} in order
     * to stop and release the background thread.
     * <p>
     * Please note this method has no effect if the instance of HttpClient is configuted to
     * use a shared connection manager.
     * <p>
     * Please note this method may not be used when the instance of HttpClient is created
     * inside an EJB container.
     *
     * @see #setConnectionManagerShared(boolean)
     * @see org.apache.http.conn.HttpClientConnectionManager#closeExpiredConnections()
     *
     * @param maxIdleTime maximum time persistent connections can stay idle while kept alive
     * in the connection pool. Connections whose inactivity period exceeds this value will
     * get closed and evicted from the pool.
     * @param maxIdleTimeUnit time unit for the above parameter.
     *
     * @since 4.4
     */
    public final CustomHttpClientBuilder evictIdleConnections(final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        this.builder.evictIdleConnections(maxIdleTime, maxIdleTimeUnit);
        return this;
    }

    private static String[] split(final String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }



    public CloseableHttpClient build() {

        builder.useSystemProperties();
        //arizzini: disabling cookie manager... we don't use cookies. REST are stateless.
        builder.disableCookieManagement();

        // TLSv1.1 and TLSv1.2 are disabled by default in Java 7, we want to enforce TLSv1.2
        final String[] supportedProtocols = SUPPORTED_TLS;
        final String[] supportedCipherSuites = split(System.getProperty("https.cipherSuites"));

        if (ApiConfig.ignoreSSLErrors()) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                // set up a TrustManager that trusts everything
                sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }
                }}, new SecureRandom());

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        sslContext,
                        supportedProtocols,
                        supportedCipherSuites,
                        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                builder.setSSLSocketFactory(sslsf);



                return builder.build();

            } catch (Exception e) {
                //don't worry we simply fall back on the original implementation if this doesn't work..
            }
        } else {
            //arizzini: fallback
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    (SSLSocketFactory) SSLSocketFactory.getDefault(),
                    supportedProtocols,
                    supportedCipherSuites,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            builder.setSSLSocketFactory(sslsf);
        }
        return builder.build();
    }


}
