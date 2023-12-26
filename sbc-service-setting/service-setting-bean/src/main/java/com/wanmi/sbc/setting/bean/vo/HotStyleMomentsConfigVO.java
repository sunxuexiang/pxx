package com.wanmi.sbc.setting.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.setting.bean.enums.HotStyleMomentsStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 爆款时刻商品配置信息VO实体类
 * @author: XinJiang
 * @time: 2022/5/9 18:19
 */
@Data
@ApiModel
public class HotStyleMomentsConfigVO implements Serializable {

    private static final long serialVersionUID = 200681439997053632L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 爆款时刻主键id
     */
    @ApiModelProperty(value = "爆款时刻主键id")
    private Long hotId;

    /**
     * 商品skuId
     */
    @ApiModelProperty(value = "商品skuId")
    private String goodsInfoId;

    /**
     * 排序顺序
     */
    @ApiModelProperty(value = "排序顺序")
    private Integer sortNum;

    /**
     * 终止标志位：1：终止，0非终止
     */
    @ApiModelProperty(value = "终止标志位：1：终止，0非终止")
    private DefaultFlag terminationFlag;

    /**
     * 1：进行中，2：暂停中，3：未开始，4：已结束，6：终止
     */
    @ApiModelProperty(value = "1：进行中，2：暂停中，3：未开始，4：已结束，6：终止")
    private HotStyleMomentsStatus status;
}
