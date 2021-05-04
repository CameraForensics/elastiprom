package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransportMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(OsMetricsGenerator.class);

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> transportStats) {
        log.debug("Generating output for transport stats: {}", transportStats);

        writer.addGauge("es_transport_server_connections")
                .withHelp("Number of opened server connections")
                .longValue(transportStats.get("server_open"));

        writer.addGauge("es_transport_rx_packets_count")
                .withHelp("Number of receieved packets")
                .longValue(transportStats.get("rx_count"));
        writer.addGauge("es_transport_rx_bytes_count")
                .withHelp("Number of receieved packets")
                .longValue(transportStats.get("rx_size_in_bytes"));
        writer.addGauge("es_transport_tx_packets_count")
                .withHelp("Number of send packets")
                .longValue(transportStats.get("tx_count"));
        writer.addGauge("es_transport_tx_bytes_count")
                .withHelp("Total size of send packets")
                .longValue(transportStats.get("tx_size_in_bytes"));

        return writer;
    }
}
