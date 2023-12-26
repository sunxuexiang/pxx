package com.wanmi.sbc.account.provider.impl.cashBack;

import com.wanmi.sbc.account.api.provider.cashBack.CashBackQueryProvider;
import com.wanmi.sbc.account.api.request.cashBack.CashBackPageRequest;
import com.wanmi.sbc.account.api.response.cashBack.CashBackPageResponse;
import com.wanmi.sbc.account.bean.vo.CashBackVO;
import com.wanmi.sbc.account.cashBack.model.root.CashBack;
import com.wanmi.sbc.account.cashBack.service.CashBackService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
public class CashBackQueryController implements CashBackQueryProvider {
    @Autowired
    private CashBackService cashBackService;
    @Override
    public BaseResponse<CashBackPageResponse> page(@Valid CashBackPageRequest request) {
        Page<CashBack> page = cashBackService.page(request);
        List<CashBackVO> cashBackVOList = getCashBackVOS(page);
        return BaseResponse.success(new CashBackPageResponse(new MicroServicePage<>(cashBackVOList, request.getPageRequest(),  page.getTotalElements())));
    }

    /**
     * Page转换List
     * @param page
     * @return
     */
    private List<CashBackVO> getCashBackVOS(Page<CashBack> page) {
        return page.getContent().stream().map(cashBack -> {
            CashBackVO vo = new CashBackVO();
            KsBeanUtil.copyPropertiesThird(cashBack, vo);
            return vo;
        }).collect(Collectors.toList());
    }

}
