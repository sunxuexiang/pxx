package com.wanmi.sbc.setting.api.constant;

/**
 * <p>模块异常码定义</p>
 * Created by of628-wenzhi on 2018-06-21-下午3:10.
 */
public final class SettingErrorCode {
    private SettingErrorCode() {
    }

    /**
     * 自营商品审核开关，不能在商品审核开关为关闭的状态下打开
     */
    public static final String AUDIT_GOODS_CLOSED = "K-091001";

    /**
     * 客户审核开关，不能在客户信息完善开关为关闭的状态下打开
     */
    public static final String CUSTOMER_INFO_CLOSED = "K-091002";

    /**
     * 暂无权限访问
     */
    public final static String ACCESS_DENIED = "K-040023";

    /**
     * 敏感词每次添加个数上限
     */
    public final static String SENSITIVE_WORDS_COUNT = "K-091301";

    /**
     * 敏感词字符数限制
     */
    public final static String SENSITIVE_WORDS_LENGTH = "K-091302";


    /**
     * 参数：无效，格式不对、非法值、越界等
     */
    public static final String INVALID_PARAMETER = "invalid-parameter";

    /**
     * 系统未知错误
     */
    public static final String SYSTEM_UNKNOWN_ERROR = "system-unknow-error";

    /**
     * appKey错误
     */
    public static final String APP_KEY_ERROR = "app-key-error";

    /**
     * appKey存在多条数据错误
     */
    public static final String APP_KEY_MULTI_ERROR = "app-key-multi-error";

    /**
     * 常用物流公司数量超限错误
     */
    public static final String EXPRESS_MAX_COUNT_ERROR = "express-company-max-count-error";

    /**
     * 图片分类名称重复
     */
    public static final String IMG_CATE_NAME_EXIST_ERROR = "img-cate-name-exist-error";

    /**
     * 素材分类名称重复
     */
    public static final String RESOURCE_CATE_NAME_EXIST_ERROR = "resource-cate-name-exist-error";

    /**
     * 图片子分类数量超限20错误
     */
    public static final String IMG_CHILD_CATE_MAX_COUNT_ERROR = "img-child-cate-max-count-error";

    /**
     * 素材子分类数量超限20错误
     */
    public static final String RESOURCE_CHILD_CATE_MAX_COUNT_ERROR = "resource-child-cate-max-count-error";

    /**
     * 图片父分类不存在错误
     */
    public static final String PARENT_IMG_CATE_NOT_EXIST_ERROR = "parent-img-cate-not-exist-error";

    /**
     * 素材父分类不存在错误
     */
    public static final String PARENT_RESOURCE_CATE_NOT_EXIST_ERROR = "parent-resource-cate-not-exist-error";

    /**
     * 素材父分类层级不能大于4
     */
    public static final String PARENT_RESOURCE_CATE_GRADE_ERROR = "K-686854";

    public static final String PARENT_RESOURCE_CATE_GRADE_ERROR_MSG = "素材分类层级不能大于4";

    /**
     * 素材子分类层级不能为空
     */
    public static final String PARENT_RESOURCE_CATE_SUB_GRADE_ERROR = "K-686855";

    public static final String PARENT_RESOURCE_CATE_SUB_GRADE_ERROR_MSG = "新增素材子分类时,层级不能为空";

    /**
     * 默认图片分类不存在错误
     */
    public static final String DEFAULT_IMG_CATE_NOT_EXIST_ERROR = "default-img-cate-not-exist-error";

    /**
     * 默认素材分类不存在错误
     */
    public static final String DEFAULT_RESOURCE_CATE_NOT_EXIST_ERROR = "default-resource-cate-not-exist-error";

    /**
     * 图片服务器未配置
     */
    public static final String IMG_SERVER_NOT_CONFIGURED = "img-server-not-configured-error";

    /**
     * 素材服务器未配置
     */
    public static final String RESOURCE_SERVER_NOT_CONFIGURED = "resource-server-not-configured-error";

    /**
     * 阿里云上传图片失败，请联系管理员
     */
    public static final String ALIYUN_IMG_UPLOAD_ERROR = "aliyun-img-upload-error";

    /**
     * 阿里云上传素材失败，请联系管理员
     */
    public static final String ALIYUN_RESOURCE_UPLOAD_ERROR = "aliyun-resource-upload-error";

    /**
     * 阿里云OSS连接不可用
     */
    public static final String ALIYUN_OSS_SERVICE_ERROR = "aliyun-oss-service-error";

    /**
     * 上传文件失败
     */
    public static final String UPLOAD_FILE_ERROR = "upload-file-error";

    /**
     * 阿里云删除图片失败，请联系管理员
     */
    public static final String  ALIYUN_DELETE_IMG_ERROR = "aliyun-delete-img-error";

    /**
     * 阿里云删除素材失败，请联系管理员
     */
    public static final String  ALIYUN_DELETE_RESOURCE_ERROR = "aliyun-delete-resource-error";

    /**
     * 图片不存在错误
     */
    public static final String IMG_NOT_EXIST_ERROR = "img-not-exist-error";

    /**
     * 素材不存在错误
     */
    public static final String RESOURCE_NOT_EXIST_ERROR = "resource-not-exist-error";

    /**
     * 非法请求错误
     */
    public static final String ILLEGAL_REQUEST_ERROR = "illegal-request-error";

    /**
     * 默认物流公司数据源格式错误
     */
    public static final String EXPRESS_FORMAT_ERROR = "default-express-company-format-error";

    /**
     * 每个店铺最多使用20个物流公司
     */
    public static final String STORE_EXPRESS_LIMIT = "store_express_limit";

    /**
     * 物流公司不存在错误
     */
    public static final String EXPRESS_NOT_EXIST_ERROR = "express-company-not-exist-error";

    /**
     * 在线客服不存在
     */
    public static final String ONLINE_SERVER_NOT_EXIST_ERROR = "online-server-not-exist-error";

    /**
     * 客服账号已存在
     */
    public static final String ONLINE_SERVER_ACCOUNT_ALREADY_EXIST = "online-server-account-already-exist";

    /**
     * 最多添加10条座席记录
     */
    public static final String ONLINE_SERVER_MAX_ERROR = "online-server-max-error";

    public static final String FILE_MAX_SIZE = "K-030403";

    public final static String EMPTY_ERROR = "K-030405";
}
