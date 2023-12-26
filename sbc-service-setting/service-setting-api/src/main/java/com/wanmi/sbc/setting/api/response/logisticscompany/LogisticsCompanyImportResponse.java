package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>物流公司修改结果</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyImportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的物流公司信息
     */
    private DefaultFlag successFlag;

    private List<String> logisticsNames;

    private List<String> logisticsCompanyNumber;

    private Long maxId;
}
