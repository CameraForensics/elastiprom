package com.cameraforensics.elastiprom.async;

import com.cameraforensics.elastiprom.CombinedClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.elasticsearch.client.RequestOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AsyncRequests {
    public static CompletableFuture<Map<String, Object>> getNodeStats(final CombinedClient client) {
        ResponseListenerAdapter result = new ResponseListenerAdapter();
        client.nodeStats(result.asResponseListener());

        return result.thenApply((response)-> {
            try {
                return new ObjectMapper().readValue(response, new TypeReference<HashMap<String, Object>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<Map<String, Object>> clusterData(final CombinedClient client) {
        ResponseListenerAdapter result = new ResponseListenerAdapter();
        client.clusterData(result.asResponseListener());

        return result.thenApply((response)-> {
            try {
                return new ObjectMapper().readValue(response, new TypeReference<HashMap<String, Object>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<ClusterHealthResponse> getClusterHealth(final CombinedClient client) {
        ActionListenerAdapter<ClusterHealthResponse> result = new ActionListenerAdapter<>();
        ClusterHealthRequest clusterHealthRequest = new ClusterHealthRequest();
        clusterHealthRequest.level(ClusterHealthRequest.Level.SHARDS);
        client.cluster().healthAsync(clusterHealthRequest, RequestOptions.DEFAULT, result.asActionListener());
        return result;
    }

    public static CompletableFuture<ClusterGetSettingsResponse> getClusterSettings(final CombinedClient client) {
        ActionListenerAdapter<ClusterGetSettingsResponse> result = new ActionListenerAdapter<>();
        ClusterGetSettingsRequest request = new ClusterGetSettingsRequest();
        request.includeDefaults();

        client.cluster().getSettingsAsync(request, RequestOptions.DEFAULT, result.asActionListener());
        return result;
    }

    public static CompletableFuture<Map<String, Object>> getPendingTasks(final CombinedClient client) {
        ResponseListenerAdapter result = new ResponseListenerAdapter();
        client.pendingTasks(result.asResponseListener());

        return result.thenApply((response)-> {
            try {
                return new ObjectMapper().readValue(response, new TypeReference<HashMap<String, Object>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<Map<String, Object>> getNodesUsage(final CombinedClient client) {
        ResponseListenerAdapter result = new ResponseListenerAdapter();
        client.nodeUsage(result.asResponseListener());

        return result.thenApply((response)-> {
            try {
                return new ObjectMapper().readValue(response, new TypeReference<HashMap<String, Object>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
