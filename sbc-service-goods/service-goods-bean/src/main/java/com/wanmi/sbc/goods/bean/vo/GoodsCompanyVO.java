package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * 商品厂商实体类
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
public class GoodsCompanyVO implements Serializable {

    private static final long serialVersionUID = -3369219732567679167L;

    @ApiModelProperty(value = "厂商编号")
    private Long companyId;

    @ApiModelProperty(value = "厂商名称")
    private String companyName;

    @ApiModelProperty(value = "联系方式")
    private String contactPhone;

    @ApiModelProperty(value = "联系地址")
    private String contactAddress;

    @ApiModelProperty(value = "品牌id")
    private String brandIds;

    @ApiModelProperty(value = "品牌名称")
    private String brandNames;

    @ApiModelProperty(value = "状态 0 启用 1 禁用")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志", notes = "0: 否, 1: 是")
    private DeleteFlag delFlag;

}
