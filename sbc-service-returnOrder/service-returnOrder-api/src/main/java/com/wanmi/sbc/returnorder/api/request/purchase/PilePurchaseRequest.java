package com.wanmi.sbc.returnorder.api.request.purchase;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.returnorder.bean.enums.FollowFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 已囤货商品
 *
 * @author yitang
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PilePurchaseRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 5400290011015741553L;
    /**
     * 当前客户等级
     */
    CustomerLevelVO customerLevel;
    /**
     * 编号
     */
    private List<Long> followIds;
    /**
     * SKU编号
     */
    private String goodsInfoId;
    /**
     * 批量SKU编号
     */
    private List<String> goodsInfoIds;
    /**
     * 批量sku
     */
    private List<GoodsInfoDTO> goodsInfos;
    /**
     * 会员编号
     */
    private String customerId;
    /**
     * 购买数量
     */
    @Range(min = 1)
    private Long goodsNum;
    /**
     * 收藏标识
     */
    @Enumerated
    private FollowFlag followFlag;
    /**
     * 校验库存
     */
    @Builder.Default
    private Boolean verifyStock = true;
    /**
     * 是否赠品 true 是 false 否
     */
    @Builder.Default
    private Boolean isGift = false;

    /**
     * 购买数量是否覆盖
     */
    @Builder.Default
    private Boolean isCover = false;

    private LocalDateTime createTime;

//    /**
//     * 邀请人id-会员id
//     */
//    @ApiModelProperty(value = "邀请人id")
//    String inviteeId;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;
    /**
     * 门店id
     */
    @ApiModelProperty(value = "门店id")
    private Long storeId;

    /**
     * saas开关
     */
    @ApiModelProperty(value = "saas开关")
    Boolean saasStatus = false;

    /**
     * 是否是pc端访问或者社交分销关闭
     */
    @ApiModelProperty(value = "是否是pc端访问或者社交分销关闭")
    private Boolean pcAndNoOpenFlag;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    /**
     * 仓库编码
     */
    @ApiModelProperty(value = "仓库编码")
    private String wareHouseCode;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

    /**
     * 是否初始化
     */
    @ApiModelProperty(value = "是否初始化")
    private Boolean isRefresh;


    public Boolean getVerifyStock() {
        return this.verifyStock == null ? true : this.verifyStock;
    }

    public Boolean getIsGift() {
        return this.isGift == null ? false : this.isGift;
    }

    public Boolean getIsCover() {
        return this.isCover == null ? false : this.isCover;
    }
}
