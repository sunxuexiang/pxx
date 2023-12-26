package com.wanmi.sbc.returnorder.shopcart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 采购单实体
 * Created by sunkun on 2017/11/27.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RetailShopCartVo {
    /**
     * 采购单编号
     */

    private Long cartId;

    /**
     * 客户编号
     */

    private String customerId;

    /**
     * 默认为0、店铺精选时对应邀请人id-会员id
     */
    String inviteeId;

    /**
     * 商品编号
     */
    private String goodsId;

    /**
     * SKU编号
     */
    private String goodsInfoId;

    /**
     * 全局购买数
     */
    private Long goodsNum;

    /**
     * 公司信息ID
     */
    private Long companyInfoId;

    /**
     * 商品是否选中
     */
    @JsonIgnore
    private DefaultFlag isCheck;

    /**
     * 采购创建时间
     */
    @JsonIgnore
    private LocalDateTime createTime;

    /**
     * 根据有效商品排序
     */

    private Integer validSort;


    /**
     * 非数据库字段删除标识
     */
    private Boolean isDelFlag =false;
}

