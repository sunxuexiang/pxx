
package com.wanmi.sbc.common.util;

import com.google.common.collect.Maps;

import java.util.HashMap;

/**
 * 常量类
 *
 * @author daiyitian
 * @version 0.0.1
 * @since 2017年4月11日 下午3:46:12
 */
public final class Constants {

    public final static Integer yes = 1;

    public final static Integer no = 0;

    // 验证码 有效期 5分钟
    public final static Long SMS_TIME = 5L;

    // 导出最大数量 1000
    public final static Integer EXPORT_MAX_SIZE = 1000;

    //分类在每个父类下上限20
    public final static Integer GOODSCATE_MAX_SIZE = 100;

    //品牌上限2000
    public final static Integer BRAND_MAX_SIZE = 2000;

    //采购单限购200
    //TODO:暂时修改为200
    public final static Integer PURCHASE_MAX_SIZE = 500;

    //囤货300
    //TODO:暂时修改为200
    public final static Integer PURCHASE_STORE_MAX_SIZE = 500;

    //商品收藏上限500
    public final static Integer FOLLOW_MAX_SIZE = 500;

    //商品导入文件大小上限2M
    public final static Integer IMPORT_GOODS_MAX_SIZE = 2;

    //商品导入错误文件的文件夹名
    public final static String ERR_EXCEL_DIR = "err_excel";

    //商品导入上传文件的文件夹名
    public final static String EXCEL_DIR = "excel";

    //店铺分类最多可添加2个层级
    public final static int STORE_CATE_GRADE = 2;

    //一级店铺分类最多20个
    // 修改成100个，线上有个商家分类88个，导致无法修改 20230808
    public final static int STORE_CATE_FIRST_NUM = 100;

    //每个一级店铺分类最多20个二级分类
    public final static int STORE_CATE_SECOND_NUM = 20;

    //店铺图片分类最多可添加3个层级
    public final static int STORE_IMAGE_CATE_GRADE = 3;

    //每个层级店铺分类最多20个
    public final static int STORE_IMAGE_CATE_NUM = 20;

    //平台最多配置100个物流公司
    public final static int EXPRESS_COMPANY_COUNT = 100;

    //每个店铺最多使用20个物流公司
    public final static int STORE_EXPRESS_COUNT = 20;

    // 分类path的分隔符
    public final static String CATE_PATH_SPLITTER = "\\|";

    public final static String STRING_SLASH_SPLITTER = "/";
    public final static String STRING_SLASH_HENG=  "-";


    //店铺关注上限200
    public final static Integer STORE_FOLLOW_MAX_SIZE = 200;

    /** 商品分类叶子分类层级(最多三层)*/
    public final static int GOODS_LEAF_CATE_GRADE = 3;

    /**
     * 默认收款账号
     */
    public final static Long DEFAULT_RECEIVABLE_ACCOUNT = -1L;


    /**
     * 营销满金额时最小金额
     */
    public final static double MARKETING_FULLAMOUNT_MIN = 0.01;

    /**
     * 营销满金额时最大金额
     */
    public final static double MARKETING_FULLAMOUNT_MAX = 99999999.99;

    /**
     * 营销满数量时最小数量
     */
    public final static Long MARKETING_FULLCOUNT_MIN = 1L;

    /**
     * 营销满数量时最大数量
     */
    public final static Long MARKETING_FULLCOUNT_MAX = 9999L;


    /**
     * 营销满数量时最小数量
     */
    public final static double MARKETING_DISCOUNT_MIN = 0.0001;

    /**
     * 营销满数量时最大数量
     */
    public final static double MARKETING_DISCOUNT_MAX = 0.9999;

    /**
     * 满赠赠品最大种数
     */
    public final static int MARKETING_Gift_TYPE_MAX = 20;

    /**
     * 满赠赠品最小数量
     */
    public final static int MARKETING_Gift_MIN = 1;

