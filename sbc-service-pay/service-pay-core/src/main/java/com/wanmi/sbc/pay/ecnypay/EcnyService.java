package com.wanmi.sbc.pay.ecnypay;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https处理类
 * @author dkq
 *
 */
public class EcnyService {
	/**
	 * 超时时间 .
	 */
	private static int SOCKET_TIMEOUT = 60000;
	/**
	 * 连接超时时间 .
	 */
	private static int CONNECT_TIMEOUT = 60000;
	
	private static  boolean ifValidateRemoteCert=false;
	
	public static boolean isIfValidateRemoteCert() {
		return ifValidateRemoteCert;
	}

	public static void setIfValidateRemoteCert(boolean ifValidateRemoteCert) {
		EcnyService.ifValidateRemoteCert = ifValidateRemoteCert;
	}




	public static String send(String url, Map<String, String> dataMap) {
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try {
			client = HttpClients.custom().build();
			RequestConfig config = RequestConfig.custom()
					.setSocketTimeout(SOCKET_TIMEOUT)
					.setConnectTimeout(CONNECT_TIMEOUT)
					.setAuthenticationEnabled(false).build();

			HttpPost post = new HttpPost(url);
			post.setProtocolVersion(org.apache.http.HttpVersion.HTTP_1_1);
			post.setConfig(config);

			HttpEntity entity = null;
			List<NameValuePair> formpair = new ArrayList<NameValuePair>();
			{
				for (String str : dataMap.keySet().toArray(
						new String[dataMap.size()])) {
					formpair.add(new BasicNameValuePair(str, dataMap.get(str)));
				}
			}

			entity = new UrlEncodedFormEntity(formpair, "UTF-8");
			if (entity != null) {
				post.setEntity(entity);
			}
			resp = client.execute(post);
			if (resp.getStatusLine().getStatusCode() == 200) {
				InputStream is = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					byte[] buffer = new byte[1024];
					is = resp.getEntity().getContent();
					int count = is.read(buffer);
					while (count != -1) {
						baos.write(buffer, 0, count);
						count = is.read(buffer);
					}
					return baos.toString("UTF-8");
				} finally {
					if (is != null) {
						try {
							is.close();
						} finally {
							// ignore
						}
					}
					if (baos != null) {
						try {
							baos.close();
						} finally {
							// ignore
						}
					}
				}
			} else {
				throw new RuntimeException(String.format(
						"发送请求失败,statusCode=%s", resp.getStatusLine()
								.getStatusCode()));
			}

		} catch (ClientProtocolException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (resp != null) {
				try {
					resp.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (client != null) {
				try {
					client.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	
	
	
	/**
	 * 功能：后台交易提交请求报文并接收同步应答报文<br>
	 * @param reqData 请求报文<br>
	 * @param reqUrl  请求地址<br>
	 * @param encoding<br>
	 * @return 应答http 200返回true ,其他false<br>
	 */
	public static Map<String,String> post(
		Map<String, String> reqData,String reqUrl,String encoding) {
		Map<String, String> rspData = new HashMap<String,String>();
		LogUtil.writeLog("请求银行地址:" + reqUrl);
		//发送后台请求数据
		HttpClientUtils hc = new HttpClientUtils(reqUrl, 30000, 30000,ifValidateRemoteCert);//连接超时时间，读超时时间（可自行判断，修改）
		try {
			int status = hc.send(reqData, encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					// 将返回结果转换为map
					Map<String,String> tmpRspData  = convertResultStringToMap(resultString);
					rspData.putAll(tmpRspData);
				}
			}else{
				LogUtil.writeLog("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return rspData;
	}
	
	/**
	 * 功能：http Get方法 便民缴费产品中使用<br>
	 * @param reqUrl 请求地址<br>
	 * @param encoding<br>
	 * @return
	 */
	public static String get(String reqUrl,String encoding) {
		
		LogUtil.writeLog("请求银行地址:" + reqUrl);
		//发送后台请求数据
		HttpClientUtils hc = new HttpClientUtils(reqUrl, 30000, 30000,ifValidateRemoteCert);
		try {
			int status = hc.sendGet(encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					return resultString;
				}
			}else{
				LogUtil.writeLog("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return null;
	}
	
	
	/**
	 * 功能：前台交易构造HTTP POST自动提交表单<br>
	 * @param reqUrl 表单提交地址<br>
	 * @param hiddens 以MAP形式存储的表单键值<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>
	 * @return 构造好的HTTP POST交易表单<br>
	 */
	public static String createAutoFormHtml(String reqUrl, Map<String, String> hiddens,String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + reqUrl
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	/**
	 * 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map = null;

		if (StringUtils.isNotBlank(result)) {
			if (result.startsWith("{") && result.endsWith("}")) {
				result = result.substring(1, result.length() - 1);
			}
			map = parseQString(result);
		}

		return map;
	}

	
	/**
	 * 解析应答字符串，生成应答要素
	 * 
	 * @param str
	 *            需要解析的字符串
	 * @return 解析的结果map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str) {

		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;//值里有嵌套
		char openName = 0;
		if(len>0){
			for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
				curChar = str.charAt(i);// 取当前字符
				if (isKey) {// 如果当前生成的是key
					
					if (curChar == '=') {// 如果读取到=分隔符 
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else  {// 如果当前生成的是value
					if(isOpen){
						if(curChar == openName){
							isOpen = false;
						}
						
					}else{//如果没开启嵌套
						if(curChar == '{'){//如果碰到，就开启嵌套
							isOpen = true;
							openName ='}';
						}
						if(curChar == '['){
							isOpen = true;
							openName =']';
						}
					}
					
					if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					} else {
						temp.append(curChar);
					}
				}
				
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map) {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

}
