package com.wanmi.sbc.stockout;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @ClassName: CityCode
 * @Description: TODO
 * @Date: 2020/9/25 19:06
 * @Version: 1.0
 */
@ApiModel
@Data
public class CityCode {
    private String code;
    private String name;
    private String parentCode;
}
