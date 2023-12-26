package com.wanmi.sbc.setting.advertising.model.root;

import com.wanmi.sbc.setting.bean.enums.AdvertisingRetailJumpType;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 零售广告位配置信息
 * @author: XinJiang
 * @time: 2022/4/18 17:09
 */
@Data
@Entity
@Table(name = "advertising_retail_config")
public class AdvertisingRetailConfig implements Serializable {

    private static final long serialVersionUID = -2737437293247757315L;

    /**
     * 配置id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "advertising_config_id")
    private String advertisingConfigId;

    /**
     * 散批广告位id
     */
    @Column(name = "advertising_id")
    private String advertisingId;

    /**
     * 广告位图片
     */
    @Column(name = "advertising_image_url")
    private String advertisingImageUrl;

    /**
     * 分栏顶部banner图标
     */
    @Column(name = "columns_banner_image_url")
    private String columnsBannerImageUrl;

    /**
     * 跳转类型
     */
    @Enumerated
    @Column(name = "jump_type")
    private AdvertisingRetailJumpType jumpType;

    /**
     * 跳转编码 分类id/商品erp编码
     */
    @Column(name = "jump_code")
    private String jumpCode;

    /**
     * 商品/分类名称
     */
    @Column(name = "jump_name")
    private String jumpName;

    /**
     * 分栏商品配置信息
     */
    @OneToMany
    @JoinColumn(name = "advertising_config_id", insertable = false, updatable = false)
    @JsonIgnore
    private List<AdvertisingRetailGoodsConfig> advertisingRetailGoodsConfigs;

    public List<AdvertisingRetailGoodsConfig> generateAdvertisingRetailGoodsConfig(String addAdvertisingConfigId){
        advertisingRetailGoodsConfigs.forEach(goodsConfig -> {
            goodsConfig.setAdvertisingConfigId(addAdvertisingConfigId);
            goodsConfig.setId(null);
        });
        return advertisingRetailGoodsConfigs;
    }
}
