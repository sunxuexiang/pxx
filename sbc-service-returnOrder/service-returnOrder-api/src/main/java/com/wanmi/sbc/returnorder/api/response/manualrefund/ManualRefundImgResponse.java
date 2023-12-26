package com.wanmi.sbc.returnorder.api.response.manualrefund;

import com.wanmi.sbc.returnorder.bean.vo.ManualRefundImgVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualRefundImgResponse implements Serializable {

    private List<ManualRefundImgVO> manualRefundImgVOList;
}
