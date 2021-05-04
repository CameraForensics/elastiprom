package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;

import java.util.Map;

public class ScriptsGenerator extends MetricsGenerator<Map<String, Object>> {
    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> scriptsStats) {
        writer.addGauge("es_scripts_compilations")
                .withHelp("Number of compilations")
                .longValue(scriptsStats.get("compilations"));

        writer.addGauge("es_scripts_cache_evictions")
                .withHelp("Number of scripts cache evictions")
                .longValue(scriptsStats.get("cache_evictions"));

        writer.addGauge("es_scripts_compilations_limits")
                .withHelp("Number of compilations timeout reached")
                .longValue(scriptsStats.get("compilation_limit_triggered"));
        return writer;
    }
}
