package com.wanmi.sbc.pay.ecnypay;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EcnyTest2 {
	
	/**
	 * 商户号：105000060121051   分行号：430000000  柜台号:051549664
	 */
	/**
	 * https://ch5.dcep.ccb.com/CCBIS/ccbMain_XM?CCB_IBSVersion=V6&MERCHANTID=105000060121051&POSID=051549664&BRANCHID=430000000&SUB_MERCHANTID=&ORDERID=19991101234&PAYMENT=0.01&CdtrWltId=&CURCODE=01&TXCODE=HT0000&REMARK1=&REMARK2=&RETURNTYPE=1&TIMEOUT=&MAC=648463bf501abf9dfa0faa99a94d02d1&Mrch_url=https://Ch5.dcep.ccb.com 
	 */
	private static String MERCHANTID="105000060121051";
	private static String POSID="051549664";
	private static String BRANCHID="430000000";
	private static String pub="2e3338603d4c924ab2b1ef75020111";
	public static void main(String[] args) {
		
		Map<String,String > map=new HashMap<String,String>();

		   String ORDERID=dateTimeNow();
			map.put("MERCHANTID", MERCHANTID);
			map.put("POSID", POSID);
			map.put("BRANCHID", BRANCHID);
			map.put("ORDERID", ORDERID);
			map.put("PAYMENT", "0.01");
			map.put("CURCODE", "01");
			map.put("TXCODE", "HT0000");
			map.put("REMARK1", "");
			map.put("REMARK2", "");
			map.put("RETURNTYPE", "");
			map.put("TIMEOUT", "");
			map.put("CdtrWltId", "");
			map.put("SUB_MERCHANTID", "");
	    String sign="MERCHANTID=105000060121051&POSID=051549664&BRANCHID=430000000&SUB_MERCHANTID=123456789&ORDERID="+ORDERID+"&PAYMENT=0.01&CdtrWltId=050000132&CURCODE=01&TXCODE=HT0000&REMARK1=&REMARK2=&RETURNTYPE=1&TIMEOUT=&PUB=2e3338603d4c924ab2b1ef75020111";
			   
	    String MAC=Md5Utils.hash(sign);
	    System.out.println(MAC);
	    String all="https://ch5.dcep.ccb.com/CCBIS/ccbMain_XM?CCB_IBSVersion=V6&"
	  	          +"MERCHANTID=105000060121051&POSID=051549664&BRANCHID=430000000&SUB_MERCHANTID=123456789&ORDERID="+ORDERID+"&PAYMENT=0.01&CdtrWltId=050000132&CURCODE=01&TXCODE=HT0000&REMARK1=&REMARK2=&RETURNTYPE=1&TIMEOUT=&MAC="+MAC
			      +"& Mrch_url= https://Ch5.dcep.ccb.com";
	   
	    System.out.println(all);
	}
	
	 public static  String dateTimeNow()
	    {
	        return  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    }

}
