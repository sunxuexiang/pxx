package com.wanmi.sbc.distribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.DistributionCommissionUtils;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.distribution.DistributionCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.distribution.DistributionCustomerSaveProvider;
import com.wanmi.sbc.customer.api.provider.distribution.DistributorLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerEnableByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributorLevelByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.detail.CustomerDetailGetCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.distribution.DistributionCustomerEnableByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.distribution.DistributorLevelByCustomerIdResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 分销业务逻辑
 */
@Slf4j
@Service
public class DistributionService {


    @Autowired
    private DistributionCustomerQueryProvider distributionCustomerQueryProvider;

    @Autowired
    private DistributionCustomerSaveProvider distributionCustomerSaveProvider;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private DistributorLevelQueryProvider distributorLevelQueryProvider;

    /**
     * 根据开关重新设置分销商品标识
     *
     * @param goodsInfoList
     */
    public void checkDistributionSwitch(List<GoodsInfoVO> goodsInfoList) {
        //需要叠加访问端Pc\app不体现分销业务
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        goodsInfoList.forEach(goodsInfoVO -> {
            if (Objects.equals(ChannelType.PC_MALL, commonUtil.getDistributeChannel().getChannelType()) || DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(distributionCacheService.queryStoreOpenFlag(String.valueOf(goodsInfoVO.getStoreId())))) {
                goodsInfoVO.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
            }
        });
    }

    /**
     * 社交分销
     * shop渠道 店铺状态验证
     *
     * @return
     */
    public boolean checkInviteeIdEnable() {
        boolean enable = Boolean.TRUE;
        //获取分销类型 和 邀请人Id
        DistributeChannel channel = commonUtil.getDistributeChannel();
        //如果是小店模式
        if (null != channel && Objects.equals(channel.getChannelType(), ChannelType.SHOP)) {
            String inviteeId = channel.getInviteeId();
            //查询当前是否开启了小店模式
            DefaultFlag queryShopOpenFlag = distributionCacheService.queryShopOpenFlag();

            if (!(DefaultFlag.YES.equals(queryShopOpenFlag) && this.isDistributor(inviteeId))) {
                enable = Boolean.FALSE;
            }
        }
        return enable;
    }

    /**
     * 是否是分销员
     *
     * @return
     */
    public boolean isDistributor(String customerId) {
        boolean enable = Boolean.FALSE;
        //查询会员基础表信息
        CustomerGetByIdRequest customerGetByIdRequest = new CustomerGetByIdRequest();
        customerGetByIdRequest.setCustomerId(customerId);
        BaseResponse<CustomerGetByIdResponse> customerGetByIdResponse = customerQueryProvider.getCustomerById(customerGetByIdRequest);
        //如果会员基础信息是null 或者不是审核通过状态
        if (Objects.isNull(customerGetByIdResponse.getContext()) || !CheckState.CHECKED.equals(customerGetByIdResponse.getContext().getCheckState())) {
            return Boolean.FALSE;
        }
        //查询会员detail表
        BaseResponse<CustomerDetailGetCustomerIdResponse> customerDetailGetCustomerIdResponseBaseResponse =
                customerDetailQueryProvider.getCustomerDetailByCustomerId(
                        CustomerDetailByCustomerIdRequest.builder().customerId(customerId).build());
        //如果会员detail表信息是null 或者不是启用状态
        if (Objects.isNull(customerDetailGetCustomerIdResponseBaseResponse.getContext()) ||
                !customerDetailGetCustomerIdResponseBaseResponse.getContext().getCustomerStatus().equals(CustomerStatus.ENABLE)) {
            return Boolean.FALSE;
        }

        //查询是否开启社交分销
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        //验证分销员状态
        DistributionCustomerEnableByCustomerIdResponse distributionCustomerEnableByIdResponse = distributionCustomerQueryProvider.checkEnableByCustomerId(
                new DistributionCustomerEnableByCustomerIdRequest(customerId)).getContext();
        if (DefaultFlag.YES.equals(openFlag) && Objects.nonNull(distributionCustomerEnableByIdResponse) && distributionCustomerEnableByIdResponse.getDistributionEnable()) {
            enable = Boolean.TRUE;
        }
        return enable;
    }

    /**
     * 根据会员ID查询分销员等级信息
     *
     * @param customerId
     * @return
     */
    public BaseResponse<DistributorLevelByCustomerIdResponse> getByCustomerId(String customerId) {
        return distributorLevelQueryProvider.getByCustomerId(new DistributorLevelByCustomerIdRequest(customerId));
    }

    /**
     * 计算分销分销佣金
     *
     * @param goodsInfoCommission 商品佣金
     * @param commissionRate      佣金比例-分销员等级
     * @return
     */
    public BigDecimal calDistributionCommission(BigDecimal goodsInfoCommission, BigDecimal commissionRate) {
        return DistributionCommissionUtils.calDistributionCommission(goodsInfoCommission, commissionRate);
    }
}
