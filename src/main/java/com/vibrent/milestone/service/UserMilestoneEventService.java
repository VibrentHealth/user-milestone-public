package com.vibrent.milestone.service;

import com.vibrent.usermilestone.dto.MilestoneEventDTO;
import com.vibrent.usermilestone.dto.MilestoneEventResponseDTO;
import com.vibrent.vxp.push.MessageHeaderDto;
import com.vibrent.vxp.push.UserMilestoneEventDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UserMilestoneEventService {
    void processUserMilestoneEvent(UserMilestoneEventDto userMilestoneEventDto, MessageHeaderDto messageHeaderDto);

    List<MilestoneEventDTO> getUserMilestoneEventByMessageIds(List<String> messageIds);

    MilestoneEventResponseDTO getUnprocessedMilestoneByConsumer(String consumer, Optional<Boolean> latest, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until, Optional<Integer> size, Optional<Integer> page);
}
