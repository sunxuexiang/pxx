package com.wanmi.sbc.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 请求
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class HttpCommonResult {
    /**
     * 返回的数据
     */
    private String resultData;

    /**
     * 请求返回的编码
     */
    private String resultCode;

//    /**
//     * 返回信息
//     */
//    private String resultMsg;
}
