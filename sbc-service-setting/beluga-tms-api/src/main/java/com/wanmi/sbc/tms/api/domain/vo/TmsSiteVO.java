package com.wanmi.sbc.tms.api.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 站点信息表
 * </p>
 *
 * @author xyy
 * @since 2023-09-16
 */
@Getter
@Setter
public class TmsSiteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //   ("站点ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long siteId;

    //   ("站点名称")
    private String siteName;

    //   ("联系人")
    private String contactPerson;

    //   ("联系电话")
    private String contactPhone;

    //   ("地址")
    private String address;

    //   ("省份代码")
    private Integer provinceCode;

    //   ("省份名称")
    private String provinceName;

    //   ("城市代码")
    private Integer cityCode;

    //   ("城市名称")
    private String cityName;

    //   ("区代码")
    private Integer districtCode;

    //   ("区名称")
    private String districtName;

    //   ("街道代码")
    private Integer streetCode;

    //   ("街道")
    private String street;

    //   ("纬度")
    private BigDecimal latitude;

    //   ("经度")
    private BigDecimal longitude;

    //   ("站点类型 (0: 承运商自提点, 1: 市场接货点)")
    private Integer siteType;

    //   ("承运商Id/市场Id")
    private String siteOwnerId;

    //   ("删除标志（0代表存在 1代表删除）")
    private Integer delFlag;


}
