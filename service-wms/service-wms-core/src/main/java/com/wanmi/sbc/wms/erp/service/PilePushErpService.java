package com.wanmi.sbc.wms.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushReturnGoodsRequest;
import com.wanmi.sbc.wms.erp.enums.PushKingdeeStatusEnum;
import com.wanmi.sbc.wms.erp.model.*;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeOrder;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeReturnGoods;
import com.wanmi.sbc.wms.erp.repository.StockPushKingdeeOrderRepository;
import com.wanmi.sbc.wms.erp.repository.StockPushKingdeeReturnGoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 囤货推erp
 *
 * @author yitang
 * @version 1.0
 */
@Service("PilePushErpServic")
@Slf4j
public class PilePushErpService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StockPushKingdeeOrderRepository stockPushKingdeeOrderRepository;

    @Autowired
    private StockPushKingdeeReturnGoodsRepository stockPushKingdeeReturnGoodsRepository;

    /**
     * 囤货
     */
    @Value("${kingdee.stockUp.url}")
    private String stockUpUrl;

    /**
     * 提货
     */
    @Value("${kingdee.stockUpRet.url}")
    private String stockUpRetUrl;

    /**
     * 推销售订单到金蝶
     */
    public void pushSalesKingdee(PileTradePushRequest tradePushRequest){
        if (Objects.nonNull(tradePushRequest)){
            //先查询
            StockPushKingdeeOrder stockPushKingdeeOrder = stockPushKingdeeOrderRepository.findStockPushKingdeeOrder(tradePushRequest.getFBillNo());
            if (Objects.isNull(stockPushKingdeeOrder)) {
                //插表
                StockPushKingdeeOrder kingdeeOrder = StockPushKingdeeOrder.builder()
                                                                          .stockOrderCode(tradePushRequest.getFBillNo())
                                                                          .pushStatus(PushKingdeeStatusEnum.CREATE.toStatus())
                                                                          .instructions("创建")
                                                                          .createTime(LocalDateTime.now())
                                                                          .updateTime(LocalDateTime.now())
                                                                          .build();
                stockPushKingdeeOrderRepository.save(kingdeeOrder);
            }
            if (tradePushRequest.getThreadPool()) {
                //使用线程池推金蝶囤货订单
                asynchronousPushSalesKingdee(tradePushRequest);
            }else {
                pushSalesKingdeeRequest(tradePushRequest);
            }
        }
    }

    //推金蝶囤货销售订单
