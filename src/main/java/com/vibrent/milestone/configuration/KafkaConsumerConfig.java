package com.vibrent.milestone.configuration;


import com.vibrent.milestone.constants.KafkaConstants;
import com.vibrent.vxp.push.MessageSpecificationEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {
    private static final String USER_MILESTONE_EVENT_GROUP_ID = "USER_MILESTONE_EVENT_GROUP_ID";
    private static final String MILESTONE_ACKNOWLEDGEMENT_GROUP_ID = "MILESTONE_ACKNOWLEDGEMENT_EVENT_GROUP_ID";
    private final String bootstrapServers;
    private final int defaultConcurrency;

    public KafkaConsumerConfig(@Value("${kafka.server}") String bootstrapServers,
                               @Value("${kafka.defaultConcurrency}") int defaultConcurrency) {
        this.bootstrapServers = bootstrapServers;
        this.defaultConcurrency = defaultConcurrency;
    }

    private Map<String, Object> getConfigProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, byte[]> userMilestoneEventConsumerFactory() {
        Map<String, Object> consumerConfigProps = getConfigProps();
        consumerConfigProps.put(ConsumerConfig.GROUP_ID_CONFIG, USER_MILESTONE_EVENT_GROUP_ID);
        consumerConfigProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerConfigProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(consumerConfigProps, stringDeserializer, new ByteArrayDeserializer());
        }
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, byte[]>> kafkaListenerContainerFactoryUserMilestoneEvent() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userMilestoneEventConsumerFactory());
        factory.setConcurrency(defaultConcurrency);
        factory.getContainerProperties().setPollTimeout(KafkaConstants.POLL_TIMEOUT);
        factory.setRecordFilterStrategy(consumerRecord -> {
            String messageSpec = extractHeader(consumerRecord.headers(), KafkaConstants.VXP_MESSAGE_SPEC);

            //discard the Record if MessageSpecification is not equal to USER_MILESTONE_EVENT

            return null == messageSpec
                    || !MessageSpecificationEnum.USER_MILESTONE_EVENT.toString().equals(messageSpec);
        });

        return factory;
    }

    @Bean
    public ConsumerFactory<String, byte[]> acknowledgementConsumerFactory() {
        Map<String, Object> consumerConfigProps = getConfigProps();
        consumerConfigProps.put(ConsumerConfig.GROUP_ID_CONFIG, MILESTONE_ACKNOWLEDGEMENT_GROUP_ID);
        consumerConfigProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerConfigProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(consumerConfigProps, stringDeserializer, new ByteArrayDeserializer());
        }
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, byte[]>> kafkaListenerContainerFactoryAcknowledgementEvent() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(acknowledgementConsumerFactory());
        factory.setConcurrency(defaultConcurrency);
        factory.getContainerProperties().setPollTimeout(KafkaConstants.POLL_TIMEOUT);
        factory.setRecordFilterStrategy(consumerRecord -> {
            String messageSpec = extractHeader(consumerRecord.headers(), KafkaConstants.VXP_MESSAGE_SPEC);

            //discard the Record if MessageSpecification is not equal to MESSAGE_ACKNOWLEDGEMENT

            return null == messageSpec
                    || !com.vibrent.vxp.support.MessageSpecificationEnum.MESSAGE_ACKNOWLEDGEMENT.toString().equals(messageSpec);
        });

        return factory;
    }


    public static String extractHeader(Headers headers, String headerKey) {
        String headerValue = null;

        if (headers != null) {
            for (Header header : headers) {
                if (headerKey.equalsIgnoreCase(header.key())
                        && header.value() != null
                        && header.value().length > 0) {
                    headerValue = new String(header.value(), StandardCharsets.UTF_8).trim();
                    //Remove leading and tailing quotes
                    headerValue = headerValue.replaceAll("(^\"+)|(\"+$)", "");
                    break;
                }
            }
        }
        return headerValue;
    }
}
