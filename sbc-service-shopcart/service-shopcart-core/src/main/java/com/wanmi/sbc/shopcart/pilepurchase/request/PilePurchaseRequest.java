package com.wanmi.sbc.shopcart.pilepurchase.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.shopcart.bean.enums.FollowFlag;
import com.wanmi.sbc.shopcart.pilepurchase.PilePurchase;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Enumerated;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商品客户收藏请求
 * Created by daiyitian on 2017/5/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PilePurchaseRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 3590025584368358090L;
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
    @Default
    private Boolean verifyStock = true;
    /**
     * 是否赠品 true 是 false 否
     */
    @Default
    private Boolean isGift = false;

    /**
     * 购买数量是否覆盖
     */
    @Default
    private Boolean isCover = false;

    private LocalDateTime createTime;

    /**
     * 邀请人id-会员id
     */
    @ApiModelProperty(value = "邀请人id")
    String inviteeId;

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

    /**
     * 订单编号 ===》 囤货明细
     */
    private String orderCode;

    /**
     * 订单pid
     */
    private String pid;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalPrice;

    /**
     * 订单优惠后商品均摊价格
     */
    private BigDecimal goodsSplitPrice;

    /**
     * 最小囤货数量
     */
    private Long minGoodsNum;

    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<PilePurchase> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();

            //客户编号
            if (StringUtils.isNotBlank(customerId)) {
                predicates.add(cbuild.equal(root.get("customerId"), customerId));
            }
            //SKU编号
            if (StringUtils.isNotBlank(goodsInfoId)) {
                predicates.add(cbuild.equal(root.get("goodsInfoId"), goodsInfoId));
            }
            //批量SKU编号
            if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
                predicates.add(root.get("goodsInfoId").in(goodsInfoIds));
            }

            //分销员编号
            if (StringUtils.isNotBlank(inviteeId)) {
                predicates.add(cbuild.equal(root.get("inviteeId"), inviteeId));
            }

            //分销员编号
            if (Objects.nonNull(companyInfoId)) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            if(Objects.nonNull(minGoodsNum)){
                predicates.add(cbuild.gt(root.get("goodsNum"), minGoodsNum));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

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
