package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.returnorder.bean.dto.PushFailLogDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author lm
 * @date 2022/11/19 16:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushFailLogRequest implements Serializable {

    List<PushFailLogDTO> pushFailLogDTOList;

}
