package com.wanmi.sbc.returnorder.api.request.trade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/1 10:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeCoinReturnVerifyRequest implements Serializable {
    private static final long serialVersionUID = 4259502124886978191L;

    private String tid;

    private List<String> goodsInfoIds;
}
