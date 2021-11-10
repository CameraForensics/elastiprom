package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.Node;
import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NodeGenerator {
    private static final Logger log = LoggerFactory.getLogger(NodeGenerator.class);

    public PrometheusFormatWriter generateMetrics(PrometheusFormatWriter writer, Node node, String nodeName) {
        log.debug("Generating output for node list: {}", node);

        writer.addGlobalLabel("node_name", nodeName);
        writer.addGauge("es_master")
                .withHelp("Flag to check is node master")
                .longValue(node.isMaster() ? 1 : 0);

        return writer;
    }
}
