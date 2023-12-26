package com.wanmi.sbc.job;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.goods.api.provider.goodsimage.GoodsImageProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品图片水印任务
 * 
 * @author Administrator
 *
 */
@JobHandler(value = "goodsImageHandler")
@Component
@Slf4j
public class GoodsImageHandler extends IJobHandler {

	/**
	 * 避免重复运行， 多实例。
	 */
	private static volatile boolean isRunning = false;
	
	@Autowired
	private GoodsImageProvider goodsImageProvider;

	@Override
	@LcnTransaction
	public ReturnT<String> execute(String paramStr) throws Exception {
		if (!isRunning) {
			isRunning = true;
			XxlJobLogger.log("商品图片水印任务开始==========  " + LocalDateTime.now());
			log.info("商品图片水印任务开始========== ：：：" + LocalDateTime.now());
			int i_cnt = goodsImageProvider.watermark().getContext();
			XxlJobLogger.log("商品图片水印任务完成==========  " + LocalDateTime.now() + "  处理：" + i_cnt + "条");
			log.info("商品图片水印任务完成========== ：：：" + LocalDateTime.now() + "  处理：" + i_cnt + "条");
			isRunning = false;
		}
		return SUCCESS;
	}

}
