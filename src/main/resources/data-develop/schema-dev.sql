

alter table artist_user_drawing_field
    drop
        foreign key if exists FK5c2gtchqlx96pgev9bxihgrn2
;

alter table artist_user_drawing_field
    drop
        foreign key if exists FKj458cpmyyqkhvks2e7hujhbrc
;

alter table chatting
    drop
        foreign key if exists FK97p7mnutiv8neq1kjrkimfgqj
;

alter table chatting
    drop
        foreign key if exists FKlq1vyv931dd06k6nq627j96qj
;

alter table chatting
    drop
        foreign key if exists FK9ql3tj5nrwmen58b42nnwhfbi
;

alter table estimate_sheet_chat
    drop
        foreign key if exists FKjjscc8hob7g9s0tj2bj90ftpp
;

alter table message_chat
    drop
        foreign key if exists FK25vwqdxyn61ir11qy5x3gbcnh
;

alter table notification_chat
    drop
        foreign key if exists FK9yvo40g7mh8eimrtkldu9aw41
;

alter table portfolio
    drop
        foreign key if exists FK89ewqqlxod28rwjqd1dip0s8c
;

alter table portfolio_content
    drop
        foreign key if exists FKk3kpg7i6v74foujccj4d40pux
;

alter table portfolio_drawing_field
    drop
        foreign key if exists FKd8wotpu093jimnuyxoy8f05y6
;

alter table portfolio_drawing_field
    drop
        foreign key if exists FKpmwhpo0fdcwav92w6mocaromu
;

alter table real_time_request
    drop
        foreign key if exists FKlytoq1shtd6p7s61cfwb6ic1f
;

alter table request_apply
    drop
        foreign key if exists FKtcfpd0df2tce8xymiyt69mcs5
;

alter table request_apply
    drop
        foreign key if exists FK6n5bb7hmsnkijnaijlj0739v9
;

alter table request_apply
    drop
        foreign key if exists FKds26jmwn66pgqakg2c1ror2vk
;

alter table users
    drop
        foreign key if exists FKn3bu8i43luloo3fefrpfuek7x
;

drop table if exists artist_user_drawing_field
;

drop table if exists chatting
;

drop table if exists drawing_field
;

drop table if exists estimate_sheet_chat
;

drop table if exists file_item
;

drop table if exists mail_template
;

drop table if exists message_chat
;

drop table if exists notice
;

drop table if exists notification_chat
;

drop table if exists portfolio
;

drop table if exists portfolio_content
;

drop table if exists portfolio_drawing_field
;

drop table if exists portfolio_room
;

drop table if exists question
;

drop table if exists real_time_request
;

drop table if exists request_apply
;

drop table if exists users
;

create table artist_user_drawing_field (
                                           artist_user_drawing_field_id bigint not null auto_increment,
                                           created_date datetime(6),
                                           updated_date datetime(6),
                                           created_by varchar(255),
                                           updated_by varchar(255),
                                           artist_user_user_id bigint,
                                           drawing_field_drawing_field_id bigint,
                                           primary key (artist_user_drawing_field_id)
) engine=InnoDB
;

create table chatting (
                          chatting_id bigint not null auto_increment,
                          created_date datetime(6),
                          updated_date datetime(6),
                          receiver_id bigint,
                          apply_id bigint,
                          sender_id bigint,
                          primary key (chatting_id)
) engine=InnoDB
;

create table drawing_field (
                               drawing_field_id bigint not null auto_increment,
                               description varchar(255),
                               field_name varchar(255) not null,
                               status varchar(255),
                               primary key (drawing_field_id)
) engine=InnoDB
;

create table estimate_sheet_chat (
                                     adult bit not null,
                                     content varchar(255) not null,
                                     end_date date not null,
                                     reference_link varchar(255),
                                     start_date date not null,
                                     title varchar(255) not null,
                                     use_type varchar(255) not null,
                                     chatting_id bigint not null,
                                     primary key (chatting_id)
) engine=InnoDB
;

create table file_item (
                           file_item_id bigint not null auto_increment,
                           created_date datetime(6),
                           updated_date datetime(6),
                           created_by varchar(255),
                           updated_by varchar(255),
                           bucket_path varchar(255),
                           file_type varchar(255),
                           status varchar(255),
                           store_file_name varchar(255),
                           upload_file_name varchar(255),
                           primary key (file_item_id)
) engine=InnoDB
;

