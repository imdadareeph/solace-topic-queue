package com.imdadareeph.solace.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "solace")
public class SolaceConfigProperties {
    private String host;
    private String username;
    private String password;
    private String vpnName;
    private List<QueueConfig> queues;
    /*private Map<String, Object> topics;
    private Map<String, Object> queues;*/

}
