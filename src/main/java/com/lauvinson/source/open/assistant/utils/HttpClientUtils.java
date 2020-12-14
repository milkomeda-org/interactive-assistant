/*
 * The MIT License (MIT)
 * Copyright © 2019 <copyright holders>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM,DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Equivalent description see [http://rem.mit-license.org/]
 */

package com.lauvinson.source.open.assistant.utils;

import com.google.gson.Gson;
import com.lauvinson.source.open.assistant.o.ApiResult;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {
    private static PoolingHttpClientConnectionManager cm;
    private static final String EMPTY_STR = "";
    private static final String UTF_8 = "UTF-8";

    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(50);
            cm.setDefaultMaxPerRoute(5);
        }
    }

    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    public static List<String> get(String url) {
        HttpGet httpGet = new HttpGet(url);
        return getResultWithContentType(httpGet);
    }

    public static URI buildURI(String url, Map<String, String > params) throws URISyntaxException {
        URIBuilder ub = new URIBuilder(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);

        return ub.build();
    }

    public static String get(String url, Map<String, String > params) throws URISyntaxException {
        HttpGet httpGet = new HttpGet(buildURI(url, params));
        return getResult(httpGet);
    }

    public static String get(String url, Map<String, Object> headers, Map<String, String> params)
            throws URISyntaxException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        if (params != null) {
            ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
            ub.setParameters(pairs);
        }

        HttpGet httpGet = new HttpGet(ub.build());
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    public static String post(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    public static String post(String url, Map<String, String> params) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(covertParams2NVPS(params), "utf-8"));

//        httpPost.setEntity(new StringEntity(JSON.toJSONString(params), ContentType.APPLICATION_FORM_URLENCODED));
        return getResult(httpPost);
    }

    public static String post(String url, Map<String, Object> headers, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);

        if (params != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }

        httpPost.setEntity(new StringEntity(new Gson().toJson(params), ContentType.APPLICATION_JSON));

        return getResult(httpPost);
    }

    public static String request(String method, String url) {

        RequestBuilder requestBuilder = RequestBuilder.create(method);
        requestBuilder.setUri(url);
        return getResult(requestBuilder);
    }

    public static String request(String method, String url, Map<String, Object> params) {

        RequestBuilder requestBuilder = RequestBuilder.create(method);
        requestBuilder.setUri(url);

        EntityBuilder entityBuilder = EntityBuilder.create();
        entityBuilder.setContentEncoding(UTF_8);
        entityBuilder.setText(new Gson().toJson(params));
        entityBuilder.setContentType(ContentType.APPLICATION_FORM_URLENCODED);

        requestBuilder.setEntity(entityBuilder.build());

        return getResult(requestBuilder);
    }

    public static String request(String method, String url, Map<String, Object> headers, Map<String, String> params) {

        RequestBuilder requestBuilder = RequestBuilder.create(method);
        requestBuilder.setUri(url);

        for (Map.Entry<String, Object> param : headers.entrySet()) {
            requestBuilder.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }

        EntityBuilder entityBuilder = EntityBuilder.create();
        entityBuilder.setContentEncoding(UTF_8);
        entityBuilder.setText(new Gson().toJson(params));
        entityBuilder.setContentType(ContentType.APPLICATION_JSON);

        requestBuilder.setEntity(entityBuilder.build());

        return getResult(requestBuilder);
    }

    public static String uploadFile(String url, byte[] file, String fileName) {

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept-Encoding", "gzip");
        httpPost.setHeader("charset", "utf-8");

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.setCharset(StandardCharsets.UTF_8);
        multipartEntityBuilder.addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, fileName);

        httpPost.setEntity(multipartEntityBuilder.build());

        return getResult(httpPost);
    }

    public static ArrayList<NameValuePair> covertParams2NVPS(Map<String, String> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (param.getValue() != null) {
                pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
        }

        return pairs;
    }

    public static final List<String> unknown = new ArrayList<>(2) {{
        add("unknown");
        add("text");
    }};
    private static List<String> getResultWithContentType(HttpRequestBase request) {
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            // response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // long len = entity.getContentLength();
                Header contentType = entity.getContentType();
                String result = EntityUtils.toString(entity, UTF_8);
                response.close();
                // httpClient.close();
                return new ArrayList<>(2) {{
                    add(result);
                    add(contentType.getValue());
                }};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return unknown;
    }

    private static String getResult(HttpRequestBase request) {
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            // response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // long len = entity.getContentLength();
                String result = EntityUtils.toString(entity, UTF_8);
                response.close();
                // httpClient.close();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EMPTY_STR;
    }

    private static String getResult(RequestBuilder requestBuilder) {
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(requestBuilder.build());
            // response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // long len = entity.getContentLength();
                String result = EntityUtils.toString(entity, UTF_8);
                response.close();
                // httpClient.close();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EMPTY_STR;
    }

    public static ApiResult toApiRequest(String resultStr) {
        return new Gson().fromJson(resultStr, ApiResult.class);
    }
}
