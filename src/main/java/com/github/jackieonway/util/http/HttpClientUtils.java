package com.github.jackieonway.util.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jackie
 */
public enum  HttpClientUtils {

    /**
     * HttpClientUtils 实例
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);
	private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
    private static final CloseableHttpClient HTTP_CLIENT;

    static {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            LOGGER.error(" create ssl context error", e);
        }
        SSLConnectionSocketFactory ssf = null;
        if (ctx != null) {
            ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
        }
		CONNECTION_MANAGER.setMaxTotal(500);
		CONNECTION_MANAGER.setDefaultMaxPerRoute(200);
        RequestConfig.Builder custom = RequestConfig.custom();
        httpClientBuilder.setConnectionManager(CONNECTION_MANAGER).setConnectionManagerShared(true);
        custom.setConnectTimeout(5000);
        custom.setSocketTimeout(10000);
        RequestConfig requestConfig = custom.build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        httpClientBuilder.setSSLSocketFactory(ssf);
		HTTP_CLIENT = httpClientBuilder.build();
    }

    public static String doGet(String url, Map<String, String> param) {
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                param.forEach(builder::addParameter);
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            // 执行请求
            response = HTTP_CLIENT.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), getContentCharSet(response.getEntity()));
            }
        } catch (Exception e) {
            LOGGER.error("do get method error ----------", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error("do get method error ----------", e);
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, Object> param) {
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                param.forEach((key,value) ->  {
                    if (Objects.nonNull(value)){
                        paramList.add(new BasicNameValuePair(key, value +""));
                    }
                });
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = HTTP_CLIENT.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("do post method error ----------", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error("do post method error ----------", e);
            }
        }
        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = HTTP_CLIENT.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("do post json method error ----------", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error("do post json method error ----------", e);
            }
        }

        return resultString;
    }

    /**
     * 发送xml格式的HTTP POST请求
     *
     * @param url 请求地址
     * @param xml 请求的xml数据
     * @return 服务端返回的数据字符串
     */
    public static String doPostXml(String url, String xml) {
        // 创建Httpclient对象
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(xml, ContentType.APPLICATION_XML);
            httpPost.setEntity(entity);
            // 执行http请求
            response = HTTP_CLIENT.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("do post xml method error ----------", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error("do post xml method error ----------", e);
            }
        }

        return resultString;
    }

    /**
     * 默认编码utf -8
     * Obtains character set of the entity, if known.
     *
     * @param entity must not be null
     * @return the character set, or null if not found
     * @throws IllegalArgumentException if entity is null
     */
    private static String getContentCharSet(final HttpEntity entity) {

        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        String charset = null;
        if (entity.getContentType() != null) {
            HeaderElement[] values = entity.getContentType().getElements();
            if (values.length > 0) {
                NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
        }

        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        return charset;
    }
}
