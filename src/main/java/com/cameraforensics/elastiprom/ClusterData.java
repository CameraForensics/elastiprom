package com.cameraforensics.elastiprom;

public class ClusterData {

    private String clusterName;
    private String version;
    private String pluginVersion;

    public ClusterData(String clusterName, String version, String pluginVersion) {
        this.clusterName = clusterName;
        this.version = version;
        this.pluginVersion = pluginVersion;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getVersion() {
        return version;
    }
}
