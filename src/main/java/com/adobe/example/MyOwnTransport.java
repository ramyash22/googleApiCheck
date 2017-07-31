package com.adobe.example;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpTransport;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;


public class MyOwnTransport extends HttpTransport {

    private final HttpClient httpClient;

    public MyOwnTransport(HttpClient httpClient) {
        this.httpClient = httpClient;
        //RequestConfig params = httpClient.getparam();
        //HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        //params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
    }

    @Override
    public boolean supportsMethod(String method) {
        return true;
    }

    @Override
    protected AdobeHttpRequest buildRequest(String method, String url) {
        HttpRequestBase requestBase;
        if (method.equals(HttpMethods.DELETE)) {
            requestBase = new HttpDelete(url);
        } else if (method.equals(HttpMethods.GET)) {
            requestBase = new HttpGet(url);
        } else if (method.equals(HttpMethods.HEAD)) {
            requestBase = new HttpHead(url);
        } else if (method.equals(HttpMethods.POST)) {
            requestBase = new HttpPost(url);
        } else if (method.equals(HttpMethods.PUT)) {
            requestBase = new HttpPut(url);
        } else if (method.equals(HttpMethods.TRACE)) {
            requestBase = new HttpTrace(url);
        } else if (method.equals(HttpMethods.OPTIONS)) {
            requestBase = new HttpOptions(url);
        } else {
            requestBase = null;
        }
        return new AdobeHttpRequest(httpClient, requestBase);
    }
}
