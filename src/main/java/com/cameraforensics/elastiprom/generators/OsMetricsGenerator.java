package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OsMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(OsMetricsGenerator.class);

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> osStats) {
        log.debug("Generating output for OS stats: {}", osStats);

        Map<String, Object> cpu = (Map<String, Object>) osStats.get("cpu");
        writer.addGauge("es_cpu_percentage")
                .withHelp("ElasticSearch CPU percentage")
                .longValue(cpu.get("percent"));

        Map<String, Object> loadAverage = (Map<String, Object>) cpu.get("load_average");
        writer.addGauge("es_cpu_loadavg")
                .withHelp("Elasticsearch CPU loadavg")
                .doubleValue(loadAverage.get("1m"),"loadavg", "1m")
                .doubleValue(loadAverage.get("5m"),"loadavg", "5m")
                .doubleValue(loadAverage.get("15m"),"loadavg", "15m");

        Map<String, Object> mem = (Map<String, Object>) osStats.get("mem");
        writer.addGauge("es_memory")
                .withHelp("Elasticsearch memory stats")
                .longValue(mem.get("free_in_bytes"), "memtype", "free")
                .longValue(mem.get("used_in_bytes"), "memtype", "used")
                .longValue(mem.get("total_in_bytes"), "memtype", "total");

        writer.addGauge("es_memory_free_percentage")
                .withHelp("Elasticsearch memory free percentage")
                .longValue(mem.get("free_percent"));

        Map<String, Object> swap = (Map<String, Object>) osStats.get("swap");
        writer.addGauge("es_swap")
                .withHelp("Elasticsearch swap stats")
                .longValue(swap.get("free_in_bytes"), "memtype", "free")
                .longValue(swap.get("used_in_bytes"), "memtype", "used")
                .longValue(swap.get("total_in_bytes"), "memtype", "total");

        return writer;
    }
}
