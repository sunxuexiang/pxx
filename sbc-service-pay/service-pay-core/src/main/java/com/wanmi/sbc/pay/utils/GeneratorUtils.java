package com.wanmi.sbc.pay.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by sunkun on 2017/8/4.
 */
@Service
public class GeneratorUtils {

    private static final String DATE_PATTERN = "yyMMddHHmmss";

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.UK);

    private static final ArrayBlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(30);

    /**
     * 生成支付单号
     *
     * @return
     */
    public static String generatePT() {
        return "PT" + LocalDateTime.now().format(dateFormat) + randomNumeric();
    }

    /**
     * 生成id 自定义前缀
     *
     * @param prefix
     * @return
     */
    public static String generate(String prefix) {
        return prefix + LocalDateTime.now().format(dateFormat) + randomNumeric();
    }

    /**
     * 获取随机数
     * @return
     */
    private static String randomNumeric() {
        String str = RandomStringUtils.randomNumeric(6);

        // 检查新生成的str 是否已经存在于历史记录中
        if (QUEUE.contains(str)) {
            str = randomNumeric();
        } else {
            // 尝试添加元素，如果队列已满则返回false
            boolean added = QUEUE.offer(str);
            if (!added) {
                // 丢弃最早添加的元素
                String discarded = QUEUE.poll();
                System.out.println("Discarded: " + discarded);
                // 添加新元素
                QUEUE.add(str);
            }
        }
        return str;
    }

//    public static void main(String[] args) {
//        int times = 1;
//        while (true) {
//            List<String> ret = new ArrayList<>();
//            for (int i = 0; i < 5; i++) {
//                String no = generatePT();
//                ret.add(no);
//            }
//            System.out.println("生成No第[" + times + "]次: [" + ret + "] size:[" + QUEUE.size() + "]");
//            Set<String> set = new HashSet<>();
//            for (String item : ret) {
//                if (!set.add(item)) {
//                    // 如果无法添加到集合中，说明已经存在，即为重复值
//                    System.out.println("重复值：" + item);
//                    return;
//                }
//            }
//            times++;
//        }
//    }
}
