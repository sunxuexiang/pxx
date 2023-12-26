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
 * <p>轮播管理新增结果</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerAdminAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的轮播管理信息
     */
    @ApiModelProperty(value = "已新增的轮播管理信息")
    private BannerAdminVO bannerAdminVO;
}
