package com.wanmi.sbc.setting.api.request.logisticscompany;

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
 * @author minchen
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyImportExcelRequest implements Serializable {

    private static final long serialVersionUID = -9118397028139700890L;


    private String ext;
    /**
     * 操作员id
     */
    private String userId;

    private List<LogisticsCompanyVO> logisticsCompanyVOS;

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

    /**
     * 物流类型
     */
    @ApiModelProperty("物流类型")
    private Integer logisticsType;

    /**
     * 市场Id
     */
    @ApiModelProperty(value = "市场Id")
    private Long marketId;
}
