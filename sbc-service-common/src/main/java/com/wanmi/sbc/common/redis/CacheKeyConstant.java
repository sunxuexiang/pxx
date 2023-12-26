package com.wanmi.sbc.common.redis;

/**
 * 缓存的key常量
 * <p>
 * 特别注意 该接口下 不放放与redis  不相干的属性 并且 变量的名称务必和变量的值 一样 以便启动的时候 删除redis中缓存的key
 *
 * @author djk
 */
public interface CacheKeyConstant {
    /**
     * 商品SKU库存的key
     */
    String SKU_STOCK_KEY = "SKU_STOCK_KEY";

    /**
     * 短信验证码的key 注册
     */
    String VERIFY_CODE_KEY = "YZM_SMS_KEY";

    /**
     * 短信验证码的key 忘记密码
     */
    String YZM_FORGET_PWD_KEY = "YZM_FORGET_PWD_KEY";

    /**
     * 短信验证码的key 修改密码
     */
    String YZM_UPDATE_PWD_KEY = "YZM_UPDATE_PWD_KEY";

    /**
     * 短信验证码的key 修改绑定手机号OLD
     */
    String YZM_MOBILE_OLD_KEY = "YZM_MOBILE_OLD_KEY";

    /**
     * 短信验证码的key 修改绑定手机号OLD 最后一步保存时用
     */
    String YZM_MOBILE_OLD_KEY_AGAIN = "YZM_MOBILE_OLD_KEY_AGAIN";

    /**
     * 短信验证码的key 修改绑定手机号NEW
     */
    String YZM_MOBILE_NEW_KEY = "YZM_MOBILE_NEW_KEY";

    /**
     * 商品分类缓存的key
     */
    String GOODS_CATE_KEY = "GOODS_CATE_KEY";
    /**
     * 商品分类缓存的key
     */
    String STORE_GOODS_CATE_KEY = "STORE_GOODS_CATE_KEY";

    /**
     * 商品分类推荐缓存的key
     */
    String GOODS_CATE_RECOMMEND = "GOODS_CATE_RECOMMEND";

    /**
     * 散批商品分类推荐缓存的key
     */
    String RETAIL_GOODS_CATE_RECOMMEND = "RETAIL_GOODS_CATE_RECOMMEND";

    /**
     * 散批商品二级分类推荐缓存key
     */
    String RETAIL_GOODS_CATE = "RETAIL_GOODS_CATE:";

    /**
     * 商品分类缓存的key(新版本)
     */
    String GOODS_CATE_NEW_KEY = "GOODS_CATE_NEW_KEY";

    /**
     * 搜索历史的key
     */
    String SEARCH_HISTORY_KEY = "SEARCH_HISTORY_KEY";

    /**
     * 搜索店铺历史的key
     */
    String STORE_SEARCH_HISTORY_KEY = "STORE_SEARCH_HISTORY_KEY";

    /**
     * 搜索分销员选品历史的key
     */
    String DISTRIBUTE_SEARCH_HISTORY_KEY = "DISTRIBUTE_SEARCH_HISTORY_KEY";

    /**
     * 搜索拼团商品历史的key
     */
    String GROUPON_SEARCH_HISTORY_KEY = "GROUPON_SEARCH_HISTORY_KEY";

    /**
     * 搜索分销推广商品历史的key
     */
    String DISTRIBUTE_GOODS_SEARCH_HISTORY_KEY = "GROUPON_SEARCH_HISTORY_KEY";

    /**
     * 短信上一次发送时间的key
     */
    String YZM_MOBILE_LAST_TIME = "YZM_MOBILE_LAST_TIME";

    /**
     * 图片验证码的key
     */
    String KAPTCHA_KEY = "KAPTCHA_KEY";

    /**
     * 注册错误次数KEY
     */
    String REGISTER_ERR = "REGISTER_ERR";

    /**
     * 登录错误次数KEY
     */
    String LOGIN_ERR = "LOGIN_ERR";

    /**
     * 商家注册验证码
     */
    String YZM_SUPPLIER_REGISTER = "YZM_SUPPLIER_REGISTER";

    /**
     * 供应商注册验证码
     */
    String YZM_PROVIDER_REGISTER = "YZM_PROVIDER_REGISTER";

    /**
     * s2b 平台 登录错误次数KEY
     */
    String S2B_BOSS_LOGIN_ERR = "S2B_BOSS_LOGIN_ERR";

