package com.vibrent.milestone.service;

import com.vibrent.milestone.converter.UserMilestoneEventMapper;
import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.repository.UserMilestoneEventRepository;
import com.vibrent.milestone.service.impl.UserMilestoneEventServiceImpl;
import com.vibrent.vxp.push.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserMilestoneEventServiceImplTest {
    
    @Mock
    UserMilestoneEventRepository userMilestoneEventRepository;
    
    @Mock
    UserMilestoneEventMapper userMilestoneEventMapper;

    @Mock
    EntityManager entityManager;

    UserMilestoneEventService userMilestoneEventService;


    @BeforeEach
    void setUp() {
        userMilestoneEventService = new UserMilestoneEventServiceImpl(userMilestoneEventRepository, userMilestoneEventMapper, entityManager);
    }

    @Test
    public void testSaveUserMileStoneEventFailureCasesVxpMessageIDIsEmpty() {

        UserMilestoneEventDto userMilestoneEventDto = buildMileStoneEventDto();
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();

        //Check MessageId is empty
        messageHeaderDto.setVxpMessageID("");
        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, messageHeaderDto);
        Assertions.assertTrue(CollectionUtils.isEmpty(userMilestoneEventRepository.findAll()));
        verify(userMilestoneEventRepository, times(0)).save(any(MilestoneEvent.class));

        //Check MessageId is empty
        messageHeaderDto.setVxpMessageID(null);
        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, messageHeaderDto);
        verify(userMilestoneEventRepository, times(0)).save(any(MilestoneEvent.class));
    }

    @Test
    void testIfNullUserMileStoneEventOrNullHeaderDtoReceived() {

        userMilestoneEventService.processUserMilestoneEvent(null, buildMessageHeaders());
        verify(userMilestoneEventRepository, times(0)).save(any(MilestoneEvent.class));

        userMilestoneEventService.processUserMilestoneEvent(buildMileStoneEventDto(), null);
        verify(userMilestoneEventRepository, times(0)).save(any(MilestoneEvent.class));
    }


    private MessageHeaderDto buildMessageHeaders() {
        MessageHeaderDto messageHeaderDto  = new MessageHeaderDto();
        messageHeaderDto.setSource("source");
        messageHeaderDto.setVxpHeaderVersion("1.0.0");
        messageHeaderDto.setVxpInReplyToID(UUID.randomUUID().toString());
        messageHeaderDto.setVxpMessageID(UUID.randomUUID().toString());
        messageHeaderDto.setVxpMessageSpec(MessageSpecificationEnum.USER_MILESTONE_EVENT);
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

    private UserMilestoneEventDto buildMileStoneEventDto() {
        UserMilestoneEventDto event = new UserMilestoneEventDto();

        event.setEventDateTime(System.currentTimeMillis());
        event.setEventType("EventType");
        event.setMetadata(Map.of("key", "value"));
        event.setSource("source");
        event.setStatus("status");
        event.setVibrentID(1L);

        return event;
    }
}