//    @Async("pushKingdeeStockSalesBack")
    public void asynchronousPushSalesKingdee(PileTradePushRequest tradePushRequest){
        pushSalesKingdeeRequest(tradePushRequest);
    }

    /**
     * 推金蝶请求
     * @param tradePushRequest
     */
    private void pushSalesKingdeeRequest(PileTradePushRequest tradePushRequest){
        logger.info("PilePushErpService.pushSalesKingdeeRequest tradePushRequest:{}",JSONObject.toJSONString(tradePushRequest));
        if (StringUtils.isNotEmpty(tradePushRequest.getLoginToken())){
            StockPushKingdeeOrder kingdeeOrder = new StockPushKingdeeOrder();
            kingdeeOrder.setStockOrderCode(tradePushRequest.getFBillNo());
            try {
                PileTradePushModel tradePushModel = copyParameter(tradePushRequest);
                //提交财务单
                Map<String,Object> requestMap = new HashMap<>();
                requestMap.put("Model",tradePushModel);
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(stockUpUrl, requestMap, tradePushRequest.getLoginToken());
                logger.info("PilePushErpService.asynchronousPushSalesKingdee httpCommonResult:{}", httpCommonResult.getResultData());
                KingDeeErpResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeErpResult.class);
                logger.info("PilePushErpService.asynchronousPushSalesKingdee code:{}",kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")){
                    kingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                    kingdeeOrder.setInstructions(kingDeeResult.getMsg());
                }else {
                    kingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                    kingdeeOrder.setInstructions(kingDeeResult.getMsg());
                }
            }catch (Exception e){
                kingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                kingdeeOrder.setInstructions("推送失败");
                logger.error("PilePushErpService.asynchronousPushSalesKingdee error fBillNo：{} e:{}",tradePushRequest.getFBillNo(),e);
            }finally {
                kingdeeOrder.setUpdateTime(LocalDateTime.now());
                stockPushKingdeeOrderRepository.updateStockPushKingdeeOrder(kingdeeOrder);
            }
        }
    }

    private PileTradePushModel copyParameter(PileTradePushRequest tradePushRequest){
        PileTradePushModel pileTradePushModel = new PileTradePushModel();
        if (Objects.nonNull(tradePushRequest)){
            pileTradePushModel.setFBillNo(tradePushRequest.getFBillNo());
            pileTradePushModel.setFDate(tradePushRequest.getFDate());
            pileTradePushModel.setFSaleOrgId(tradePushRequest.getFSaleOrgId());
            pileTradePushModel.setFCustId(tradePushRequest.getFCustId());
            pileTradePushModel.setFSalerId(tradePushRequest.getFSalerId());
            pileTradePushModel.setFBankAccount(tradePushRequest.getFBankAccount());
            pileTradePushModel.setFCollectType(tradePushRequest.getFCollectType());
            pileTradePushModel.setFSetType(tradePushRequest.getFSetType());
            List<PileTradePushtableBodyModel> modelList = new ArrayList<>();
            if (tradePushRequest.getPushTableBodyRequestList().size()>0){
                tradePushRequest.getPushTableBodyRequestList().stream().forEach(pileTradePushTableBodyRequest -> {
                    PileTradePushtableBodyModel model = new PileTradePushtableBodyModel();
                    model.setFMaterialId(pileTradePushTableBodyRequest.getFMaterialId());
                    model.setFQty(pileTradePushTableBodyRequest.getFQty());
                    model.setFPrice(pileTradePushTableBodyRequest.getFPrice());
                    model.setFTaxPrice(pileTradePushTableBodyRequest.getFTaxPrice());
                    model.setFIsFree(pileTradePushTableBodyRequest.getFIsFree());
                    model.setFDSBMText(pileTradePushTableBodyRequest.getFDSBMText());
                    modelList.add(model);
                });
            }
            pileTradePushModel.setPushTableBodyRequestList(modelList);
        }
        return pileTradePushModel;
    }

    /**
     * 推送退货单到金蝶
     */
    public void pushReturnGoodsKingdee(PileTradePushReturnGoodsRequest pushReturnGoodsRequest){
        if (Objects.nonNull(pushReturnGoodsRequest)){
            StockPushKingdeeReturnGoods stockPushKingdeeReturnGoods = stockPushKingdeeReturnGoodsRepository.findStockPushKingdeeReturnGoodsOrder(pushReturnGoodsRequest.getFBillNo());
            if (Objects.isNull(stockPushKingdeeReturnGoods)) {
                StockPushKingdeeReturnGoods kingdeeReturnGoods = StockPushKingdeeReturnGoods.builder()
                                                                                            .stockReturnGoodsCode(pushReturnGoodsRequest.getFBillNo())
                                                                                            .instructions("创建")
                                                                                            .stockOrderCode(pushReturnGoodsRequest.getFSaleNum())
                                                                                            .pushStatus(PushKingdeeStatusEnum.CREATE.toStatus())
                                                                                            .createTime(LocalDateTime.now())
                                                                                            .updateTime(LocalDateTime.now())
                                                                                            .build();
                stockPushKingdeeReturnGoodsRepository.save(kingdeeReturnGoods);
            }
            if (pushReturnGoodsRequest.getThreadPool()) {
                //使用线程池推金蝶退货单
                asynchronousPushReturnGoodsKingdee(pushReturnGoodsRequest);
            }else {
                pushReturnGoodsKingdeeRequest(pushReturnGoodsRequest);
            }
        }
    }

    //推金蝶囤货退货订单
