package com.vibrent.milestone.service.impl;

import com.vibrent.milestone.converter.MilestoneEventConsumerAckMapper;
import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.repository.MilestoneEventConsumerAckRepository;
import com.vibrent.milestone.repository.UserMilestoneEventRepository;
import com.vibrent.milestone.service.MilestoneEventConsumerService;
import com.vibrent.vxp.support.AcknowledgementEventDto;
import com.vibrent.vxp.support.MessageHeaderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Service
public class MilestoneEventConsumerServiceImpl implements MilestoneEventConsumerService {

    private final UserMilestoneEventRepository userMilestoneEventRepository;
    private final MilestoneEventConsumerAckRepository milestoneEventConsumerAckRepository;

    public MilestoneEventConsumerServiceImpl(UserMilestoneEventRepository userMilestoneEventRepository, MilestoneEventConsumerAckRepository milestoneEventConsumerAckRepository) {
        this.userMilestoneEventRepository = userMilestoneEventRepository;
        this.milestoneEventConsumerAckRepository = milestoneEventConsumerAckRepository;
    }


    @Override
    public void saveAcknowledgementEvent(AcknowledgementEventDto acknowledgementEventDto, MessageHeaderDto messageHeaderDto) {

        if (acknowledgementEventDto == null || messageHeaderDto == null) {
            log.warn("UserMilestone: received null acknowledgementEventDto or headers.  AcknowledgementEventDto : {}, headers: {}", acknowledgementEventDto, messageHeaderDto);
            return;
        }

        if (!StringUtils.hasText(messageHeaderDto.getVxpInReplyToID())) {
            log.warn("UserMilestone: AcknowledgementEvent received with null VXP-IN-REPLY-TO-ID.  AcknowledgementEventDto : {}, headers: {}", acknowledgementEventDto, messageHeaderDto);
            return;
        }

        MilestoneEvent milestoneEvent = userMilestoneEventRepository.findByMessageId(messageHeaderDto.getVxpInReplyToID());

        if (milestoneEvent == null) {
            log.warn("UserMilestone: Message id not found in MilestoneEvent table, VXP-IN-REPLY-TO-ID. {}", messageHeaderDto.getVxpInReplyToID());
            return;
        }

        var milestoneEventConsumerAck = milestoneEventConsumerAckRepository.findByMilestoneEvent_IdAndConsumer(milestoneEvent.getId(),acknowledgementEventDto.getConsumer());

        try {
            if(Objects.isNull(milestoneEventConsumerAck)){
                milestoneEventConsumerAck = MilestoneEventConsumerAckMapper.convertToMilestoneEventConsumerAck(acknowledgementEventDto, messageHeaderDto, milestoneEvent);
            } else{
                milestoneEventConsumerAck.setProcessed(acknowledgementEventDto.isProcessed());
            }
            milestoneEventConsumerAckRepository.save(milestoneEventConsumerAck);

        } catch (Exception e) {
            log.error("UserMilestone: Failed to save AcknowledgementEvent to DB errMsg: {}", e.getMessage(), e);
        }
    }
}