    /**
     * s2b 平台 登录错误5次，账号锁定时间KEY
     */
    String S2B_BOSS_LOCK_TIME = "S2B_BOSS_LOCK_TIME";

    /**
     * s2b 商家 登录错误次数KEY
     */
    String S2B_SUPPLIER_LOGIN_ERR = "S2B_SUPPLIER_LOGIN_ERR";

    /**
     * s2b 商家 登录错误5次，账号锁定时间KEY
     */
    String S2B_SUPPLIER_LOCK_TIME = "S2B_SUPPLIER_LOCK_TIME";

    /**
     * 用户
     */
    String USER_EMPLOYEE = "USER_EMPLOYEE";

    /**
     * 角色下的所有的功能
     */
    String ROLE_FUNCTION = "ROLE_FUNCTION";
    /**
     * 用户拥有的角色
     */
    String USER_ROLE = "USER_ROLE";

    /**
     * 微信
     */
    String WE_CHAT = "WE_CHAT";

    /**
     * C 端用户登录发送验证码
     */
    String YZM_CUSTOMER_LOGIN = "YZM_CUSTOMER_LOGIN";


    /**
     * C 端用户登录发送验证码
     */
    String TSFXYY_YZM_ALL_LOGIN = "TSFXYY_YZM_ALL_LOGIN";

    /**
     * c 端用户微信绑定验证码
     */
    String WX_BINDING_LOGIN = "WX_BINDING_LOGIN";

    /**
     * 弹框登录时的发送验证码
     */
    String REGISTER_MODAL_CODE = "REGISTER_MODAL_CODE";

    /**
     * 设置支付密码时的验证码
     */
    String BALANCE_PAY_PASSWORD = "BALANCE_PAY_PASSWORD";

    /**
     * 忘记支付密码时的验证码
     */
    String FIND_BALANCE_PAY_PASSWORD = "FIND_BALANCE_PAY_PASSWORD";

    /**
     * APP设置支付密码时的验证码
     */
    String YZM_APP_BALANCE_PAY_PASSWORD = "YZM_APP_BALANCE_PAY_PASSWORD";

    /**
     * APP忘记支付密码时的验证码
     */
    String YZM_APP_FIND_BALANCE_PAY_PASSWORD = "YZM_APP_FIND_BALANCE_PAY_PASSWORD";

    /**
     * 敏感词
     */
    String BAD_WORD = "BAD_WORD";

    /**
     * 商品评价系数
     */
    String EVALUATE_RATIO = "EVALUATE_RATIO";

    /**
     * 通过小程序登录的登录信息
     */
    String WEAPP_LOGIN_INFO = "WEAPP_LOGIN_INFO";

    /**
     * 会员导入成功发送通知
     */
    String IMPORT_CUSTOMER = "IMPORT_CUSTOMER";

    /**
     * saas开关
     */
    String SAAS_SETTING = "SAAS_SETTING";

    /**
     * saas主域名
     */
    String SAAS_DOMAIN = "SAAS_DOMAIN";

    /**
     * 增值服务-企业购-设置
     */
    String VAS_IEP_SETTING = "VAS_IEP_SETTING";

    /**
     * 门店小程序配置
     */
    String STORE_WECHAT_MINI_PROGRAM_CONFIG = "STORE_WECHAT_MINI_PROGRAM_CONFIG";

    /**
     * 微信小程序AccessToken
     */
    String WECHAT_SMALL_PROGRAM_ACCESS_TOKEN = "WECHAT_SMALL_PROGRAM_ACCESS_TOKEN";

    /**
     * 门店微信分享配置
     */
    String STORE_WECHAT_SHARE_SET = "STORE_WECHAT_SHARE_SET";

    /**
     * 友盟配置信息
     */
    String UMENG_CONFIG = "UMENG_CONFIG";

    /**
     * redis 缓存saas域名配置信息
     */
    String CACHE_KEY = "SAAS_KEY";

    /**
     * redis 缓存saas域名配置信息, 提供给Nginx配置使用
     */
    String CACHE_PC_KEY_FOR_NGINX = "SAAS_PC_KEY";

    /**
     * redis 缓存saas域名配置信息, 提供给Nginx配置使用
     */
    String CACHE_MOBILE_KEY_FOR_NGINX = "SAAS_MOBILE_KEY";

    /**
     * 企业购信息
     */
    String IEP_SETTING = "IEP_SETTING";

