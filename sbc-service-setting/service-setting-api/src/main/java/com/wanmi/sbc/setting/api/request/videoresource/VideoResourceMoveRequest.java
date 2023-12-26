package com.wanmi.sbc.setting.api.request.videoresource;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量移动视频教程素材资源请求
 * @author hudong
 * @date 2023-06-26 10:01:22
 */
@Getter
@Setter
public class VideoResourceMoveRequest {
    /**
     * 素材分类编号
     */
    @NotNull
    private String cateId;

    /**
     * 批量素材资源ID
     */
    @NotEmpty
    private List<Long> resourceIds;

    /**
     * 店铺标识
     */
    private Long storeId;

}
