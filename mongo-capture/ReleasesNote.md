# mongo升级到4.2，开启事务
mongodb 升级到4.2并开启了事务的时候oplog结构有变更
## 事务
原来可以通过ns字段来判断是不是对应的collection，但是开启了事务之后就需要先判断是不是事务操作的文档，通过：
* op -> c
* ns -> admin.$cmd 

则为事务操作，所以需要取 <font color=red> **o -> applyOps** </font> 的list，同一个事务的操作放在一起需要循环解析

## 非事务操作
* op -> i,u,d
* ns -> collectionName
* o -> data
* ts -> 时间