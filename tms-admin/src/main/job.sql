create table job_qrtz_job_details
  (
    sched_name varchar(120) not null,
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    description varchar(250) null,
    job_class_name   varchar(250) not null,
    is_durable varchar(1) not null,
    is_nonconcurrent varchar(1) not null,
    is_update_data varchar(1) not null,
    requests_recovery varchar(1) not null,
    job_data blob null,
    primary key (sched_name,job_name,job_group)
);

create table job_qrtz_triggers
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    description varchar(250) null,
    next_fire_time bigint(13) null,
    prev_fire_time bigint(13) null,
    priority integer null,
    trigger_state varchar(16) not null,
    trigger_type varchar(8) not null,
    start_time bigint(13) not null,
    end_time bigint(13) null,
    calendar_name varchar(200) null,
    misfire_instr smallint(2) null,
    job_data blob null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,job_name,job_group)
        references job_qrtz_job_details(sched_name,job_name,job_group)
);

create table job_qrtz_simple_triggers
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    repeat_count bigint(7) not null,
    repeat_interval bigint(12) not null,
    times_triggered bigint(10) not null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
        references job_qrtz_triggers(sched_name,trigger_name,trigger_group)
);

create table job_qrtz_cron_triggers
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    cron_expression varchar(200) not null,
    time_zone_id varchar(80),
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
        references job_qrtz_triggers(sched_name,trigger_name,trigger_group)
);

-- create table job_qrtz_simprop_triggers
--   (
--     sched_name varchar(120) not null,
--     trigger_name varchar(200) not null,
--     trigger_group varchar(200) not null,
--     str_prop_1 varchar(512) null,
--     str_prop_2 varchar(512) null,
--     str_prop_3 varchar(512) null,
--     int_prop_1 int null,
--     int_prop_2 int null,
--     long_prop_1 bigint null,
--     long_prop_2 bigint null,
--     dec_prop_1 numeric(13,4) null,
--     dec_prop_2 numeric(13,4) null,
--     bool_prop_1 varchar(1) null,
--     bool_prop_2 varchar(1) null,
--     primary key (sched_name,trigger_name,trigger_group),
--     foreign key (sched_name,trigger_name,trigger_group)
--     references job_qrtz_triggers(sched_name,trigger_name,trigger_group)
-- );

create table job_qrtz_blob_triggers
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    blob_data blob null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
        references job_qrtz_triggers(sched_name,trigger_name,trigger_group)
);

-- create table job_qrtz_calendars
--   (
--     sched_name varchar(120) not null,
--     calendar_name  varchar(200) not null,
--     calendar blob not null,
--     primary key (sched_name,calendar_name)
-- );
--
create table job_qrtz_paused_trigger_grps
  (
    sched_name varchar(120) not null,
    trigger_group  varchar(200) not null,
    primary key (sched_name,trigger_group)
);

create table job_qrtz_fired_triggers
  (
    sched_name varchar(120) not null,
    entry_id varchar(95) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    instance_name varchar(200) not null,
    fired_time bigint(13) not null,
    sched_time bigint(13) not null,
    priority integer not null,
    state varchar(16) not null,
    job_name varchar(200) null,
    job_group varchar(200) null,
    is_nonconcurrent varchar(1) null,
    requests_recovery varchar(1) null,
    primary key (sched_name,entry_id)
);

-- create table job_qrtz_scheduler_state
--   (
--     sched_name varchar(120) not null,
--     instance_name varchar(200) not null,
--     last_checkin_time bigint(13) not null,
--     checkin_interval bigint(13) not null,
--     primary key (sched_name,instance_name)
-- );
--
create table job_qrtz_locks
  (
    sched_name varchar(120) not null,
    lock_name  varchar(40) not null,
    primary key (sched_name,lock_name)
);

