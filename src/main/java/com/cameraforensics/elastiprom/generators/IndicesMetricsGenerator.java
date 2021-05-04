package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class IndicesMetricsGenerator extends MetricsGenerator<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(IndicesMetricsGenerator.class);

    @Override
    public PrometheusFormatWriter generateMetrics(final PrometheusFormatWriter writer, final Map<String, Object> indicesStats) {
        log.debug("Generating output based on indicies stats: {}", indicesStats);

        writeDocsStats(writer, (Map<String, Object>) indicesStats.get("docs"));
        writeStoreStats(writer, (Map<String, Object>) indicesStats.get("store"));
        writeIndexingStats(writer, (Map<String, Object>) indicesStats.get("indexing"));
        writeFeldsDataStats(writer, (Map<String, Object>) indicesStats.get("fielddata"));
        writeQueryCacheStats(writer, (Map<String, Object>) indicesStats.get("query_cache"));
        writeRecoveryStats(writer, (Map<String, Object>) indicesStats.get("recovery"));
        writeSearchStats(writer, (Map<String, Object>) indicesStats.get("search"));
        writeTransLogStats(writer, (Map<String, Object>) indicesStats.get("translog"));

        Map<String, Object> flush = (Map<String, Object>) indicesStats.get("flush");
        Map<String, Object> refresh = (Map<String, Object>) indicesStats.get("refresh");
        Map<String, Object> merge = (Map<String, Object>) indicesStats.get("merges");

        writer.addSummary("es_flush")
                .withHelp("Summary of flush operations")
                .summaryFromObject(flush.get("total"), flush.get("total_time_in_millis"));
        writer.addSummary("es_refresh")
                .withHelp("Summary of refresh operations")
                .summaryFromObject(refresh.get("total"), refresh.get("total_time_in_millis"));
        writer.addSummary("es_merge")
                .withHelp("Summary of merge operations")
                .summaryFromObject(merge.get("total"), merge.get("total_time_in_millis"));
        writer.addSummary("es_merge_docs")
                .withHelp("Summary of merge operations per doc")
                .summaryFromObject(merge.get("total_docs"), merge.get("total_time_in_millis"));
        return writer;
    }

    private void writeTransLogStats(PrometheusFormatWriter writer, Map<String, Object> translog) {
        writer.addGauge("es_translog_size_bytes")
                .withHelp("Size of translog queue")
                .longValue(translog.get("size_in_bytes"));

        writer.addGauge("es_translog_uncommited_opertations")
                .withHelp("Number of uncommited operations")
                .longValue(translog.get("uncommitted_operations"));

        writer.addGauge("es_translog_uncommited_size_bytes")
                .withHelp("Size of uncommited operations")
                .longValue(translog.get("uncommitted_size_in_bytes"));

        writer.addGauge("es_translog_earliest_last_modified")
                .withHelp("Age of earliest last modified document")
                .longValue(translog.get("earliest_last_modified_age"));
    }

    private void writeSearchStats(PrometheusFormatWriter writer, Map<String, Object> search) {
        writer.addGauge("es_search_query_current")
                .withHelp("Number of search queries executed in cluster")
                .longValue(search.get("query_current"));
        writer.addSummary("es_search_query")
                .withHelp("Total number of search queries executed in cluster")
                .summaryFromObject(search.get("query_total"), search.get("query_time_in_millis"));
    }

    private void writeRecoveryStats(PrometheusFormatWriter writer, Map<String, Object> recoveryStats) {
        writer.addGauge("es_recovery_source")
                .withHelp("Number of ongoing recoveries for which a shard serves as a source")
                .longValue(recoveryStats.get("current_as_source"));

        writer.addGauge("es_recovery_target")
                .withHelp("Number of ongoing recoveries for which a shard serves as a target")
                .longValue(recoveryStats.get("current_as_target"));

        writer.addGauge("es_recovery_throttletime_ms")
                .withHelp("Throttle time of pending recovery")
                .longValue(recoveryStats.get("throttle_time_in_millis"));
    }

    private void writeQueryCacheStats(PrometheusFormatWriter writer, Map<String, Object> queryCache) {
        writer.addGauge("es_querycache_size")
                .withHelp("Number of documents that's are in the cache")
                .longValue(queryCache.get("total_count"));

        writer.addCounter("es_querycache_count")
                .withHelp("Number of documents cached")
                .longValue(queryCache.get("cache_count"));

        writer.addCounter("es_querycache_evictions")
                .withHelp("Number of evicted document")
                .longValue(queryCache.get("evictions"));

        writer.addCounter("es_querycache_hitcount")
                .withHelp("Number of documents found in a cache")
                .longValue(queryCache.get("hit_count"));

        writer.addGauge("es_querycache_memory_bytes")
                .withHelp("Size of query cache in memory")
                .longValue(queryCache.get("memory_size_in_bytes"));
    }

    private void writeFeldsDataStats(PrometheusFormatWriter writer, Map<String, Object> fieldData) {
        writer.addGauge("es_fielddata_size_bytes")
                .withHelp("Size of total fielddata size")
                .longValue(fieldData.get("memory_size_in_bytes"));

        writer.addGauge("es_fielddata_eviction_count")
                .withHelp("Number of evictions in fielddata")
                .longValue(fieldData.get("evictions"));
    }

    private void writeIndexingStats(PrometheusFormatWriter writer, Map<String, Object> indexingStats) {
        log.debug("Dumping data from indexes: {}", indexingStats);

        writer.addGauge("es_indexing_current")
                .withHelp("Number of active index operations")
                .longValue(indexingStats.get("index_current"));
        writer.addCounter("es_indexing_failed_count")
                    .withHelp("Counter of failed indexing operations")
                    .longValue(indexingStats.get("index_failed"));
        writer.addCounter("es_indexing_delete_count")
                    .withHelp("Number of delete operations")
                    .longValue(indexingStats.get("delete_total"));
        writer.addGauge("es_indexing_delete_current")
                    .withHelp("Number of active delete operations")
                    .longValue(indexingStats.get("delete_current"));
        writer.addGauge("es_indexng_isthrottled")
                    .withHelp("Flag to check is node throttled")
                    .longValue(((Boolean) indexingStats.get("is_throttled"))? 1 : 0);
        writer.addSummary("es_indexing")
                .withHelp("Indexing latency")
                .summaryFromObject(indexingStats.get("index_total"), indexingStats.get("index_time_in_millis"));
        writer.addSummary("es_delete")
                .withHelp("Delete latency")
                .summaryFromObject(indexingStats.get("delete_total"), indexingStats.get("delete_time_in_millis"));
    }

    private void writeDocsStats(PrometheusFormatWriter writer, Map<String, Object> docsStats) {
        writer.addCounter("es_common_docs_count")
                .withHelp("Elasticsearch documents counter")
                .longValue(docsStats.get("count"));
        writer.addCounter("es_common_docs_deleted_count")
                .withHelp("Elasticsearch documents deleted")
                .longValue(docsStats.get("deleted"));
//                FIXME
//        writer.addGauge("es_common_docs_size_bytes")
//                .withHelp("Size in bytes occupied by all docs")
//                .valueFromObject(getDynamicValue(docsStats, "getTotalSizeInBytes"));
    }

    private void writeStoreStats(PrometheusFormatWriter writer, Map<String, Object> storeStats) {
        writer.addGauge("es_common_store_size")
                .withHelp("Elasticsearch storage size (in bytes)")
                .longValue(storeStats.get("size_in_bytes"));
    }
}
