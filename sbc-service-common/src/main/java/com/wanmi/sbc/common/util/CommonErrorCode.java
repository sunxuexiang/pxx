package com.wanmi.sbc.common.util;

/**
 * <p>公共异常码定义</p>
 * Created by of628-wenzhi on 2018-06-21-下午2:58.
 */
public final class CommonErrorCode {
    /**
     * 指定异常，不走国际化，异常信息由B2bRuntimeException字段result设定
     */
    public final static String
            SPECIFIED = "K-999999";


    /**
     * 针对我们的业务权限
     */
    public final static String METHOD_NOT_ALLOWED = "K-999998";


    /**
     * 重复提交
     */
    public final static String REPEAT_REQUEST = "K-999997";

    /**
     * 重复提交
     */
    public final static String INCLUDE_BAD_WORD = "K-999996";

    /**
     * 操作成功
     */
    public final static String SUCCESSFUL = "K-000000";

    /**
     * 操作失败
     */
    public final static String FAILED = "K-000001";

    /**
     * 大于指定数量
     */
    public final static String MaxCount = "K-091003";

    /**
     * 账号已被禁用
     */
    public final static String EMPLOYEE_DISABLE = "K-000005";

    /**
     * 参数错误
     */
    public final static String PARAMETER_ERROR = "K-000009";

    /**
     * 数据已存在
     */
    public final static String DATA_EXIST = "K-000088";

    /**
     * 验证码错误
     */
    public final static String VERIFICATION_CODE_ERROR = "K-000010";

    /**
     * 上传文件失败
     */
    public final static String UPLOAD_FILE_ERROR = "K-000011";

    /**
     * 发送失败
     */
    public final static String SEND_FAILURE = "K-000012";

    /**
     * 您没有权限访问
     */
    public final static String PERMISSION_DENIED = "K-000014";


    /**
     * 非法字符
     */
    public final static String ILLEGAL_CHARACTER = "K-000017";


    /**
     * 小程序被禁用，请检查设置
     */
    public final static String WEAPP_FORBIDDEN = "K-000018";


    /**
     * 阿里云连接异常
     */
    public final static String ALIYUN_CONNECT_ERROR = "K-090702";

    /**
     * 阿里云上传图片失败
     */
    public final static String ALIYUN_IMG_UPLOAD_ERROR = "K-090703";

    private CommonErrorCode() {
    }

    /**
     * 系统未知错误
     */
    public static final  String SYSTEM_UNKNOWN_ERROR = "system-unknow-error";


    /**
     * 常用物流公司数量超限错误
     */
    public static final String EXPRESS_MAX_COUNT_ERROR = "K-090901";

    /**
     * 状态值已发生改变
     */
    public static final String STATUS_HAS_BEEN_CHANGED_ERROR = "K-090601";

    public static final String ORDER_EXIT_LOGISTICS_COMPANY = "K-050319";
}