//    @Async("pushKingdeeStockReturnGoodsBack")
    public void asynchronousPushReturnGoodsKingdee(PileTradePushReturnGoodsRequest pushReturnGoodsRequest){
        pushReturnGoodsKingdeeRequest(pushReturnGoodsRequest);
    }

    /**
     * 推金蝶囤货退货请求
     * @param pushReturnGoodsRequest
     */
    private void pushReturnGoodsKingdeeRequest(PileTradePushReturnGoodsRequest pushReturnGoodsRequest){
        logger.info("PilePushErpService.pushReturnGoodsKingdeeRequest pushReturnGoodsRequest:{}",pushReturnGoodsRequest);
        if (StringUtils.isNotEmpty(pushReturnGoodsRequest.getLoginToken())){
            StockPushKingdeeReturnGoods returnGoods = new StockPushKingdeeReturnGoods();
            returnGoods.setStockReturnGoodsCode(pushReturnGoodsRequest.getFBillNo());
            try {
                PilePushKingdeeReturnGoodsModel returnGoodsModel = copyReturnGoodsParameter(pushReturnGoodsRequest);
                //提交财务单
                Map<String,Object> requestMap = new HashMap<>();
                requestMap.put("Model",returnGoodsModel);
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(stockUpRetUrl, requestMap, pushReturnGoodsRequest.getLoginToken());
                logger.info("PilePushErpService.asynchronousPushReturnGoodsKingdee httpCommonResult:{}", httpCommonResult.getResultData());
                KingDeeErpResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeErpResult.class);
                logger.info("PilePushErpService.asynchronousPushReturnGoodsKingdee code:{}",kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")){
                    returnGoods.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                    returnGoods.setInstructions(kingDeeResult.getMsg());
                }else {
                    returnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                    returnGoods.setInstructions(kingDeeResult.getMsg());
                }
            }catch (Exception e){
                returnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                returnGoods.setInstructions("推送失败");
                logger.error("PilePushErpService.asynchronousPushReturnGoodsKingdee error fBillNo：{} e:{}",pushReturnGoodsRequest.getFBillNo(),e);
            }finally {
                returnGoods.setUpdateTime(LocalDateTime.now());
                stockPushKingdeeReturnGoodsRepository.updateStockPushKingdeeReturnGoodsOrder(returnGoods);
            }
        }
    }

    private PilePushKingdeeReturnGoodsModel copyReturnGoodsParameter(PileTradePushReturnGoodsRequest pushReturnGoodsRequest){
        PilePushKingdeeReturnGoodsModel returnGoodsModel = new PilePushKingdeeReturnGoodsModel();
        if (Objects.nonNull(pushReturnGoodsRequest)){
            returnGoodsModel.setFBillNo(pushReturnGoodsRequest.getFBillNo());
            returnGoodsModel.setFDate(pushReturnGoodsRequest.getFDate());
            returnGoodsModel.setFSaleOrgId(pushReturnGoodsRequest.getFSaleOrgId());
            returnGoodsModel.setFRetcustId(pushReturnGoodsRequest.getFCustId());
            returnGoodsModel.setFSalerId(pushReturnGoodsRequest.getFSalerId());
            returnGoodsModel.setFSaleNum(pushReturnGoodsRequest.getFSaleNum());
            returnGoodsModel.setFNote(pushReturnGoodsRequest.getFNote());
            returnGoodsModel.setFBankAccount(pushReturnGoodsRequest.getFBankAccount());
            returnGoodsModel.setFCollectType(pushReturnGoodsRequest.getFCollectType());
            returnGoodsModel.setFSetType(pushReturnGoodsRequest.getFSetType());
            List<PilePushKingdeeReturnGoodsTableBodyModel> modelList = new ArrayList<>();
            if (pushReturnGoodsRequest.getPushReturnGoodsTableBodyRequestList().size()>0){
                pushReturnGoodsRequest.getPushReturnGoodsTableBodyRequestList().stream().forEach(pushReturnGoodsTableBodyRequest -> {
                    PilePushKingdeeReturnGoodsTableBodyModel model = new PilePushKingdeeReturnGoodsTableBodyModel();
                    model.setFMaterialId(pushReturnGoodsTableBodyRequest.getFMaterialId());
                    model.setFQty(pushReturnGoodsTableBodyRequest.getFQty());
                    model.setFLot(pushReturnGoodsTableBodyRequest.getFLot());
                    model.setFStockId(pushReturnGoodsTableBodyRequest.getFStockId());
                    model.setFTaxPrice(pushReturnGoodsTableBodyRequest.getFTaxPrice());
                    modelList.add(model);
                });
            }
            returnGoodsModel.setPushReturnGoodsTableBodyRequestList(modelList);
        }
        return returnGoodsModel;
    }

}
