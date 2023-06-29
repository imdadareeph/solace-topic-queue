package com.imdadareeph.solace.handler;

import com.imdadareeph.solace.config.QueueConfig;
import com.imdadareeph.solace.config.SolaceConfigProperties;
import com.imdadareeph.solace.exception.SolaceTopicQueueException;
import com.solacesystems.jcsmp.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Slf4j
@Component
@EnableConfigServer
@RefreshScope
public class SolaceFactory {
    private static Logger logger = LoggerFactory.getLogger(SolaceFactory.class);

    private static SolaceFactory instance = new SolaceFactory();


    @Autowired
    private SolaceConfigProperties solaceConfigProperties;

    private List<QueueConfig> queues;
    private JCSMPSession jcsmpSession;
    public boolean created;

    private SolaceFactory() {
        //this.queues = configProperties.getQueues();
    }

    public SolaceFactory(JCSMPSession session, SolaceConfigProperties configProperties) {
        this.solaceConfigProperties = configProperties;
        this.jcsmpSession = session;
    }

    public static SolaceFactory getInstance() {
        return instance;
    }

    @SneakyThrows
    public void createQueuesAndTopics() {

        logger.info("config properties host = " + solaceConfigProperties.getHost());
        Optional.ofNullable(solaceConfigProperties)
                .filter(ObjectUtils::isNotEmpty)
                .stream()
                .forEach(props -> logger.info(" Application configurations \n" + props.toString()));
        List<QueueConfig> queueConfigs = solaceConfigProperties.getQueues();

        for (QueueConfig queueConfig : queueConfigs) {
            String queueName = queueConfig.getName();
            List<String> topics = queueConfig.getTopics();


            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
            jcsmpSession.provision(queue, null, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
            logger.info("Queue provisioned: " + queueName);
            for (String topicName : topics) {

                Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
                try {
                    jcsmpSession.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);
                    logger.info("Topic provisioned: " + topic);
                } catch (JCSMPException jce) {
                    logger.info("Topic provisioned failed for " + topic, jce.getMessage());
                    //throw new SolaceTopicQueueException(jce);
                }

            }
        }
        created = true;
    }


    /*@SneakyThrows
    public void createQueuesAndTopics(JCSMPSession session, SolaceConfigProperties solaceConfigProperties) {

        logger.info("config properties host = " + solaceConfigProperties.getHost());
        Optional.ofNullable(solaceConfigProperties)
                .filter(ObjectUtils::isNotEmpty)
                .stream()
                .forEach(props -> logger.info(" Application configurations \n" + props.toString()));
        List<QueueConfig> queueConfigs = solaceConfigProperties.getQueues();

        for (QueueConfig queueConfig : queueConfigs) {
            String queueName = queueConfig.getName();
            List<String> topics = queueConfig.getTopics();


            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
            session.provision(queue, null, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
            logger.info("Queue provisioned: " + queueName);
            for (String topicName : topics) {

                Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
                session.addSubscription(queue, topic, JCSMPSession.WAIT_FOR_CONFIRM);
                logger.info("Topic provisioned: " + topic);
            }
        }
        created= true;
    }*/


    @Deprecated
    public void createTopicEndpoints(JCSMPSession session, Map<String, Object> topics) throws JCSMPException {
        for (String topicName : topics.keySet()) {
            TopicEndpoint topic = JCSMPFactory.onlyInstance().createDurableTopicEndpoint(topicName);
            session.provision(topic, null, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
            logger.debug("Topic provisioned: ", topicName);
            logger.info("Topic provisioned: ", topicName);
            System.out.println("Topic provisioned: " + topicName);
        }
    }

    @Deprecated
    public void createQueuesOnly(JCSMPSession session, Map<String, Object> queues) throws JCSMPException {
        for (String queueName : queues.keySet()) {
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
            session.provision(queue, null, JCSMPSession.FLAG_IGNORE_ALREADY_EXISTS);
            logger.debug("Queue provisioned: ", queueName);
            logger.info("Queue provisioned: ", queueName);
            System.out.println("Queue provisioned: " + queueName);
        }
    }

    @Deprecated
    public void deleteTopicEndpoints(JCSMPSession session, Map<String, Object> topics) throws JCSMPException {

        for (String topicName : topics.keySet()) {
            TopicEndpoint topic = JCSMPFactory.onlyInstance().createDurableTopicEndpoint(topicName);
            session.deprovision(topic, JCSMPSession.FLAG_IGNORE_DOES_NOT_EXIST);
            logger.info("TopicEndpoint deleted: ", topicName);
        }
    }

    @Deprecated
    public void deleteQueuesOnly(JCSMPSession session, Map<String, Object> queues) throws JCSMPException {
        for (String queueName : queues.keySet()) {
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
            try {
                session.deprovision(queue, JCSMPSession.FLAG_IGNORE_DOES_NOT_EXIST);
                logger.info("Queue deleted: ", queueName);
            } catch (JCSMPErrorResponseException e) {
                if (e.getResponseCode() == JCSMPErrorResponseSubcodeEx.QUEUE_NOT_FOUND) {
                    logger.info("Queue does not exist: ", queueName);
                } else {
                    logger.info("config properties host = " + queueName);
                    throw new SolaceTopicQueueException("Error in deleteQueuesOnly ", e);
                }
            }
        }
    }

}
