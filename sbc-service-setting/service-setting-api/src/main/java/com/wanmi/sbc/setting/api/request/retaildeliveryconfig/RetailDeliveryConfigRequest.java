package com.wanmi.sbc.setting.api.request.retaildeliveryconfig;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;

import com.wanmi.sbc.setting.bean.vo.RetailDeliverConfigVO;
import io.swagger.annotations.ApiModel;
import lombok.*;


@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailDeliveryConfigRequest extends SettingBaseRequest {


	private static final long serialVersionUID = 9133257074168038652L;
	RetailDeliverConfigVO retailDeliverConfigVO;


}