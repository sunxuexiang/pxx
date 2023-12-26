package com.wanmi.sbc.setting.region.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "region_copy")
public class RegionCopy implements Serializable {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    /**
     * 编码
     */
    @Column(name = "code")
    private Long code;

    /**
     * 城市名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 上一级编号
     */
    @Column(name = "parent_code")
    private Long parentCode;

    /**
     * 等级
     */
    @Column(name = "level")
    private Integer level;

}
