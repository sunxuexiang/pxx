package com.wanmi.sbc.advertisingnew;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangchen
 */
@JobHandler(value = "adCompleteHandler")
@Component
@Slf4j
public class AdCompleteHandler extends IJobHandler {

	@Autowired
	private AdActivityProvider adActivityProvider;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		log.info("广告批量完成定时任务开始#####################");
		adActivityProvider.batchComplete();
		log.info("广告批量完成定时任务结束#####################");
		return SUCCESS;
	}
}
