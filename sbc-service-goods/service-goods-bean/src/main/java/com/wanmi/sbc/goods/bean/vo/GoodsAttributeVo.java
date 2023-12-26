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
import java.util.List;


/**
 * 商品属性实体类
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class GoodsAttributeVo implements Serializable {
    /**
     * 编号
     */
    @ApiModelProperty(value = "编号")
    private String attributeId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String attribute;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private List<String> attributes;

    /**
     * 状态 0 启用 1 禁用
     */
    @ApiModelProperty(value = "状态")
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
