package com.wanmi.sbc.wms.constant;

/**
 * @ClassName: WMSResponseCode
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 20:44
 * @Version: 1.0
 */
public abstract class WMSResponseCode {

    public static final String SUCCESS="0000";//成功

    public static final String FAIL_DATA_CHECK_STR="S000";//数据校验异常

    public static final String FAIL_SIGN_STR="S001";//验签错误

    public static final String FAIL_SYSTEM_STR="S002";//系统异常

    public static final String FAIL_DATA_STR="S003";//数据错误

    public static final String FAIL_GOODS_MASTER_STR="S004";//货主错误

    public static final String FAIL_UNUPDATE="S006";//记录已存在且不允许修改

    public static final String FAIL_OTHER="S007";//其他错误
}