    /**
     * 满赠赠品最大数量
     */
    public final static int MARKETING_Gift_MAX = 999;

    /**
     * 商品类目默认属性值
     */
    public static final Long GOODS_DEFAULT_REL = Long.valueOf(0);
    /**
     * 商品类目关联属性最大数量
     */
    public static final int GOODS_PROP_REAL_SIZE = 20;
    /**
     * 属性值最大数量
     */
    public static final int GOODS_PROP_DETAIL_REAL_SIZE = 100;

    /**
     * 用户的全平台ID，非店铺客户的默认等级，用于级别价计算
     */
    public static final long GOODS_PLATFORM_LEVEL_ID = 0;

    /**
     * 单品运费模板最大数量
     */
    public static final int FREIGHT_GOODS_MAX_SIZE = 20;

    public static final int ONE = 1;

    /***
     * 采购单distributod_id默认值0
     */
    public  static final String PURCHASE_DEFAULT = "0";


    /**
     * 积分价值
     */
    public static final Long POINTS_WORTH = 100L;

    /**
     * 秒杀活动进行时间
     */
    public static final Long FLASH_SALE_LAST_HOUR = 2L;

    /**
     * 秒杀活动商品抢购资格有效期：5分钟
     */
    public static final Long FLASH_SALE_GOODS_QUALIFICATIONS_VALIDITY_PERIOD = 5L;

    /**
     * 秒杀活动抢购商品订单类型："FLASH_SALE"
     */
    public static final String FLASH_SALE_GOODS_ORDER_TYPE = "FLASH_SALE";

    //S2B平台最多配置100个物流公司
    public final static int S2B_EXPRESS_COMPANY_COUNT = 100;


    /**
     * boss默认店铺id
     */
    public static final Long BOSS_DEFAULT_STORE_ID = -1L;
    /**
     * boss默认企业id
     */
    public static final Long BOSS_DEFAULT_COMPANY_INFO_ID = -1L;

    /**
     * 默认批发市场id
     */
    public static final Long BOSS_DEFAULT_MARKET_ID = -1L;

    /**
     * 合并积分 子账户默认积分设置
     */
    public static final Long RELA_CUSTOMER_INTEGRAL = 0L;

    /**
     * 默认的业务员 目前先写死 后续业务再改动
     */
    public static final String DEFAULT_EMPLOYEE_ID = "2c8080815cd3a74a015cd3ae86850001";

    /**
     * 分类品牌上传文件夹
     */
    public final static String BRAND_EXCEL_DIR = "excel_brand";

    /**
     * 分类品牌上传文件大小
     */
    public final static Integer BRAND_EXCEL_MAX_SIZE = 2;

    public final static String ERR_BRAND_EXCEL_DIR = "excel_brand_err";

    public final static String OVER_BOOKING = "over_bookingd_stock";

    public final static String NEW_PILE_OVER_BOOKING = "new_pile_over_booking_stock";

    public final static String NEW_PILE_PICK_OVER_BOOKING = "new_pile_pick_over_booking_stock";

    public final static String OVER_ORDER_BOOKING = "over_order_bookingd_stock";

    public final static String EMPTY_STR = "";

    /**
     * im每天时间段
     */
    public final static Integer IM_MAX_DAY_HOUR = 24;


    /**
     * 分仓商品前缀
     */
    public final static HashMap<Long, String> ERP_NO_PREFIX = new HashMap<Long, String>(){
        {
            put(1L,"001-");put(7L,"002-");put(46L,"002-");put(47L,"003-");
            put(49L,"001-");put(50L,"002-");put(51L,"003-");
        }
    };

    /**
     * 散批和批发仓库关联关系 在散批商品导入时获取批发的运费模板
     */
    public final static HashMap<Long,Long> WHOLESALE_BULK = new HashMap<Long,Long>(){
        {
            //49 ---1   50---46   51--47
            put(49L,1L);put(50L,46L);put(51L,47L);
        }
    };



    private Constants() {
    }

}
