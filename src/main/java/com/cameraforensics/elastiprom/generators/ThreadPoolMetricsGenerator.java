package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ThreadPoolMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(ThreadPoolMetricsGenerator.class);
    private static final String LABEL_NAME = "threadpool";

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> threadPoolStats) {
        log.debug("Generating output for ThreadPool stats: {}", threadPoolStats);

        SingleValueWriter threads = writer.addGauge("es_threadpool_threads")
                .withHelp("Number of configured threads in the threadpool");
        SingleValueWriter queue = writer.addGauge("es_threadpool_queue")
                .withHelp("Size of queue configured in the threadpool");
        SingleValueWriter active = writer.addGauge("es_threadpool_active")
                .withHelp("Active threads in the threadpool");
        SingleValueWriter largest = writer.addGauge("es_threadpool_largest")
                .withHelp("Largest number of threads in the threadpool");

        SingleValueWriter completed = writer.addCounter("es_threadpool_completed")
                .withHelp("Number of completed tasks in the threadpool");
        SingleValueWriter rejected = writer.addGauge("es_threadpool_rejected")
                .withHelp("Number of rejected tasks in the threadpool");



        for (Map.Entry<String, Object> entry : threadPoolStats.entrySet()) {
            String name = entry.getKey();
            Map<String, Object> threadPoolStat = (Map<String, Object>) entry.getValue();

            threads.longValue(threadPoolStat.get("threads"), LABEL_NAME, name);
            queue.longValue(threadPoolStat.get("queue"), LABEL_NAME, name);
            active.longValue(threadPoolStat.get("active"), LABEL_NAME, name);
            largest.longValue(threadPoolStat.get("largest"), LABEL_NAME, name);

            completed.longValue(threadPoolStat.get("completed"), LABEL_NAME, name);
            rejected.longValue(threadPoolStat.get("rejected"), LABEL_NAME, name);
        }
        return writer;
    }
}
