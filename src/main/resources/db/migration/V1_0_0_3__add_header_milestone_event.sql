-- --------------------------------------------------------------------------------------------------------------------
-- Date          : September 12, 2022             Added By  : Gaurav Gupta
-- JIRA ID       : AC-123971                Comments  : Added header in  milestone event table
-- --------------------------------------------------------------------------------------------------------------------

ALTER TABLE `milestone_event` ADD column `header` json DEFAULT NULL AFTER `metadata`;
