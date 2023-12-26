package com.wanmi.sbc.shopcart.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wanmi.sbc.common.enums.DefaultFlag;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class RetailShopCartDTO implements Serializable {


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



    private BigDecimal goodsBNum;

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
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)

    private LocalDateTime createTime;

    /**
     * 根据有效商品排序
     */

    private Integer validSort;


    /**
     * 装箱id
     */

    private Long devanningId;


    /**
     * 仓库ID
     */

    private Long wareId;

    /**
     * 父级SKUID
     */

    private String parentGoodsInfoId;

    /**
     * 非数据库字段删除标识
     */
    private Boolean isDelFlag =false;
}


