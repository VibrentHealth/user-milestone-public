package com.vibrent.milestone.messaging.listener;


import com.vibrent.milestone.messaging.KafkaMessageBuilder;
import com.vibrent.milestone.service.UserMilestoneEventService;
import com.vibrent.milestone.util.JacksonUtil;
import com.vibrent.vxp.push.MessageHeaderDto;
import com.vibrent.vxp.push.UserMilestoneEventDto;
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
 * Kafka Listener for User Milestone Event
 */
@Service
@Slf4j
public class UserMilestoneEventListener {

    private final boolean kafkaEnabled;
    private final UserMilestoneEventService userMilestoneEventService;

    public UserMilestoneEventListener(@Value("${kafka.enabled}") boolean kafkaEnabled,
                                      UserMilestoneEventService userMilestoneEventService) {
        this.kafkaEnabled = kafkaEnabled;
        this.userMilestoneEventService = userMilestoneEventService;
    }

    @KafkaListener(topics = "${kafka.topics.vxpPushParticipant}", groupId = "userMilestoneEventListener",
            containerFactory = "kafkaListenerContainerFactoryUserMilestoneEvent")
    public void listener(@Payload byte[] payloadByteArray,
                         @Headers MessageHeaders requestHeaders) {

        if (!kafkaEnabled) {
            log.warn("kafka is not enabled");
            return;
        }

        UserMilestoneEventDto userMilestoneEventDto = null;
        try {
            userMilestoneEventDto = JacksonUtil.getMapper().readValue(payloadByteArray, UserMilestoneEventDto.class);
        } catch (IOException e) {
            log.error("user-milestone: Cannot convert Payload to UserMilestoneEventDto and received payload is {} ", new String(payloadByteArray, StandardCharsets.UTF_8), e);
            return;
        }

        MessageHeaderDto headers = KafkaMessageBuilder.toMessageHeaderDto(requestHeaders);
        //Log received user milestone event with VXP_MESSAGE_SPEC=USER_MILESTONE_EVENT
        log.info("user-milestone: Received User Milestone Event. UserMilestoneEventDto: {} MessageHeaders: {}", userMilestoneEventDto, headers);

        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, headers);
    }

}
