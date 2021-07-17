package com.howtodoinjava.app.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class HttpClientConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConfig.class);

	//@formatter:off
	private static final int CONNECT_TIMEOUT = Integer.getInteger("HTTP_CONNECT_TIMEOUT", 10_000);
	private static final int REQUEST_TIMEOUT = Integer.getInteger("HTTP_REQUEST_TIMEOUT", 30_000);
	private static final int SOCKET_TIMEOUT = Integer.getInteger("HTTP_REQUEST_TIMEOUT", REQUEST_TIMEOUT);
	private static final int MAX_TOTAL_CONNECTIONS = Integer.getInteger("MAX_TOTAL_CONNECTIONS", 50);
	private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS 
				= Integer.getInteger("DEFAULT_KEEP_ALIVE_TIME_MILLIS", 20_000);
	private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS 
				= Integer.getInteger("DEFAULT_KEEP_ALIVE_TIME_MILLIS", DEFAULT_KEEP_ALIVE_TIME_MILLIS);
	//@formatter:on

	@Bean
	public PoolingHttpClientConnectionManager poolingConnectionManager() {
		SSLContextBuilder builder = new SSLContextBuilder();
		try {
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		}
		catch (NoSuchAlgorithmException | KeyStoreException e) {
			LOGGER.error("Pooling Connection Manager Initialisation failure because of "
					+ e.getMessage(), e);
		}

		SSLConnectionSocketFactory sslsf = null;
		try {
			sslsf = new SSLConnectionSocketFactory(builder.build());
		}
		catch (KeyManagementException | NoSuchAlgorithmException e) {
			LOGGER.error("Pooling Connection Manager Initialisation failure because of "
					+ e.getMessage(), e);
		}

		org.apache.http.config.Registry<
				ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
						.<ConnectionSocketFactory> create().register("https", sslsf)
						.register("http", new PlainConnectionSocketFactory())
						.build();

		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		return poolingConnectionManager;
	}

	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (response, context) -> {
			HeaderElementIterator it = new BasicHeaderElementIterator(
					response.headerIterator(HTTP.CONN_KEEP_ALIVE));

			while (it.hasNext()) {
				org.apache.http.HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();

				if (value != null && param.equalsIgnoreCase("timeout")) {
					return Long.parseLong(value) * 1000;
				}
			}
			return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
		};
	}

	@Bean
	public CloseableHttpClient httpClient() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(REQUEST_TIMEOUT)
				.setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();

		return HttpClients.custom().setDefaultRequestConfig(requestConfig)
				.setConnectionManager(poolingConnectionManager())
				.setKeepAliveStrategy(connectionKeepAliveStrategy())
				.build();
	}

	@Bean
	public Runnable
			idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
		return new Runnable() {

			@Override
			@Scheduled(fixedDelay = 10000)
			public void run() {
				try {
					if (connectionManager != null) {
						LOGGER.trace(
								"run IdleConnectionMonitor - Closing expired and idle connections...");
						connectionManager.closeExpiredConnections();
						connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS,
								TimeUnit.SECONDS);
					}
					else {
						LOGGER.trace(
								"run IdleConnectionMonitor - Http Client Connection manager is not initialised");
					}
				}
				catch (Exception e) {
					LOGGER.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}",
							e.getMessage(), e);
				}
			}
		};
	}
}