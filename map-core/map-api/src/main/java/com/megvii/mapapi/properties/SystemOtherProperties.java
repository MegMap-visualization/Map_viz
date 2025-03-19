package com.megvii.mapapi.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "system-other")
@Data
@Component
@NoArgsConstructor
public class SystemOtherProperties {
    private boolean allow;
    private String auth;
    private String securityJsCode;
    private SystemOtherProperties.EffectiveDigit effectiveDigit;

    @Data
    @NoArgsConstructor
    public static class EffectiveDigit {
        private Integer lon;
        private Integer lat;

    }
}


