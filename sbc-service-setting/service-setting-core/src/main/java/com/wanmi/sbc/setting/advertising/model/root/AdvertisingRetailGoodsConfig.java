package com.wanmi.sbc.setting.advertising.model.root;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @description: 散批广告位分栏商品配置信息实体类
 * @author: XinJiang
 * @time: 2022/4/20 15:35
 */
@Data
@Entity
@Table(name = "advertising_retail_goods_config")
public class AdvertisingRetailGoodsConfig implements Serializable {

    private static final long serialVersionUID = 9191669343896531529L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 散批广告位id
     */
    @Column(name = "advertising_config_id")
    private String advertisingConfigId;

    /**
     * 商品skuId
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 排序顺序
     */
    @Column(name = "sort_num")
    private Integer sortNum;
}
