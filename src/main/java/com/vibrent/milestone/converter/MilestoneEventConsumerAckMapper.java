package com.vibrent.milestone.converter;

import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.domain.MilestoneEventConsumerAck;
import com.vibrent.vxp.support.AcknowledgementEventDto;
import com.vibrent.vxp.support.MessageHeaderDto;

public class MilestoneEventConsumerAckMapper {

    private MilestoneEventConsumerAckMapper() {
    }

    public static MilestoneEventConsumerAck convertToMilestoneEventConsumerAck(AcknowledgementEventDto acknowledgementEventDto, MessageHeaderDto messageHeaderDto, MilestoneEvent milestoneEvent){
        MilestoneEventConsumerAck milestoneEventConsumerAck = new MilestoneEventConsumerAck();
        milestoneEventConsumerAck.setConsumer(acknowledgementEventDto.getConsumer());
        milestoneEventConsumerAck.setProcessed(acknowledgementEventDto.isProcessed());
        milestoneEventConsumerAck.setMessageId(messageHeaderDto.getVxpInReplyToID());
        milestoneEventConsumerAck.setMilestoneEvent(milestoneEvent);

        return milestoneEventConsumerAck;
    }
}
