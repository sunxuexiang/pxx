package com.wanmi.sbc.pay.ecnypay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.pay.api.request.EcnyPayRequest;

public class EcnyTest {

	 private static String pay_url="https://ch5.dcep.ccb.com/CCBIS/ccbMain_XM";
	 private static String query_url="https://ch5.dcep.ccb.com/CCBIS/ccbMain_XM";  
	 //商户号：105000060121051   分行号：430000000  柜台号:051549664
	 private static String  MERCHANTID="105000060121051";
	 private static String  POSID="051549664";
	 private static String  BRANCHID="430000000";
	 private static String  Mrch_url="https://Ch5.dcep.ccb.com";
	 private static String  pubkey="2e3338603d4c924ab2b1ef75020111";  //
	 //商户号：105000060121051   分行号：430000000  柜台号:051549664
	 public static void main(String[] args){
		 String ORDERID=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		 String timeout="";//new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime() + 15 * 60 *1000);
		 EcnyPayRequest ecnyPayRequest=new EcnyPayRequest();
		 ecnyPayRequest.setMERCHANTID(MERCHANTID);
		 ecnyPayRequest.setBRANCHID(BRANCHID);
		 ecnyPayRequest.setPOSID(POSID);
		 ecnyPayRequest.setORDERID(ORDERID);
		 ecnyPayRequest.setPAYMENT("0.01");
		 ecnyPayRequest.setCURCODE("01");
		 ecnyPayRequest.setTXCODE("HT0000");
		// ecnyPayRequest.setREMARK1("测试");
		// ecnyPayRequest.setSUB_MERCHANTID("123456789");
		// ecnyPayRequest.setCdtrWltId("050000132");
		 ecnyPayRequest.setTIMEOUT(timeout);
		 ecnyPayRequest.setMrch_url(Mrch_url);
		 ecnyPayRequest.setRETURNTYPE("1");
		 ecnyPayRequest.setPUB(pubkey);
		 //Map<String,String> ss=sendEcnyPay(ecnyPayRequest);
		// System.out.println(JSON.toJSONString(ss));
		 //   
		 ecnyPayRequest.setCCB_IBSVersion("V6");
		 String bs=createEcnyPayHtml(ecnyPayRequest);
		 System.out.println(bs);
		 
		  Map<String, String> requestData = new HashMap<>();
	        requestData.put("MERCHANTID", ecnyPayRequest.getMERCHANTID());            
	        requestData.put("POSID", ecnyPayRequest.getPOSID());         
	        requestData.put("BRANCHID", ecnyPayRequest.getBRANCHID()); 
	        requestData.put("ORDERID", ecnyPayRequest.getORDERID()); 
	        requestData.put("PAYMENT", ecnyPayRequest.getPAYMENT());
	        requestData.put("CURCODE", ecnyPayRequest.getCURCODE()); //币种 缺省为01－人民币（只支持人民币支付）
	        requestData.put("TXCODE",  ecnyPayRequest.getTXCODE()); //交易码 HT0000

	        requestData.put("REMARK1", ecnyPayRequest.getREMARK1());                             
	        requestData.put("REMARK2", ecnyPayRequest.getREMARK2());                     
	        requestData.put("RETURNTYPE", ecnyPayRequest.getRETURNTYPE());            
	        requestData.put("TIMEOUT", ecnyPayRequest.getTIMEOUT()); 
	        requestData.put("CdtrWltId", ecnyPayRequest.getCdtrWltId());  
	        requestData.put("SUB_MERCHANTID", ecnyPayRequest.getSUB_MERCHANTID());                              //交易金额，单位分，不要带小数点
	        requestData.put("PUB", ecnyPayRequest.getPUB());
	        String reqdata=createrequestString(requestData);
		    String MAC=SignUtils.createSign(requestData); 
		    System.out.println(MAC);
		    String all="https://ch5.dcep.ccb.com/CCBIS/ccbMain_XM?CCB_IBSVersion=V6&"
		    		+ reqdata
                    +"&MAC="+MAC
				    +"&Mrch_url= https://Ch5.dcep.ccb.com";
		   
		    System.out.println("aaa:"+all);
		   
		    testquery(ORDERID);
	 }
	 
	 
	 public static void testquery(String ORDERID){
		 //随便定义
		 Map<String, String> requestData = new HashMap<>();
	     requestData.put("MERCHANTID", MERCHANTID);            
	     requestData.put("POSID",   POSID);         
	     requestData.put("BRANCHID", BRANCHID); 
	     requestData.put("Ordr_ID", ORDERID); 
	     requestData.put("TXCODE",  "PDPCX0"); //交易码 HT0000
	
	     /***商户接入参数***/
	     requestData.put("SYS_TX_STATUS", "00");   
	     requestData.put("CCB_IBSVersion", "V6");
	     String ccbParam="TXCODE=PDPCX0&Txn_Fcn_Type=1&Cst_AccNo=&QR_CODE=&Crdt_TpCd=1010&Crdt_No=&OnLn_Py_Txn_Ordr_ID="+ORDERID+"&Ahn_TxnAmt=0.01&TERMINALID=&=FIRM_CODE&DEVICE_CODE=&VERSION=&ProductName=&REMARK1=&REMARK2=&PUB="+pubkey;
	     
	     requestData.put("ccbParam", SignUtils.md5(ccbParam));   
	     String jsonResult = EcnyService.send(query_url, requestData);
	     System.out.println(jsonResult);
     
	 }
	 
	 
	 /**
		 * 固定顺序
		 * @param params
		 * @return
		 */
	   public static String createrequestString(Map<String, String> params) {
			
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
					      + "TIMEOUT="+isNullOrEmpty(params.get("TIMEOUT"));
			return signStr;
		}
	 public static  String dateTimeNow()
	    {
	        return  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    }

