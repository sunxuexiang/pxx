package com.wanmi.sbc.wms.requestwms.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: Inventory
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 16:45
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory implements Serializable {

    private static final long serialVersionUID = -4231944887669907672L;

    /**
     * 仓库 ID
     */
    @NotBlank
    @JSONField(name = "WarehouseID")
    private String WarehouseID;

    /**
     * 货主 ID
     */
    @NotBlank
    @JSONField(name = "CustomerID")
    private String CustomerID;

    /**
     * SKU 编码
     */
    @JSONField(name = "SKU")
    private String SKU;

    /**
     * SKU 编码
     */
    @JSONField(name = "SKUS")
    private String skus;

    /**
     * 特价仓的Id
     */
    @JSONField(name = "Lotatt04")
    private String Lotatt04;

    /**
     * 库存日期
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JSONField(name = "INVDate")
    private LocalDateTime INVDate;

    /**
     * 每页记录数
     */
    @JSONField(name = "pageSize")
    private Integer pageSize;

    /**
     * 页数
     */
    @JSONField(name = "pageNo")
    private Integer pageNo;
}
