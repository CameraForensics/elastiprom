package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class PendingTasksMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(PendingTasksMetricsGenerator.class);

    public PrometheusFormatWriter generateMetrics(PrometheusFormatWriter writer,
                                                  Map<String, Object> pendingClusterTasksResponse) {
        List<Map<String, Object>> pendingTasks = (List<Map<String, Object>>) pendingClusterTasksResponse.get("tasks");

        log.debug("Generating output for PendingTasks stats: {}", pendingTasks);

        writer.addGauge("es_tasks_count")
                .withHelp("Number of background tasks running")
                .value(pendingTasks.size());

        SingleValueWriter esTasksTimeInQueueSeconds = writer
                .addGauge("es_tasks_time_in_queue_millis")
                .withHelp("How long this task is in queue (in seconds)");

        for (Map<String, Object> pendingTask : pendingTasks) {
            esTasksTimeInQueueSeconds.longValue(pendingTask.get("time_in_queue_millis"),
                    "id", (String) pendingTask.get("source"),
                    "priority", (String) pendingTask.get("priority"));
        }

        return writer;
    }
}
