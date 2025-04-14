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
    collate = utf8mb3_bin;

create index accountDetails_email_index
    on accountDetails (email);

create unique index accountDetails_phone_uindex
    on accountDetails (phone);

create unique index accountDetails_username_uindex
    on accountDetails (username);

commit ;