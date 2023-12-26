package com.wanmi.sbc.goods.provider.impl.customerarealimitdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.customerarelimitdetail.CustomerAreaLimitDetailProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailAddRequest;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchNosModifyRequest;
import com.wanmi.sbc.goods.api.response.customerarealimitdetail.CustomerAreaLimitDetailResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.CustomerAreaLimitDetailVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.customerarealimitdetail.Service.CustomerAreLimitDetailService;
import com.wanmi.sbc.goods.customerarealimitdetail.model.root.CustomerAreaLimitDetail;
import com.wanmi.sbc.goods.customerarealimitdetail.repository.CustomerAreaLimitDetailRepository;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;


@RestController
@Validated
public class CustomerAreaLimitDetailController implements CustomerAreaLimitDetailProvider {

    @Autowired
    private CustomerAreLimitDetailService customerAreLimitDetailService;
    @Autowired
    private CustomerAreaLimitDetailRepository customerAreaLimitDetailRepository;


    @Override
    public BaseResponse<CustomerAreaLimitDetailResponse> listByCids(CustomerAreaLimitDetailRequest request) {
        List<CustomerAreaLimitDetail> customerAreaLimitDetails = customerAreLimitDetailService.getbycustomerIdAndGoodsInfoidAndRegionId(
                request.getCustomerId(),
                request.getGoodsInfoId(),
                request.getRegionIds(),
                request.getTid()
        );
        return BaseResponse.success(CustomerAreaLimitDetailResponse.builder().detailVOS(KsBeanUtil.convert(customerAreaLimitDetails, CustomerAreaLimitDetailVO.class)).build());
    }

    @Override
    public BaseResponse addByOrder(CustomerAreaLimitDetailAddRequest request) {
        List<CustomerAreaLimitDetail> convert = KsBeanUtil.convert(request.getList(), CustomerAreaLimitDetail.class);
        customerAreaLimitDetailRepository.saveAll(convert);
        return BaseResponse.SUCCESSFUL();
    }
}
