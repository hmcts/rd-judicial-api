package uk.gov.hmcts.reform.judicialapi.processor;

import static java.util.stream.Collectors.toSet;

import com.microsoft.applicationinsights.extensibility.TelemetryProcessor;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.Telemetry;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class TelemetryProcessing implements TelemetryProcessor {

    @Override
    public boolean process(Telemetry telemetry) {

        if (telemetry == null) {
            return true;
        }

        if (telemetry instanceof RequestTelemetry) {
            RequestTelemetry requestTelemetry = (RequestTelemetry) telemetry;
            Map<String, String> properties = requestTelemetry.getProperties();
            Set<String> keys = properties.entrySet()
                .stream().filter(mp -> mp.getValue().contains("@")
                    && mp.getValue().contains("."))
                .map(Map.Entry::getKey)
                .collect(toSet());

            for (String key : keys) {
                String value = properties.get(key);
                requestTelemetry.getProperties().put(key,
                    value.replace((value.substring(2, value.indexOf("."))), "****"));
            }
        }
        return true;
    }
}
