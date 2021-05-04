package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class FsMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(FsMetricsGenerator.class);
    private static final String PATH_LABEL = "path";

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> fsInfo) {
        log.debug("Generating data about FS stats: {}", fsInfo);

        writer.addGauge("es_fs_total_bytes")
                .withHelp("Total size of ES storage")
                .longValue(((Map<String, Object>) fsInfo.get("total")).get("total_in_bytes"));
        writer.addGauge("es_fs_available_bytes")
                .withHelp("Available size of ES storage")
                .longValue(((Map<String, Object>) fsInfo.get("total")).get("available_in_bytes"));
        writer.addGauge("es_fs_free_bytes")
                .withHelp("Free size of ES storage")
                .longValue(((Map<String, Object>) fsInfo.get("total")).get("free_in_bytes"));

        SingleValueWriter pathTotal = writer.addGauge("es_fs_path_total_bytes")
                .withHelp("Total size of ES storage");
        SingleValueWriter pathAvailable = writer.addGauge("es_fs_path_available_bytes")
                .withHelp("Available size of ES storage");
        SingleValueWriter pathFree = writer.addGauge("es_fs_path_free_bytes")
                .withHelp("Free size of ES storage");

        List<Map<String,Object>> pathStats = (List) fsInfo.get("data");
        for (Map<String, Object> pathStat : pathStats) {
            String path = (String) pathStat.get("path");

            pathTotal.longValue(pathStat.get("total_in_bytes"), PATH_LABEL, path);
            pathAvailable.longValue(pathStat.get("available_in_bytes"), PATH_LABEL, path);
            pathFree.longValue(pathStat.get("free_in_bytes"), PATH_LABEL, path);
        }
        return writer;
    }
}
