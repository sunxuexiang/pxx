package com.wanmi.sbc.goods.api.constant;

/**
 * <p>商品品牌异常码定义</p>
 * Created by of628-wenzhi on 2018-06-21-下午4:37.
 */
public final class GoodsBrandErrorCode {
    private GoodsBrandErrorCode() {
    }

    /**
     * 品牌不存在
     */
    public final static String NOT_EXIST = "K-030201";

    /**
     * 名称已存在
     */
    public final static String NAME_ALREADY_EXISTS = "K-030203";

    /**
     * 品牌排序序号已存在
     */
    public final static String SEQ_NUM_ALREADY_EXISTS = "K-030204";

}
