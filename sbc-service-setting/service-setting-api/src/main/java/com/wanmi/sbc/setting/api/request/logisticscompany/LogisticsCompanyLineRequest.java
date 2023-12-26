package com.wanmi.sbc.setting.api.request.logisticscompany;

import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyLineVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:43
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyLineRequest extends LogisticsCompanyLineVO implements Serializable {
    private List<Long> lineIdList;
}