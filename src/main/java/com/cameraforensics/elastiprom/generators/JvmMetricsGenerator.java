package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SummaryValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/*
 Aligned with process data returned by JVM client

 https://github.com/prometheus/client_java/blob/master/simpleclient_hotspot/src/main/java/io/prometheus/client/hotspot/
 including:
   StandardExports.java
   MemoryPoolsExports.java
*/


public class JvmMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(JvmMetricsGenerator.class);

    @Override
    public PrometheusFormatWriter generateMetrics(PrometheusFormatWriter writer, Map<String, Object> jvmStats) {
        log.debug("Generating output for JVM stats: {}", jvmStats);

        log.debug("Now memory: {}", jvmStats.get("mem"));
        writer.addGauge("jvm_memory_bytes_used")
                .withHelp("Used bytes of a given JVM memory area.")
                .longValue(((Map<String, Object>)jvmStats.get("mem")).get("heap_used_in_bytes"), "area", "heap")
                .longValue(((Map<String, Object>)jvmStats.get("mem")).get("non_heap_used_in_bytes"), "area", "nonheap");


        writer.addGauge("jvm_memory_bytes_committed")
                .withHelp("Committed (bytes) of a given JVM memory area")
                .longValue(((Map<String, Object>)jvmStats.get("mem")).get("heap_committed_in_bytes"), "area", "heap")
                .longValue(((Map<String, Object>)jvmStats.get("mem")).get("non_heap_committed_in_bytes"), "area", "nonheap");

        writer.addGauge("jvm_memory_bytes_max")
                .withHelp("Max (bytes) of a given JVM memory area.")
                .longValue(((Map<String, Object>)jvmStats.get("mem")).get("heap_max_in_bytes"), "area", "heap");


        //JVM threads
        log.debug("Now threads: {}", jvmStats.get("threads"));
        writer.addGauge("jvm_threads_current")
                .withHelp("Current thread count of a JVM")
                .longValue(((Map<String, Object>)jvmStats.get("threads")).get("count"));

        writer.addGauge("jvm_threads_peak")
                .withHelp("Peak thread count of a JVM")
                .longValue(((Map<String, Object>)jvmStats.get("threads")).get("peak_count"));

        //JVM classes
        log.debug("Now classes: {}", jvmStats.get("classes"));
        writer.addGauge("jvm_classes_loaded")
                .withHelp("The number of classes that are currently loaded in the JVM")
                .longValue(((Map<String, Object>)jvmStats.get("classes")).get("current_loaded_count"));

        writer.addCounter("jvm_classes_loaded_total")
                .withHelp("The total number of classes that have been loaded since the JVM has started execution")
                .longValue(((Map<String, Object>)jvmStats.get("classes")).get("total_loaded_count"));

        writer.addCounter("jvm_classes_unloaded_total")
                .withHelp("The total number of classes that have been unloaded since the JVM has started execution")
                .longValue(((Map<String, Object>)jvmStats.get("classes")).get("total_unloaded_count"));

        SummaryValueWriter gcValueWriter = writer.addSummary("jvm_gc_collection_seconds")
                .withHelp("The total number of seconds spend on GC collection");

        Map<String, Object> collectors = (Map<String, Object>) ((Map<String, Object>) jvmStats.get("gc")).get("collectors");
        for (Map.Entry<String, Object> collector : collectors.entrySet()) {
            String name = collector.getKey();
            Map<String, Object> vals = (Map<String, Object>) collector.getValue();
            gcValueWriter.summaryFromObject(vals.get("collection_count"), vals.get("collection_time_in_millis"), "name", name);
        }

        //ES custom fields
        log.debug("Now custom: {}", jvmStats);

        writer.addGauge("es_jvm_timestamp")
                .withHelp("Timestamp of last JVM status scrap")
                .longValue(jvmStats.get("timestamp"));

        writer.addGauge("es_jvm_uptime")
                .withHelp("Node uptime in millis")
                .longValue(jvmStats.get("uptime_in_millis"));

        writer.addGauge("es_jvm_memory_heap_used_percent")
                .withHelp("Heap memory of JVM (in percentage)")
                .longValue(((Map<String, Object>) jvmStats.get("mem")).get("heap_used_percent"));

        return writer;
    }
}
