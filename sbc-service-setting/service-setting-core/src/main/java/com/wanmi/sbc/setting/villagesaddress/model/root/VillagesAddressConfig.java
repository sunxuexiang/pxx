package com.wanmi.sbc.setting.villagesaddress.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: TODO
 * @author: XinJiang
 * @time: 2022/4/29 9:44
 */
@Data
@Entity
@Table(name = "villages_address_config")
public class VillagesAddressConfig implements Serializable {

    private static final long serialVersionUID = -6540342617289273769L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 省
     */
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 市
     */
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 区
     */
    @Column(name = "area_id")
    private Long areaId;

    /**
     * 街道
     */
    @Column(name = "village_id")
    private Long villageId;

    /**
     * 省名
     */
    @Column(name = "province_name")
    private String provinceName;

    /**
     * 市名
     */
    @Column(name = "city_name")
    private String cityName;

    /**
     * 区名
     */
    @Column(name = "area_name")
    private String areaName;

    /**
     * 街道名
     */
    @Column(name = "village_name")
    private String villageName;

    /**
     * 省市区中文地址
     */
    @Column(name = "detail_address")
    private String detailAddress;

    /**
     * 创建时间
     */
    @CreatedDate
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 店铺标识
     */
    @Column(name="store_id")
    private Long storeId;

    /**
     * 删除标志
     */
    @Column(name = "del_flag")
    @Enumerated
    @Access(value=AccessType.PROPERTY)
    private DeleteFlag delFlag;

    public DeleteFlag getDelFlag() {
        if(this.delFlag==null){
            this.delFlag=DeleteFlag.NO;
        }
        return delFlag;
    }
}
