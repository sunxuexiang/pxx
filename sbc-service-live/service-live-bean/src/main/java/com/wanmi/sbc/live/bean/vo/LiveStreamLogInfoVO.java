package com.wanmi.sbc.live.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>直播记录商品优惠劵VO</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveStreamLogInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播记录id
     */
    @ApiModelProperty(value = "直播记录id")
    private Integer liveId;
    /**
     * 商品直播记录
     */
    @ApiModelProperty(value = "商品直播记录")
    private String goodsInfoIds;

    /**
     * 优惠劵活动记录
     */
    @ApiModelProperty(value = "优惠劵活动记录")
    private String activityIds;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
