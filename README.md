# webapp

shao

1. iac
2. email:
    1. reminder
3.

qi

1. dynamodb + email verification
2. nfr ppt

todo

1. db & rds? container?

# 如何创建一个task

1. 选择一个已有的list
2. 填充list id, summary, task, due, priority
3. 上传附件
    1. 上传附件和名字
    2. 返回附件，前端保存附件id
    3. 在task创建完毕后附件attach给task
4. 添加Tag
    1. 创建tag或选择已有tag
    2. **tag不允许重名**
    3. 返回tag，前端保存tag id
    4. 在task创建完毕后tag attach给task

# 各种实体类与task的关系

1. comment: comment表中有taskId，创建comment时直接与task绑定。表现为post时，同时传入comment内容和taskId，成功即为绑定成功
    1. 限制：数量无限制
2. tag(可修改): tag与task有一张关联表，tag_task_relation，创建tag时先保存tag，在保存/创建task的时候，往tag_task_relation中插入一条tag_task_relation。
    1. 限制：一个task最多10个tag。tag名**不可重复**，实现上在创建tag时，如果tag名重复，返回已有的tag
3. attachment(可删除):attachment表中有taskId，userId，创建attachment时先保存attachment，在保存/创建task的时候，再将attachment attach到task上
    1. 限制：一个task最多5个attachment
    2. 删除：如果某个attachment在一定时间内没有attach给某个task，则视为task创建失败。需要在s3及db中删除
4. task本身:
    1. 修改：
        - 可移动至另外一个list
        - 可修改字段值
    2. 删除：级联删除comment和attachment，不可删除tag
