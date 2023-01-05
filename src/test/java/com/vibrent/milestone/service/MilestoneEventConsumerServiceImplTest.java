package com.vibrent.milestone.service;

import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.domain.MilestoneEventConsumerAck;
import com.vibrent.milestone.repository.MilestoneEventConsumerAckRepository;
import com.vibrent.milestone.repository.UserMilestoneEventRepository;
import com.vibrent.milestone.service.impl.MilestoneEventConsumerServiceImpl;
import com.vibrent.vxp.support.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MilestoneEventConsumerServiceImplTest {

    public static final String MESSAGE_ID = "some-message-id";

    @Mock
    private UserMilestoneEventRepository userMilestoneEventRepository;

    @Mock
    private MilestoneEventConsumerAckRepository milestoneEventConsumerAckRepository;

    private MilestoneEventConsumerService milestoneEventConsumerService;

    @BeforeEach
    void setUp() {
        milestoneEventConsumerService = new MilestoneEventConsumerServiceImpl(userMilestoneEventRepository, milestoneEventConsumerAckRepository);
    }

    @Test
    void testSaveMilestoneEventConsumerAckEvent() {
        AcknowledgementEventDto acknowledgementEventDto = buildAcknowledgementEventDto();
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();
        when(userMilestoneEventRepository.findByMessageId(MESSAGE_ID)).thenReturn(new MilestoneEvent());

        milestoneEventConsumerService.saveAcknowledgementEvent(acknowledgementEventDto,messageHeaderDto);

        verify(milestoneEventConsumerAckRepository, times(1)).save(any(MilestoneEventConsumerAck.class));
    }

    @Test
    void testSaveMilestoneEventConsumerAckEventFailureWhenEventDtoOrHeaderReceivedNull() {
        AcknowledgementEventDto acknowledgementEventDto = buildAcknowledgementEventDto();
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();

        milestoneEventConsumerService.saveAcknowledgementEvent(null,messageHeaderDto);
        verify(milestoneEventConsumerAckRepository, times(0)).save(any(MilestoneEventConsumerAck.class));


        milestoneEventConsumerService.saveAcknowledgementEvent(acknowledgementEventDto,null);
        verify(milestoneEventConsumerAckRepository, times(0)).save(any(MilestoneEventConsumerAck.class));

        milestoneEventConsumerService.saveAcknowledgementEvent(null,null);
        verify(milestoneEventConsumerAckRepository, times(0)).save(any(MilestoneEventConsumerAck.class));

    }

    @Test
    void testSaveMilestoneEventConsumerAckEventFailureCasesWhenVxpReplyToIDIsEmpty() {
        AcknowledgementEventDto acknowledgementEventDto = buildAcknowledgementEventDto();
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();
        messageHeaderDto.setVxpInReplyToID("");
        milestoneEventConsumerService.saveAcknowledgementEvent(acknowledgementEventDto, messageHeaderDto);
        verify(milestoneEventConsumerAckRepository, times(0)).save(any(MilestoneEventConsumerAck.class));

        messageHeaderDto.setVxpInReplyToID(null);
        milestoneEventConsumerService.saveAcknowledgementEvent(acknowledgementEventDto, messageHeaderDto);
        verify(milestoneEventConsumerAckRepository, times(0)).save(any(MilestoneEventConsumerAck.class));

        messageHeaderDto.setVxpInReplyToID(MESSAGE_ID);
        milestoneEventConsumerService.saveAcknowledgementEvent(acknowledgementEventDto, messageHeaderDto);
        verify(milestoneEventConsumerAckRepository, times(0)).save(any(MilestoneEventConsumerAck.class));

    }

    @Test
    void testSaveExistingMilestoneEventConsumerAckEvent() {
        AcknowledgementEventDto acknowledgementEventDto = buildAcknowledgementEventDto();
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();
        when(userMilestoneEventRepository.findByMessageId(MESSAGE_ID)).thenReturn(new MilestoneEvent());
        when(milestoneEventConsumerAckRepository.findByMilestoneEvent_IdAndConsumer(any(),any())).thenReturn(new MilestoneEventConsumerAck());
        milestoneEventConsumerService.saveAcknowledgementEvent(acknowledgementEventDto,messageHeaderDto);

        verify(milestoneEventConsumerAckRepository, times(1)).save(any(MilestoneEventConsumerAck.class));
    }



    private AcknowledgementEventDto buildAcknowledgementEventDto(){
        AcknowledgementEventDto acknowledgementEventDto = new AcknowledgementEventDto();
        acknowledgementEventDto.setProcessed(true);
        acknowledgementEventDto.setConsumer("consumer");
        return acknowledgementEventDto;
    }

    private MessageHeaderDto buildMessageHeaders() {
        MessageHeaderDto messageHeaderDto  = new MessageHeaderDto();
        messageHeaderDto.setSource("source");
        messageHeaderDto.setVxpHeaderVersion("1.0.0");
        messageHeaderDto.setVxpInReplyToID(MESSAGE_ID);
        messageHeaderDto.setVxpMessageID(UUID.randomUUID().toString());
        messageHeaderDto.setVxpMessageSpec(MessageSpecificationEnum.MESSAGE_ACKNOWLEDGEMENT);
        messageHeaderDto.setVxpMessageSpecVersion("1.0.0");
        messageHeaderDto.setVxpMessageTimestamp(System.currentTimeMillis());
        messageHeaderDto.setVxpOriginator(RequestOriginatorEnum.VXPMS);
        messageHeaderDto.setVxpPattern(IntegrationPatternEnum.PUSH);
        messageHeaderDto.setVxpProgramID(1L);
        messageHeaderDto.setVxpTenantID(43L);
        messageHeaderDto.setVxpTrigger(ContextTypeEnum.EVENT);
        messageHeaderDto.setVxpUserID(1L);
        messageHeaderDto.setVxpWorkflowInstanceID(UUID.randomUUID().toString());
        return messageHeaderDto;
    }
}
