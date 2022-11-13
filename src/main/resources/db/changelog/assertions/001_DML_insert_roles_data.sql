--liquibase formatted sql
--changeset Milosz08:001-dml-insert-roles-data

INSERT INTO LOCAL_USER_ROLE
VALUES (1, 'moderator', PARSEDATETIME(NOW(), 'yyyy-mm-dd hh:mm:ss.SSS'), PARSEDATETIME(NOW(), 'yyyy-mm-dd hh:mm:ss.SSS'));

INSERT INTO LOCAL_USER_ROLE
VALUES (2, 'admin', PARSEDATETIME(NOW(), 'yyyy-mm-dd hh:mm:ss.SSS'), PARSEDATETIME(NOW(), 'yyyy-mm-dd hh:mm:ss.SSS'));

INSERT INTO LOCAL_USER_ROLE
VALUES (3, 'user', PARSEDATETIME(NOW(), 'yyyy-mm-dd hh:mm:ss.SSS'), PARSEDATETIME(NOW(), 'yyyy-mm-dd hh:mm:ss.SSS'));
