package com.wanmi.sbc.setting.advertising.model.root;

import com.wanmi.sbc.setting.bean.enums.AdvertisingConfigType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @description: 首页广告位配置实体类
 * @author: XinJiang
 * @time: 2022/2/18 9:44
 */
@Data
@Entity
@Table(name = "home_page_advertising_space_config")
public class AdvertisingConfig implements Serializable {

    private static final long serialVersionUID = 6863284246030924193L;

    /**
     * 配置id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "advertising_config_id")
    private String advertisingConfigId;

    /**
     * 首页广告位id
     */
    @Column(name = "advertising_id")
    private String advertisingId;

    /**
     * 广告位图片
     */
    @Column(name = "advertising_iamge")
    private String advertisingImage;

    /**
     * 跳转链接
     */
    @Column(name = "jump_link")
    private String jumpLink;

    /**
     * 魔方海报页名称
     */
    @Column(name = "mofang_advertising_name")
    private String moFangAdvertisingName;

    /**
     * 魔方海报页编码
     */
    @Column(name = "mofang_page_code")
    private String moFangPageCode;

    /**
     * 是否套装广告位：0-否，1-是
     */
    @Enumerated
    @Column(name = "is_suit")
    private AdvertisingConfigType isSuit = AdvertisingConfigType.POSTER;


    /**
     * 定义新入住商家日期类型（1天，2月）
     */
    @Column(name = "date_type")
    private Integer dateType;

    /**
     * 定义新入住商家日期天、月数
     */
    @Column(name = "date_num")
    private Integer dateNum;

    /**
     * 活动标题
     */
    @Column(name = "activity_title")
    private String activityTitle;

    /**
     * 记录主表相关联的首页广告位Ids
     */
    @Column(name = "advertising_ids")
    private String advertisingIds;

}
