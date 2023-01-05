package com.vibrent.milestone.repository;

import com.vibrent.milestone.domain.MilestoneEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMilestoneEventRepository extends JpaRepository<MilestoneEvent, Long> {
    MilestoneEvent findByMessageId(String messageId);

    List<MilestoneEvent> findByMessageIdIn(List<String> messageIds);
}
