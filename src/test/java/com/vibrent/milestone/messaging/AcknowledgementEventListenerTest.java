package com.vibrent.milestone.messaging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vibrent.milestone.domain.MilestoneEventConsumerAck;
import com.vibrent.milestone.messaging.listener.AcknowledgementEventListener;
import com.vibrent.milestone.service.MilestoneEventConsumerService;
import com.vibrent.milestone.util.JacksonUtil;
import com.vibrent.vxp.support.AcknowledgementEventDto;
import com.vibrent.vxp.support.MessageHeaderDto;
import com.vibrent.vxp.support.MessageSpecificationEnum;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AcknowledgementEventListenerTest {

    private AcknowledgementEventListener acknowledgementEventListener;
    private static final String TOPIC_NAME = "event.vxp.push.participant.ack";

    @Mock
    private MilestoneEventConsumerService milestoneEventConsumerService;


    @BeforeEach
    void setUp() {
        acknowledgementEventListener = new AcknowledgementEventListener(true, milestoneEventConsumerService);
    }

    @DisplayName("When AcknowledgementEvent message is received " +
            "then verify message is processed.")
    @Test
    void processOnlyWhenAcknowledgementEventMsgIsReceived() throws JsonProcessingException {

        Message<AcknowledgementEventDto> message = buildMessage(new AcknowledgementEventDto(), MessageSpecificationEnum.MESSAGE_ACKNOWLEDGEMENT);
        Logger logger = (Logger) LoggerFactory.getLogger(AcknowledgementEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();

        acknowledgementEventListener.listener(JacksonUtil.getMapper().writeValueAsString(message.getPayload()).getBytes(StandardCharsets.UTF_8), message.getHeaders());
        List<ILoggingEvent> logsList = listAppender.list;
        verify(milestoneEventConsumerService, times(1)).saveAcknowledgementEvent(any(AcknowledgementEventDto.class), any(MessageHeaderDto.class));

        assertEquals("INFO", logsList.get(0).getLevel().toString());
        assertTrue(logsList.get(0).toString().contains("Received acknowledgement Event"));
    }

    @DisplayName("When Kafka is not enabled " +
            "then verify message is not processed.")
    @Test
    void shouldNotProcessWhenKafkaIsNotEnabled() throws JsonProcessingException {
        acknowledgementEventListener = new AcknowledgementEventListener(false, milestoneEventConsumerService);
        Message<AcknowledgementEventDto> message = buildMessage(new AcknowledgementEventDto(), MessageSpecificationEnum.MESSAGE_ACKNOWLEDGEMENT);
        Logger logger = (Logger) LoggerFactory.getLogger(AcknowledgementEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();

        acknowledgementEventListener.listener(JacksonUtil.getMapper().writeValueAsString(message.getPayload()).getBytes(StandardCharsets.UTF_8), message.getHeaders());
        verify(milestoneEventConsumerService, times(0)).saveAcknowledgementEvent(any(AcknowledgementEventDto.class), any(MessageHeaderDto.class));

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("WARN", logsList.get(0).getLevel().toString());
    }

    @DisplayName("when Invalid Acknowledgement Event Msg Is Received " +
            "then verify payload is not processed.")
    @Test
    void processOnlyWhenInvalidAcknowledgementEventPayloadIsReceived() throws JsonProcessingException {
        Message<AcknowledgementEventDto> message = buildMessage(new AcknowledgementEventDto(), MessageSpecificationEnum.MESSAGE_ACKNOWLEDGEMENT);
        Logger logger = (Logger) LoggerFactory.getLogger(AcknowledgementEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();
        acknowledgementEventListener.listener(JacksonUtil.getMapper().writeValueAsString("test_invalid").getBytes(StandardCharsets.UTF_8), message.getHeaders());
        List<ILoggingEvent> logsList = listAppender.list;

        assertEquals("ERROR", logsList.get(0).getLevel().toString());
    }


    private Message<AcknowledgementEventDto> buildMessage(AcknowledgementEventDto payload, MessageSpecificationEnum messageSpecificationEnum) {

        payload.setConsumer("user-milestone-service");
        payload.setProcessed(true);
        MessageBuilder<AcknowledgementEventDto> messageBuilder = MessageBuilder.withPayload(payload);

        messageBuilder.setHeader(KafkaHeaders.TOPIC, TOPIC_NAME);
        messageBuilder.setHeader(VXP_MESSAGE_SPEC, messageSpecificationEnum.toValue());
        messageBuilder.setHeader(VXP_TRIGGER, "EVENT");
        messageBuilder.setHeader(VXP_PATTERN, "PUSH");
        messageBuilder.setHeader(VXP_ORIGINATOR, "PTBE");
        return messageBuilder.build();
    }

}
