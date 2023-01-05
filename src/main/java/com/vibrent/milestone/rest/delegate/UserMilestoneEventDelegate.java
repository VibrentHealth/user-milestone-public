package com.vibrent.milestone.rest.delegate;

import com.vibrent.milestone.converter.UserMilestoneEventMapper;
import com.vibrent.milestone.service.UserMilestoneEventService;
import com.vibrent.usermilestone.dto.MilestoneEventDTO;
import com.vibrent.usermilestone.dto.MilestoneEventResponseDTO;
import com.vibrent.usermilestone.resource.EventApiDelegate;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class UserMilestoneEventDelegate implements EventApiDelegate {

    final UserMilestoneEventService userMilestoneEventService;

    UserMilestoneEventMapper userMilestoneEventMapper;
    @Override
    public ResponseEntity<List<MilestoneEventDTO>> getMilestoneByID(List<String> messageIds) {
        return ResponseEntity.ok(userMilestoneEventService.getUserMilestoneEventByMessageIds(messageIds));
    }

    @Override
    public ResponseEntity<MilestoneEventResponseDTO> getUnprocessedMilestoneByConsumer(String consumer, Optional<Boolean> latest, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until, Optional<Integer> size, Optional<Integer> page) {
        return ResponseEntity.ok(userMilestoneEventService.getUnprocessedMilestoneByConsumer(consumer,latest,since,until,size,page));
    }
}
