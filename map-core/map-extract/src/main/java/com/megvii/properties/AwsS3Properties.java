package com.megvii.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "aws-s3")
@Data
public class AwsS3Properties {
    private String accessKey;
    private String secretKey;
    private String endPoint;
}
