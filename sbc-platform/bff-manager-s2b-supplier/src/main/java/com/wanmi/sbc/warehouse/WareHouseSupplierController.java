package com.wanmi.sbc.warehouse;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHousePickListResponse;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.warehouse.request.MatchWareHouseRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baijianzhong
 * @ClassName WareHourseController
 * @Date 2020-06-01 15:52
 * @Description TODO
 **/
@Api(tags = "WareHouseController", description = "匹配分仓")
@RestController
@RequestMapping(value = "/warehouse")
public class WareHouseSupplierController {

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 根据收货地址匹配分仓
     * @param request
     * @return
     */
    @ApiOperation("根据收货地址匹配分仓")
    @RequestMapping(value = "/match-ware-house-for-supplier", method = RequestMethod.POST)
    public BaseResponse<WareHouseListResponse> matchWareHouse(@RequestBody @Valid MatchWareHouseRequest request){
        List<WareHouseVO> wareHouseVO = this.matchWareStore(request.getCityCode());
        return BaseResponse.success(WareHouseListResponse.builder()
                .wareHouseVOList(wareHouseVO)
                .build());
    }

    /**
     * 匹配分仓——主仓和副仓
     * @param cityCode
     * @return
     */
    private List<WareHouseVO> matchWareStore(Long cityCode){
        Long storeId = commonUtil.getStoreId();
        List<WareHouseVO> returnWareHouses = new ArrayList<>();
        //1. 从redis里获取所有的
        List<WareHouseVO> wareHouseMainList;
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            if(CollectionUtils.isNotEmpty(wareHouseVOS)){
                wareHouseMainList = wareHouseVOS.stream()
                        .filter(wareHouseVO -> storeId.equals(wareHouseVO.getStoreId())).collect(Collectors.toList());
            }else{
                wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                        .storeId(storeId).build()).getContext().getWareHouseVOList();
            }
        }else{
            wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .storeId(storeId).build()).getContext().getWareHouseVOList();
        }
        List<WareHouseVO> mainWareHouseVOS = wareHouseMainList.stream().filter(w->PickUpFlag.NO.equals(w.getPickUpFlag())).collect(Collectors.toList());
        List<WareHouseVO> subWareHouseVOS = wareHouseMainList.stream().filter(w->PickUpFlag.YES.equals(w.getPickUpFlag())).collect(Collectors.toList());
        //设置selectedAreas
        mainWareHouseVOS.stream().forEach(w->{
            String[] cityIds = w.getDestinationArea().split(",");
            Long[] cityIdList = (Long[]) ConvertUtils.convert(cityIds, Long.class);
            w.setSelectedAreas(Arrays.asList(cityIdList));
        });
        //2. 匹配分仓信息
        if(mainWareHouseVOS.stream().anyMatch(w->w.getSelectedAreas().contains(cityCode))){
            List<WareHouseVO> matchedMainWareHouses = mainWareHouseVOS.stream().filter(w->w.getSelectedAreas()
                    .contains(cityCode)).collect(Collectors.toList());
            returnWareHouses.addAll(matchedMainWareHouses);
        }
        returnWareHouses.addAll(subWareHouseVOS);

        if(CollectionUtils.isNotEmpty(returnWareHouses)){
            return returnWareHouses;
        }
        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"该收获地址没有可配的仓库，请重新修改收货地址");
    }


    /**
     * 根据获取所有自提点
     * @return
     */
    @ApiOperation("根据收货地址匹配分仓")
    @RequestMapping(value = "/match-pick-up-for-supplier", method = RequestMethod.GET)
    public BaseResponse<WareHousePickListResponse> matchPickUpWareHouse(){

        Long storeId = commonUtil.getStoreId();
        //1. 从redis里获取所有的
        List<WareHouseVO> wareHouseMainList;
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            if(CollectionUtils.isNotEmpty(wareHouseVOS)){
                wareHouseMainList = wareHouseVOS.stream()
                        .filter(wareHouseVO -> storeId.equals(wareHouseVO.getStoreId())).collect(Collectors.toList());
            }else{
                wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                        .storeId(storeId).build()).getContext().getWareHouseVOList();
            }
        }else{
            wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .storeId(storeId).build()).getContext().getWareHouseVOList();
        }
        List<WareHouseVO> subWareHouseVOS = wareHouseMainList.stream().filter(w->PickUpFlag.YES.equals(w.getPickUpFlag())).collect(Collectors.toList());
        return BaseResponse.success(WareHousePickListResponse.builder()
                .wareHouseVOList(subWareHouseVOS)
                .build());
    }

}
