package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ShardsGenerator extends MetricsGenerator<List<Map<String, Object>>> {
    private static final Logger log = LoggerFactory.getLogger(ShardsGenerator.class);

    @Override
    public PrometheusFormatWriter generateMetrics(PrometheusFormatWriter writer, List<Map<String, Object>> shards) {
        log.debug("Generating output for shards: {}", shards);

        for (Map<String, Object> shard : shards) {
            writer.addCounter("es_shard_documents")
                    .withHelp("Number of documents in shard")
                    .longValue(shard.get("docs"), "node_name", (String) shard.get("node"), "index", (String) shard.get("index"), "prirep", (String) shard.get("prirep"), "state", (String) shard.get("state"), "shard", (String) shard.get("shard"));

        }
        return writer;
    }

}
