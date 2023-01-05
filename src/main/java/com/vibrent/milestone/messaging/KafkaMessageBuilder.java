package com.vibrent.milestone.messaging;

import com.vibrent.vxp.push.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.MessageHeaders;

import java.io.IOException;

import static com.vibrent.milestone.constants.KafkaConstants.*;


@Slf4j
public class KafkaMessageBuilder {
    private KafkaMessageBuilder() {
    }

    public static MessageHeaderDto toMessageHeaderDto(MessageHeaders headers) {
        if (headers == null) {
            return null;
        }

        try {
            MessageHeaderDto messageHeaderDto = new MessageHeaderDto();
            messageHeaderDto.setVxpMessageSpecVersion(getString(headers, VXP_MESSAGE_SPEC_VERSION));
            messageHeaderDto.setVxpTenantID(getLong(headers, VXP_TENANT_ID));
            messageHeaderDto.setVxpProgramID(getLong(headers, VXP_PROGRAM_ID));
            messageHeaderDto.setVxpWorkflowInstanceID(getString(headers, VXP_WORKFLOW_INSTANCE_ID));
            messageHeaderDto.setVxpMessageID(getString(headers, VXP_MESSAGE_ID));
            messageHeaderDto.setVxpInReplyToID(getString(headers, VXP_IN_REPLY_TO_ID));
            messageHeaderDto.setVxpHeaderVersion(getString(headers, VXP_HEADER_VERSION));
            messageHeaderDto.setVxpUserID(getLong(headers, VXP_USER_ID));
            messageHeaderDto.setVxpOriginator(getRequestOriginatorEnum(headers));
            IntegrationPatternEnum integrationPatternEnum = getIntegrationPatternEnum(headers);
            messageHeaderDto.setVxpPattern(integrationPatternEnum);
            messageHeaderDto.setVxpMessageSpec(getMessageSpecificationEnum(headers));
            messageHeaderDto.setVxpTrigger(getContextTypeEnum(headers));
            messageHeaderDto.setVxpWorkflowName(getWorkflowNameEnum(headers));
            Long timestamp = getLong(headers, VXP_MESSAGE_TIMESTAMP);
            if (timestamp != null) {
                messageHeaderDto.setVxpMessageTimestamp(timestamp);
            }
            return messageHeaderDto;
        } catch (IOException | NumberFormatException e) {
            log.warn("Exception received when parsing the message Headers. headers: {}", headers, e);
        }

        return null;
    }

    public static com.vibrent.vxp.support.MessageHeaderDto toAcknowledgementMessageHeaderDto(MessageHeaders headers) {
        if (headers == null) {
            return null;
        }

        try {
            com.vibrent.vxp.support.MessageHeaderDto messageHeaderDto = new com.vibrent.vxp.support.MessageHeaderDto();
            messageHeaderDto.setVxpMessageSpecVersion(getString(headers, VXP_MESSAGE_SPEC_VERSION));
            messageHeaderDto.setVxpTenantID(getLong(headers, VXP_TENANT_ID));
            messageHeaderDto.setVxpProgramID(getLong(headers, VXP_PROGRAM_ID));
            messageHeaderDto.setVxpWorkflowInstanceID(getString(headers, VXP_WORKFLOW_INSTANCE_ID));
            messageHeaderDto.setVxpMessageID(getString(headers, VXP_MESSAGE_ID));
            messageHeaderDto.setVxpInReplyToID(getString(headers, VXP_IN_REPLY_TO_ID));
            messageHeaderDto.setVxpHeaderVersion(getString(headers, VXP_HEADER_VERSION));
            messageHeaderDto.setVxpUserID(getLong(headers, VXP_USER_ID));
            messageHeaderDto.setVxpOriginator(getSupportRequestOriginatorEnum(headers));
            com.vibrent.vxp.support.IntegrationPatternEnum integrationPatternEnum = getSupportIntegrationPatternEnum(headers);
            messageHeaderDto.setVxpPattern(integrationPatternEnum);
            messageHeaderDto.setVxpMessageSpec(getSupportMessageSpecificationEnum(headers));
            messageHeaderDto.setVxpTrigger(getSupportContextTypeEnum(headers));
            messageHeaderDto.setVxpWorkflowName(getSupportWorkflowNameEnum(headers));
            Long timestamp = getLong(headers, VXP_MESSAGE_TIMESTAMP);
            if (timestamp != null) {
                messageHeaderDto.setVxpMessageTimestamp(timestamp);
            }
            return messageHeaderDto;
        } catch (IOException | NumberFormatException e) {
            log.warn("Exception received when parsing the message Headers. headers: {}", headers, e);
        }

        return null;
    }

    private static WorkflowNameEnum getWorkflowNameEnum(@NonNull MessageHeaders headers) throws IOException {
        String enumString;
        WorkflowNameEnum workflowNameEnum = null;
        enumString = getString(headers, VXP_WORKFLOW_NAME);
        if (enumString != null) {
            workflowNameEnum = WorkflowNameEnum.forValue(enumString);
        }
        return workflowNameEnum;
    }

