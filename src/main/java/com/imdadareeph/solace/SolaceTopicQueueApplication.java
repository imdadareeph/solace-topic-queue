package com.imdadareeph.solace;

import com.imdadareeph.solace.config.SolaceConfigProperties;
import com.imdadareeph.solace.handler.SolaceFactory;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@EnableConfigServer
@ComponentScan("com.imdadareeph.*")
@RequiredArgsConstructor
@RefreshScope
public class SolaceTopicQueueApplication {

    private static Logger logger = LoggerFactory.getLogger(SolaceTopicQueueApplication.class);

    private final SolaceConfigProperties solaceConfigProperties;
    private final ConfigurableApplicationContext applicationContext;
    private final ContextRefresher contextRefresher;

    public static void main(String[] args) {
        SpringApplication.run(SolaceTopicQueueApplication.class, args);
    }

    @Bean
    public JCSMPSession jcsmpSession(SolaceConfigProperties properties) throws JCSMPException {
        JCSMPSession session = null;

        try {

            JCSMPProperties jcsmpProperties = new JCSMPProperties();
            jcsmpProperties.setProperty(JCSMPProperties.HOST, properties.getHost());
            jcsmpProperties.setProperty(JCSMPProperties.USERNAME, properties.getUsername());
            jcsmpProperties.setProperty(JCSMPProperties.PASSWORD, properties.getPassword());
            jcsmpProperties.setProperty(JCSMPProperties.VPN_NAME, properties.getVpnName());
            jcsmpProperties.setProperty(JCSMPProperties.IGNORE_DUPLICATE_SUBSCRIPTION_ERROR, true);
            session = JCSMPFactory.onlyInstance().createSession(jcsmpProperties);
            session.connect();
        } catch (Exception e) {
            logger.info("config properties host = " + properties.getHost());
        }
        return session;
    }

    @Bean
    public SolaceFactory solaceFactoryInstance() {
        SolaceFactory solaceFactory = SolaceFactory.getInstance();
        return solaceFactory;
    }


    @Bean
    public CommandLineRunner provisionTopicsAndQueues(JCSMPSession session, SolaceConfigProperties properties) {
        return args -> {

            final SolaceFactory solaceFactoryInstance = SolaceFactory.getInstance();
            solaceFactoryInstance.setJcsmpSession(session);
            solaceFactoryInstance.setSolaceConfigProperties(properties);
            solaceFactoryInstance.createQueuesAndTopics();
        };
    }





   /* @Scheduled(cron = "${config.refresh.cron}")
    public void refreshConfig() {
        boolean refreshed = contextRefresher.refresh();

        if (refreshed) {
            System.out.println("Configuration refreshed successfully");
        } else {
            System.out.println("No configuration changes detected");
        }
    }*/


}
