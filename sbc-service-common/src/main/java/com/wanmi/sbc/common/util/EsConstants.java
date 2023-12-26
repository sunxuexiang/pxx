package com.wanmi.sbc.common.util;

/**
 * ES常量相关类
 * Created by daiyitian
 */
public interface EsConstants {

    /**
     * ES索引
     */
    String INDEX_NAME = "s2b";

    /**
     * ES spu文档类型
     */
    String DOC_GOODS_TYPE = "es_goods";

    /**
     * ES sku文档类型
     */
    String DOC_GOODS_INFO_TYPE = "es_goods_info";

    /**
     * ES品牌分类联合文档类型
     */
    String DOC_CATE_BRAND_TYPE = "goods_cate_brand";

    /**
     * 默认，中文分词
     */
    String DEF_ANALYZER = "ik_max_word";

    /**
     * 订单index
     */
    String ORDER_INDEX = "b2b_trade_order";

    /**
     * 订单type
     */
    String ORDER_TYPE = "trade_order";

    /**
     * 退单索引
     */
    String RETURN_ORDER_INDEX = "b2b_return_order";

    /**
     * 退单type
     */
    String RETURN_ORDER_TYPE = "return_order";

    /**
     *
     * 敏感词
     */
    String SEARCH_ASSOCIATIONAL_WORD = "es_search_associational_word";

    /**
     * ES 零售 spu文档类型
     */
    String DOC_RETAIL_GOODS_TYPE = "es_retail_goods";

    /**
     * ES 零售 sku文档类型
     */
    String DOC_RETAIL_GOODS_INFO_TYPE = "es_retail_goods_info";


    /**
     * ES 零售 spu文档类型
     */
    String DOC_BULK_GOODS_TYPE = "es_bulk_goods";

    /**
     * ES 零售 sku文档类型
     */
    String DOC_BULK_GOODS_INFO_TYPE = "es_bulk_goods_info";

    /**
     * ES 腾讯IM聊天
     */
    String DOC_IM_MESSAGE = "es_im_message";

}
