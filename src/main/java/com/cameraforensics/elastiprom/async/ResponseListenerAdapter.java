package com.cameraforensics.elastiprom.async;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

class ResponseListenerAdapter extends CompletableFuture<String> {
    public ResponseListener asResponseListener() {
        ResponseListenerAdapter self = this;

        return new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
//                RequestLine requestLine = response.getRequestLine();
//                HttpHost host = response.getHost();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode < 200 || statusCode > 299) {
                    self.completeExceptionally(new RuntimeException("Status was: " + statusCode));
                }
                else {
//                    Header[] headers = response.getHeaders();
                    String responseBody;
                    try {
                        responseBody = EntityUtils.toString(response.getEntity());
                    } catch (IOException e) {
                        self.completeExceptionally(e);
                        return;
                    }

                    self.complete(responseBody);
                }
            }

            @Override
            public void onFailure(Exception e) {
                self.completeExceptionally(e);
            }
        };
    }

}
