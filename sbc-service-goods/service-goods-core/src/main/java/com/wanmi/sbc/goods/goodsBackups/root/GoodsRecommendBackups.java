package com.wanmi.sbc.goods.goodsBackups.root;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 商品推荐备份
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Entity
@Table(name = "goods_recommend_backups")
public class GoodsRecommendBackups implements Serializable {
    /**
     * 商品编号，采用UUID
     * customer_goods_id
     * customer_id
     * sku_id
     * company_id
     * count
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "customer_goods_id")
    private String customerGoodsId;

    /**
     * 用户
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * sku
     */
    @Column(name = "sku_id")
    private String skuId;
    /**
     * 公司
     */
    @Column(name = "company_id")
    private String companyId;
    /**
     * 数量
     */
    @Column(name = "count")
    private String count;
    /**
     * 数量
     */
    @Column(name = "create_time")
    private String createTime;
}
