package com.wanmi.sbc.pay.api.response;

import com.wanmi.sbc.pay.bean.vo.PayWithDrawVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author lm
 * @date 2022/10/21 9:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayWithDrawResponse implements Serializable {

    private PayWithDrawVO payWithDrawVO;
}
