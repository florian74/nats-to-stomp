package com.util.messaging.yaml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.ArrayList;
import java.util.List;


@Configuration
@ConfigurationProperties
@EnableConfigurationProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PropertySources({
        @PropertySource(value = "classpath:configuration.yml", factory = YamlPropertyConfig.class),
        @PropertySource(value = "${server.config.path}", factory=YamlPropertyConfig.class,  ignoreResourceNotFound = true) // the last read is used
})
public class Config {
    List<Redirection> configs = new ArrayList<>();
}
