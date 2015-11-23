create table Resources
(
	name varchar(128) not null primary key,
	maxCapacity double not null default 100.0,
	reservedCapacity double not null default 0
)
;


create table Policy
(
	name varchar(128) not null primary key,
	description varchar(128) null,
	priority int not null default 5	
)
;


create table PolicyResource
(
	policy_name varchar(128) not null,
	resource_name varchar(128) not null,
	reserved_capacity double not null default 1
)
;

--job_exec_registry table has master entry for every job launched


create table job_exec_registry
(
	job_ticket varchar(128) not null primary key,	
	job_name varchar(128) not null,	
	exec_host varchar(128) not null,	
	exec_start_time timestamp not null,
	exec_end_time timestamp  null,
	job_complete_status varchar(64) not null default 'WAITING'
);

--job_exec_tracker tracks every step of each job status
create table job_exec_tracker
(
	job_ticket varchar(128) not null primary key,
	exec_update_time timestamp  not null,	
	exec_status varchar(64) not null default 'WAITING',
	exec_progress double not null default 0
);



-- job_ticket and resource_name are unique
-- to find out all resource used by a job instance where job_ticket='<job_ticket>'
create table job_consumption_report
(	
	job_ticket varchar(128) not null,
	resource_name varchar(128) not null,
	leased_capacity double not null default 1,	
	check_out_time timestamp  not null,
	check_in_time timestamp  null
);

--Put history detail in separate table is for perfromance resason when query against data, as most RDBMS does not optimized for CLOB and simple data type in same table.
--dataset_history_id is unique id (UUID can be used for this purpose)
--This history view would serve like CVS/subversion, it can even check into one those those repository if necessary
create table dataset_history
(
	sor_code varchar(64) not null,
	data_domain_code varchar(64) not null,
	version int not null default 1,
	dataset_history_id varchar(64) not null 	
);

create table dataset_history_detail
(
	dataset_history_id varchar(64) not null primary key,
	dataset_detail clob not null
);


create table sor
(
	sor_code varchar(64) not null primary key,
	sor_name varchar(128) not null,
	description varchar(256) null
);


create table dataset_group
(
	dataset_group_name varchar(128) not null,
	sor_code varchar(64) not null,
	dataset_code  varchar(64) not null 
);

create table dataset
(
	dataset_code  varchar(64) not null primary key,
	sor_code varchar(64) not null,
	dataset_name  varchar(128) not null,
	charset_name  varchar(64) not null default 'UTF8',	
	description varchar(256) null
	
);

--Domain name should unique
create table data_domain
(
	data_domain_code varchar(64) not null primary key,
	data_domain_name varchar(128) not null,
	description varchar(256) null
);


create table field_metadata
(
	field_id integer not null primary key,
	dataset_code varchar(64) not null, 
	field_name varchar(64) not null,
	field_type varchar(64) not null,
	field_length integer not null,
	field_position integer not null default 1
);


--without using field id here is better suite for querying, data would not be exetrmely normalized
--data_domain_name, dataset_name and field_name should be unique
create table data_domain_detail
(
	data_domain_name varchar(128) not null,
	dataset_name varchar(128) not null, 
	field_name varchar(64) not null,
);











