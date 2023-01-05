package com.vibrent.milestone.messaging.listener;


import com.vibrent.milestone.messaging.KafkaMessageBuilder;
import com.vibrent.milestone.service.MilestoneEventConsumerService;
import com.vibrent.milestone.util.JacksonUtil;
import com.vibrent.vxp.support.AcknowledgementEventDto;
import com.vibrent.vxp.support.MessageHeaderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Kafka Listener for Acknowledgement Event
 */
@Service
@Slf4j
public class AcknowledgementEventListener {

    private final boolean kafkaEnabled;
    private final MilestoneEventConsumerService milestoneEventConsumerService;

    public AcknowledgementEventListener(@Value("${kafka.enabled}") boolean kafkaEnabled, MilestoneEventConsumerService milestoneEventConsumerService) {
        this.kafkaEnabled = kafkaEnabled;
        this.milestoneEventConsumerService = milestoneEventConsumerService;
    }

    @KafkaListener(topics = "${kafka.topics.acknowledgementEvent}", groupId = "acknowledgementEventListener",
            containerFactory = "kafkaListenerContainerFactoryAcknowledgementEvent")
    public void listener(@Payload byte[] payloadByteArray,
                         @Headers MessageHeaders requestHeaders) {

        if (!kafkaEnabled) {
            log.warn("kafka is not enabled");
            return;
        }

        AcknowledgementEventDto acknowledgementEventDto = null;
        try {
            acknowledgementEventDto = JacksonUtil.getMapper().readValue(payloadByteArray, AcknowledgementEventDto.class);
        } catch (IOException e) {
            log.error("user-milestone: Cannot convert payload to AcknowledgementEventDto and received payload is {} ", new String(payloadByteArray, StandardCharsets.UTF_8), e);
            return;
        }

        MessageHeaderDto headers = KafkaMessageBuilder.toAcknowledgementMessageHeaderDto(requestHeaders);
        log.info("User-Milestone: Received acknowledgement Event. AcknowledgementEventDto: {} MessageHeaders: {}", acknowledgementEventDto, headers);
        milestoneEventConsumerService.saveAcknowledgementEvent(acknowledgementEventDto, headers);
    }
}
