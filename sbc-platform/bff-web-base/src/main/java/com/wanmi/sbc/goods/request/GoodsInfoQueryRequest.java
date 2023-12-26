package com.wanmi.sbc.goods.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.es.elastic.request.EsPropQueryRequest;
import com.wanmi.sbc.es.elastic.request.dto.EsGoodsInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfoQueryRequest extends BaseQueryRequest {

    /**
     * 批量品牌编号
     */
    @ApiModelProperty(value = "批量品牌编号")
    private List<Long> brandIds;

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    /**
     * 分类编号集合
     */
    @ApiModelProperty(value = "分类编号集合")
    private List<Long> cateIds;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    private Integer companyType;

    @ApiModelProperty(value = "分销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销")
    private Integer distributionGoodsAudit;

    /**
     * 分销商品状态，配合分销开关使用
     */
    @ApiModelProperty(value = "分销商品状态，配合分销开关使用")
    private Integer distributionGoodsStatus;

    /**
     * 企业购商品过滤
     */
    @ApiModelProperty(value = "分销商品状态，配合分销开关使用")
    private Integer enterPriseGoodsStatus;

    /**
     * 未登录时,前端采购单缓存信息
     */
    @Valid
    @ApiModelProperty(value = "未登录时,前端采购单缓存信息")
    private List<EsGoodsInfoDTO> esGoodsInfoDTOList;

    /**
     * 是否是特价商品
     */
    @ApiModelProperty(value = "是否是特价商品")
    private Integer goodsInfoType;

    /**
     * 是否能匹配仓
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

    /**
     * 多个 属性与对应的属性值id列表
     */
    @ApiModelProperty(value = "多个 属性与对应的属性值id列表")
    private List<EsPropQueryRequest> propDetails = new ArrayList<>();

    @ApiModelProperty(value = "是否根据品类绑定的品牌排序")
    private Boolean sortByCateBrand = false;

    /**
     * 关键字，可能含空格
     */
    @ApiModelProperty(value = "关键字，可能含空格")
    private String keywords;

    @ApiModelProperty(value = "排序标识:/**\n" +
            "     * 排序标识\n" +
            "     * 0: 销量倒序->时间倒序->市场价倒序\n" +
            "     * 1:上架时间倒序->销量倒序->市场价倒序\n" +
            "     * 2:市场价倒序->销量倒序\n" +
            "     * 3:市场价升序->销量倒序\n" +
            "     * 4:销量倒序->市场价倒序\n" +
            "     * 5:评论数倒序->销量倒序->市场价倒序\n" +
            "     * 6:好评倒序->销量倒序->市场价倒序\n" +
            "     * 7:收藏倒序->销量倒序->市场价倒序\n" +
            "     */")
    private Integer sortFlag;

    /**
     * 批量店铺分类编号
     */
    @ApiModelProperty(value = "批量店铺分类编号")
    private List<Long> storeCateIds;

    private Long storeCateId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 匹配的分仓Id
     */
    @ApiModelProperty(value = "匹配的分仓Id")
    private Long wareId;

    @ApiModelProperty(value = "商品分类，0散称，1定量, 不传查询全部")
    private Integer isScatteredQuantitative;


}