	 private static Map<String,String> sendEcnyPay(EcnyPayRequest ecnyPayRequest) {
	        Map<String, String> requestData = new HashMap<>();
	        requestData.put("MERCHANTID", ecnyPayRequest.getMERCHANTID());            
	        requestData.put("POSID", ecnyPayRequest.getPOSID());         
	        requestData.put("BRANCHID", ecnyPayRequest.getBRANCHID()); 
	        requestData.put("ORDERID", ecnyPayRequest.getORDERID()); 
	        requestData.put("PAYMENT", ecnyPayRequest.getPAYMENT());
	        requestData.put("CURCODE", ecnyPayRequest.getCURCODE()); //币种 缺省为01－人民币（只支持人民币支付）
	        requestData.put("TXCODE",  ecnyPayRequest.getTXCODE()); //交易码 HT0000

	        /***商户接入参数***/
	        requestData.put("REMARK1", ecnyPayRequest.getREMARK1());                             
	        requestData.put("REMARK2", ecnyPayRequest.getREMARK2());                     
	        requestData.put("RETURNTYPE", ecnyPayRequest.getRETURNTYPE());            
	        requestData.put("TIMEOUT", ecnyPayRequest.getTIMEOUT());
	        requestData.put("CdtrWltId", ecnyPayRequest.getCdtrWltId());  
	        requestData.put("SUB_MERCHANTID", ecnyPayRequest.getSUB_MERCHANTID());                              //交易金额，单位分，不要带小数点
	        requestData.put("PUB", ecnyPayRequest.getPUB());
	        String MAC=SignUtils.createSign(requestData); 
	        requestData.remove("PUB");
	        requestData.put("MAC", MAC);  
	        //requestData.put("Mrch_url", ecnyPayRequest.getMrch_url()); 
	        // .properties文件中的acpsdk.frontTransUrl
	        Map<String,String> rspdata = EcnyService.post(requestData,pay_url,"UTF-8");   //生成自动跳转的Html表单
	        return rspdata;
	    }
	
		public static String isNullOrEmpty(String value){
			if (StringUtils.isEmpty(value)){
				return "";
			}
			return value;
		}
	 private static String createEcnyPayHtml(EcnyPayRequest ecnyPayRequest) {
	        Map<String, String> requestData = new HashMap<>();
	        requestData.put("MERCHANTID", ecnyPayRequest.getMERCHANTID());            
	        requestData.put("POSID", ecnyPayRequest.getPOSID());         
	        requestData.put("BRANCHID", ecnyPayRequest.getBRANCHID()); 
	        requestData.put("ORDERID", ecnyPayRequest.getORDERID()); 
	        requestData.put("PAYMENT", ecnyPayRequest.getPAYMENT());
	        requestData.put("CURCODE", ecnyPayRequest.getCURCODE()); //币种 缺省为01－人民币（只支持人民币支付）
	        requestData.put("TXCODE",  ecnyPayRequest.getTXCODE()); //交易码 HT0000

	        /***商户接入参数***/
	        requestData.put("REMARK1", isNullOrEmpty(ecnyPayRequest.getREMARK1()));                             
	        requestData.put("REMARK2", isNullOrEmpty(ecnyPayRequest.getREMARK2()));                     
	        requestData.put("RETURNTYPE", isNullOrEmpty(ecnyPayRequest.getRETURNTYPE()));            
	        requestData.put("TIMEOUT", ecnyPayRequest.getTIMEOUT()); 
	        requestData.put("CdtrWltId", isNullOrEmpty(ecnyPayRequest.getCdtrWltId()));  
	        requestData.put("SUB_MERCHANTID", isNullOrEmpty(ecnyPayRequest.getSUB_MERCHANTID()));                              //交易金额，单位分，不要带小数点
	        requestData.put("PUB", ecnyPayRequest.getPUB());
	        String MAC=SignUtils.createSign(requestData); 
	        requestData.remove("PUB");
	        requestData.put("MAC", MAC);  
	        requestData.put("Mrch_url", isNullOrEmpty(ecnyPayRequest.getMrch_url())); 
	        requestData.put("CCB_IBSVersion", isNullOrEmpty(ecnyPayRequest.getCCB_IBSVersion()));
	        // .properties文件中的acpsdk.frontTransUrl
	        String html = EcnyService.createAutoFormHtml(pay_url, requestData, "UTF-8");   //生成自动跳转的Html表单
	        return html;
	    }
}
