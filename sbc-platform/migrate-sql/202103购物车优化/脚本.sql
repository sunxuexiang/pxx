-- 增加购物车初始化有效商品排序
alter table `sbc-order`.purchase add valid_sort tinyint default 0 null;