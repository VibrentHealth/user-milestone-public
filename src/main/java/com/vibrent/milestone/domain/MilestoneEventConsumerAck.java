package com.vibrent.milestone.domain;

import com.vibrent.milestone.domain.common.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "milestone_event_consumer_ack")
@Data
public class MilestoneEventConsumerAck extends AbstractEntity {

    private static final long serialVersionUID = -7622035023203811132L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdOrGenerated")
    @GenericGenerator(name = "IdOrGenerated", strategy = "com.vibrent.milestone.domain.common.UseIdOrGenerate")
    private Long id;

    @Column(name = "message_id")
    private String messageId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "milestone_event_id", nullable = false)
    private MilestoneEvent milestoneEvent;

    @Column(name = "consumer")
    private String consumer;

    @Column(name = "processed")
    private boolean processed;
}
