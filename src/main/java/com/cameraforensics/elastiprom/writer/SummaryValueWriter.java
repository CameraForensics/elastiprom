package com.cameraforensics.elastiprom.writer;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class SummaryValueWriter {
    private final ValueWriter valueWriter;
    private final String name;

    SummaryValueWriter(StringWriter writer, String name, Map<String, String> globalLabels) {
        this.valueWriter = new ValueWriter(writer, globalLabels);
        this.name = name;
    }

    public SummaryValueWriter withSharedLabel(String labelName, String labelValue) {
        valueWriter.addSharedLabel(labelName, labelValue);
        return this;
    }

    public void summaryFromObject(Object data, Object millisData, String...labels) {
        Long collectionCount = Long.parseLong(String.valueOf(data));
        Long millis = Long.parseLong(String.valueOf(millisData));
        if (labels.length % 2 != 0) {
            throw new IllegalArgumentException("Wrong number of labels, should be in pairs..");
        }
        Map<String, String> paramsMap = new LinkedHashMap<>();
        for (int i = 0; i < labels.length; i++) {
            paramsMap.put(labels[i], labels[++i]);
        }
        valueWriter.writeValue(name + "_count", collectionCount, paramsMap);
        valueWriter.writeValue(name + "_sum", millis, paramsMap);
    }
}
