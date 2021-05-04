package com.cameraforensics.elastiprom.generators;

import com.cameraforensics.elastiprom.writer.PrometheusFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class MetricsGenerator<T> {
    private static final Logger log = LoggerFactory.getLogger(MetricsGenerator.class);

    static double getDynamicValue(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            Object value = method.invoke(obj);
            if (value.getClass().isAssignableFrom(Long.class)) {
                Long l = (Long) value;
                return l.doubleValue();
            }
            return (double) value;
        } catch (NoSuchMethodException e) {
            log.error("There are no getTotalSizeInBytes method defined");
            return -1.0;
        } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
            log.error("Exception during method invocation: {}", e.getMessage());
            return -1.0;
        }
    }
    public abstract PrometheusFormatWriter generateMetrics(PrometheusFormatWriter writer, T inputData);
}
