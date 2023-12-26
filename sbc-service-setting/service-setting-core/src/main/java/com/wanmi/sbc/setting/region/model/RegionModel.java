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
@Table(name = "region")
public class RegionModel implements Serializable {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 城市名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 上一级编号
     */
    @Column(name = "parent_id")
    private Long parentId;

}
