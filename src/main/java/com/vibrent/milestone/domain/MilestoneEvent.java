package com.vibrent.milestone.domain;

import com.vibrent.milestone.domain.common.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "milestone_event")
@Data
public class MilestoneEvent extends AbstractEntity {

    private static final long serialVersionUID = -7622035023203811132L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdOrGenerated")
    @GenericGenerator(name = "IdOrGenerated", strategy = "com.vibrent.milestone.domain.common.UseIdOrGenerate")
    private Long id;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "vibrent_id")
    private Long vibrentId;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "type")
    private String type;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "status")
    private String status;

    @Column(name = "source")
    private String source;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "header")
    private String header;
}
