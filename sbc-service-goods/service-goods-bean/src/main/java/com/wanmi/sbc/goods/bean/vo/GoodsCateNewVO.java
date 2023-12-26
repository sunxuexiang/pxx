package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class GoodsCateNewVO implements Serializable {
    private static final long serialVersionUID = 5839945592017742493L;

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String cateName;

    /**
     * 父类编号
     */
    @ApiModelProperty(value = "父类编号")
    private Long cateParentId;

    /**
     * 分类图片
     */
    @ApiModelProperty(value = "分类图片")
    private String cateImg;

    /**
     * 分类路径
     */
    @ApiModelProperty(value = "分类路径")
    private String catePath;

    /**
     * 分类层次
     */
    @ApiModelProperty(value = "分类层次")
    private Integer cateGrade;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 一对多关系，子分类
     */
    @ApiModelProperty(value = "一对多关系，子分类")
    private List<GoodsCateNewVO> goodsCateList;
}
