package com.wanmi.sbc.marketing.util.error;

/**
 * <p>营销异常码定义</p>
 * Created by of628-wenzhi on 2018-06-21-下午5:26.
 */
public final class MarketingErrorCode {
    private MarketingErrorCode() {
    }

    /**
     * 营销活动不存在
     */
    public final static String NOT_EXIST = "K-080001";

    /**
     * 营销活动无法删除
     */
    public final static String MARKETING_CANNOT_DELETE = "K-080002";

    /**
     * 营销活动无法暂停
     */
    public final static String MARKETING_CANNOT_PAUSE = "K-080003";

    /**
     * 营销商品活动时间冲突
     */
    public final static String MARKETING_GOODS_TIME_CONFLICT = "K-080004";

    /**
     * 营销满系金额错误
     */
    public final static String MARKETING_FULLAMOUNT_ERROR = "K-080005";

    /**
     * 营销满系数量错误
     */
    public final static String MARKETING_FULLCOUNT_ERROR = "K-080006";

    /**
     * 满折折扣错误
     */
    public final static String MARKETING_DISCOUNT_ERROR = "K-080007";

    /**
     * 减免金额大于条件金额
     */
    public final static String MARKETING_REDUCTION_AMOUNT_ERROR = "K-080008";

    /**
     * 最少选择1种赠品
     */
    public final static String MARKETING_GIFT_TYPE_MIN_1 = "K-080009";

    /**
     * 最多选择20种赠品
     */
    public final static String MARKETING_GIFT_TYPE_MAX_20 = "K-080010";

    /**
     * 多级促销条件金额不可相同
     */
    public final static String MARKETING_MULTI_LEVEL_AMOUNT_NOT_ALLOWED_SAME = "K-080011";

    /**
     * 多级促销条件件数不可相同
     */
    public final static String MARKETING_MULTI_LEVEL_COUNT_NOT_ALLOWED_SAME = "K-080012";

    /**
     * 赠品数量仅限1-999间的整数
     */
    public final static String MARKETING_GIFT_COUNT_BETWEEN_1_AND_999 = "K-080013";

    /**
     * 营销活动无法暂停
     */
    public final static String MARKETING_CANNOT_START = "K-080014";

    /**
     * 当前营销活动已开始或已结束，无法编辑
     */
    public final static String MARKETING_STARTED_OR_ENDED = "K-080015";

    /**
     * 没有权限查看目标营销活动
     */
    public final static String MARKETING_NO_AUTH_TO_VIEW = "K-080016";

    /**
     * 营销活动已暂停
     */
    public final static String MARKETING_SUSPENDED = "K-080017";

    /**
     * 营销活动已过期
     */
    public final static String MARKETING_OVERDUE = "K-080018";

    /**
     * 商品已存在于其它进行中拼团活动
     */
    public final static String GROUPON_GOODS_ALREADY_INUSE = "K-080019";

    /**
     * 选择的拼团商品已被下架或禁售
     */
    public final static String GROUPON_GOODS_ALREADY_DISABLED = "K-080020";

    /**
     * 选择的拼团活动分类已被删除
     */
    public final static String GROUPON_CATE_NOT_EXIST = "K-080021";

    /**
     * 无进行中营销活动
     */
    public final static String NOT_EXISTS = "K-080022";

    /**
     * 营销活动开始时间早于当前时间
     */
    public final static String MARKETING_CREATE_TIME_BEFORE_NOW = "K-080023";
    /**
     * 营销活动开始时间不能晚于结束时间
     */
    public final static String MARKETING_CREATE_TIME_AFTER_END_TIME = "K-080024";
    /**
     * 已结束或已终止的营销活动不能进行编辑
     */
    public final static String CANNOT_EDIT_WHEN_MARKETING_TERMINATED = "K-080025";
    /**
     * 当前活动未关联商品
     */
    public final static String MARKETING_NOT_RELATE_GOODS = "K-080026";
    /**
     * 单用户限购量或总限购量不能大于99999，请检查
     */
    public final static String PER_USER_BUY_OR_TOTAL_BUY_OVER_LIMIT = "K-080027";
    /**
     * 商品中的单用户的限购量不得大于总用户的限购量
     */
    public final static String PER_USER_BUY_THAN_TOTAL_BUY = "K-080028";
    /**
     * 营销类型与当前活动子类型不匹配
     */
    public final static String MARKETING_TYPE_NOT_MATCH_SUB_TYPE = "K-080029";

    /**
     * 营销活动未配置等级
     */
    public final static String MARKETING_NOT_HAVE_LEVEL = "K-080030";
    /**
     * 不能同时参加多级营销活动和营销活动叠加
     */
    public final static String MARKETING_LEVEL_AND_IS_OVERLAP_ONLY_CHOOSE_ONE = "K-080031";

    /**
     * 活动梯度最多配置5级
     */
    public final static String MARKETIN_LEVEL_THAN_FIVE = "K-080032";

    /**
     * 营销活动门槛不能设置为空
     */
    public final static String MARKETIN_LEVEL_THRESHOLD_IS_NULL = "K-080033";

    /**
     * 请检查营销活动门槛是否按照等级增加
     */
    public final static String MARKETIN_LEVEL_THRESHOLD_NOT_INCREMENT = "K-080034";

}
