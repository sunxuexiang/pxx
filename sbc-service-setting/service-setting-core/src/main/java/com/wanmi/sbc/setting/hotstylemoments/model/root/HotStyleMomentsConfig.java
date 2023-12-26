package com.wanmi.sbc.setting.hotstylemoments.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @description: 爆款时刻商品配置信息实体类
 * @author: XinJiang
 * @time: 2022/5/9 17:47
 */
@Data
@Entity
@Table(name = "hot_style_moments_config")
public class HotStyleMomentsConfig implements Serializable {

    private static final long serialVersionUID = -8569650615855550995L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * 爆款时刻主键id
     */
    @Column(name = "hot_id")
    private Long hotId;

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

    /**
     * 终止标志位：1：终止，0非终止
     */
    @Column(name = "termination_flag")
    @Enumerated
    private DefaultFlag terminationFlag = DefaultFlag.NO;
}
