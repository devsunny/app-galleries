create table account
(
	id number(16, 0) not null primary key, 
	--#SEQUENCE,min=1,step=1
	fname varchar(64) not null, --#FIRST_NAME
	lname varchar(64) not null, --#LAST_NAME
	DOB DATE NOT NULL, --#DATE,format=yyyyMMdd
	house_number INT NOT null, --#UINT,min=1,max=100000
	street varchar(128) NOT null, --#STREET
	city varchar(64) NOT null, --#CITY
	state varchar(64) NOT null, --#STATE
	zip varchar(5) NOT null, --#zip
	SSN VARCHAR(11) NULL, --#FORMATTED_STRING,format=DDD-DD-DDDD
	SACCNT VARCHAR(10) NULL --#FORMATTED_STRING,format=XDD-DD-DDD
)

;


create table Product
(
	id number(16, 0) not null primary key, 
	--#SEQUENCE,min=1,step=1
	fname varchar(64) not null, --#FIRST_NAME
	lname varchar(64) not null, --#LAST_NAME
	DOB DATE NOT NULL, --#DATE,format=yyyyMMdd
	house_number INT NOT null, --#UINT,min=1,max=100000
	street varchar(128) NOT null, --#STREET
	city varchar(64) NOT null, --#CITY
	state varchar(64) NOT null, --#STATE
	zip varchar(5) NOT null, --#zip
	SSN VARCHAR(11) NULL, --#FORMATTED_STRING,format=DDD-DD-DDDD
	SACCNT VARCHAR(10) NULL --#FORMATTED_STRING,format=XDD-DD-DDD
)
;




create table orders1
(
	order_id number(16, 0) not null , --#SEQUENCE,min=1,step=1
	account_id number(16, 0) not null,  
	product_id number(16, 0) not null, 
	created_date timestamp not null,    --#TIMESTAMP
	total_price number(16, 2) not null, --#LUXURY_PRICE
	primary key(order_id, account_id)
);


ALTER TABLE orders1 ADD FOREIGN KEY (account_id)  REFERENCES account (id);
ALTER TABLE orders1 ADD FOREIGN KEY (product_id)  REFERENCES Product (id);
