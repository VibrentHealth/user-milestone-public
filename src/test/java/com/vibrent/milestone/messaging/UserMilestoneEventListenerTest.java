package com.vibrent.milestone.messaging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vibrent.milestone.messaging.listener.UserMilestoneEventListener;
import com.vibrent.milestone.service.UserMilestoneEventService;
import com.vibrent.milestone.util.JacksonUtil;
import com.vibrent.vxp.push.MessageSpecificationEnum;
import com.vibrent.vxp.push.UserMilestoneEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.vibrent.milestone.constants.KafkaConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserMilestoneEventListenerTest {

    private UserMilestoneEventListener userMilestoneEventListener;
    private static final String TOPIC_NAME = "event.vxp.push.participant";

    @Mock
    private UserMilestoneEventService userMilestoneEventService;

    @BeforeEach
    void setUp() {
        userMilestoneEventListener = new UserMilestoneEventListener(true, userMilestoneEventService);
    }

    @DisplayName("when UserMilestoneEvent message is received " +
            "then verify message is processed.")
    @Test
    void processOnlyWhenUserMilestoneEventMsgIsReceived() throws JsonProcessingException {

        Message<UserMilestoneEventDto> message = buildMessage(new UserMilestoneEventDto(), MessageSpecificationEnum.USER_MILESTONE_EVENT);
        Logger logger = (Logger) LoggerFactory.getLogger(UserMilestoneEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();

        userMilestoneEventListener.listener(JacksonUtil.getMapper().writeValueAsString(message.getPayload()).getBytes(StandardCharsets.UTF_8), message.getHeaders());
        List<ILoggingEvent> logsList = listAppender.list;

        assertEquals("INFO", logsList.get(0).getLevel().toString());
        assertTrue(logsList.get(0).toString().contains("Received User Milestone Event"));
    }

    @DisplayName("When Kafka is not enabled " +
            "then verify message is not processed.")
    @Test
    void shouldNotProcessWhenKafkaIsNotEnabled() throws JsonProcessingException {
        userMilestoneEventListener = new UserMilestoneEventListener(false, userMilestoneEventService);
        Message<UserMilestoneEventDto> message = buildMessage(new UserMilestoneEventDto(), MessageSpecificationEnum.USER_MILESTONE_EVENT);
        Logger logger = (Logger) LoggerFactory.getLogger(UserMilestoneEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();

        userMilestoneEventListener.listener(JacksonUtil.getMapper().writeValueAsString(message.getPayload()).getBytes(StandardCharsets.UTF_8), message.getHeaders());
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().toString());
    }

    @DisplayName("when Invalid UserMilestoneEvent Msg Is Received " +
            "then verify payload is not processed.")
    @Test
    void processOnlyWhenInvalidUserMilestoneEventPayloadIsReceived() throws JsonProcessingException {
        Message<UserMilestoneEventDto> message = buildMessage(new UserMilestoneEventDto(), MessageSpecificationEnum.USER_MILESTONE_EVENT);
        Logger logger = (Logger) LoggerFactory.getLogger(UserMilestoneEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();
        userMilestoneEventListener.listener(JacksonUtil.getMapper().writeValueAsString("test_invalid").getBytes(StandardCharsets.UTF_8), message.getHeaders());
        List<ILoggingEvent> logsList = listAppender.list;

        assertEquals("ERROR", logsList.get(0).getLevel().toString());
    }


    private Message<UserMilestoneEventDto> buildMessage(UserMilestoneEventDto payload, MessageSpecificationEnum messageSpecificationEnum) {

        MessageBuilder<UserMilestoneEventDto> messageBuilder = MessageBuilder.withPayload(payload);

        messageBuilder.setHeader(KafkaHeaders.TOPIC, TOPIC_NAME);
        messageBuilder.setHeader(VXP_MESSAGE_SPEC, messageSpecificationEnum.toValue());
        messageBuilder.setHeader(VXP_TRIGGER, "EVENT");
        messageBuilder.setHeader(VXP_PATTERN, "PUSH");
        messageBuilder.setHeader(VXP_ORIGINATOR, "PTBE");
        return messageBuilder.build();
    }

}
