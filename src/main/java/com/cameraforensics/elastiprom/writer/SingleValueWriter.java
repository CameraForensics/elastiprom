package com.cameraforensics.elastiprom.writer;

import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SingleValueWriter {
    private final ValueWriter valueWriter;
    private final String name;

    SingleValueWriter(StringWriter writer, String name, Map<String, String> globalLabels) {
        this.valueWriter = new ValueWriter(writer, globalLabels);
        this.name = name;
    }

    public SingleValueWriter withSharedLabel(String labelName, String labelValue) {
        valueWriter.addSharedLabel(labelName, labelValue);
        return this;
    }

    public void value(double value) {
        this.value(value, Collections.emptyMap());
    }

    public SingleValueWriter value(double value, Map<String, String> labels) {
        valueWriter.writeValue(name, value, labels);
        return this;
    }

    public SingleValueWriter longValue(Object data, Map<String, String> labels) {
        if (data == null) {
            data = "0";
        }
        long value = Long.parseLong((String.valueOf(data)));
        valueWriter.writeValue(name, value, labels);
        return this;
    }

    public SingleValueWriter longValue(Object data, String...labels) {
        if (data == null) {
            data = "0";
        }
        long value = Long.parseLong((String.valueOf(data)));
        return value(value, labels);
    }

    public SingleValueWriter doubleValue(Object data, String...labels) {
        if (data == null) {
            data = "0";
        }
        double value = Double.parseDouble((String.valueOf(data)));
        return value(value, labels);
    }

    public SingleValueWriter value(double value, String...labels) {
        if (labels.length % 2 != 0) {
            throw new IllegalArgumentException("Wrong number of labels, should be in pairs..");
        }

        Map<String, String> paramsMap = new LinkedHashMap<>();
        for (int i = 0; i < labels.length; i++) {
            paramsMap.put(labels[i], labels[++i]);
        }

        return this.value(value, paramsMap);
    }

    public void summary(long collectionCount, long millis, String...labels) {
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
