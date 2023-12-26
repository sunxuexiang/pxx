package com.wanmi.sbc.setting.api.response.banneradmin;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.BannerAdminVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>轮播管理分页结果</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerAdminPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 轮播管理分页结果
     */
    @ApiModelProperty(value = "轮播管理分页结果")
    private MicroServicePage<BannerAdminVO> bannerAdminVOPage;
}
