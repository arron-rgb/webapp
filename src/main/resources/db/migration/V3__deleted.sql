alter table t_tag
    add deleted int default 0 null;

alter table t_attachment
    add deleted int default 0 null;

alter table t_list
    add deleted int default 0 null;

alter table t_user
    add deleted int default 0 null;

alter table t_comment
    add deleted int default 0 null;

