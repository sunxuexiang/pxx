package com.wanmi.sbc.goods.provider.impl.devanninggoodsinfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchNosModifyRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import com.wanmi.sbc.goods.redis.RedisCache;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@RestController
@Validated
public class DevanningGoodsInfoController implements DevanningGoodsInfoProvider {

    @Autowired
    private DevanningGoodsInfoService devanningGoodsInfoService;
    @Autowired
    private DevanningGoodsInfoRepository devanningGoodsInfoRepository;
    @Autowired
    private RedisCache redisCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse add(@Valid DevanningGoodsInfoRequest devanningGoodsInfoRequest) {
        devanningGoodsInfoRepository.save(KsBeanUtil.convert(devanningGoodsInfoRequest.getDevanningGoodsInfoVO(), DevanningGoodsInfo.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse addBatch(DevanningGoodsInfoListRequest devanningGoodsInfoRequest) {
        if(CollectionUtils.isEmpty(devanningGoodsInfoRequest.getDevanningGoodsInfoVOS())){
            return BaseResponse.SUCCESSFUL();
        }
        devanningGoodsInfoRequest.getDevanningGoodsInfoVOS().forEach(d->{
            devanningGoodsInfoRepository.save(KsBeanUtil.convert(d, DevanningGoodsInfo.class));
        });

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse update(DevanningGoodsInfoRequest devanningGoodsInfoRequest) {
        devanningGoodsInfoRepository.saveAndFlush(KsBeanUtil.convert(devanningGoodsInfoRequest.getDevanningGoodsInfoVO(), DevanningGoodsInfo.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<DevanningGoodsInfoResponse> getQueryList(@Valid DevanningGoodsInfoPageRequest request) {
     //   request.setAddedFlag(1);
        request.setDelFlag(0);
        request.setDivisorFlag(new BigDecimal(Integer.valueOf(1)));
        DevanningGoodsInfoQueryRequest devanningGoodsInfoQueryRequest = new DevanningGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, devanningGoodsInfoQueryRequest);
        List<DevanningGoodsInfo> allList = devanningGoodsInfoRepository.findAll(devanningGoodsInfoQueryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(allList)){
            allList= Collections.emptyList();
        }
        DevanningGoodsInfoResponse devanningGoodsInfoResponse =new DevanningGoodsInfoResponse();
        devanningGoodsInfoResponse.setDevanningGoodsInfoVOS(devanningGoodsInfoService.getVOWithStoreName(allList));
        //判断拆箱商品是否在购物车存在
//        devanningGoodsInfoResponse.getDevanningGoodsInfoVOS().forEach(v->{
//            if (v.getDivisorFlag().compareTo(BigDecimal.ONE)!=0){
//                if (Objects.nonNull(request.getDevanningId())){
//                    if (redisCache.HashHasKey(RedisKeyConstants.SHOPPING_CART_EXTRA_HASH.concat(request.getCustomerId())
//                            .concat(request.getWareId().toString()),request.getDevanningId().toString())){
//                        v.setIsExit(true);
//                    }else v.setIsExit(false);
//                }
//            }else {
//                v.setIsExit(false);
//            }
//        });

        return BaseResponse.success(devanningGoodsInfoResponse);
    }

    @Override
    public BaseResponse<DevanningGoodsInfoResponse> getBQueryList(DevanningGoodsInfoPageRequest request) {

        request.setDivisorFlag(new BigDecimal(Integer.valueOf(1)));
        DevanningGoodsInfoQueryRequest devanningGoodsInfoQueryRequest = new DevanningGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, devanningGoodsInfoQueryRequest);
        List<DevanningGoodsInfo> allList = devanningGoodsInfoRepository.findAll(devanningGoodsInfoQueryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(allList)){
            allList= Collections.emptyList();
        }
        DevanningGoodsInfoResponse devanningGoodsInfoResponse =new DevanningGoodsInfoResponse();
        devanningGoodsInfoResponse.setDevanningGoodsInfoVOS( KsBeanUtil.convertList(allList, DevanningGoodsInfoVO.class));
        //判断拆箱商品是否在购物车存在
//        devanningGoodsInfoResponse.getDevanningGoodsInfoVOS().forEach(v->{
//            if (v.getDivisorFlag().compareTo(BigDecimal.ONE)!=0){
//                if (Objects.nonNull(request.getDevanningId())){
//                    if (redisCache.HashHasKey(RedisKeyConstants.SHOPPING_CART_EXTRA_HASH.concat(request.getCustomerId())
//                            .concat(request.getWareId().toString()),request.getDevanningId().toString())){
//                        v.setIsExit(true);
//                    }else v.setIsExit(false);
//                }
//            }else {
//                v.setIsExit(false);
//            }
//        });

        return BaseResponse.success(devanningGoodsInfoResponse);
    }

    @Override
    public BaseResponse<DevanningGoodsInfoResponse> getmaxdata(@Valid DevanningGoodsInfoPageRequest request) {
        List<DevanningGoodsInfoVO> goodsDevanningMaxstep = devanningGoodsInfoService.getGoodsDevanningMaxstep(request);
        DevanningGoodsInfoResponse devanningGoodsInfoResponse =new DevanningGoodsInfoResponse();
        devanningGoodsInfoResponse.setDevanningGoodsInfoVOS(goodsDevanningMaxstep);
        return BaseResponse.success(devanningGoodsInfoResponse);
    }

    @Override
    public BaseResponse batchUpdateBatchNos(@RequestBody @Valid GoodsInfoBatchNosModifyRequest request) {
        return devanningGoodsInfoService.batchUpdateGoodsInfoBatchNo(request.getGoodsBatchNoDTOS());
    }

    @Override
    public BaseResponse<List<Long>> getIdsByStoreId(DevanningGoodsInfoPageRequest request) {
        return BaseResponse.success(devanningGoodsInfoService.getIdsByStoreId(request.getDevanningIds(),request.getStoreId()));
    }
}
