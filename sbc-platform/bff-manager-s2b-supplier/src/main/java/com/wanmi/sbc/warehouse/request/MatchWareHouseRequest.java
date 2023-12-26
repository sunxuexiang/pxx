package com.wanmi.sbc.warehouse.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author baijianzhong
 * @ClassName MatchWareHouseRequest
 * @Date 2020-06-01 16:12
 * @Description TODO 根据市code匹配分仓
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchWareHouseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 市code
     */
    @NotNull
    private Long cityCode;

}
