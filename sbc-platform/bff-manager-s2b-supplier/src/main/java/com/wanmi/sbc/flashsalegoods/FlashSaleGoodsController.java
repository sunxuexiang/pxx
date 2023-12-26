package com.wanmi.sbc.flashsalegoods;

import com.alipay.api.domain.WarehouseVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.api.constant.FlashSaleErrorCode;
import com.wanmi.sbc.goods.api.constant.WareHouseErrorCode;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.flashsalegoods.*;
import com.wanmi.sbc.goods.api.response.flashsalegoods.*;
import com.wanmi.sbc.goods.bean.vo.FlashSaleGoodsVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Api(description = "抢购商品表管理API", tags = "FlashSaleGoodsController")
@RestController
@RequestMapping(value = "/flashsalegoods")
public class FlashSaleGoodsController {

    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;

    @Autowired
    private FlashSaleGoodsSaveProvider flashSaleGoodsSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询抢购商品表")
    @PostMapping("/page")
    public BaseResponse<FlashSaleGoodsPageResponse> getPage(@RequestBody @Valid FlashSaleGoodsPageRequest pageReq) {
        pageReq.setStoreId(commonUtil.getStoreId());
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("id", "desc");
        return flashSaleGoodsQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询抢购商品表")
    @PostMapping("/list")
    public BaseResponse<FlashSaleGoodsListResponse> getList(@RequestBody @Valid FlashSaleGoodsListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("id", "desc");
        return flashSaleGoodsQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询抢购商品表")
    @GetMapping("/{id}")
    public BaseResponse<FlashSaleGoodsByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FlashSaleGoodsByIdRequest idReq = new FlashSaleGoodsByIdRequest();
        idReq.setId(id);
        return flashSaleGoodsQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增抢购商品表")
    @PostMapping("/batchAdd")
    public BaseResponse<FlashSaleGoodsAddResponse> batchAdd(@RequestBody @Valid FlashSaleGoodsBatchAddRequest addReq) {
        WareHouseVO mainWare = getMainWare();
        if (Objects.isNull(mainWare)){
            throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NOT_EXIST);
        }
        addReq.setWareId(mainWare.getWareId());
        FlashSaleGoodsVO vo = addReq.getFlashSaleGoodsVOList().get(0);
        LocalDateTime activityDate = DateUtil.parse(vo.getActivityDate()+vo.getActivityTime(),
                "yyyy-MM-ddHH:mm");
        if (LocalDateTime.now().isBefore(activityDate.minusHours(1))) {
            addReq.getFlashSaleGoodsVOList().forEach(flashSaleGoodsVO -> {
                flashSaleGoodsVO.setDelFlag(DeleteFlag.NO);
                flashSaleGoodsVO.setCreatePerson(commonUtil.getOperatorId());
                flashSaleGoodsVO.setStoreId(commonUtil.getStoreId());
                flashSaleGoodsVO.setCreateTime(LocalDateTime.now());
                flashSaleGoodsVO.setSalesVolume(0L);
                flashSaleGoodsVO.setActivityFullTime(activityDate);
            });
            operateLogMQUtil.convertAndSend("抢购商品表管理","新增抢购商品表","新增抢购商品表");
            return flashSaleGoodsSaveProvider.batchAdd(addReq);
        } else {
            throw new SbcRuntimeException(FlashSaleErrorCode.NOT_ADDED);
        }
    }

    @ApiOperation(value = "修改抢购商品表")
    @PutMapping("/modify")
    public BaseResponse<FlashSaleGoodsModifyResponse> modify(@RequestBody @Valid FlashSaleGoodsModifyRequest modifyReq) {
        WareHouseVO mainWare = getMainWare();
        if (Objects.isNull(mainWare)){
            throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NOT_EXIST);
        }
        modifyReq.setWareId(mainWare.getWareId());
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        modifyReq.setUpdateTime(LocalDateTime.now());
        if (Objects.isNull(modifyReq.getMaxNum())){
            modifyReq.setMaxNum(modifyReq.getStock());
        }
        operateLogMQUtil.convertAndSend("抢购商品表管理","修改抢购商品表","修改抢购商品表");
        return flashSaleGoodsSaveProvider.modify(modifyReq);
    }

    @ApiOperation(value = "根据id删除抢购商品表")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        WareHouseVO mainWare = getMainWare();
        if (Objects.isNull(mainWare)){
            throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NOT_EXIST);
        }
        FlashSaleGoodsDelByIdRequest delByIdReq = new FlashSaleGoodsDelByIdRequest();
        delByIdReq.setId(id);
        delByIdReq.setWareId(mainWare.getWareId());
        operateLogMQUtil.convertAndSend("抢购商品表管理","根据id删除抢购商品表","根据id删除抢购商品表");
        return flashSaleGoodsSaveProvider.deleteById(delByIdReq);
    }

    /**
     * @Description: 判断是否为未开始或是进行中的抢购商品
     * @param: goodsInfoId
     * @Author: Bob
     * @Date: 2019-06-12 15:11
     */
//    @ApiOperation(value = "判断是否为未开始或是进行中的抢购商品")
//    @GetMapping("/isFlashSale/{goodsInfoId}")
//    public BaseResponse isFlashSale(@PathVariable String goodsInfoId){
//
//        return BaseResponse.SUCCESSFUL();
//    }

//    @GetMapping("/get")
//    public BaseResponse flashsaleInfo(){
//
//    }

    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: 查询主仓信息
     * @Return: com.wanmi.sbc.goods.bean.vo.WareHouseVO
     * @Author: yxb
     * @Date: 2021/2/20 10:25
     */
    private WareHouseVO getMainWare(){
        List<WareHouseVO> wareHouseVOS = commonUtil.queryAllWareHouses();
        Optional<WareHouseVO> first = wareHouseVOS.stream()
                .filter(param -> DeleteFlag.NO.equals(param.getDelFlag()) && DefaultFlag.YES.equals(param.getDefaultFlag())).findFirst();
        return first.orElse(null);
    }

}
