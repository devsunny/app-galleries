create table USERS (
    userid varchar(80) not null,   
    firstname varchar(80) not null,
    lastname varchar(80) not null,    
    password varchar(80) not null,
    constraint pk_account primary key (userid)
);