    private static com.vibrent.vxp.support.WorkflowNameEnum getSupportWorkflowNameEnum(@NonNull MessageHeaders headers) throws IOException {
        String enumString;
        com.vibrent.vxp.support.WorkflowNameEnum workflowNameEnum = null;
        enumString = getString(headers, VXP_WORKFLOW_NAME);
        if (enumString != null) {
            workflowNameEnum = com.vibrent.vxp.support.WorkflowNameEnum.forValue(enumString);
        }
        return workflowNameEnum;
    }

    private static ContextTypeEnum getContextTypeEnum(@NonNull MessageHeaders headers) throws IOException {
        String enumString;
       ContextTypeEnum contextTypeEnum = null;
        enumString = getString(headers, VXP_TRIGGER);
        if (enumString != null) {
            contextTypeEnum = ContextTypeEnum.forValue(enumString);
        }
        return contextTypeEnum;
    }

    private static com.vibrent.vxp.support.ContextTypeEnum getSupportContextTypeEnum(@NonNull MessageHeaders headers) throws IOException {
        String enumString;
        com.vibrent.vxp.support.ContextTypeEnum contextTypeEnum = null;
        enumString = getString(headers, VXP_TRIGGER);
        if (enumString != null) {
            contextTypeEnum = com.vibrent.vxp.support.ContextTypeEnum.forValue(enumString);
        }
        return contextTypeEnum;
    }

    private static MessageSpecificationEnum getMessageSpecificationEnum(@NonNull MessageHeaders headers) throws IOException {
        String enumString;
        MessageSpecificationEnum messageSpecificationEnum = null;
        enumString = getString(headers, VXP_MESSAGE_SPEC);
        if (enumString != null) {

            messageSpecificationEnum = MessageSpecificationEnum.forValue(enumString);
        }
        return messageSpecificationEnum;
    }

    private static com.vibrent.vxp.support.MessageSpecificationEnum getSupportMessageSpecificationEnum(@NonNull MessageHeaders headers) throws IOException {
        String enumString;
        com.vibrent.vxp.support.MessageSpecificationEnum messageSpecificationEnum = null;
        enumString = getString(headers, VXP_MESSAGE_SPEC);
        if (enumString != null) {

            messageSpecificationEnum = com.vibrent.vxp.support.MessageSpecificationEnum.forValue(enumString);
        }
        return messageSpecificationEnum;
    }

    private static IntegrationPatternEnum getIntegrationPatternEnum(@NonNull MessageHeaders headers) throws IOException {
        IntegrationPatternEnum integrationPatternEnum = null;
        String enumString = getString(headers, VXP_PATTERN);
        if (enumString != null) {
            integrationPatternEnum = IntegrationPatternEnum.forValue(enumString);
        }
        return integrationPatternEnum;
    }

    private static com.vibrent.vxp.support.IntegrationPatternEnum getSupportIntegrationPatternEnum(@NonNull MessageHeaders headers) throws IOException {
        com.vibrent.vxp.support.IntegrationPatternEnum integrationPatternEnum = null;
        String enumString = getString(headers, VXP_PATTERN);
        if (enumString != null) {
            integrationPatternEnum = com.vibrent.vxp.support.IntegrationPatternEnum.forValue(enumString);
        }
        return integrationPatternEnum;
    }

    private static RequestOriginatorEnum getRequestOriginatorEnum(@NonNull MessageHeaders headers) throws IOException {
        RequestOriginatorEnum requestOriginatorEnum = null;
        String enumString = getString(headers, VXP_ORIGINATOR);
        if (enumString != null) {
            requestOriginatorEnum = RequestOriginatorEnum.forValue(enumString);
        }

        return requestOriginatorEnum;
    }

    private static com.vibrent.vxp.support.RequestOriginatorEnum getSupportRequestOriginatorEnum(@NonNull MessageHeaders headers) throws IOException {
        com.vibrent.vxp.support.RequestOriginatorEnum requestOriginatorEnum = null;
        String enumString = getString(headers, VXP_ORIGINATOR);
        if (enumString != null) {
            requestOriginatorEnum = com.vibrent.vxp.support.RequestOriginatorEnum.forValue(enumString);
        }

        return requestOriginatorEnum;
    }

    public static Long getLong(@NonNull MessageHeaders headers, @NonNull String key) {
        Object obj = headers.get(key);
        if (obj != null) {
            return Long.valueOf(obj.toString());
        }
        return null;
    }

    public static String getString(@NonNull MessageHeaders headers, @NonNull String key) {
        Object obj = headers.get(key);
        if (obj == null || obj instanceof String) {
            return (String) obj;
        }
        return obj.toString();
    }
}
