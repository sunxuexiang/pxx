package com.wanmi.sbc.setting.api.request.videomanagement;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.bean.enums.StateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/20
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoFollowQueryRequest extends BaseQueryRequest {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String customerId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String followCustomerId;

    /**
     * 上传用户id
     */
    @ApiModelProperty(value = "上传用户id")
    private String coverFollowCustomerId;
    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识,0:未删除1:已删除")
    private DeleteFlag delFlag;

    /**
     * 状态0:上架,1:下架
     */
    @ApiModelProperty(value = "状态0:上架,1:下架")
    private StateType state;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;
}
