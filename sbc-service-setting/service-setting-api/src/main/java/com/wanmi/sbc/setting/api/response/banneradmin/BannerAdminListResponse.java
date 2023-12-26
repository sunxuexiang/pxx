package com.wanmi.sbc.setting.api.response.banneradmin;

import com.wanmi.sbc.setting.bean.vo.BannerAdminVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>轮播管理列表结果</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerAdminListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 轮播管理列表结果
     */
    @ApiModelProperty(value = "轮播管理列表结果")
    private List<BannerAdminVO> bannerAdminVOList;
}
