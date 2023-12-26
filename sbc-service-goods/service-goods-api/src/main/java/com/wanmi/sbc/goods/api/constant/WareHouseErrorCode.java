package com.wanmi.sbc.goods.api.constant;

/**
 * <p>仓库异常码定义</p>
 * @author zhangwenchang
 */
public final class WareHouseErrorCode {
    private WareHouseErrorCode() {
    }

    /**
     * 仓库名称已存在
     */
    public final static String WARE_NAME_EXIST = "K-031401";

    /**
     * 仓库编码已存在
     */
    public final static String WARE_CODE_EXIST = "K-031402";

    /**
     * 仓库存在商品库存，无法删除。
     */
    public final static String NOT_DELETE_BY_HAVE_STOCK = "K-031403";

    /**
     * 仓库为默认仓，无法删除。
     */
    public final static String NOT_DELETE_BY_DEFAULT_FLAG = "K-031404";

    /**
     * 存在默认仓，无法仓库初始化。
     */
    public final static String NOT_INIT_BY_DEFAULT_FLAG = "K-031405";


    /**
     * 仓库不存在
     */
    public final static String WARE_HOUSE_NOT_EXIST = "K-031406";

    /**
     * 仓库范围不覆盖（改地区未覆盖）
     */
    public final static String WARE_HOUSE_NO_SERVICE = "K-031407";

}

