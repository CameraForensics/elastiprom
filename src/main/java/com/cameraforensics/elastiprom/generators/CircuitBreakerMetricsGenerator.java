package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CircuitBreakerMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerMetricsGenerator.class);
    private static final String LABEL_NAME = "circuit_name";

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> allStats) {
        log.debug("Generating data about circuit breaker: {}", allStats);

        SingleValueWriter circuitBreakerLimit = writer
                .addGauge("es_breaker_limit_bytes")
                .withHelp("Memory limit of circuit breaker in bytes");

        SingleValueWriter circuitBreakerEstimated = writer
                .addGauge("es_breaker_estimated_bytes")
                .withHelp("Estimated memory of circuit breaker in bytes");

        SingleValueWriter circuitBreakerTripped = writer
                .addCounter("es_breaker_tripped")
                .withHelp("Counter of how many times circuit breaker tripped");


        SingleValueWriter circuitBreakerOverhead = writer.addGauge("es_breaker_overhead")
                .withHelp("Overhead factor for circuit breaker");

        for (Map.Entry<String, Object> entry : allStats.entrySet()) {
            String name = entry.getKey();
            Map<String, Object> cbStats = (Map<String, Object>) entry.getValue();

            circuitBreakerEstimated.longValue(cbStats.get("estimated_size_in_bytes"), LABEL_NAME, name);
            circuitBreakerLimit.longValue(cbStats.get("limit_size_in_bytes"), LABEL_NAME, name);
            circuitBreakerTripped.longValue(cbStats.get("tripped"), LABEL_NAME, name);
            circuitBreakerOverhead.doubleValue(cbStats.get("overhead"), LABEL_NAME, name);
        }
        return writer;
    }
}
