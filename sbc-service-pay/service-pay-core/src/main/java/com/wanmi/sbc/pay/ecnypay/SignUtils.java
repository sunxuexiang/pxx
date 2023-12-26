package com.wanmi.sbc.pay.ecnypay;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * Created by serv on 2014/8/20.
 */
public abstract class SignUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(SignUtils.class);

	private static ThreadLocal<String> threadLocalKey = new ThreadLocal<String>();

	protected SignUtils() {
	}

	public static void setKey(String key) {
		threadLocalKey.set(key);
	}

	public static boolean verifySign(Map<String, String> parameters) {
		boolean flag = false;
		String signatrue="";
		if (Optional.ofNullable(parameters.get("signatrue")).isPresent()){
			signatrue=parameters.get("signatrue").toString();
		}
		String signStr = createSign(parameters);
		if (signStr != null && signStr.equals(signatrue)) {
			flag = true;
		}
		return flag;
	};

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createWeixinLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	/**
	 * 上游代付sign算法
	 * 
	 * @param characterEncoding
	 * @param parameters
	 * @param key
	 *            密钥
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String createSign(Map<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		sb.append(createLinkString(parameters));
		System.out.println(sb.toString());
		
		LogUtil.writeLog("签名串为:"+sb.toString());
		String sign = Md5Utils.hash(sb.toString());
		return sign;
	}

	
	public static String createQuerySign(Map<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		sb.append(createQueryLinkString(parameters));
		System.out.println(sb.toString());
		
		LogUtil.writeLog("签名串为:"+sb.toString());
		String sign = Md5Utils.hash(sb.toString());
		return sign;
	}
	
	public static String md5(String md5Str) {
		String sign = Md5Utils.hash(md5Str);
		return sign;
	}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraObjectFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();
		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = (sArray.get(key) == null ? "" : sArray.get(key).toString());
			if (value == null || value.equals("") || key.equalsIgnoreCase("signatrue") ) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 把数组所有元素按照ascII排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String isNullOrEmpty(String value){
		if (StringUtils.isEmpty(value)){
			return "";
		}
		return value;
	}
	
	public static String createLinkString(Map<String, String> params) {
		
		//固体顺序的
		String SUB_MERCHANTID="";
		String CdtrWltId="";
		if (StringUtils.isNotEmpty(params.get("SUB_MERCHANTID"))){
			SUB_MERCHANTID="SUB_MERCHANTID="+params.get("SUB_MERCHANTID")+"&";
	    }
		if (StringUtils.isNotEmpty(params.get("CdtrWltId"))){
			CdtrWltId="CdtrWltId="+params.get("CdtrWltId")+"&";
	    }
		String signStr="MERCHANTID="+isNullOrEmpty(params.get("MERCHANTID"))+"&"
				      + "POSID="+isNullOrEmpty(params.get("POSID"))+"&"
				      + "BRANCHID="+isNullOrEmpty(params.get("BRANCHID"))+"&"
				      +SUB_MERCHANTID
				      + "ORDERID="+isNullOrEmpty(params.get("ORDERID"))+"&"
				      + "PAYMENT="+isNullOrEmpty(params.get("PAYMENT"))+"&"
				      +CdtrWltId
				      + "CURCODE="+isNullOrEmpty(params.get("CURCODE"))+"&"
				      + "TXCODE="+isNullOrEmpty(params.get("TXCODE"))+"&"
				      + "REMARK1="+isNullOrEmpty(params.get("REMARK1"))+"&"
				      + "REMARK2="+isNullOrEmpty(params.get("REMARK2"))+"&"
				      + "RETURNTYPE="+isNullOrEmpty(params.get("RETURNTYPE"))+"&"
				      + "TIMEOUT="+isNullOrEmpty(params.get("TIMEOUT"))+"&"
				      + "PUB="+isNullOrEmpty(params.get("PUB"));
		return signStr; 
	}
   
    /**
     * 订单查询顺序
     * @param params
     * @return
     */
	public static String createQueryLinkString(Map<String, String> params) {
		
		//固体顺序的
		String SUB_MERCHANTID="";
		String CdtrWltId="";
		if (StringUtils.isNotEmpty(params.get("SUB_MERCHANTID"))){
			SUB_MERCHANTID="SUB_MERCHANTID="+params.get("SUB_MERCHANTID")+"&";
	    }
		if (StringUtils.isNotEmpty(params.get("CdtrWltId"))){
			CdtrWltId="CdtrWltId="+params.get("CdtrWltId")+"&";
	    }
		String signStr="MERCHANTID="+isNullOrEmpty(params.get("MERCHANTID"))+"&"
				      + "POSID="+isNullOrEmpty(params.get("POSID"))+"&"
				      + "BRANCHID="+isNullOrEmpty(params.get("BRANCHID"))+"&"
				      +SUB_MERCHANTID
				      + "ORDERID="+isNullOrEmpty(params.get("ORDERID"))+"&"
				      + "PAYMENT="+isNullOrEmpty(params.get("PAYMENT"))+"&"
				      +CdtrWltId
				      + "CURCODE="+isNullOrEmpty(params.get("CURCODE"))+"&"
				      + "TXCODE="+isNullOrEmpty(params.get("TXCODE"))+"&"
				      + "REMARK1="+isNullOrEmpty(params.get("REMARK1"))+"&"
				      + "REMARK2="+isNullOrEmpty(params.get("REMARK2"))+"&"
				      + "RETURNTYPE="+isNullOrEmpty(params.get("RETURNTYPE"))+"&"
				      + "TIMEOUT="+isNullOrEmpty(params.get("TIMEOUT"))+"&"
				      + "PUB="+isNullOrEmpty(params.get("PUB"));
		return signStr; 
	}
}