create table `job_qrtz_trigger_info` (
  `id` int(11) not null auto_increment,
  `job_group` varchar(200) not null comment '执行器主键id',
  `job_cron` varchar(128) not null comment '任务执行cron',
  `job_desc` varchar(255) not null,
	`address` varchar(255) not null,
	`command` text not null,
  `add_time` datetime default null,
  `update_time` datetime default null,
  `author` varchar(64) default null comment '作者',
  `alarm_email` varchar(255) default null comment '报警邮件',
  `executor_handler` varchar(255) default null comment '执行器任务handler',
  primary key (`id`)
) engine=innodb default charset=utf8;

create table `job_qrtz_trigger_log` (
  `id` int(11) not null auto_increment,
  `job_group` varchar(200) not null comment '执行器主键id',
  `job_id` int(11) not null comment '任务，主键id',
  `executor_address` varchar(255) default null comment '执行器地址，本次执行的地址',
  `executor_handler` varchar(255) default null comment '执行器任务handler',
  `executor_param` varchar(512) default null comment '执行器任务参数',

-- 	`executor_sharding_param` varchar(20) default null comment '执行器任务分片参数，格式如 1/2',
--   `executor_fail_retry_count` int(11) not null default '0' comment '失败重试次数',

	`trigger_time` datetime default null comment '调度-时间',
  `trigger_code` int(11) not null comment '调度-结果',
  `trigger_msg` text comment '调度-日志',
  `handle_time` datetime default null comment '执行-时间',
  `handle_code` int(11) not null comment '执行-状态',
  `handle_msg` text comment '执行-日志',
  primary key (`id`),
  key `i_trigger_time` (`trigger_time`)
) engine=innodb default charset=utf8;

-- create table `job_qrtz_trigger_logglue` (
--   `id` int(11) not null auto_increment,
--   `job_id` int(11) not null comment '任务，主键id',
--   `glue_type` varchar(50) default null comment 'glue类型',
--   `glue_source` mediumtext comment 'glue源代码',
--   `glue_remark` varchar(128) not null comment 'glue备注',
--   `add_time` timestamp null default null,
--   `update_time` timestamp null default null on update current_timestamp,
--   primary key (`id`)
-- ) engine=innodb default charset=utf8;

-- create table job_qrtz_trigger_registry (
--   `id` int(11) not null auto_increment,
--   `registry_group` varchar(255) not null,
--   `registry_key` varchar(255) not null,
--   `registry_value` varchar(255) not null,
--   `update_time` timestamp not null default current_timestamp,
--   primary key (`id`)
-- ) engine=innodb default charset=utf8;

create table `job_qrtz_trigger_group` (
  `id` int(11) not null auto_increment,
  `app_name` varchar(64) not null comment '执行器appname',
  `title` varchar(12) not null comment '执行器名称',
  `order` tinyint(4) not null default '0' comment '排序',
  `address_type` tinyint(4) not null default '0' comment '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` varchar(512) default null comment '执行器地址列表，多地址逗号分隔',
  primary key (`id`)
) engine=innodb default charset=utf8;

create table `user`  (
  `username` varchar(255) character set utf8 collate utf8_bin not null,
  `password` varchar(255) character set utf8 collate utf8_bin null default null,
  `email` varchar(255) character set utf8 collate utf8_bin null default null,
  `role` varchar(255) character set utf8 collate utf8_bin null default '',
  primary key (`username`) using btree
) engine = innodb character set = utf8 collate = utf8_bin row_format = dynamic;

create table `user_token`  (
  `token` varchar(255) character set utf8 collate utf8_bin not null,
  `username` varchar(255) character set utf8 collate utf8_bin not null,
  `expiration` bigint(255) null default null,
  primary key (`username`) using btree,
  constraint `usertoken` foreign key (`username`) references `user` (`username`) on delete cascade on update cascade
) engine = innodb character set = utf8 collate = utf8_bin row_format = dynamic;


create table `job_group`  (
  `app_name` varchar(255) character set utf8 collate utf8_general_ci not null,
  `title` varchar(255) character set utf8 collate utf8_bin not null,
  primary key (`app_name`) using btree
) engine = innodb character set = utf8 collate = utf8_general_ci row_format = dynamic;


commit;