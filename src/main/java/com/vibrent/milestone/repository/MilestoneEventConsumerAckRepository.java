package com.vibrent.milestone.repository;

import com.vibrent.milestone.domain.MilestoneEventConsumerAck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilestoneEventConsumerAckRepository extends JpaRepository<MilestoneEventConsumerAck, Long> {

    MilestoneEventConsumerAck findByMilestoneEvent_IdAndConsumer(Long id, String consumer);
}
