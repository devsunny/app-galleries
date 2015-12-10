create table account
(
	id number(16, 0) not null primary key, 
	--#SEQUENCE,min=1,step=1
	fname varchar(64) not null, --#FIRST_NAME
	lname varchar(64) not null, --#LAST_NAME
	DOB DATE NOT NULL, --#DATE,format=yyyyMMdd
	house_number int null, --#UINT,min=1,max=100000
	street varchar(128) null, --#STREET
	city varchar(64) null, --#CITY
	zip varchar(5) null, --#zip
	SSN VARCHAR(11) NULL, --#FORMATTED_STRING,format=DDD-DD-DDDD
	SACCNT VARCHAR(10) NULL --#FORMATTED_STRING,format=XDD-DD-DDD
)

;

create table orders1
(
	order_id number(16, 0) not null primary key, --#SEQUENCE,min=1,step=1
	account_id number(16, 0) not null,  --#REF,ref=account.id,min=0,max=100
	created_date timestamp not null, --#TIMESTAMP
	total_price number(16, 2) not null --#UDOUBLE
);

CREATE TABLE Persons
(
P_Id int NOT NULL,
LastName varchar(255) NOT NULL,
FirstName varchar(255),
Address varchar(255),
City varchar(255),
CONSTRAINT pk_PersonID PRIMARY KEY (P_Id,LastName)
)
;


CREATE TABLE Orders2
(
O_Id int NOT NULL,
OrderNo int NOT NULL,
P_Id int,
PRIMARY KEY (O_Id),
CONSTRAINT fk_PerOrders FOREIGN KEY (P_Id)
REFERENCES Persons(P_Id)
)
;



ALTER TABLE Orders
ADD CONSTRAINT fk_PerOrders
FOREIGN KEY (account_id)
REFERENCES account(id);
