package com.wanmi.sbc.setting.api.response.storeresource;

import com.wanmi.sbc.setting.bean.vo.StoreResourceVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>店铺资源库列表结果</p>
 * @author lq
 * @date 2019-11-05 16:12:49
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResourceListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 店铺资源库列表结果
     */
    @ApiModelProperty(value = "店铺资源库列表结果")
    private List<StoreResourceVO> storeResourceVOList;
}
