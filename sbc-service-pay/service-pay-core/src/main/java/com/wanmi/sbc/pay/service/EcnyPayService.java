package com.wanmi.sbc.pay.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayClient;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.pay.api.request.EcnyPayRequest;
import com.wanmi.sbc.pay.api.request.EcnyQueryRequest;
import com.wanmi.sbc.pay.api.request.PayExtraRequest;
import com.wanmi.sbc.pay.api.request.PayRequest;
import com.wanmi.sbc.pay.bean.enums.TerminalType;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.pay.bean.enums.TradeType;
import com.wanmi.sbc.pay.ecnypay.EcnyService;
import com.wanmi.sbc.pay.ecnypay.SignUtils;
import com.wanmi.sbc.pay.model.root.PayChannelItem;
import com.wanmi.sbc.pay.model.root.PayGateway;
import com.wanmi.sbc.pay.model.root.PayTradeRecord;
import com.wanmi.sbc.pay.repository.ChannelItemRepository;
import com.wanmi.sbc.pay.repository.GatewayRepository;
import com.wanmi.sbc.pay.repository.TradeRecordRepository;
import com.wanmi.sbc.pay.utils.GeneratorUtils;
import com.wanmi.sbc.pay.utils.PayValidates;

/**
 * @program: service-pay
 * @description: 支付宝
 * @create: 2019-01-28 16:37
 **/
@Service
public class EcnyPayService {

    @Resource
    private TradeRecordRepository recordRepository;
    @Resource
    private ChannelItemRepository channelItemRepository;

    @Resource
    private GatewayRepository gatewayRepository;

    
    @Value("${ecny.pay.url}")
    private String pay_url;
    
    @Value("${ecny.query.url}")
    private String query_url;

    @Value("${ecny.ifValidateRemoteCert}")
    private boolean fValidateRemoteCert;

    @Value("${ecny.ibsversion}")
    private String ibsversion;
    

    //回调地址
    private static final String CALLBACK = "/tradeCallback/aliPayCallBack";

