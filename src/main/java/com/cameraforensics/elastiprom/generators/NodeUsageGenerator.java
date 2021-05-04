package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NodeUsageGenerator {
    private static final Logger log = LoggerFactory.getLogger(NodeUsageGenerator.class);
    private static final String ENDPOINT_LABEL = "endpoint";

    public PrometheusFormatWriter generateMetrics(PrometheusFormatWriter writer, Map<String, Object> nodeUsage, String nodeId) {
        log.debug("Generating output for REST usage: {}", nodeUsage);

        SingleValueWriter restActions = writer.addCounter("es_rest_count")
                .withHelp("Number of REST endpoint executions");

        Map<String, Object> rest = (Map<String, Object>) nodeUsage.get("rest_actions");

        rest.forEach((key, value) -> restActions.longValue(value, ENDPOINT_LABEL, key, "node_id", nodeId));
        return writer;
    }
}