create table mail_template (
                               mail_template_id bigint not null auto_increment,
                               created_date datetime(6),
                               updated_date datetime(6),
                               content varchar(2000) not null,
                               mail_template_name varchar(255) not null,
                               title varchar(255) not null,
                               primary key (mail_template_id)
) engine=InnoDB
;

create table message_chat (
                              message varchar(255),
                              chatting_id bigint not null,
                              primary key (chatting_id)
) engine=InnoDB
;

create table notice (
                        notice_id bigint not null auto_increment,
                        created_date datetime(6),
                        updated_date datetime(6),
                        created_by varchar(255),
                        updated_by varchar(255),
                        content varchar(2000) not null,
                        disabled bit not null,
                        title varchar(100) not null,
                        top bit not null,
                        view integer not null,
                        primary key (notice_id)
) engine=InnoDB
;

create table notification_chat (
                                   message varchar(255),
                                   chatting_id bigint not null,
                                   primary key (chatting_id)
) engine=InnoDB
;

create table portfolio (
                           portfolio_id bigint not null auto_increment,
                           created_date datetime(6),
                           updated_date datetime(6),
                           created_by varchar(255),
                           updated_by varchar(255),
                           main_image_url varchar(255),
                           status varchar(255),
                           tags varchar(255),
                           title varchar(255),
                           view bigint,
                           artist_user_user_id bigint,
                           primary key (portfolio_id)
) engine=InnoDB
;

create table portfolio_content (
                                   dtype varchar(31) not null,
                                   port_folio_content_id bigint not null auto_increment,
                                   created_date datetime(6),
                                   updated_date datetime(6),
                                   created_by varchar(255),
                                   updated_by varchar(255),
                                   image_url varchar(255),
                                   text varchar(255),
                                   video_url varchar(255),
                                   port_folio_id bigint,
                                   primary key (port_folio_content_id)
) engine=InnoDB
;

create table portfolio_drawing_field (
                                         portfolio_drawing_field_id bigint not null auto_increment,
                                         created_date datetime(6),
                                         updated_date datetime(6),
                                         created_by varchar(255),
                                         updated_by varchar(255),
                                         drawing_field_drawing_field_id bigint,
                                         portfolio_id bigint,
                                         primary key (portfolio_drawing_field_id)
) engine=InnoDB
;

create table portfolio_room (
                                port_folio_room_id bigint not null auto_increment,
                                created_date datetime(6),
                                updated_date datetime(6),
                                created_by varchar(255),
                                updated_by varchar(255),
                                portfolio_room_status varchar(255) not null,
                                portfolio_roomurl varchar(255) not null,
                                status_message varchar(255),
                                primary key (port_folio_room_id)
) engine=InnoDB
;

create table question (
                          question_id bigint not null auto_increment,
                          created_date datetime(6),
                          updated_date datetime(6),
                          created_by varchar(255),
                          updated_by varchar(255),
                          content varchar(2000) not null,
                          disabled bit not null,
                          title varchar(100) not null,
                          top bit not null,
                          view integer not null,
                          primary key (question_id)
) engine=InnoDB
;

create table real_time_request (
                                   real_time_request_id bigint not null auto_increment,
                                   created_date datetime(6),
                                   updated_date datetime(6),
                                   adult bit not null,
                                   content varchar(255) not null,
                                   end_date date not null,
                                   reference_link varchar(255),
                                   start_date date not null,
                                   status varchar(255) not null,
                                   title varchar(255) not null,
                                   use_type varchar(255) not null,
                                   user_id bigint not null,
                                   primary key (real_time_request_id)
) engine=InnoDB
;

create table request_apply (
                               dtype varchar(31) not null,
                               request_apply_id bigint not null auto_increment,
                               created_date datetime(6),
                               updated_date datetime(6),
                               status varchar(255),
                               artist_user_id bigint,
                               client_user_id bigint,
                               real_time_request_id bigint,
                               primary key (request_apply_id)
) engine=InnoDB
;

