package com.cameraforensics.elastiprom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class NodeStatsResponse {

    private String clusterName;
    private List<NodeStats> nodes = new ArrayList<>();


    class NodeStats {
        private long timestamp;
        private String name;
        @JsonProperty("transport_address")
        private String transportAddress;
        private String ip;
        private List<String> roles;
        private Attributes attributes;
        private Indices indices;

    }

    class Attributes {
        @JsonProperty("xpack.installed")
        private String xPackInstalled;

        @JsonProperty("transform.node")
        private String transformNode;
    }

    class Indices {
        private Docs docs;
    }

    class Docs {
        private long count;
        private long deleted;
    }

    class Store {
        @JsonProperty("size_in_bytes")
        private long sizeInBytes;

        @JsonProperty("reserved_in_bytes")
        private long reservedInBytes;
    }
}
