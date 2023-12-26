

#wms 原表
DELETE FROM standard_sku WHERE    ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  
DELETE FROM standard_goods WHERE     ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  

# 商品
DELETE FROM goods  WHERE    ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  
DELETE FROM goods_info  WHERE     ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  
#散批
DELETE FROM bulk_goods WHERE     ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  
DELETE FROM bulk_goods_info WHERE ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  

DELETE FROM devanning_goods_info WHERE     ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  

SELECT  * FROM standard_sku WHERE     ware_id <>'' and ware_id is not null  and  ware_id NOT in (1,2,3);  







