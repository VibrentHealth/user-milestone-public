package com.vibrent.milestone.integration.rest.delegate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.domain.MilestoneEventConsumerAck;
import com.vibrent.milestone.exceptions.MilestoneExceptionHandler;
import com.vibrent.milestone.integration.IntegrationTestBase;
import com.vibrent.milestone.repository.MilestoneEventConsumerAckRepository;
import com.vibrent.milestone.repository.UserMilestoneEventRepository;
import com.vibrent.milestone.util.JacksonUtil;
import com.vibrent.usermilestone.dto.MilestoneEventDTO;
import com.vibrent.usermilestone.dto.MilestoneEventResponseDTO;
import com.vibrent.usermilestone.resource.EventApiController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class EventApiTest extends IntegrationTestBase {

    private static final String GET_MILESTONE_EVENT_BY_MESSAGE_ID = "/api/milestone/v1/event";
    private static final String GET_ALL_UNPROCESSED_MILESTONES_MILESTONE_EVENT = "/api/milestone/v1/event/unprocessed";
    @Autowired
    EventApiController eventApiController;

    @Autowired
    UserMilestoneEventRepository userMilestoneEventRepository;

    @Autowired
    MilestoneEventConsumerAckRepository milestoneEventConsumerAckRepository;

    @Autowired
    EntityManager entityManager;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventApiController)
                .setControllerAdvice(MilestoneExceptionHandler.class)
                .build();
    }


    @WithMockUser(roles = "USER")
    @Test
    void testGetuserMilestoneEndpointWithInvalidRole() throws Exception {
        var mvcResult = mockMvc.perform(get(GET_MILESTONE_EVENT_BY_MESSAGE_ID).queryParam("messageId", "ttttt"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testGetuserMilestoneEndpointWithInvalidParams() throws Exception {
        var mvcResult = mockMvc.perform(get(GET_MILESTONE_EVENT_BY_MESSAGE_ID+"?messageId="))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testWhenMessageIdsNotFoundThenReturnEmptyResponse() throws Exception {
       var mvcResult = mockMvc.perform(get(GET_MILESTONE_EVENT_BY_MESSAGE_ID).queryParam("messageId", "ttttt", "ggggg"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        List<MilestoneEventDTO> events = JacksonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<MilestoneEventDTO>>() { });

       assertEquals(0, events.size());
    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testGetUsermilestoneEventsMessageIdSuccessResponse() throws Exception {
        MilestoneEvent event1 = userMilestoneEventRepository.save(buildMileStoneEvent("message-id-1"));
        MilestoneEvent event2 = userMilestoneEventRepository.save(buildMileStoneEvent("message-id-2"));
        var mvcResult = mockMvc.perform(get(GET_MILESTONE_EVENT_BY_MESSAGE_ID).queryParam("messageId", event1.getMessageId(), event2.getMessageId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        List<MilestoneEventDTO> events = JacksonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<MilestoneEventDTO>>() { });

        assertEquals(2, events.size());
    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testGetUsermilestoneEventsMessageIdErrorResponse() throws Exception {
        MilestoneEvent event1 = userMilestoneEventRepository.save(buildMileStoneEvent("message-id-1"));

        //Update invalid metadata
        event1.setMetadata("[]");
        userMilestoneEventRepository.save(event1);

        mockMvc.perform(get(GET_MILESTONE_EVENT_BY_MESSAGE_ID).queryParam("messageId", event1.getMessageId()))
                .andExpect(status().isInternalServerError());

    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testGetUsermilestoneEventsMessageIdErrorResponseWithInvalidHeader() throws Exception {
        MilestoneEvent event1 = userMilestoneEventRepository.save(buildMileStoneEvent("message-id-1"));

        //Update invalid header
        event1.setHeader("[]");
        userMilestoneEventRepository.save(event1);

        mockMvc.perform(get(GET_MILESTONE_EVENT_BY_MESSAGE_ID).queryParam("messageId", event1.getMessageId()))
                .andExpect(status().isInternalServerError());

    }

    private MilestoneEvent buildMileStoneEvent(String messageId) {
       MilestoneEvent event = new MilestoneEvent();

        event.setMessageId(messageId);
        event.setVibrentId(1L);
        event.setEntityId(null);
        event.setTimestamp(System.currentTimeMillis());
        event.setStatus("status");
        event.setSource("source");
        event.setMetadata("{}");
        event.setHeader("{}");

        return event;
    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testGetUnprocessedUserMilestoneEndpointWithInvalidParams() throws Exception {
        var mvcResult = mockMvc.perform(get(GET_ALL_UNPROCESSED_MILESTONES_MILESTONE_EVENT))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testWhenConsumerNotFoundThenReturnEmptyResponse() throws Exception {
        var mvcResult = mockMvc.perform(get(GET_ALL_UNPROCESSED_MILESTONES_MILESTONE_EVENT).queryParam("consumer", "test"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        MilestoneEventResponseDTO events = JacksonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<MilestoneEventResponseDTO>() { });

        assertEquals(0, events.getContent().size());
    }

    @WithMockUser(roles = "EVENT_READER")
    @Test
    void testWhenConsumerNotFoundThenReturnSucessResponse() throws Exception {
        MilestoneEvent event1 = userMilestoneEventRepository.save(buildMileStoneEvent("message-id-1"));
        userMilestoneEventRepository.save(buildMileStoneEvent("message-id-2"));
        milestoneEventConsumerAckRepository.save(buildMileStoneEventConsumerAck(event1));
        var mvcResult = mockMvc.perform(get(GET_ALL_UNPROCESSED_MILESTONES_MILESTONE_EVENT).queryParam("consumer", "test")
                        .queryParam("since", "2000-08-02T10:15:30+01:00").queryParam("until","2099-08-02T10:15:30+01:00"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        MilestoneEventResponseDTO events = JacksonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<MilestoneEventResponseDTO>() { });

        assertNotEquals(0, events.getContent().size());
    }

    private MilestoneEventConsumerAck buildMileStoneEventConsumerAck(MilestoneEvent event1) {
        MilestoneEventConsumerAck milestoneEventConsumerAck = new MilestoneEventConsumerAck();
        milestoneEventConsumerAck.setMilestoneEvent(event1);
        milestoneEventConsumerAck.setConsumer("test");
        milestoneEventConsumerAck.setProcessed(false);
        milestoneEventConsumerAck.setMessageId(event1.getMessageId());
        return milestoneEventConsumerAck;
    }
}
