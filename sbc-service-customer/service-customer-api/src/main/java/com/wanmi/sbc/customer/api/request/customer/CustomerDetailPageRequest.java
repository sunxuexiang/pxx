package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 会员详情查询参数
 * Created by CHENLI on 2017/4/19.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = -1281379836937760934L;
    /**
     * 会员详细信息标识UUID
     */
    @ApiModelProperty(value = "会员详细信息标识UUID")
    private String customerDetailId;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 账户
     */
    @ApiModelProperty(value = "账户")
    private String customerAccount;

    /**
     * 客户IDs
     */
    @ApiModelProperty(value = "客户IDs")
    private List<String> customerIds;

    /**
     * 客户等级ID
     */
    @ApiModelProperty(value = "客户等级ID")
    private Long customerLevelId;

    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String customerName;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private Long areaId;

    /**
     * 账号状态 0：启用中  1：禁用中
     */
    @ApiModelProperty(value = "账号状态")
    private CustomerStatus customerStatus;

    /**
     * 删除标志 0未删除 1已删除
     */
    @ApiModelProperty(value = "删除标志", dataType = "com.wanmi.sbc.common.enums.DeleteFlag")
    private Integer delFlag;

    /**
     * 审核状态 0：待审核 1：已审核 2：审核未通过
     */
    @ApiModelProperty(value = "审核状态", dataType = "com.wanmi.sbc.customer.bean.enums.CheckState")
    private Integer checkState;

    /**
     * 负责业务员
     */
    @ApiModelProperty(value = "负责业务员")
    private String employeeId;

    /**
     * 负责业务员集合
     */
    @ApiModelProperty(value = "负责业务员集合")
    private List<String> employeeIds;

    /**
     * 白鲸管家
     */
    @ApiModelProperty(value = "白鲸管家")
    private String managerId;

    /**
     * 白鲸管家集合
     */
    @ApiModelProperty(value = "白鲸管家集合")
    private List<String> managerIds;

    /**
     * 精确查询-账户
     */
    @ApiModelProperty(value = "精确查询-账户")
    private String equalCustomerAccount;

    /**
     * 精确查找-商家下的客户
     */
    @ApiModelProperty(value = "精确查找-商家下的客户")
    private Long companyInfoId;

    /**
     * 禁用原因
     */
    @ApiModelProperty(value = "禁用原因")
    private String forbidReason;

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型")
    private CustomerType customerType;

    /**
     * 关键字搜索，目前范围：会员名称、客户账户
     */
    @ApiModelProperty(value = "关键字搜索，目前范围：会员名称、客户账户")
    private String keyword;

    /**
     * 可用积分段查询开始
     */
    @ApiModelProperty(value = "可用积分段查询开始")
    private Long pointsAvailableBegin;

    /**
     * 是否为分销员
     */
    @ApiModelProperty(value = "是否为分销员")
    private DefaultFlag isDistributor;

    /**
     * 可用积分段查询结束
     */
    @ApiModelProperty(value = "可用积分段查询结束")
    private Long pointsAvailableEnd;

    /**
     * 是否是我的客户（S2b-Supplier使用）
     */
    @ApiModelProperty(value = "是否是我的客户（S2b-Supplier使用")
    private Boolean isMyCustomer;

    /**
     * 会员注册类型
     */
    @ApiModelProperty(value = "会员注册类型")
    private CustomerRegisterType customerRegisterType;


    /**
     * 是否标星 0否，1是
     */
    @ApiModelProperty(value = "是否标星 0否，1是")
    private DefaultFlag beaconStar;

    /*
     * 是否返现 0否 1是
     */
    @ApiModelProperty(value = "是否返现 0否 1是")
    private Integer cashbackFlag;
    /**
     * 是否为直播账号 0：否 1：是
     */
    @ApiModelProperty(value = "是否为直播账号 0：否 1：是")
    private Integer isLive;

    @ApiModelProperty(value = "注册开始时间")
    private String beginTime;

    @ApiModelProperty(value = "注册结束时间")
    private String endTime;
}
