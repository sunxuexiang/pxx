ALTER TABLE `sbc-goods`.`goods_image` 
ADD COLUMN `watermark_url` varchar(255) NULL COMMENT '水印图地址' AFTER `big_url`;
