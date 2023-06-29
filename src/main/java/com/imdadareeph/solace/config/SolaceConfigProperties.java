package com.imdadareeph.solace.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@ToString
@RefreshScope
public class SolaceConfigProperties {

    //@Value("${host}")
    private String host;
    //@Value("${username}")
    private String username;
    //@Value("${password}")
    private String password;
   // @Value("${msgVpn}")
    private String vpnName;
   // @Value("${queues}")
    private List<QueueConfig> queues;
    /*private Map<String, Object> topics;
    private Map<String, Object> queues;*/

}
