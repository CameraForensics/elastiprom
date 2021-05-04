package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import com.cameraforensics.elastiprom.writer.SingleValueWriter;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterStateMetricsGenerator extends MetricsGenerator<ClusterGetSettingsResponse> {
    private static final Logger log = LoggerFactory.getLogger(ClusterStateMetricsGenerator.class);

    public PrometheusFormatWriter generateMetrics(PrometheusFormatWriter writer, ClusterGetSettingsResponse clusterState) {
        log.debug("Generating data about cluster state: {}", clusterState);

        SingleValueWriter persistentGauge = writer.addGauge("es_cluster_persistent_settings")
                .withHelp("Cluster persistent settings value visible from this node");
        fillSettings(persistentGauge, clusterState.getPersistentSettings());

        SingleValueWriter transientGauge = writer.addGauge("es_cluster_transient_settings")
                .withHelp("Cluster persistent settings value visible from this node");
        fillSettings(transientGauge, clusterState.getTransientSettings());

        SingleValueWriter settingsGauge = writer.addGauge("es_cluster_settings")
                .withHelp("Cluster effective settings value visible from this node");
        fillSettings(settingsGauge, clusterState.getDefaultSettings());

        return writer;
    }

    private void fillSettings(SingleValueWriter settingsGauge, Settings settings) {
        settings.keySet().forEach((key) -> {
            settingsGauge.value(1, key, settings.get(key));
        });
    }
}
