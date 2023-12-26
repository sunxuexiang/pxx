package com.wanmi.sbc.order.api.request.purchase;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class PurchaseListNewRequest extends BaseQueryRequest {

    /**
     * SKU编号
     */
    @ApiModelProperty(value = "SKU编号")
    private String goodsInfoId;

    /**
     * 批量SKU编号
     */
    @ApiModelProperty(value = "批量SKU编号")
    private List<String> goodsInfoIds;

    /**
     * 邀请人id-会员id
     */
    @ApiModelProperty(value = "邀请人id")
    String inviteeId;

    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * 会员编号
     */
    @ApiModelProperty(value = "会员编号")
    private String customerId;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

    /**
     * 是否初始化
     */
    @ApiModelProperty(value = "是否初始化")
    private Boolean isRefresh = Boolean.FALSE;

    /**
     * 是否分页
     */
    @ApiModelProperty(value = "是否分页")
    private Boolean isPage = Boolean.TRUE;
}
