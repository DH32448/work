create table accountDetails
(
    id       int auto_increment,
    username varchar(255) not null,
    phone    varchar(255) not null,
    email    varchar(255) null,
    role     varchar(255) not null,
    password varchar(255) not null,
    constraint accountDetails_pk
        primary key (id)
)
    collate = utf8mb4_unicode_ci;

create index accountDetails_email_index
    on accountDetails (email);

create unique index accountDetails_phone_uindex
    on accountDetails (phone);

create unique index accountDetails_username_uindex
    on accountDetails (username);

commit ;

create table Carousel
(
    id      int auto_increment,
    uuid    varchar(255) not null,
    title   varchar(255) not null,
    text    varchar(255) not null,
    imgdata LONGBLOB     null,
    constraint Carousel_pk
        primary key (id)
)
    charset = utf8mb4;

commit ;
create table accountinfo
(
    id           int auto_increment,
    aid          int               not null comment '关联账号表主键id',
    name         varchar(255)      not null comment '姓名,未输入就随机一个',
    sex          enum ('男', '女') null comment '性别',
    age          int               null comment '年龄',
    registertime DATETIME              not null comment '注册日期',
    text         varchar(255)      null comment '简介',
    constraint accountinfo_pk
        primary key (id),
    constraint accountinfo_accountdetails_id_fk
        foreign key (aid) references accountdetails (id)
)
    comment '用户信息表' charset = utf8mb4;

commit ;