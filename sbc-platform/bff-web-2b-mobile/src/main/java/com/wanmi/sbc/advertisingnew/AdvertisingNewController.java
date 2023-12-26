package com.wanmi.sbc.advertisingnew;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.request.statistic.StatisticAddRequest;
import com.wanmi.sbc.advertising.api.response.QueryActiveActResp;
import com.wanmi.sbc.advertising.bean.constant.AdConstants;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.bean.enums.StatisticInfoType;
import com.wanmi.sbc.common.base.BaseResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 *
 */
@Slf4j
@Api(description = "广告相关功能API", tags = "AdvertisingNewController")
@RestController
@RequestMapping(value = "advertising")
@EnableBinding
public class AdvertisingNewController {

	@Autowired
	private AdActivityProvider adActivityProvider;

	@Autowired
	private BinderAwareChannelResolver resolver;

	/**************** 广告接口开始 *******************/
	
	/**
	 * 查询运行中的广告
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "查询运行中的广告")
	@PostMapping("/adActivity/queryActiveAct")
	BaseResponse<QueryActiveActResp> queryActiveAct(@RequestBody @Valid ActQueryActiveActRequest req){
		BaseResponse<QueryActiveActResp> listQuery = adActivityProvider.queryActiveAct(req);
		return listQuery;
	}
	


	/**************** 广告接口结束 *******************/



	/**
	 * 记录广告展示信息,mobile端
	 * 
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "记录广告展示信息")
	@PostMapping("/adStatistic/addImpressionInfo")
	BaseResponse addImpressionInfo(@RequestBody @Valid StatisticAddRequest request) {
		request.setStatisticInfoType(StatisticInfoType.IMPRESSION.ordinal());
		String jsonString = JSON.toJSONString(request);
		resolver.resolveDestination(AdConstants.AD_STATISTIC_ADD).send(MessageBuilder.withPayload(jsonString).build());
		return BaseResponse.SUCCESSFUL();
	}

	/**
	 * 记录广告展示信息,mobile端
	 * 
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "记录广告点击信息")
	@PostMapping("/adStatistic/addClickInfo")
	BaseResponse addClickInfo(@RequestBody @Valid StatisticAddRequest request) {
		request.setStatisticInfoType(StatisticInfoType.CLICK.ordinal());
		String jsonString = JSON.toJSONString(request);
		resolver.resolveDestination(AdConstants.AD_STATISTIC_ADD).send(MessageBuilder.withPayload(jsonString).build());
		return BaseResponse.SUCCESSFUL();
	}

}

