package com.wanmi.sbc.setting.provider.impl.print;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.print.PrintConfigProvider;
import com.wanmi.sbc.setting.api.request.print.PrintConfigRequest;
import com.wanmi.sbc.setting.api.response.print.PrintConfigResponse;
import com.wanmi.sbc.setting.bean.vo.PrintConfigVO;
import com.wanmi.sbc.setting.print.PrintConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>基本设置保存服务接口实现</p>
 * @author lq
 * @date 2019-11-05 16:08:31
 */
@RestController
@Validated
public class PrintConfigController implements PrintConfigProvider {

	@Autowired
	private PrintConfigService printConfigService;


	@Override
	public BaseResponse<PrintConfigResponse> modify(@RequestBody @Valid PrintConfigRequest printConfigRequest) {
		PrintConfigVO printConfigVO = printConfigService.modify(KsBeanUtil.convert(printConfigRequest, PrintConfigVO.class));
		return BaseResponse.success(PrintConfigResponse.builder().printConfigVO(printConfigVO).build());
	}

	@Override
	public BaseResponse<PrintConfigResponse> findOne() {
		return BaseResponse.success(PrintConfigResponse.builder()
				.printConfigVO(printConfigService.findFirst())
				.build());
	}
}