    /**
     * 直播商品
     */
    String LIVE_GOODS = "LIVE_GOODS";

    /**
     * 直播房间ID
     *
     */
    String LIVE_ROOM_ID = "LIVE_ROOM_ID";

    /**
     * 直播房间列表
     *
     */
    String LIVE_ROOM_LIST = "LIVE_ROOM_LIST";

    /**
     * 直播回放视频
     *
     */
    String LIVE_ROOM_REPLAY = "LIVE_ROOM_REPLAY";

    /**
     * 验证码，绑定银行卡
     */
    String YZM_BIND_BANK_CARD = "YZM_BIND_BANK_CARD";

    /**
     * 验证码，注销用户
     */
    String YZM_LOG_CURRENT_USER_OFF = "YZM_LOG_CURRENT_USER_OFF";
    /**
     * 订单统计数据
     */
    String TRADE_GET_TOGETHER = "TRADE_GET_TOGETHER";

    /**
     * 广告位信息列表缓存key
     */
    String ADVERTISING_LIST_KEY = "ADVERTISING_LIST_KEY";

    /**
     * 广告位信息商家端列表缓存key
     */
    String ADVERTISING_LIST_STORE_ID_KEY = "ADVERTISING_LIST_STORE_ID_KEY";

    /**
     * 启动页广告位信息缓存key
     */
    String START_PAGE_ADVERTISING = "START_PAGE_ADVERTISING";

    /**
     * 散批广告位信息列表缓存key
     */
    String ADVERTISING_RETAIL_LIST_KEY = "ADVERTISING_RETAIL_LIST_KEY";

    /**
     * 散批鲸喜推荐设置商品缓存key
     */
    String RETAIL_GOODS_RECOMMEND = "RETAIL_GOODS_RECOMMEND";

    /**
     * 散批推荐设置商品缓存key
     */
    String BULK_GOODS_RECOMMEND = "BULK_GOODS_RECOMMEND";

    /**
     * 凑箱设置
     */
    String GATHER_BOX_SET = "GATHER_BOX_SET";

    /**
     * 散批惊喜推荐商品setting缓存key
     */
    String RETAIL_GOODS_RECOMMEND_SETTING = "RETAIL_GOODS_RECOMMEND_SETTING";

    /**
     * 散批爆款时刻缓存
     */
    String RETAIL_HOT_STYLE_MOMENTS = "RETAIL_HOT_STYLE_MOMENTS";

    /**
     * usersig签名
     */
    String REDIS_IM_USER_SIG = "SILENCE:IM_USER_SIG:";

    /**
     * 直播加购人数
     */
    String LIVE_ADD_PURCHSE = "LIVE:ADD_PURCHSE:";
    /**
     * 直播立购人数
     */
    String LIVE_ONCE_PURCHSE ="LIVE:ONCE_PURCHSE:";
    /**
     * 直播优惠卷领取人数
     */
    String LIVE_COUPON_GET = "LIVE:COUPON_GET:";

    /**
     * 用户直播间点赞人数
     */
    String LIVE_ROOM_LIKE="LIVE:ROOM:LIKE:";

    /**
     * 用户直播间福袋参与
     */
    String LIVE_BAG_JOIN="LIVE:BAG:JOIN:";

    /**
     * 主播离开回来状态 0 离开 1 回来
     */
    String LIVE_HOST_STATUS="LIVE:HOST:STATUS:";


    /**
     * redisson读写锁 用户地址网点距离表
     */

    String NETWORK_ADRESS_DISTANCE = "NETWORK:ADRESS:DISTANCE:";




    /**
     * redisson 用于存储网点距离和用户地址距离的缓存
     */

    String H_NETWORK_ADRESS_DISTANCE = "H:NETWORK:ADRESS:DISTANCE:";

    /**
     * redisson公平锁 网点停用时用到
     */

    String NETWORK_STOP_F = "NETWORK_STOP_F:";
    /**
     * 千人千面商品缓存（用户）
     */

    String SCREEN_ORDER_ADD_LAST_TIME = "MERCHANT_RECOMMEND_GOODS:";
    /**
     * 千人千面商品缓存(产品)
     */

    String SCREEN_ORDER_ADD_LAST_USERID = "MERCHANT_RECOMMEND_USERID:";
    /**
     * 千人千面商品缓存（公司）
     */

    String SCREEN_ORDER_ADD_LAST_COMPANY = "MERCHANT_RECOMMEND_COMPANY:";
}