create table users (
                       dtype varchar(31) not null,
                       user_id bigint not null auto_increment,
                       created_date datetime(6),
                       updated_date datetime(6),
                       adult_certification bit not null,
                       account_number varchar(20) not null,
                       bank_name varchar(20) not null,
                       deleted bit not null,
                       deleted_time datetime(6),
                       email varchar(100) not null,
                       gender varchar(5) not null,
                       mobile_carrier varchar(10) not null,
                       name varchar(100) not null,
                       nickname varchar(100) not null,
                       password varchar(100) not null,
                       phone_number varchar(14) not null,
                       profile_image varchar(255) not null,
                       role varchar(20) not null,
                       advertisement bit not null,
                       personal_info bit not null,
                       policy bit not null,
                       service bit not null,
                       email_news_notification bit,
                       email_request_notification bit,
                       kakao_news_notification bit,
                       kakao_request_notification bit,
                       push_request_notification bit,
                       status_mark varchar(255),
                       portfolio_room_id bigint,
                       primary key (user_id)
) engine=InnoDB
;

alter table mail_template
    add constraint UK_9lnfk9bvduxv5gdwchdpuys2q unique (mail_template_name)
;

alter table portfolio_room
    add constraint UK_fu11ae8peeru1bat8n28wsu57 unique (portfolio_roomurl)
;

alter table artist_user_drawing_field
    add constraint FK5c2gtchqlx96pgev9bxihgrn2
        foreign key (artist_user_user_id)
            references users (user_id)
;

alter table artist_user_drawing_field
    add constraint FKj458cpmyyqkhvks2e7hujhbrc
        foreign key (drawing_field_drawing_field_id)
            references drawing_field (drawing_field_id)
;

alter table chatting
    add constraint FK97p7mnutiv8neq1kjrkimfgqj
        foreign key (receiver_id)
            references users (user_id)
;

alter table chatting
    add constraint FKlq1vyv931dd06k6nq627j96qj
        foreign key (apply_id)
            references request_apply (request_apply_id)
;

alter table chatting
    add constraint FK9ql3tj5nrwmen58b42nnwhfbi
        foreign key (sender_id)
            references users (user_id)
;

alter table estimate_sheet_chat
    add constraint FKjjscc8hob7g9s0tj2bj90ftpp
        foreign key (chatting_id)
            references chatting (chatting_id)
;

alter table message_chat
    add constraint FK25vwqdxyn61ir11qy5x3gbcnh
        foreign key (chatting_id)
            references chatting (chatting_id)
;

alter table notification_chat
    add constraint FK9yvo40g7mh8eimrtkldu9aw41
        foreign key (chatting_id)
            references chatting (chatting_id)
;

alter table portfolio
    add constraint FK89ewqqlxod28rwjqd1dip0s8c
        foreign key (artist_user_user_id)
            references users (user_id)
;

alter table portfolio_content
    add constraint FKk3kpg7i6v74foujccj4d40pux
        foreign key (port_folio_id)
            references portfolio (portfolio_id)
;

alter table portfolio_drawing_field
    add constraint FKd8wotpu093jimnuyxoy8f05y6
        foreign key (drawing_field_drawing_field_id)
            references drawing_field (drawing_field_id)
;

alter table portfolio_drawing_field
    add constraint FKpmwhpo0fdcwav92w6mocaromu
        foreign key (portfolio_id)
            references portfolio (portfolio_id)
;

alter table real_time_request
    add constraint FKlytoq1shtd6p7s61cfwb6ic1f
        foreign key (user_id)
            references users (user_id)
;

alter table request_apply
    add constraint FKtcfpd0df2tce8xymiyt69mcs5
        foreign key (artist_user_id)
            references users (user_id)
;

alter table request_apply
    add constraint FK6n5bb7hmsnkijnaijlj0739v9
        foreign key (client_user_id)
            references users (user_id)
;

alter table request_apply
    add constraint FKds26jmwn66pgqakg2c1ror2vk
        foreign key (real_time_request_id)
            references real_time_request (real_time_request_id)
;

alter table users
    add constraint FKn3bu8i43luloo3fefrpfuek7x
        foreign key (portfolio_room_id)
            references portfolio_room (port_folio_room_id)
;
