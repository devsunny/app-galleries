CREATE TABLE Persons2 --#label="Super Person",varname=Person
(
P_Id int NOT NULL,
LastName varchar(255) NOT NULL, --#LAST_NAME, label='Last Name',varname=lastName
FirstName varchar(255),
Address varchar(255),
description varchar(255), --#uitype=textarea
City varchar(255),
STATE varchar(24), --#ENUM,values="NY|NJ|CT|MA|PA",uitype=checkbox
CONSTRAINT uc_PersonID UNIQUE (P_Id)
);