package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.cameraforensics.elastiprom.writer.ValueUtils.convertToSeconds;

public class IngestMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(IngestMetricsGenerator.class);

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> ingestStats) {
        log.debug("Generating metrics about ingest stats: {}", ingestStats);

        Map<String, Object> total = (Map<String, Object>) ingestStats.get("total");
        writer.addCounter("es_ingest_total_count")
                .withHelp("Total number of processed documents during ingestion phase")
                .longValue(total.get("count"));

        writer.addCounter("es_ingest_total_time_seconds")
                .withHelp("Total time spend on processed documents during ingestion phase")
                .value(convertToSeconds(total.get("time_in_millis")));

        writer.addGauge("es_ingest_total_current")
                .withHelp("The total number of ingest preprocessing operations that have failed")
                .longValue(total.get("current"));

        writer.addGauge("es_ingest_total_failed_count")
                .withHelp("The total number of ingest preprocessing operations that have failed")
                .longValue(total.get("failed"));


        SingleValueWriter es_ingest_pipeline_count = writer.addCounter("es_ingest_pipeline_count")
                .withHelp("Total number of processed documents during ingestion phase in pipeline");

        SingleValueWriter es_ingest_pipeline_time_seconds = writer.addCounter("es_ingest_pipeline_time_seconds")
                .withHelp("Total time spend on processed documents during ingestion phase in pipeline");

        SingleValueWriter es_ingest_pipeline_current = writer.addGauge("es_ingest_pipeline_current")
                .withHelp("The total number of ingest preprocessing operations that have failed in pipeline");

        SingleValueWriter es_ingest_pipeline_failed_count = writer.addGauge("es_ingest_pipeline_failed_count")
                .withHelp("The total number of ingest preprocessing operations that have failed in pipeline");

        Map<String, Object> pipelines = (Map<String, Object>) ingestStats.get("pipelines");
        for (Map.Entry<String, Object> pipeline : pipelines.entrySet()) {
            String pipelineName = pipeline.getKey();
            Map<String, Object> stats = (Map<String, Object>) pipeline.getValue();

            es_ingest_pipeline_count.longValue(stats.get("count"), "pipeline", pipelineName);
            es_ingest_pipeline_time_seconds.value(convertToSeconds(stats.get("time_in_millis")), "pipeline", pipelineName);
            es_ingest_pipeline_current.longValue(stats.get("current"), "pipeline", pipelineName);
            es_ingest_pipeline_failed_count.longValue(stats.get("count"), "pipeline", pipelineName);
        }

        return writer;
    }
}
