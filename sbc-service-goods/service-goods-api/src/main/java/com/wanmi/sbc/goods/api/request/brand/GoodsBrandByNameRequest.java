package com.wanmi.sbc.goods.api.request.brand;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 根据品牌名称获取数据
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsBrandByNameRequest implements Serializable {

    private static final long serialVersionUID = 4390819159191294564L;

    /**
     * 品牌编号
     */
    @ApiModelProperty(value = "品牌编号")
    @NotBlank
    private String brandName;
}
