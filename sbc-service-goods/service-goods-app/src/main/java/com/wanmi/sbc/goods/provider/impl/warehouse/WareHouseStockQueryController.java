package com.wanmi.sbc.goods.provider.impl.warehouse;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseStockQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.*;
import com.wanmi.sbc.goods.bean.vo.WareHouseStockVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseStock;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import com.wanmi.sbc.goods.warehouse.service.WareHouseStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商品库位关联表服务接口实现</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@RestController
@Validated
public class WareHouseStockQueryController implements WareHouseStockQueryProvider {
    @Autowired
    private WareHouseStockService wareHouseStockService;


    @Override
    public BaseResponse<WareHouseBySkuIdResponse> getByWareHouseIdAndSkuId(@RequestBody @Valid WareHouseBySkuIdRequest wareHouseBySkuIdRequest) {
        List<WareHouseStock> wareHouseStockList =
                wareHouseStockService.getByWareIdAndSkuId(wareHouseBySkuIdRequest.getWareId(), wareHouseBySkuIdRequest.getSkuId());
        List<WareHouseStockVO> wareHouseStockVOList = wareHouseStockService.wrapperVo(wareHouseStockList);
        WareHouseBySkuIdResponse wareHouseBySkuIdResponse = new WareHouseBySkuIdResponse();
        if(!CollectionUtils.isEmpty(wareHouseStockVOList)){
            StringBuilder sb = new StringBuilder();
            for(WareHouseStockVO wareHouseStockVO : wareHouseStockVOList){
                sb = sb.append(wareHouseStockVO.getStockName()).append(",");
            }
            wareHouseBySkuIdResponse.setStockName(sb.substring(0,sb.length()-1));
            wareHouseBySkuIdResponse.setSortStockName(wareHouseStockVOList.get(0).getSortStockName());
        }
        return BaseResponse.success(wareHouseBySkuIdResponse);
    }


}

