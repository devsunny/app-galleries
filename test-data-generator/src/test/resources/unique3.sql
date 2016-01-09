
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
City varchar(255),
CONSTRAINT uc_PersonID UNIQUE (P_Id)
)



CREATE UNIQUE INDEX index_Personsname
ON Persons2 (P_Id)
;

ALTER TABLE Persons
ADD UNIQUE (P_Id);


ALTER TABLE Persons ADD CONSTRAINT uc_PersonID UNIQUE (P_Id);

