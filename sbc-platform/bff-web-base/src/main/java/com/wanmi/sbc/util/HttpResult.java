package com.wanmi.sbc.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author baijianzhong
 * @ClassName HttpResult
 * @Date 2020-06-04 20:08
 * @Description TODO
 **/
@Data
@AllArgsConstructor
public class HttpResult {

    /**
     * 返回的数据
     */
    private String resultData;

    /**
     * 请求返回的编码
     */
    private String resultCode;

}
