package com.wanmi.sbc.returnorder.api.response.orderpicking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/12 14:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPickingResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String tradeId;

    private Integer status;

}
