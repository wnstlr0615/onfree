INSERT INTO onfree_test.users
(dtype, user_id, created_date, updated_date, adult_certification, account_number, bank_name, deleted, deleted_time, email, gender, mobile_carrier, name, nickname, password, phone_number, profile_image, `role`, advertisement, personal_info, policy, service, email_news_notification, email_request_notification, kakao_news_notification, kakao_request_notification, push_request_notification, status_mark, portfolio_room_id)
VALUES('N', 1, '2022-03-17 20:54:41.390439', '2022-03-17 20:54:41.390439', 1, '010-8888-9999', 'IBK', 0, NULL, 'wnstlr0615@naver.com', 'MAN', 'SKT', '준식', '온프리!!', '{bcrypt}$2a$10$3a0uDYQ5gmvDV7ELva2wjuhV4VgvxbBWh24FEjAZiJO4nvanVQ7mG', '010-8888-9999', 'http://onfree.io/images/123456789', 'NORMAL', 1, 1, 1, 1, 1, 1, 1, 1, 1, NULL, NULL);


INSERT INTO onfree_test.portfolio_room
(port_folio_room_id, created_date, updated_date, created_by, updated_by, portfolio_room_status, portfolio_roomurl, status_message)
VALUES(1, '2022-03-17 15:55:43.153709', '2022-03-17 15:55:43.153709', '운영자', '운영자', 'PUBLIC_PORTFOLIO_ROOM', 'joon', '');
;

INSERT INTO onfree_test.users
(dtype, user_id, created_date, updated_date, adult_certification, account_number, bank_name, deleted, deleted_time, email, gender, mobile_carrier, name, nickname, password, phone_number, profile_image, `role`, advertisement, personal_info, policy, service, email_news_notification, email_request_notification, kakao_news_notification, kakao_request_notification, push_request_notification, status_mark, portfolio_room_id)
VALUES('A', 2, '2022-03-17 15:49:40.466493', '2022-03-17 15:49:40.466493', 1, '010-8888-9999', 'IBK', 0, NULL, 'joon1@naver.com', 'MAN', 'SKT', '준식', '온프리!!', '{bcrypt}$2a$10$ct0QGvOXWaL39NJ5ZD.0QuJFxGSgolvk70gZBHawf2bHT7X60dFJu', '010-8888-9999', 'http://onfree.io/images/123456789', 'ARTIST', 1, 1, 1, 1, 1, 1, 1, 1, 1, 'OPEN', 1);
;

