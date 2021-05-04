package com.cameraforensics.elastiprom.writer;

public class ValueUtils {

    public static Long convertToSeconds(Object millis) {
        Long millisConverted = Long.parseLong(String.valueOf(millis));
        if (millisConverted > 0) {
            return millisConverted / 1000;
        }
        return millisConverted;
    }

}
