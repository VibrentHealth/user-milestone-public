-- --------------------------------------------------------------------------------------------------------------------
-- Date          : July 11, 2022             Added By  : Vishnu Kotu
-- JIRA ID       : AC-118941                Comments  : Added  user milestone event table
-- --------------------------------------------------------------------------------------------------------------------

CREATE TABLE user_milestone.`milestone_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_id` varchar(255) DEFAULT NULL,
  `vibrent_id` bigint(20) DEFAULT NULL,
  `entity_id` varchar(300) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `metadata` json DEFAULT NULL,
  `created_on` bigint(20) DEFAULT NULL,
  `updated_on` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_milestone_event_message_id` (`message_id`)
);