INSERT INTO onfree_test.real_time_request
(real_time_request_id, created_date, updated_date, adult, content, end_date, reference_link, start_date, status, title, use_type, user_id)
values
(1, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-20', 'referenceLink', '2022-03-18', 'REQUEST_RECRUITING', '실시간 의뢰 제목1', 'COMMERCIAL', 1),
(2, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-21', 'referenceLink', '2022-03-18', 'REQUEST_RECRUITING', '실시간 의뢰 제목2', 'NOT_COMMERCIAL', 1),
(3, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', true, '실시간 의뢰 내용입니다.', '2022-03-23', 'referenceLink', '2022-03-20', 'REQUEST_RECRUITING', '실시간 의뢰 제목3', 'COMMERCIAL', 2),
(4, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-24', 'referenceLink', '2022-03-20', 'REQUEST_RECRUITING', '실시간 의뢰 제목4', 'NOT_COMMERCIAL', 1),
(5, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', true, '실시간 의뢰 내용입니다.', '2022-03-25', 'referenceLink', '2022-03-24', 'REQUEST_RECRUITING', '실시간 의뢰 제목5', 'NOT_COMMERCIAL', 2),
(6, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', true, '실시간 의뢰 내용입니다.', '2022-03-21', 'referenceLink', '2022-03-18', 'REQUEST_RECRUITING', '실시간 의뢰 제목6', 'COMMERCIAL', 1),
(7, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-22', 'referenceLink', '2022-03-21', 'REQUEST_RECRUITING', '실시간 의뢰 제목7', 'COMMERCIAL', 2),
(8, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', true, '실시간 의뢰 내용입니다.', '2022-03-23', 'referenceLink', '2022-03-18', 'REQUEST_RECRUITING', '실시간 의뢰 제목8', 'NOT_COMMERCIAL', 2),
(9, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-25', 'referenceLink', '2022-03-20', 'REQUEST_RECRUITING', '실시간 의뢰 제목9', 'COMMERCIAL', 1),
(10, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', true, '실시간 의뢰 내용입니다.', '2022-03-29', 'referenceLink', '2022-03-20', 'REQUEST_RECRUITING', '실시간 의뢰 제목10', 'NOT_COMMERCIAL', 1),
(11, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-20', 'referenceLink', '2022-03-18', 'REQUEST_RECRUITING', '실시간 의뢰 제목11', 'COMMERCIAL', 1),
(12, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-20', 'referenceLink', '2022-03-18', 'REQUEST_RECRUITING', '실시간 의뢰 제목12', 'COMMERCIAL', 1),
(13, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', false, '실시간 의뢰 내용입니다.', '2022-03-20', 'referenceLink', '2022-03-18', 'REQUEST_RECRUITING', '실시간 의뢰 제목1222', 'COMMERCIAL', 1);
;

insert into onfree_test.drawing_field
(drawing_field_id, field_name, description, status)
values
(1, '캐릭터 디자인', '캐릭터 디자인 관련 내용입니다. ', 'USED'),
(2, '버츄얼 디자인', '버츄얼 디자인 관련 내용입니다. ', 'USED'),
(3, '일러스트', '일러스트 디자인 관련 내용입니다. ', 'USED'),
(4, '게임삽화/원화', '게임삽화/원화 디자인 관련 내용입니다. ', 'USED'),
(5, '만화, 소설표지', '만화, 소설표지 디자인 관련 내용입니다. ', 'USED'),
(6, '애니메이팅/밈', '애니메이팅/밈 디자인 관련 내용입니다. ', 'USED'),
(7, '파츠 제작', '파츠 제작 디자인 관련 내용입니다. ', 'USED'),
(8, '19+', '19+ 디자인 관련 내용입니다. ', 'USED')
;

insert into onfree_test.mail_template
(mail_template_id, created_date, updated_date, content, mail_template_name, title)
VALUES
(1, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '<a href=''<URL>''>이메일 인증하기</a>', 'CHECK_EMAIL', '[이메일 인증] 온프리 이메일 인증 확인'),
(2, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '<a href=''<URL>''>비밀번호 변경하기</a>', 'PASSWORD_RESET_TEMPLATE', '[이메일 인증] 온프리 비밀번호 설정')
;

insert into onfree_test.notice
(notice_id, created_date, updated_date, created_by, updated_by, content, disabled, title, top, view)
VALUES
(1, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 1', false, 0),
(2, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 2', true, 0),
(3, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 3', false, 0),
(4, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 4', true, 0),
(5, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 5', false, 0),
(6, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 6', true, 0),
(7, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 7', false, 0),
(8, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 8', true, 0),
(9, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 9', false, 0),
(10, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 10', true, 0),
(11, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '공지 내용입니다.', false, '공지 11', false, 0)
;

insert into onfree_test.question
(question_id, created_date, updated_date, created_by, updated_by, content, disabled, title, top, view)
VALUES
(1, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 1', false, 0),
(2, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 2', true, 0),
(3, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 3', false, 0),
(4, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 4', true, 0),
(5, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 5', false, 0),
(6, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 6', true, 0),
(7, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 7', false, 0),
(8, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 8', true, 0),
(9, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 9', false, 0),
(10, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 10', true, 0),
(11, '2022-03-17 11:41:15.007633', '2022-03-17 11:41:15.007633', '운영자', '운영자', '질문 내용입니다.', false, '질문 11', false, 0)
;
