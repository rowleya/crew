INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, NAME, CREATION_DATE, ENABLED) VALUES ('styler', 'a6589b4b54e0d9c6b7cad840744ed48a', 's.tyler@example.org', 'Sam Tyler', '2008-04-06 18:20:02', 1);
INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, NAME, CREATION_DATE, ENABLED) VALUES ('ghunt', 'a6589b4b54e0d9c6b7cad840744ed48a', 'g.hunt@example.org', 'Gene Hunt', '2008-04-06 18:20:02', 1);
INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, NAME, CREATION_DATE, ENABLED) VALUES ('cskelton', 'a6589b4b54e0d9c6b7cad840744ed48a', 'c.skelton@example.org', 'Chris Skelton', '2008-04-06 18:20:02', 1);
INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, NAME, CREATION_DATE, ENABLED) VALUES ('rcarling', 'a6589b4b54e0d9c6b7cad840744ed48a', 'r.carling@example.org', 'Ray Carling', '2008-04-06 18:20:02', 1);
INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, NAME, CREATION_DATE, ENABLED) VALUES ('acartwright', 'a6589b4b54e0d9c6b7cad840744ed48a', 'a.cartwright@example.org', 'Annie Cartwright', '2008-04-06 18:20:02', 1);
INSERT INTO GROUPS (GROUPID, NAME, DESCRIPTION) VALUES ('USER_GROUP', 'Users', 'Default group for users');
INSERT INTO GROUPS (GROUPID, NAME, DESCRIPTION) VALUES ('ADMIN_GROUP', 'Admins', 'Default group for users');
INSERT INTO GROUPS (GROUPID, NAME, DESCRIPTION) VALUES ('ILRT_GROUP', 'ILRT', 'Default group for ilrt people');
INSERT INTO ROLES (ROLEID, NAME, DESCRIPTION) VALUES ('USER', 'User', 'Role for users');
INSERT INTO ROLES (ROLEID, NAME, DESCRIPTION) VALUES ('ADMIN', 'Admin', 'Admin users');
INSERT INTO ROLES (ROLEID, NAME, DESCRIPTION) VALUES ('ACADEMIC', 'Academic', 'Academic Users');
INSERT INTO USERS_GROUPS (USERNAME, GROUPID) VALUES ('styler', 'USER_GROUP');
INSERT INTO USERS_GROUPS (USERNAME, GROUPID) VALUES ('ghunt', 'USER_GROUP');
INSERT INTO USERS_GROUPS (USERNAME, GROUPID) VALUES ('cskelton', 'USER_GROUP');
INSERT INTO USERS_GROUPS (USERNAME, GROUPID) VALUES ('rcarling', 'USER_GROUP');
INSERT INTO USERS_GROUPS (USERNAME, GROUPID) VALUES ('acartwright', 'USER_GROUP');
INSERT INTO USERS_GROUPS (USERNAME, GROUPID) VALUES ('ghunt', 'ADMIN_GROUP');
INSERT INTO GROUPS_ROLES (GROUPID, ROLEID) VALUES ('USER_GROUP', 'USER');
INSERT INTO GROUPS_ROLES (GROUPID, ROLEID) VALUES ('ADMIN_GROUP', 'USER');