package com.cameraforensics.elastiprom;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.elasticsearch.client.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Disabled("Used for local testing")
public class MetricsControllerTest {

    MetricsController controller = new MetricsController(new Elasticsearch("localhost", 9200, "http", "none", "7.9.0"));

    @Test
    public void can_get_stats() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity<String>> result = controller.nodeStats();
        ResponseEntity<String> response = result.get();
        System.out.println(response.getBody());
    }

    @Test
    public void can_get_cluster_stats() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity<String>> result = controller.clusterHealth();
        ResponseEntity<String> response = result.get();
        System.out.println(response.getBody());
    }

    @Test
    public void can_get_cluster_settings() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity<String>> result = controller.clusterSettings();
        ResponseEntity<String> response = result.get();
        System.out.println(response.getBody());
    }

    @Test
    public void can_get_cluster_pending_tasks() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity<String>> result = controller.clusterPendingTasks();
        ResponseEntity<String> response = result.get();
        System.out.println(response.getBody());
    }

    @Test
    public void can_get_node_usage() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity<String>> result = controller.nodeUsage();
        ResponseEntity<String> response = result.get();
        System.out.println(response.getBody());
    }

    @Test
    public void can_get_all_metrics() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity<String>> result = controller.allMetrics();
        ResponseEntity<String> response = result.get();
        System.out.println(response.getBody());
    }

}
