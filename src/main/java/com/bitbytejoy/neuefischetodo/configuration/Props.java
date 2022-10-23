package com.bitbytejoy.neuefischetodo.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "props")
public class Props {
    private String jwtSecret;
}
