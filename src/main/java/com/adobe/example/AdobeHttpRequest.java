package com.adobe.example;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.util.Preconditions;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

public class AdobeHttpRequest extends LowLevelHttpRequest {

    private final HttpClient httpClient;

    private final HttpRequestBase request;

    AdobeHttpRequest(HttpClient httpClient, HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    public void addHeader(String name, String value) throws IOException {
        request.addHeader(name, value);
    }

    public LowLevelHttpResponse execute() throws IOException {
        {
/*            if (getStreamingContent() != null) {
                Preconditions.checkArgument(request instanceof HttpEntityEnclosingRequest,
                        "Apache HTTP client does not support %s requests with content.",
                        request.getRequestLine().getMethod());
                ContentEntity entity = new ContentEntity(getContentLength(), getStreamingContent());
              //  entity.setContentEncoding(getContentEncoding());
              //  entity.setContentType(getContentType());
               // ((HttpEntityEnclosingRequest) request).setEntity(entity);
            }*/
            return new AdobeHttpResponse(request, httpClient.execute(request));
        }
    }
}
