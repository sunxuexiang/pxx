package com.wanmi.sbc.goods.api.constant;

/**
 * <p>商品异常码定义</p>
 * Created by of628-wenzhi on 2018-06-21-下午4:29.
 */
public final class GoodsErrorCode {
    private GoodsErrorCode() {
    }

    /**
     * 商品不存在
     */
    public final static String NOT_EXIST = "K-030001";

    /**
     * SPU编码不可重复
     */
    public final static String SPU_NO_EXIST = "K-030002";

    /**
     * SKU编码不可重复
     */
    public final static String SKU_NO_EXIST = "K-030003";

    /**
     * SKU编码[{0}]重复
     */
    public final static String SKU_NO_H_EXIST = "K-030004";

    /**
     * 商品不允许修改商品分类
     */
    public final static String EDIT_GOODS_CATE = "K-030005";

    /**
     * 当前商品不属于当前的商家
     */
    public final static String NOT_BELONG_SUPPLIER = "K-030006";

    /**
     * 商品已下架
     */
    public final static String NOT_ADDEDFLAG = "K-030007";

    /**
     * 商品设价类型错误
     */
    public final static String PRICE_TYPE_ERROR = "K-030008";

    /**
     * 商品排序序号已存在
     */
    public final static String SEQ_NUM_ALREADY_EXISTS = "K-030009";

    /**
     * 小B端商品加入数量报错信息
     */
    public final static String CHECK_COUNTS_BY_CUSTOMER_ID="K-130001";

    /**
     * 添加企业购商品校验失败
     */
    public final static String ENTERPRISE_INVALID_ERROR = "K-030702";

    /**
     * 同步获取最新商品库存信息失败！！！
     */
    public final static String GOODS_ASYNC_ERROR = "K-030701";

    /**
     * 本品商品不能为空
     */
    public final static String GOODS_PRODUCT_NOT_EXISTS = "K-030702";

    /**
     * 商品未下架不能修改步长
     */
    public final static String GOODS_NOT_UPDATE_ADDSTEPE = "K-030609";


    /**
     * 套装商品未设置关联本品商品信息！！！
     */
    public final static String GOODS_PRODUCT_NOT_EXISTS_FOR_QUERY = "K-030703";

    /**
     * 所选分类含有非三级分类
     */
    public final static String GOODS_CATE_GRADE_ERROR = "K-032201";

    /**
     * 推荐分类不能超过10个
     */
    public final static String GOODS_CATE_GRADE_RECOMMEND = "K-032202";

    /**
     * 选中分类与推荐分类重复，请去掉后提交
     */
    public final static String GOODS_CATE_RECOMMEND_REPEAT = "K-032203";
    /**
     * 规格编码不可重复
     */
    public final static String ATTR_NO_EXIST = "K-032204";
}

