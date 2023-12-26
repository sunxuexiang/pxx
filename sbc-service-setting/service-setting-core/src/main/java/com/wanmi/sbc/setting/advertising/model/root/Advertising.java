package com.wanmi.sbc.setting.advertising.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 首页广告位实体类
 * @author: XinJiang
 * @time: 2022/2/18 9:28
 */
@Data
@Entity
@Table(name = "home_page_advertising_space")
public class Advertising extends BaseEntity {

    private static final long serialVersionUID = 2100757983028274411L;

    /**
     * 首页广告位ID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "advertising_id")
    private String advertisingId;

    /**
     * 广告名称
     */
    @Column(name = "advertising_name")
    private String advertisingName;

    /**
     * 广告位类型：0-通栏推荐位，1-分栏推荐位，2-轮播推荐位
     */
    @Enumerated
    @Column(name = "advertising_type")
    private AdvertisingType advertisingType;

    /**
     * 是否删除标志 0：否，1：是
     */
    @Enumerated
    @Column(name = "del_flag")
    private DeleteFlag delFlag = DeleteFlag.NO;

    /**
     * 排序顺序
     */
    @Column(name = "sort_Num")
    private Integer sortNum;

    /**
     * 删除时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(name = "del_time")
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @Column(name = "del_person")
    private String delPerson;

    /**
     * 仓库id
     */
    @Column(name = "ware_id")
    private Long wareId;

    /**
     * 店铺id
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 广告位配置信息
     */
    @OneToMany
    @JoinColumn(name = "advertising_id", insertable = false, updatable = false)
    @JsonIgnore
    private List<AdvertisingConfig> advertisingConfigList;

}
