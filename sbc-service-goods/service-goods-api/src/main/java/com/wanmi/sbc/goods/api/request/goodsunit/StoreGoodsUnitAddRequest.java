package com.wanmi.sbc.goods.api.request.goodsunit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 新增单位属性请求对象
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class StoreGoodsUnitAddRequest implements Serializable {

    private static final long serialVersionUID = -1722241951475976469L;
    /**
     * 单位属性，采用UUID
     */
    @ApiModelProperty(value = "单位编号，采用UUID")
    private String storeGoodsUnitId;
    /**
     * 单位属性
     */
    @ApiModelProperty(value = "单位编号，采用UUID")
    private String unit;
    /**
     * 删除标识,0: 未删除 1: 已删除
     */
    @ApiModelProperty(value = "删除标识,0: 未删除 1: 已删除")
    private Integer delFlag;
    /**
     * 是否启用 0：停用，1：启用
     */
    @ApiModelProperty(value = "是否启用 0：停用，1：启用")
    private Integer status;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "修改人")
    private String updatePerson;

    @ApiModelProperty(value = "商户Id")
    private Long companyInfoId;

    private Boolean supplierUpdate;

}
