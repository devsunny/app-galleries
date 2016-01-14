CREATE TABLE Persons2 --#label="Super Person",orderby="firstName,lastName"
(
P_Id int NOT NULL primary key,
LastName varchar(255) NOT NULL, --#LAST_NAME, label='Last Name',varname=lastName,grouplevel=1,drilldown=1
FirstName varchar(255), 
Address varchar(255),   --#groupfunction=count
description varchar(255), --#uitype=textarea
City varchar(255),
STATE varchar(24), --#ENUM,values="NY|NJ|CT|MA|PA",uitype=checkbox
CONSTRAINT uc_PersonID UNIQUE (P_Id)
);


CREATE TABLE Persons1 --#label="Super Person",orderby="firstName,lastName"
(
P_Id int NOT NULL primary key,
LastName varchar(255) NOT NULL, --#LAST_NAME, label='Last Name',varname=lastName,grouplevel=1,drilldown=1
FirstName varchar(255), --#grouplevel=2,drilldown=2
Address varchar(255),   --#groupfunction=count
description varchar(255), --#uitype=textarea
City varchar(255),
STATE varchar(24), --#ENUM,values="NY|NJ|CT|MA|PA",uitype=checkbox
CONSTRAINT uc_PersonID UNIQUE (LastName)
);