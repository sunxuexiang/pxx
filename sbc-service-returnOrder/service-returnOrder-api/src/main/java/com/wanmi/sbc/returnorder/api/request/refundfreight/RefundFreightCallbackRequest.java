package com.wanmi.sbc.returnorder.api.request.refundfreight;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 14:31
 */
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundFreightCallbackRequest implements Serializable {

    private static final long serialVersionUID = 5507796037825799383L;

    private String rid;

    private Integer refundStatus;


}
