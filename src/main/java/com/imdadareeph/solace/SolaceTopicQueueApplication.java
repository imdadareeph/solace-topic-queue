package com.imdadareeph.solace;

import com.imdadareeph.solace.config.QueueConfig;
import com.imdadareeph.solace.config.SolaceConfigProperties;
import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class SolaceTopicQueueApplication {

    private static Logger logger = LoggerFactory.getLogger(SolaceTopicQueueApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SolaceTopicQueueApplication.class, args);
    }

    @Bean
    public JCSMPSession jcsmpSession(SolaceConfigProperties properties) throws JCSMPException {
        JCSMPProperties jcsmpProperties = new JCSMPProperties();
        jcsmpProperties.setProperty(JCSMPProperties.HOST, properties.getHost());
        jcsmpProperties.setProperty(JCSMPProperties.USERNAME, properties.getUsername());
        jcsmpProperties.setProperty(JCSMPProperties.PASSWORD, properties.getPassword());
        jcsmpProperties.setProperty(JCSMPProperties.VPN_NAME, properties.getVpnName());

        jcsmpProperties.setProperty(JCSMPProperties.IGNORE_DUPLICATE_SUBSCRIPTION_ERROR, true);

        JCSMPSession session = JCSMPFactory.onlyInstance().createSession(jcsmpProperties);
        session.connect();

        return session;
    }


    @Bean
    public CommandLineRunner provisionTopicsAndQueues(JCSMPSession session, SolaceConfigProperties properties) {
        return args -> {

            List<QueueConfig> queueConfigs = properties.getQueues();
            //createTopics(session, properties.getTopics());
            createQueues(session, queueConfigs);

            // Uncomment the following lines to delete queues and topics
            // deleteTopics(session, properties.getTopics());
            // deleteQueues(session, properties.getQueues());
        };
    }

    private void createQueues(JCSMPSession session, List<QueueConfig> queues) throws JCSMPException {
        for (QueueConfig queueConfig : queues) {
            String queueName = queueConfig.getName();
            List<String> topics = queueConfig.getTopics();

            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
            session.provision(queue, null, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
            logger.info("Queue provisioned: "+ queueName);

            for (String topicName : topics) {

                Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
                session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);

               // Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
               // TopicEndpoint topicEndpoint = JCSMPFactory.onlyInstance().createDurableTopicEndpoint(topicName);
                //session.addSubscription(topic,true);
                //session.provision(topicEndpoint, null, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
                logger.info("Topic provisioned: "+ topic);
            }
        }
    }

	/*private void createTopics(JCSMPSession session, Map<String, Object> topics) throws JCSMPException {
		for (String topicName : topics.keySet()) {
			//Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
			TopicEndpoint topic = JCSMPFactory.onlyInstance().createDurableTopicEndpoint(topicName);
			session.provision(topic, null,JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
			logger.debug("Topic provisioned: " , topicName);
			logger.info("Topic provisioned: " , topicName);
			System.out.println("Topic provisioned: " + topicName);
		}
	}

	private void createQueues(JCSMPSession session, Map<String, Object> queues) throws JCSMPException {
		for (String queueName : queues.keySet()) {
			Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
			session.provision(queue, null,JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
			logger.debug("Queue provisioned: " , queueName);
			logger.info("Queue provisioned: " , queueName);
			System.out.println("Queue provisioned: " + queueName);
		}
	}*/

    private void deleteTopics(JCSMPSession session, Map<String, Object> topics) throws JCSMPException {

        for (String topicName : topics.keySet()) {
            TopicEndpoint topic = JCSMPFactory.onlyInstance().createDurableTopicEndpoint(topicName);
            session.deprovision(topic, JCSMPSession.FLAG_IGNORE_DOES_NOT_EXIST);
            logger.info("Topic deleted: ", topicName);
            System.out.println("Topic deleted: " + topicName);
        }
    }


    private void deleteQueues(JCSMPSession session, Map<String, Object> queues) throws JCSMPException {
        for (String queueName : queues.keySet()) {
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
            try {
                session.deprovision(queue, JCSMPSession.FLAG_IGNORE_DOES_NOT_EXIST);
                logger.info("Queue deleted: ", queueName);
                System.out.println("Queue deleted: " + queueName);
            } catch (JCSMPErrorResponseException e) {
                if (e.getResponseCode() == JCSMPErrorResponseSubcodeEx.QUEUE_NOT_FOUND) {
                    logger.info("Queue does not exist: ", queueName);
                    System.out.println("Queue does not exist: " + queueName);
                } else {
                    throw e;
                }
            }
        }
    }

}