    /**
     * 数字人民币调用sdk生成支付数据
     * @param request
     * @return
     */
    @Transactional(noRollbackFor = SbcRuntimeException.class)
    public String pay(@Valid PayExtraRequest request) {
        PayChannelItem item = getPayChannelItem(request.getChannelItemId(),request.getStoreId());
        //该付款方式是否支持该渠道
        if (item.getTerminal() != request.getTerminal()) {
            throw new SbcRuntimeException("K-100202");
        }

        PayValidates.verifyGateway(item.getGateway());

        //订单重复付款检验
        PayTradeRecord record = recordRepository.findByBusinessId(request.getBusinessId());
        if (!Objects.isNull(record)) {
            //如果重复支付，判断状态，已成功状态则做异常提示
            if (record.getStatus() == TradeStatus.SUCCEED)
                throw new SbcRuntimeException("K-100203");
        }

        //数字人币支付的参数
        String MERCHANTID = item.getGateway().getConfig().getApiKey();
        String POSID = item.getGateway().getConfig().getAccount();
        String BRANCHID = item.getGateway().getConfig().getAppId();
        String notifyUrl = item.getGateway().getConfig().getBossBackUrl();
        String Mrch_url=item.getGateway().getConfig().getPcWebUrl();
        String PUB=item.getGateway().getConfig().getPublicKey();
        String form=null;
        if (TerminalType.H5.equals(request.getTerminal())) {
         try {
        	    EcnyPayRequest ecnyPayRequest = new EcnyPayRequest();//创建API对应的request
        	    ecnyPayRequest.setMERCHANTID(MERCHANTID);
        	    ecnyPayRequest.setPOSID(POSID);
        	    ecnyPayRequest.setBRANCHID(BRANCHID);
        	    ecnyPayRequest.setMrch_url(Mrch_url);
        	    ecnyPayRequest.setPAYMENT(String.valueOf(request.getAmount()));
        	    ecnyPayRequest.setCURCODE("01"); //缺省为01－人民币（只支持人民币支付）
        	    ecnyPayRequest.setTXCODE("HT0000");//HT0000 交易码
        	    ecnyPayRequest.setTIMEOUT(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime() + 15 * 60 *1000));
        	    ecnyPayRequest.setRETURNTYPE("1");
        	    ecnyPayRequest.setCCB_IBSVersion(ibsversion); //版本号
        	    ecnyPayRequest.setPUB(PUB);
                form = createEcnyPayHtml(ecnyPayRequest); //调用生成表单
            } catch (Exception e) {
                e.printStackTrace();
                throw new SbcRuntimeException("K-100208",new Object[]{"数字人民币H5支付"});
            }
        } 
        savePayRecord(request);
        return form;
    }

    /*
     * @Description: 新增、更新交易记录
     * @Author: Bob
     * @Date: 2019-03-05 13:54
    */
    private void savePayRecord(PayRequest request) {
        PayTradeRecord record = new PayTradeRecord();
        record.setId(GeneratorUtils.generatePT());
        record.setApplyPrice(request.getAmount());
        record.setBusinessId(request.getBusinessId());
        record.setChargeId(request.getBusinessId());
        record.setClientIp(request.getClientIp());
        record.setChannelItemId(request.getChannelItemId());
//        record.setTradeNo(result.getTradeNo());
        record.setTradeType(TradeType.PAY);
        record.setPracticalPrice(request.getAmount());
        record.setCreateTime(LocalDateTime.now());
        record.setStatus(TradeStatus.PROCESSING);
        recordRepository.deleteByBusinessId(request.getBusinessId());
        recordRepository.saveAndFlush(record);
    }
    

	public static String isNullOrEmpty(String value){
		if (StringUtils.isEmpty(value)){
			return "";
		}
		return value;
	}
   private  String createEcnyPayHtml(EcnyPayRequest ecnyPayRequest) {
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
   
    /*
     * @Description: 获取配置信息
     * @Author: Bob
     * @Date: 2019-03-05 13:55
    */
    private PayChannelItem getPayChannelItem(Long channelItemId,Long storeId) {
        PayChannelItem item = channelItemRepository.findById(channelItemId).get();
        PayValidates.verfiyPayChannelItem(item);
        // 获取网关
        PayGateway gateway = gatewayRepository.queryByNameAndStoreId(item.getGatewayName(),storeId);
        item.setGateway(gateway);
        return item;
    }
    
    private String ecnyQueryResult(EcnyQueryRequest ecnyQueryRequest) {
    	
    	PayTradeRecord record=recordRepository.findByBusinessId(ecnyQueryRequest.getOrdr_ID());
    	if (record==null){
    		throw new SbcRuntimeException("K-100208",new Object[]{"数字人民币H5订单查询，未查询到订单号"});
    	}
    	String Amount=String.valueOf(record.getPracticalPrice());
    	Long channelItemId=record.getChannelItemId();
    	String orderId=record.getBusinessId();
    	PayChannelItem item = channelItemRepository.findById(record.getChannelItemId()).get();
        PayGateway gateway = item.getGateway();
        String MERCHANTID = gateway.getConfig().getApiKey();
        String POSID = gateway.getConfig().getAccount();
        String BRANCHID = gateway.getConfig().getAppId(); 
        String Mrch_url=gateway.getConfig().getPcWebUrl();
        String PUB=gateway.getConfig().getPublicKey();
        Map<String, String> requestData = new HashMap<>();
        requestData.put("MERCHANTID", MERCHANTID);            
        requestData.put("POSID", BRANCHID);         
        requestData.put("BRANCHID", BRANCHID); 
        requestData.put("Ordr_ID", orderId); 
        requestData.put("TXCODE",  "PDPCX0"); //交易码 PDPCX0 查询
        /***商户接入参数***/
        requestData.put("SYS_TX_STATUS", "00"); 
        requestData.put("CCB_IBSVersion", "V6");//版本号
        String ccbParam="TXCODE=PDPCX0&Txn_Fcn_Type=1&Cst_AccNo=&QR_CODE=&Crdt_TpCd=1010&Crdt_No=&OnLn_Py_Txn_Ordr_ID="+orderId
        		       +"&Ahn_TxnAmt="+Amount+"&TERMINALID=&=FIRM_CODE&DEVICE_CODE=&VERSION=&ProductName=&REMARK1=&REMARK2=&PUB="+PUB;
        
        requestData.put("ccbParam", SignUtils.md5(ccbParam));   
        String jsonResult = EcnyService.send(query_url, requestData);
        return jsonResult;
    }
 
    private AlipayTradeQueryResponse queryEcnypayPaymentResult(String businessId){
    	EcnyQueryRequest request = new EcnyQueryRequest();
       
        AlipayTradeQueryResponse response = null;
        try {
            String  jsonResult= ecnyQueryResult(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SbcRuntimeException("K-100208",new Object[]{"查询订单接口"});
        }
        return response;
    }

}
