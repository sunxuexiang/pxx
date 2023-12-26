package com.wanmi.sbc.marketing.util;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChineseNumberUtil {

	private static String[] chineseNums = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
	private static String[] units = { "", "十", "百", "千", "万", "十万", "百万", "千万", "亿" };

	public static String toChineseNumber(BigDecimal decimal) {
		// 整数部分
		BigDecimal integerPart = decimal.setScale(0, BigDecimal.ROUND_DOWN);
		// 小数部分
		BigDecimal decimalPart = decimal.remainder(BigDecimal.ONE);
		// 小数部分字符串 如0.11即为11
		String decimalPartString = decimalPart.movePointRight(decimalPart.scale()).toString();
		// 整数部分字符串
		String integerPartString = integerPart.toString();
		int integerLength = integerPartString.length();
		StringBuilder result = new StringBuilder();

		// 处理整数部分
		for (int i = 0; i < integerLength; i++) {
			int digit = Character.getNumericValue(integerPartString.charAt(i)); // 获取当前位的数字
			if (digit != 0) {
				result.append(chineseNums[digit]); // 将数字转换为中文数字并添加到结果中
				result.append(units[integerLength - i - 1]); // 添加单位字符
			} else {
				// 处理零的情况
				if (i < integerLength - 1 && Character.getNumericValue(integerPartString.charAt(i + 1)) != 0) {
					result.append(chineseNums[digit]);
				}
			}
		}
		log.info("integerPart[{}]", result);

		// 处理小数部分
		if (decimalPart.compareTo(BigDecimal.ZERO) != 0) {
			result.append("点");
			for (int i = 0; i < decimalPartString.length(); i++) {
				int digit = Character.getNumericValue(decimalPartString.charAt(i));
				result.append(chineseNums[digit]); // 将数字转换为中文数字并添加到结果中
			}
		}
		log.info("result[{}]", result);
		return result.toString();
	}
}
