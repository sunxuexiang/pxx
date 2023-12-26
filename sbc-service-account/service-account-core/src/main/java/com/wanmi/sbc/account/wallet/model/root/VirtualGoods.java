package com.wanmi.sbc.account.wallet.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "virtual_goods")
public class VirtualGoods implements Serializable {
    private static final long serialVersionUID = 958093196756295251L;

    @Id
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 商品名
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 面值
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * 赠送金额
     */
    @Column(name = "give_price")
    private BigDecimal givePrice;

    /**
     * 总面值
     */
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 删除标识 0:正常  1:删除
     */
    @Column(name = "del_flag")
   // @Enumerated
    private Integer delFlag = 0;

    /**
     * 删除时间
     */
    @Column(name = "del_time")
    private LocalDateTime delTime;

    /**
     * 0:是第一次赠送 1:否
     */
    @Column(name = "first_send_flag")
    private Integer firstSendFlag;

    /**
     * 赠送的优惠券id集合   中间用,隔开
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 赠送的商品
     */
    @Column(name = "give_goods")
    private String giveGoods;
}
