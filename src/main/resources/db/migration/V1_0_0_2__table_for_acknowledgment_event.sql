-- --------------------------------------------------------------------------------------------------------------------
-- Date          : July 20, 2022             Added By  : Ritesh Khaire
-- JIRA ID       : AC-118943                 Comments  : Added acknowledgement event table
-- --------------------------------------------------------------------------------------------------------------------

CREATE TABLE user_milestone.`milestone_event_consumer_ack` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_id` varchar(255) NOT NULL,
  `milestone_event_id` bigint(20) DEFAULT NULL,
  `consumer` varchar(255) NOT NULL,
  `processed` BIT(1) NOT NULL DEFAULT b'1',
  `created_on` bigint(20) DEFAULT NULL,
  `updated_on` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_milestone_event_consumer_ack_mapping_milestone_event_id` FOREIGN KEY (`milestone_event_id`) REFERENCES `milestone_event` (`id`)
);

