package com.wanmi.sbc.goods.api.request.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @Author: songhanlin
 * @Date: Created In 10:17 2018-12-18
 * @Description: 商品分类excel导入请求Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsUpImportRequest implements Serializable {

    private static final long serialVersionUID = -1565355981812149930L;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "用户Id")
    private MultipartFile file;
}
