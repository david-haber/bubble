create table account (id identity,
username varchar unique,
firstName varchar not null,
lastName varchar not null,
age int,
location varchar,
email varchar,
gender varchar,
primary key (id));

create table comment(id identity,
topic int,
commenter varchar,
text varchar,
level int,
parent int,
datecreated timestamp default current_timestamp, 
agreeing varchar,
rank real, primary key(id));

create table topic(
id identity,
title varchar unique,
datecreated timestamp default current_timestamp,
primary key(id));

create table votes(
voter varchar not null,
commentid int,
upvote boolean,
primary key(voter,commentid)
);

insert into votes (voter, commentid, upvote) values ('oli', 1, true);
insert into votes (voter, commentid, upvote) values ('david', 1, true);
insert into votes (voter, commentid, upvote) values ('james', 1, false);
insert into topic (title) values ('iPod');