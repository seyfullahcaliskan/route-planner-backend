alter table if exists users alter column status type varchar(30);
alter table if exists route_plan alter column status type varchar(30);
alter table if exists route_stop alter column status type varchar(30);
alter table if exists route_execution alter column status type varchar(30);
alter table if exists route_reoptimization_history alter column status type varchar(30);
alter table if exists address_import_batch alter column status type varchar(30);
alter table if exists navigation_event alter column status type varchar(30);