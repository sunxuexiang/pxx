package com.wanmi.sbc.wms.provider.impl.requestwms;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.request.wms.InventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import com.wanmi.sbc.wms.constant.ERPWareHouseConstants;
import com.wanmi.sbc.wms.requestwms.model.Inventory;
import com.wanmi.sbc.wms.requestwms.model.InventoryQueryReturn;
import com.wanmi.sbc.wms.requestwms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: RequestWMSInventroyController
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 17:55
 * @Version: 1.0
 */
@RestController
public class RequestWMSInventroyController implements RequestWMSInventoryProvider {

    @Autowired
    private InventoryService inventoryService;

    @Override
    public BaseResponse<InventoryQueryResponse> queryInventory(@RequestBody @Valid InventoryQueryRequest recordAddRequest) {

        String Lotatt04 = recordAddRequest.getLotatt04();
        Inventory inventory = new Inventory();
        KsBeanUtil.copyPropertiesThird(recordAddRequest, inventory);
        List<InventoryQueryReturnVO> list = this.queryWareStockFromWms(inventory,Lotatt04);
        return BaseResponse.success(new InventoryQueryResponse(list));
    }

    @Override
    public BaseResponse<InventoryQueryResponse> batchQueryInventory(@RequestBody @Valid BatchInventoryQueryRequest batchInventoryQueryRequest) {
        if(CollectionUtils.isEmpty(batchInventoryQueryRequest.getSkuIds())){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        String skuStr = batchInventoryQueryRequest.getSkuIds().toString();
        String sku = skuStr.substring(1,skuStr.length() -1);
        batchInventoryQueryRequest.setSKU(sku);
        Inventory inventory = new Inventory();
        String Lotatt04 = batchInventoryQueryRequest.getLotatt04();
        KsBeanUtil.copyPropertiesThird(batchInventoryQueryRequest, inventory);
        List<InventoryQueryReturnVO> list = this.queryWareStockFromWms(inventory,Lotatt04);
        return BaseResponse.success(new InventoryQueryResponse(list));
    }

    @Override
    public BaseResponse<InventoryQueryResponse> queryInventoryBySku(@RequestBody @Valid InventoryQueryRequest inventoryQueryRequest) {
        if(CollectionUtils.isEmpty(inventoryQueryRequest.getWareHouseCode())){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<InventoryQueryReturnVO> returnInventorys = new ArrayList<>();
        Inventory inventory = new Inventory();
        KsBeanUtil.copyPropertiesThird(inventoryQueryRequest, inventory);
        List<InventoryQueryReturnVO> inventoryQueryReturnVOS = this.queryWareStockFromWms(inventory,null);
        List<InventoryQueryReturnVO> filterInfos = inventoryQueryReturnVOS.stream()
                .filter(i->inventoryQueryRequest.getWareHouseCode().contains(i.getLotatt04())).collect(Collectors.toList());
        //1.分仓处理
        Map<String,List<InventoryQueryReturnVO>> stockInventoryMap = filterInfos.stream().collect(Collectors.groupingBy(InventoryQueryReturnVO::getWarehouseId));

        for(String wareCode: stockInventoryMap.keySet()){
            //2.根据sku处理
            Map<String,List<InventoryQueryReturnVO>> leftMap =  stockInventoryMap.get(wareCode).stream().collect(Collectors.groupingBy(InventoryQueryReturnVO::getSku));
            for(String id: leftMap.keySet()){
                InventoryQueryReturnVO inventoryQueryReturn1 = new InventoryQueryReturnVO();
                List<InventoryQueryReturnVO> chilList = leftMap.get(id);
                chilList.sort((a,b) -> a.getLotatt01().compareTo(b.getLotatt01()));
                if(chilList.size() == 1){
                    KsBeanUtil.copyPropertiesThird(chilList.get(0),inventoryQueryReturn1);
                }else if(chilList.size() > 1){
                    BigDecimal stockSum = chilList.stream().map(InventoryQueryReturnVO::getQty).reduce(BigDecimal.ZERO,(a,b)->a.add(b));
                    KsBeanUtil.copyPropertiesThird(chilList.get(0),inventoryQueryReturn1);
                    inventoryQueryReturn1.setQty(stockSum);
                    inventoryQueryReturn1.setLotatt01(chilList.get(0).getLotatt01() + "~" + chilList.get(chilList.size()-1).getLotatt01());
                }
                inventoryQueryReturn1.setStockNum(inventoryQueryReturn1.getQty());
                returnInventorys.add(inventoryQueryReturn1);
            }
        }
        return BaseResponse.success(new InventoryQueryResponse(returnInventorys));
    }

    /**
     * 查询库存
     * @return
     */
    private List<InventoryQueryReturnVO> queryWareStockFromWms(Inventory inventory ,String Lotatt04){

        List<InventoryQueryReturn> resultInventories = new ArrayList<>();
        List<InventoryQueryReturn> inventoryQueryReturn = inventoryService.queryInventory(inventory);
        if(CollectionUtils.isEmpty(inventoryQueryReturn)){
            return new ArrayList<>();
        }
        //普通商品的查库存需要合并同仓的数据
        if(!CollectionUtils.isEmpty(inventoryQueryReturn)
                && Objects.nonNull(Lotatt04)
                && !(ERPWareHouseConstants.MAIN_MARKETING_WH.equals(Lotatt04) || ERPWareHouseConstants.SUB_MARKETING_WH.equals(Lotatt04))){
            //1.分仓处理
            Map<String,List<InventoryQueryReturn>> stockInventoryMap = inventoryQueryReturn.stream().collect(Collectors.groupingBy(InventoryQueryReturn::getWarehouseId));
            for(String wareCode: stockInventoryMap.keySet()){
                //2.根据sku处理
                Map<String,List<InventoryQueryReturn>> leftMap =  stockInventoryMap.get(wareCode).stream().collect(Collectors.groupingBy(InventoryQueryReturn::getSku));
                for(String id: leftMap.keySet()){
                    InventoryQueryReturn inventoryQueryReturn1 = new InventoryQueryReturn();
                    List<InventoryQueryReturn> chilList = leftMap.get(id);
                    chilList.sort((a,b) -> a.getLotatt01().compareTo(b.getLotatt01()));
                    if(chilList.size() == 1){
                        KsBeanUtil.copyPropertiesThird(chilList.get(0),inventoryQueryReturn1);
                    }else if(chilList.size() > 1){
                        BigDecimal stockSum = chilList.stream().map(InventoryQueryReturn::getQty).reduce(BigDecimal.ZERO,(a,b)->a.add(b));
                        KsBeanUtil.copyPropertiesThird(chilList.get(0),inventoryQueryReturn1);
                        inventoryQueryReturn1.setQty(stockSum);
                        inventoryQueryReturn1.setLotatt01(chilList.get(0).getLotatt01() + "~" + chilList.get(chilList.size()-1).getLotatt01());
                    }
                    inventoryQueryReturn1.setStockNum(inventoryQueryReturn1.getQty());
                    resultInventories.add(inventoryQueryReturn1);
                }
            }
        }else{
            resultInventories = inventoryQueryReturn;
        }
        return KsBeanUtil.convertList(resultInventories,InventoryQueryReturnVO.class);
    }
}
