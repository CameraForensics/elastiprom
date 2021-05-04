package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;

import java.util.Map;

public class HttpMetricsGenerator extends MetricsGenerator<Map<String, Object>> {

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> httpStats) {
        writer.addGauge("es_http_server_open")
            .withHelp("Number of opened HTTP connection")
            .longValue(httpStats.get("current_open"));
        writer.addGauge("es_http_total_open")
                .withHelp("Number of totally opened HTTP connection")
                .longValue(httpStats.get("total_opened"));

        return writer;
    }
}
