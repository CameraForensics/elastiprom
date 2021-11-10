package com.cameraforensics.elastiprom;

import com.cameraforensics.elastiprom.generators.*;
import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.elasticsearch.Version;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.cameraforensics.elastiprom.async.AsyncRequests.*;

@RestController
public class MetricsController {
    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);
    private final JvmMetricsGenerator jvmMetricsGenerator = new JvmMetricsGenerator();
    private final IndicesMetricsGenerator indicesMetricsGenerator = new IndicesMetricsGenerator();
    private final ClusterHealthMetricsGenerator clusterHealthMetricsGenerator = new ClusterHealthMetricsGenerator();
    private final OsMetricsGenerator osMetricsGenerator = new OsMetricsGenerator();
    private final ClusterStateMetricsGenerator clusterStateMetricsGenerator = new ClusterStateMetricsGenerator();
    private final TransportMetricsGenerator transportMetricsGenerator = new TransportMetricsGenerator();
    private final IngestMetricsGenerator ingestMetricsGenerator = new IngestMetricsGenerator();
    private final ProcessMetricsGenerator processMetricsGenerator = new ProcessMetricsGenerator();
    private final PendingTasksMetricsGenerator pendingTasksMetricsGenerator = new PendingTasksMetricsGenerator();
    private final CircuitBreakerMetricsGenerator circuitBreakerMetricsGenerator = new CircuitBreakerMetricsGenerator();
    private final FsMetricsGenerator fsMetricsGenerator = new FsMetricsGenerator();
    private final ThreadPoolMetricsGenerator threadPoolMetricsGenerator = new ThreadPoolMetricsGenerator();
    private final NodeUsageGenerator nodeUsageGenerator = new NodeUsageGenerator();
    private final HttpMetricsGenerator httpMetricsGenerator = new HttpMetricsGenerator();
    private final ScriptsGenerator scriptsGenerator = new ScriptsGenerator();
    private final NodeGenerator nodeGenerator = new NodeGenerator();
    private final ShardsGenerator shardsGenerator = new ShardsGenerator();

    private Elasticsearch elasticsearch;

    @Autowired
    public MetricsController(Elasticsearch elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    private static Function<Throwable, ResponseEntity<String>> handleGetMetricsFailure = throwable -> {
        log.error("Error getting stats: {}", throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    };

    @GetMapping(value = "/metrics", produces = "text/plain")
    public CompletableFuture<ResponseEntity<String>> allMetrics() {
        CombinedClient client = elasticsearch.client();
        return createWriter(elasticsearch.getClusterData()).thenCombine(getNodeStats(client), this::generateNodeMetrics)
                .thenCombine(getNodeStats(client), this::generateNodeMetrics)
                .thenCombine(getClusterHealth(client), clusterHealthMetricsGenerator::generateMetrics)
                .thenCombine(getClusterSettings(client), clusterStateMetricsGenerator::generateMetrics)
                .thenCombine(getPendingTasks(client), pendingTasksMetricsGenerator::generateMetrics)
                .thenCombine(getShardList(client), shardsGenerator::generateMetrics)
                .thenCombine(getNodesUsage(client), this::generateNodeUsageMetrics)
                .thenCombine(getNodeList(client), this::generateNodeListMetrics)
                .thenApply(PrometheusFormatWriter::toString)
                .thenApply(ResponseEntity::ok)
                .exceptionally(handleGetMetricsFailure)
                ;
    }

    @GetMapping(value = "/metrics/node", produces = "text/plain")
    public CompletableFuture<ResponseEntity<String>> nodeStats() {
        CombinedClient client = elasticsearch.client();
        return createWriter(elasticsearch.getClusterData()).thenCombine(getNodeStats(client), this::generateNodeMetrics)
                .thenCombine(getNodeList(client), this::generateNodeListMetrics)
                .thenApply(PrometheusFormatWriter::toString)
                .thenApply(ResponseEntity::ok)
                .exceptionally(handleGetMetricsFailure)
                ;
    }

    @GetMapping(value = "/metrics/cluster/health", produces = "text/plain")
    public CompletableFuture<ResponseEntity<String>> clusterHealth() {
        CombinedClient client = elasticsearch.client();
        return createWriter(elasticsearch.getClusterData())
                .thenCombine(getClusterHealth(client), clusterHealthMetricsGenerator::generateMetrics)
                .thenApply(PrometheusFormatWriter::toString)
                .thenApply(ResponseEntity::ok)
                .exceptionally(handleGetMetricsFailure)
                ;
    }

    @GetMapping(value = "/metrics/cluster/settings", produces = "text/plain")
    public CompletableFuture<ResponseEntity<String>> clusterSettings() {
        CombinedClient client = elasticsearch.client();
        return createWriter(elasticsearch.getClusterData())
                .thenCombine(getClusterSettings(client), clusterStateMetricsGenerator::generateMetrics)
                .thenApply(PrometheusFormatWriter::toString)
                .thenApply(ResponseEntity::ok)
                .exceptionally(handleGetMetricsFailure)
                ;
    }

    @GetMapping(value = "/metrics/cluster/pending-tasks", produces = "text/plain")
    public CompletableFuture<ResponseEntity<String>> clusterPendingTasks() {
        CombinedClient client = elasticsearch.client();
        return createWriter(elasticsearch.getClusterData())
                .thenCombine(getPendingTasks(client), pendingTasksMetricsGenerator::generateMetrics)
                .thenApply(PrometheusFormatWriter::toString)
                .thenApply(ResponseEntity::ok)
                .exceptionally(handleGetMetricsFailure)
                ;
    }

    @GetMapping(value = "/metrics/node-usage", produces = "text/plain")
    public CompletableFuture<ResponseEntity<String>> nodeUsage() {
        CombinedClient client = elasticsearch.client();
        return createWriter(elasticsearch.getClusterData())
                .thenCombine(getNodesUsage(client), this::generateNodeUsageMetrics)
                .thenApply(PrometheusFormatWriter::toString)
                .thenApply(ResponseEntity::ok)
                .exceptionally(handleGetMetricsFailure)
                ;
    }

    private static CompletableFuture<PrometheusFormatWriter> createWriter(final ClusterData clusterData) {
        Map<String, String> globalLabels = new HashMap<>();
        globalLabels.put("cluster", clusterData.getClusterName());
        globalLabels.put("cluster_version", clusterData.getVersion());

        PrometheusFormatWriter writer = new PrometheusFormatWriter(globalLabels);

        writer.addGauge("es_prometheus_version")
                .withHelp("Plugin version to track across a cluster")
                .value(1, "pluginVersion", "7.9.0", "es_version", Version.CURRENT.toString());

        return CompletableFuture.completedFuture(writer);
    }

    private PrometheusFormatWriter generateNodeListMetrics(PrometheusFormatWriter writer, List<Node> nodes) {
        for (Node node : nodes){
            log.debug("Found node stats: {}", node);
            nodeGenerator.generateMetrics(writer, node, node.getName());
        }

        return writer;
    }

    private PrometheusFormatWriter generateNodeUsageMetrics(PrometheusFormatWriter writer, Map<String, Object> responseData) {
        Map<String, Object> nodes = (Map<String, Object>) responseData.get("nodes");
        for (Map.Entry<String, Object> node : nodes.entrySet()){
            String nodeId = node.getKey();
            Map<String, Object> nodeUsage = (Map<String, Object>) node.getValue();
            log.debug("Found node stats: {}", nodeUsage);
            nodeUsageGenerator.generateMetrics(writer, nodeUsage, nodeId);
        }

        return writer;
    }

    private PrometheusFormatWriter generateNodeMetrics(PrometheusFormatWriter writer, Map<String, Object> responseData) {
        Map<String, Object> nodes = (Map<String, Object>) responseData.get("nodes");
        for (Map.Entry<String, Object> node : nodes.entrySet()){
            String nodeId = node.getKey();
            Map<String, Object> nodeStats = (Map<String, Object>) node.getValue();
            String nodeName = (String) nodeStats.get("name");
            writer.addGlobalLabel("node_name", nodeName);
            writer.addGlobalLabel("node_id", nodeId);
            log.debug("Found node stats: {} Has JVM stats? {}", nodeStats, nodeStats.keySet());
            jvmMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("jvm"));
            fsMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("fs"));
            httpMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("http"));
            indicesMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("indices"));
            osMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("os"));
            ingestMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("ingest"));
            processMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("process"));
            scriptsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("script"));
            threadPoolMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("thread_pool"));
            transportMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("transport"));
            circuitBreakerMetricsGenerator.generateMetrics(writer, (Map<String, Object>) nodeStats.get("breakers"));
        }

        return writer;
    }

}
