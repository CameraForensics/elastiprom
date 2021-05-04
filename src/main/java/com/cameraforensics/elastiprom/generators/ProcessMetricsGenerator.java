package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/*
 Aligned with process data returned by JVM and golang clients

 https://github.com/prometheus/client_java/blob/master/simpleclient_hotspot/src/main/java/io/prometheus/client/hotspot/StandardExports.java
 https://github.com/prometheus/client_golang/blob/master/prometheus/process_collector.go
*/

public class ProcessMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(OsMetricsGenerator.class);

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> processStats) {
        log.debug("Generating output for Process stats: {}", processStats);

        writer.addGauge("process_open_fds")
                .withHelp("Number of opened FD handles")
                .longValue(processStats.get("open_file_descriptors"));

        writer.addGauge("process_max_fds")
                .withHelp("Number of max FD handles available")
                .longValue(processStats.get("max_file_descriptors"));

        Map<String, Object> cpu = (Map<String, Object>) processStats.get("cpu");
        writer.addGauge("process_cpu_millis_total")
                .withHelp("Total user and system CPU time spent in millis.")
        .longValue(cpu.get("total_in_millis"));

        writer.addGauge("process_start_time_seconds")
                .withHelp("Start time of the process since unix epoch in seconds.")
                .longValue(processStats.get("timestamp"));

        Map<String, Object> mem = (Map<String, Object>) processStats.get("mem");
        writer.addGauge("process_virtual_memory_bytes")
                .withHelp("Virtual memory size in bytes.")
                .longValue(mem.get("total_virtual_in_bytes"));

        return writer;
    }
}
