package com.wanmi.sbc.setting.api.response.banneradmin;

import com.wanmi.sbc.setting.bean.vo.BannerAdminVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）轮播管理信息response</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerAdminByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 轮播管理信息
     */
    @ApiModelProperty(value = "轮播管理信息")
    private BannerAdminVO bannerAdminVO;
}
