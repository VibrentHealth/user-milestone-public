package com.vibrent.milestone.service;

import com.vibrent.vxp.support.AcknowledgementEventDto;
import com.vibrent.vxp.support.MessageHeaderDto;

public interface MilestoneEventConsumerService {

    void saveAcknowledgementEvent(AcknowledgementEventDto acknowledgementEventDto, MessageHeaderDto messageHeaderDto);
}
