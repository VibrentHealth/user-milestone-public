package com.vibrent.milestone.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.exceptions.BusinessProcessingException;
import com.vibrent.milestone.util.JacksonUtil;
import com.vibrent.usermilestone.dto.MilestoneEventDTO;
import com.vibrent.vxp.push.MessageHeaderDto;
import com.vibrent.vxp.push.UserMilestoneEventDto;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface UserMilestoneEventMapper {

    default MilestoneEvent convertToMilestoneEvent(@NonNull UserMilestoneEventDto userMilestoneEventDto, @NonNull MessageHeaderDto messageHeaderDto) {
        try {
            MilestoneEvent milestoneEvent = new MilestoneEvent();
            milestoneEvent.setMessageId(messageHeaderDto.getVxpMessageID());
            milestoneEvent.setVibrentId(userMilestoneEventDto.getVibrentID());
            milestoneEvent.setType(userMilestoneEventDto.getEventType());
            milestoneEvent.setTimestamp(Objects.requireNonNullElse(messageHeaderDto.getVxpMessageTimestamp(), Instant.now().toEpochMilli()));
            milestoneEvent.setStatus(userMilestoneEventDto.getStatus());
            milestoneEvent.setSource(userMilestoneEventDto.getSource());
            milestoneEvent.setMetadata(JacksonUtil.getMapper().writeValueAsString(userMilestoneEventDto));
            milestoneEvent.setHeader(JacksonUtil.getMapper().writeValueAsString(messageHeaderDto));
            return milestoneEvent;

        } catch (JsonProcessingException e) {
            throw new BusinessProcessingException("Failed to convert UserMilestoneEventDto to MilestoneEvent", e);
        }
    }

    @Mapping(target = "metadata", expression = "java(toJson(milestoneEvent.getMetadata()))")
    @Mapping(target = "header", expression = "java(toHeader(milestoneEvent.getHeader()))")
    MilestoneEventDTO convertToMilestoneEventDTO(MilestoneEvent milestoneEvent);

    @Named("UserMilestoneDtoStringToJson")
    default Object toJson(String jsonString) {

        try {
            return JacksonUtil.getMapper().readValue(jsonString, UserMilestoneEventDto.class);
        } catch (JsonProcessingException e) {
            throw new BusinessProcessingException("Failed to convert the UserMilestoneEventDto json string - " + jsonString);
        }
    }

    @Named("UserMilestoneDtoStringToHeader")
    default Object toHeader(String jsonString) {

        try {
            return JacksonUtil.getMapper().readValue(jsonString, MessageHeaderDto.class);
        } catch (JsonProcessingException e) {
            throw new BusinessProcessingException("Failed to convert the MessageHeaderDto json string - " + jsonString);
        }
    }
}
