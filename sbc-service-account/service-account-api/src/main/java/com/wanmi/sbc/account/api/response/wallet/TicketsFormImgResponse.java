package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.account.bean.vo.TicketsFormImgVO;
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
public class TicketsFormImgResponse implements Serializable {

    private List<TicketsFormImgVO> ticketsFormImgVOList;
}
