package com.wanmi.sbc.common.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 订单ID生成
 * Created by Administrator on 2017/4/18.
 */
@Service
public class GeneratorService {

    /**
     * 父订单号前缀
     */
    public static final String _PREFIX_PARENT_TRADE_ID = "PO";

    /**
     * 批发订单号前缀
     */
    public static final String _PREFIX_TRADE_ID = "O";

    /**
     * 新囤货订单号前缀
     */
    public static final String _NEW_PILE_PREFIX_TRADE_ID = "NP";

    /**
     * 新囤货父订单号前缀
     */
    public static final String _NEW_PILE_PARENT_PREFIX_TRADE_ID = "NPPO";

    /**
     * 新提货订单号前缀
     */
    public static final String _NEW_PILE_PREFIX_TRADE_PK_ID = "OPK";

    /**
     * 零售订单号前缀
     */
    public static final String _PREFIX_RETAIL_TRADE_ID = "L";

    /**
     * 拆箱散批前缀
     */
    public static final String _PREFIX_BULK_TRADE_ID = "SP";

    /**
     * 尾款订单号前缀
     */
    public static final String _PREFIX_TRADE_TAIL_ID = "OT";

    /**
     * 供应商订单号前缀
     */
    public static final String _PREFIX_PROVIDER_TRADE_ID = "P";

    /**
     * 理赔单号前缀
     */
    public static final String _PREFIX_PROVIDER_CLAIMS = "LP";

    private static final String DATE_PATTERN = "yyMMddHHmmss";

    private static final ArrayBlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(50);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.UK);

    /**
     * 生成tid
     * O+ "yyyyMMddHHmmss" + random(3)
     */
    public String generateTid() {
        return _PREFIX_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成tid(新囤货)
     * NP+ "yyyyMMddHHmmss" + random(3)
     */
    public String generateNewPileTid() {
        return _NEW_PILE_PREFIX_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成新提货
     * NP+ "yyyyMMddHHmmss" + random(3)
     */
    public String generateNewPilePickTid() {
        return _NEW_PILE_PREFIX_TRADE_PK_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生产零售tid
     * @return
     */
    public String generateRetailTid() {
        return _PREFIX_RETAIL_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }


    /**
     * 生产散批tid
     * @return
     */
    public String generateBulkTid() {
        return _PREFIX_BULK_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }


    /**
     * 生成供应商tid
     * P+ "yyyyMMddHHmmss" + random(3)
     */
    public String generateProviderTid() {
        return _PREFIX_PROVIDER_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成父订单号 po+id （用于组织批量订单合并支付，目前仅在支付与退款中使用）
     * O+ "yyyyMMddHHmmss" + random(3)
     */
    public String generatePoId() {
        return _PREFIX_PARENT_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成oid
     * OD+ "yyyyMMddHHmmss" + random(3)
     *
     * @return
     */
    public String generateOid() {
        return "OD" + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成支付单号
     *
     * @return
     */
    public String generatePid() {
        return "PD" + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成支付单流水号
     *
     * @return
     */
    public String generateSid() {
        return "P" + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }


    /**
     * 生成退款单号
     *
     * @return
     */
    public String generateRid() {
        return "RD" + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 退款单流水单号
     *
     * @return
     */
    public String generateRF() {
        return "RF" + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成拼团团号
     */
    public String generateGrouponNo() {
        return "G" + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }


    /**
     * 生成id 自定义前缀
     *
     * @param prefix
     * @return
     */
    public String generate(String prefix) {
        return prefix + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成囤货订单 PID  NPPO
     * @return
     */
    public String generateNpPoId() {
        return _NEW_PILE_PREFIX_TRADE_ID + _PREFIX_PARENT_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    /**
     * 生成囤货提货新父订单号 PID  OPKOP
     * @return
     */
    public String generateOPKPoId() {
        return _NEW_PILE_PREFIX_TRADE_PK_ID + _PREFIX_PARENT_TRADE_ID + LocalDateTime.now().format(dateTimeFormatter) + randomNumeric();
    }

    private String randomNumeric() {
        String str = RandomStringUtils.randomNumeric(6);
        // 检查新生成的str 是否已经存在于历史记录中
        if (QUEUE.contains(str)) {
            str = randomNumeric();
        } else {
            // 尝试添加元素，如果队列已满则返回false
            boolean added = QUEUE.offer(str);
            if (!added) {
                // 丢弃最早添加的元素
                QUEUE.poll();
                // 添加新元素
                QUEUE.add(str);
            }
        }
        return str;
    }
}
