package com.wanmi.sbc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * <p>
 * </p>
 *
 * @version 1.0
 */
public class UUIDUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(UUIDUtil.class);
    private static UIDFactory uuid = null;

    static {
        try {
            uuid = UIDFactory.getInstance("UUID");
        } catch (Exception unsex) {
            LOGGER.info("Init UIDFactory Failed", unsex);
        }
    }

    /**
     * Constructor for the UUIDGener object
     */
    private UUIDUtil() {
    }

    /**
     * 获取uuid字符
     *
     * @author lihe 2013-7-4 下午5:31:09
     * @return
     * @see
     * @since
     */
    public static String getUUID() {
        return uuid.getNextUID();
    }

    private static String getUUIDBase() {
        return UUID.randomUUID().toString().replace("-",Constants.EMPTY_STR);
    }

    public static String getUUID19() {
        return getUUIDBase().substring(0,19);
    }

    /**
     * 获取会员的ErpId
     * @return
     */
    public static String getErpCustomerId(){
        String uuid = UUID.randomUUID().toString();
        return "ds-" + uuid.substring(19);
    }

    /**
     * toC会员在erp的固定Id
     * @return
     */
    public static String erpTcConstantsId(){
        return "ds-tc-customer-all";
    }
}
