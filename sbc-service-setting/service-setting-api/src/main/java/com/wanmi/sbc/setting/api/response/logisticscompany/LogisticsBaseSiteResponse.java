package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.wanmi.sbc.setting.bean.vo.LogisticsBaseSiteVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:44
*/
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsBaseSiteResponse implements Serializable{

    private LogisticsBaseSiteVO logisticsBaseSiteVO;
}
