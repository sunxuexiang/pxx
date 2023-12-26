package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.StoreType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by sunkun on 2017/11/6.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 6518396307249530736L;
    /**
     * 商家id列表
     */
    @ApiModelProperty(value = "商家id列表")
    private List<Long> companyInfoIds;
    /**
     * 入驻商家业务
     */
    @ApiModelProperty(value = "入驻商家业务")
    private String investmentManager;


    @ApiModelProperty(value = "商家id列表")
    private List<Long> companyInfoIdsNotIn;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    /**
     * 商家账号
     */
    @ApiModelProperty(value = "商家账号")
    private String accountName;

    /**
     * 商家编号
     */
    @ApiModelProperty(value = "商家编号")
    private String companyCode;

    /**
     * 签约结束日期
     */
    @ApiModelProperty(value = "签约结束日期")
    private String contractEndDate;

    /**
     * 账户状态  -1:全部 0：启用   1：禁用
     */
    @ApiModelProperty(value = "账户状态(-1:全部,0:启用,1:禁用)")
    private Integer accountState;

    /**
     * 店铺状态 -1：全部,0:开启,1:关店,2:过期
     */
    @ApiModelProperty(value = "店铺状态(-1:全部,0:开启,1:关店,2:过期)" )
    private Integer storeState;

    /**
     * 审核状态 -1全部 ,0:待审核,1:已审核,2:审核未通过
     */
    @ApiModelProperty(value = "审核状态(-1:全部,0:待审核,1:已审核,2:审核未通过)")
    private Integer auditState;

    /**
     * 商家删除状态
     */
    @ApiModelProperty(value = "商家删除状态")
    private DeleteFlag deleteFlag;

    /**
     * 申请入驻时间 开始时间
     */
    @ApiModelProperty(value = "申请入驻时间 开始时间")
    private String applyEnterTimeStart;

    /**
     * 申请入驻时间 结束时间
     */
    @ApiModelProperty(value = "申请入驻时间 结束时间")
    private String applyEnterTimeEnd;

    /**
     * 是否确认打款 (-1:全部,0:否,1:是)
     */
    @ApiModelProperty(value = "是否确认打款(-1:全部,0:否,1:是)")
    private Integer remitAffirm;

    /**
     * 商家类型 0、供应商 1、商家
     */
    @ApiModelProperty(value = "商家类型 0、供应商 1、商家")
    private StoreType storeType;

    @ApiModelProperty(value = "公司类型 0、平台自营 1、第三方商家 2、统仓统配 3、零售超市 4、新散批")
    private Integer companyType;

    @ApiModelProperty(value = "是否推荐 0、否 1、是")
    private Integer recommendFlag;

    @ApiModelProperty(value = "是否自营 0、否 1、是")
    private Integer selfManage;

    private Long marketId;

    // 签约开始时间
    private String applyTimeStart;
    // 签约结束时间
    private String applyTimeEnd;
}
