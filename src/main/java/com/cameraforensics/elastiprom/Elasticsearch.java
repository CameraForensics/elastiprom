package com.cameraforensics.elastiprom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.cameraforensics.elastiprom.async.AsyncRequests.clusterData;

@Service
public class Elasticsearch {
    private static final Logger log = LoggerFactory.getLogger(Elasticsearch.class);

    @Value("${es.host}")
    private String host;

    @Value("${es.port}")
    private Integer port;

    @Value("${es.scheme}")
    private String scheme;

    @Value("${plugin.version}")
    private String pluginVersion;

    @Value("${es.auth}")
    private String auth;

    private CombinedClient client;
    private ClusterData clusterData;

    public Elasticsearch() {}

    public Elasticsearch(final String host, final Integer port, final String scheme, final String auth, final String pluginVersion) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
        this.pluginVersion = pluginVersion;
        this.auth = auth;
        this.setup();
    }

    @PostConstruct
    public void setup() {
        log.info("Connecting to cluster at: {}://{}:{}", scheme, port, host);
        client = new CombinedClient(host, port, scheme, auth);
        CompletableFuture<Map<String, Object>> result = clusterData(client);
        try {
            Map<String, Object> data = result.get();
            log.debug("Data from cluster root request: {}", data);
            clusterData = new ClusterData((String) data.get("cluster_name"), (String) ((Map<String, Object>) data.get("version")).get("number"), pluginVersion);
        } catch (InterruptedException e) {
            log.error("Interrupted fetching cluster data", e);
            throw new RuntimeException("Cluster is unreachable");
        } catch (ExecutionException e) {
            log.error("Exception fetching cluster data", e);
            throw new RuntimeException("Cluster is unreachable");
        }
    }

    public CombinedClient client() {
        return client;
    }

    public ClusterData getClusterData() {
        return clusterData;
    }
}
