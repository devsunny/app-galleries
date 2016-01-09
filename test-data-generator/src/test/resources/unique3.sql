
CREATE TABLE Persons1
(
P_Id int NOT NULL,
LastName varchar(255) NOT NULL,
FirstName varchar(255),
Address varchar(255),
City varchar(255),
UNIQUE (P_Id)
)
;

CREATE TABLE Persons2
(
P_Id int NOT NULL,
LastName varchar(255) NOT NULL,
FirstName varchar(255),
Address varchar(255),
City varchar(255)
)
;



CREATE TABLE Persons3
(
P_Id int NOT NULL,
LastName varchar(255) NOT NULL,
FirstName varchar(255),
Address varchar(255),
City varchar(255)
)
;


CREATE TABLE Persons4
(
P_Id int NOT NULL,
LastName varchar(255) NOT NULL,
FirstName varchar(255),
Address varchar(255),
City varchar(255)
)
;


CREATE UNIQUE INDEX index_Personsname ON Persons2 (P_Id);

ALTER TABLE Persons3 ADD UNIQUE (P_Id)
ALTER TABLE Persons3 ADD PRIMARY KEY (P_Id)

ALTER TABLE Persons4 ADD CONSTRAINT pk_PersonID PRIMARY KEY (P_Id)
ALTER TABLE Persons4 ADD CONSTRAINT uc_PersonID UNIQUE (P_Id);

