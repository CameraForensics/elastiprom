package com.cameraforensics.elastiprom.async;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;

import java.util.concurrent.CompletableFuture;

class ActionListenerAdapter<T> extends CompletableFuture<T> {
    public ActionListener<T> asActionListener() {
        ActionListenerAdapter<T> self = this;

        return new ActionListener<T>() {
            @Override
            public void onResponse(T t) {
                self.complete(t);
            }

            @Override
            public void onFailure(Exception e) {
                self.completeExceptionally(e);
            }
        };
    }
}
