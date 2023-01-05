package com.vibrent.milestone.integration.service;

import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.integration.IntegrationTestBase;
import com.vibrent.milestone.repository.UserMilestoneEventRepository;
import com.vibrent.milestone.service.UserMilestoneEventService;
import com.vibrent.vxp.push.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
public class UserMilestoneEventServiceTest extends IntegrationTestBase {

    @Autowired
    UserMilestoneEventService userMilestoneEventService;

    @Autowired
    UserMilestoneEventRepository userMilestoneEventRepository;

    @Test
    public void testSaveUserMileStoneEvent() {

        Assertions.assertTrue(CollectionUtils.isEmpty(userMilestoneEventRepository.findAll()));
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();
        UserMilestoneEventDto userMilestoneEventDto = buildMileStoneEventDto();
        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, messageHeaderDto);
        List<MilestoneEvent> milestoneEvents = userMilestoneEventRepository.findAll();
        Assertions.assertFalse(CollectionUtils.isEmpty(milestoneEvents));

        Assertions.assertNull(milestoneEvents.get(0).getEntityId());
        Assertions.assertNotNull(milestoneEvents.get(0).getMetadata());
        Assertions.assertNotNull(milestoneEvents.get(0).getHeader());
        Assertions.assertEquals(messageHeaderDto.getVxpMessageID() ,milestoneEvents.get(0).getMessageId());
        Assertions.assertEquals(userMilestoneEventDto.getSource() ,milestoneEvents.get(0).getSource());
        Assertions.assertEquals(userMilestoneEventDto.getStatus() ,milestoneEvents.get(0).getStatus());
        Assertions.assertEquals(userMilestoneEventDto.getEventType() ,milestoneEvents.get(0).getType());
        Assertions.assertEquals(userMilestoneEventDto.getVibrentID() ,milestoneEvents.get(0).getVibrentId());
        Assertions.assertEquals(messageHeaderDto.getVxpMessageTimestamp() ,milestoneEvents.get(0).getTimestamp());
    }

    @Test
    public void testSaveUserMileStoneEventFailureWhenMessgeIdAlreadyExist() {

        UserMilestoneEventDto userMilestoneEventDto = buildMileStoneEventDto();
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();

        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, messageHeaderDto);

        //Entry already exist with same messageId
        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, messageHeaderDto);
        Assertions.assertEquals(1, userMilestoneEventRepository.findAll().size());
    }

    @Test
    public void testSaveUserMileStoneEventFailureCasesVxpMessageIDIsEmpty() {

        UserMilestoneEventDto userMilestoneEventDto = buildMileStoneEventDto();
        MessageHeaderDto messageHeaderDto = buildMessageHeaders();

        //Check MessageId is empty
        messageHeaderDto.setVxpMessageID("");
        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, messageHeaderDto);
        Assertions.assertTrue(CollectionUtils.isEmpty(userMilestoneEventRepository.findAll()));

        //Check MessageId is empty
        messageHeaderDto.setVxpMessageID(null);
        userMilestoneEventService.processUserMilestoneEvent(userMilestoneEventDto, messageHeaderDto);
        Assertions.assertTrue(CollectionUtils.isEmpty(userMilestoneEventRepository.findAll()));
    }

    @Test
    void testIfNullUserMileStoneEventOrNullHeaderDtoReceived() {

        userMilestoneEventService.processUserMilestoneEvent(null, buildMessageHeaders());
        Assertions.assertTrue(CollectionUtils.isEmpty(userMilestoneEventRepository.findAll()));

        userMilestoneEventService.processUserMilestoneEvent(buildMileStoneEventDto(), null);
        Assertions.assertTrue(CollectionUtils.isEmpty(userMilestoneEventRepository.findAll()));
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
