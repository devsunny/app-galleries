create table manufacturers --#itemsPerPage=10
(
id bigint not null auto_increment, --#SEQUENCE,min=1
active bit not null,  --#ENUM, values=0,1
created_on datetime,  --#TIMESTAMP,format="yyyy-MM-dd HH:mm:ss",label="Created On"
link_rewrite varchar(255), --#label="Rewrite",max=32
meta_description varchar(255), --#label="Description",max=32
meta_keywords varchar(255), --#label="Keywords",max=32
meta_title varchar(255), --#label="Title",max=32
name varchar(255), --#max=32
updated_on datetime, --#label="Last Updated",TIMESTAMP,format="yyyy-MM-dd HH:mm:ss"
primary key (id), 
unique (name));

create table Account --#itemsPerPage=10
(
id bigint not null primary key auto_increment , --#SEQUENCE,min=1,groupfunction=count
Last_name varchar(32) NOT null, --#Last_NAME, label="Last Name"
first_name varchar(32) NOT null, --#FIRST_NAME, label="FIRST Name"
house_number int not null, --#UINT,min=1,max=100,label="House number"
Street varchar(128) not null, --#STREET
city varchar(128) not null, --#CITY
state varchar(62) not null, --#STATE,drilldown=1
zip_code varchar(10) not null --#ZIP
);