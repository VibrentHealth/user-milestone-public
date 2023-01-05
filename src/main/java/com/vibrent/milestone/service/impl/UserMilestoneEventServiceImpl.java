package com.vibrent.milestone.service.impl;

import com.vibrent.milestone.converter.UserMilestoneEventMapper;
import com.vibrent.milestone.domain.MilestoneEvent;
import com.vibrent.milestone.repository.UserMilestoneEventRepository;
import com.vibrent.milestone.service.UserMilestoneEventService;
import com.vibrent.milestone.util.PaginationUtil;
import com.vibrent.usermilestone.dto.MilestoneEventDTO;
import com.vibrent.usermilestone.dto.MilestoneEventResponseDTO;
import com.vibrent.vxp.push.MessageHeaderDto;
import com.vibrent.vxp.push.UserMilestoneEventDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;

@Slf4j
@AllArgsConstructor
@Service
public class UserMilestoneEventServiceImpl implements UserMilestoneEventService {

    private final UserMilestoneEventRepository userMilestoneEventRepository;
    private final UserMilestoneEventMapper userMilestoneEventMapper;
    private final EntityManager entityManager;

    private static final int PAGE_SIZE = 100;
    private static final int PAGE_NO = 0;



    @Override
    public void processUserMilestoneEvent(UserMilestoneEventDto userMilestoneEventDto, MessageHeaderDto messageHeaderDto) {

        if(userMilestoneEventDto == null || messageHeaderDto == null){
            log.warn("UserMilestone: received null userMilestoneEventDto or headers.  UserMilestoneEvent : {}, headers: {}", userMilestoneEventDto, messageHeaderDto);
            return;
        }

        if (!StringUtils.hasText(messageHeaderDto.getVxpMessageID())) {
            log.warn("UserMilestone: UserMilestoneEvent received with null message ID.  UserMilestoneEvent : {}, headers: {}", userMilestoneEventDto, messageHeaderDto);
            return;
        }

        MilestoneEvent milestoneEvent = userMilestoneEventRepository.findByMessageId(messageHeaderDto.getVxpMessageID());

        if (milestoneEvent != null) {
            log.info("UserMilestone: UserMilestoneEvent entry with messageId: {} already exist.  UserMilestoneEvent : {}", messageHeaderDto.getVxpMessageID(), userMilestoneEventDto);
            return;
        }

        try {
            milestoneEvent = userMilestoneEventMapper.convertToMilestoneEvent(userMilestoneEventDto, messageHeaderDto);
            userMilestoneEventRepository.save(milestoneEvent);
        } catch (Exception e) {
            log.error("UserMilestone: Failed to save the User Milestone event. errMsg: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<MilestoneEventDTO> getUserMilestoneEventByMessageIds(List<String> messageIds) {
        return userMilestoneEventRepository.findByMessageIdIn(messageIds)
                .stream()
                .map(userMilestoneEventMapper::convertToMilestoneEventDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MilestoneEventResponseDTO getUnprocessedMilestoneByConsumer(String consumer, Optional<Boolean> latest, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until, Optional<Integer> size, Optional<Integer> page) {
        MilestoneEventResponseDTO milestoneEventResponseDTO = new MilestoneEventResponseDTO();
        try {
            int pageNo = page.orElse(PAGE_NO);
            int pageSize = size.orElse(PAGE_SIZE);
            TypedQuery<MilestoneEvent> query = entityManager.createQuery(buildQuery(consumer, since, until), MilestoneEvent.class);
            query.setFirstResult((pageNo) * pageSize);
            query.setMaxResults(pageSize);
            List<MilestoneEvent> result = query.getResultList();

            Query queryTotal = entityManager.createQuery(buildQueryTotalCount(consumer, since, until));
            long countResult = (long) queryTotal.getSingleResult();
            int totalPages = (int) ((countResult == 0) ? 0 : ceil((float)countResult / (float)pageSize));
            milestoneEventResponseDTO.setPage(PaginationUtil.getPageInfo(totalPages, countResult, pageSize, pageNo));
            milestoneEventResponseDTO.setLinks(PaginationUtil.addLinks(totalPages, consumer, latest, since, until, pageSize, pageNo));
            milestoneEventResponseDTO.setContent(result.stream()
                    .map(userMilestoneEventMapper::convertToMilestoneEventDTO)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("UserMilestone: Error to fetch unprocessed Milestone events. errMsg: {}", e.getMessage(), e);
        }
        return milestoneEventResponseDTO;
    }

    private String buildQuery(String consumer, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select ME from MilestoneEvent ME LEFT JOIN MilestoneEventConsumerAck " +
                        "MEA ON MEA.milestoneEvent.id = ME.id AND MEA.consumer = '")
                .append(consumer)
                .append("' where (MEA.id IS NULL OR MEA.processed = 0)");
        since.ifPresent(offsetDateTime -> queryBuilder.append(" AND ME.timestamp >= ").append(offsetDateTime.toInstant().toEpochMilli()));
        until.ifPresent(offsetDateTime -> queryBuilder.append(" AND ME.timestamp <= ").append(offsetDateTime.toInstant().toEpochMilli()));
        return queryBuilder.toString();
    }

    private String buildQueryTotalCount(String consumer, Optional<OffsetDateTime> since, Optional<OffsetDateTime> until) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select count(ME.id) from MilestoneEvent ME LEFT JOIN MilestoneEventConsumerAck " +
                        "MEA ON MEA.milestoneEvent.id = ME.id AND MEA.consumer = '")
                .append(consumer)
                .append("' where (MEA.id IS NULL OR MEA.processed = 0)");
        since.ifPresent(offsetDateTime -> queryBuilder.append(" AND ME.timestamp >= ").append(offsetDateTime.toInstant().toEpochMilli()));
        until.ifPresent(offsetDateTime -> queryBuilder.append(" AND ME.timestamp <= ").append(offsetDateTime.toInstant().toEpochMilli()));
        return queryBuilder.toString();
    }

}
