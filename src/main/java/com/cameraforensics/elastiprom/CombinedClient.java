package com.cameraforensics.elastiprom;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.*;
import org.elasticsearch.client.cluster.RemoteInfoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpointConfig;

public class CombinedClient {
    private static final Logger log = LoggerFactory.getLogger(CombinedClient.class);

    private final int CONNECT_TIMEOUT = 5000;
    private final int SOCKET_TIMEOUT = 60000;

    private String hostname;
    private int port;
    private String scheme;
    private String auth;

    private RestClient lowLevelClient;
    private RestHighLevelClient highLevelClient;

    public CombinedClient(final String hostname, final int port, final String scheme, final String auth) {
        this.hostname = hostname;
        this.port = port;
        this.scheme = scheme;
        this.auth = auth;
    }

    public void nodeList(final ResponseListener listener) {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Accept", "application/json");
        Request req = new Request("GET", "/_cat/nodes");
        req.setOptions(builder.build());
        getLowLevelClient().performRequestAsync(req, listener);
    }

    public void shardList(final ResponseListener listener) {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Accept", "application/json");
        Request req = new Request("GET", "/_cat/shards");
        req.setOptions(builder.build());
        getLowLevelClient().performRequestAsync(req, listener);
    }

    public void nodeStats(final ResponseListener listener) {
//        Request req = new Request("GET", "/_nodes/_local/stats");
        Request req = new Request("GET", "/_nodes/stats");
        getLowLevelClient().performRequestAsync(req, listener);
    }

    public void clusterData(final ResponseListener listener) {
        Request req = new Request("GET", "/");
        getLowLevelClient().performRequestAsync(req, listener);
    }

    public void pendingTasks(final ResponseListener listener) {
        Request req = new Request("GET", "/_cluster/pending_tasks");
        getLowLevelClient().performRequestAsync(req, listener);
    }

    public void nodeUsage(final ResponseListener listener) {
        Request req = new Request("GET", "/_nodes/usage");
        getLowLevelClient().performRequestAsync(req, listener);
    }

    public ClusterClient cluster() {
        return highLevelClient.cluster();
    }

    private RestHighLevelClient getHighLevelClient() {
        if (highLevelClient == null) {
            RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, port, scheme))
                    .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                            .setConnectTimeout(CONNECT_TIMEOUT)
                            .setSocketTimeout(SOCKET_TIMEOUT));

            if (auth.toLowerCase().startsWith("basic")) {
                String[] parts = auth.split(":");
                if (parts.length != 3) {
                    log.error("Auth is set to basic, but setting is malformed. It should be formatted as: basic:username:password. Setting value found: {}", auth);
                    throw new RuntimeException("Auth is set to basic, but setting is malformed. It should be formatted as: basic:username:password. Setting value found: " + auth);
                }
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(parts[1], parts[2]));

                builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            }

            highLevelClient = new RestHighLevelClient(builder);
        }
        return highLevelClient;
    }

    private RestClient getLowLevelClient() {
        if (lowLevelClient == null) {
            lowLevelClient = getHighLevelClient().getLowLevelClient();
        }
        return lowLevelClient;
    }

}
