package com.wanmi.sbc.goods.provider.impl.devanninggoodsinfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByParentIdRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAndStockListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByInfoIdAndStepResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.response.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@Validated
public class DevanningGoodsInfoQueryController implements DevanningGoodsInfoQueryProvider {

    @Autowired
    private DevanningGoodsInfoService devanningGoodsInfoService;
    @Autowired
    private DevanningGoodsInfoRepository devanningGoodsInfoRepository;

    @Override
    public BaseResponse<DevanningGoodsInfoByInfoIdAndStepResponse> getInfoByIdAndStep(DevanningGoodsInfoRequest request) {

        DevanningGoodsInfo infoByIdAndStep = devanningGoodsInfoRepository.getInfoByIdAndStep(
                request.getDevanningGoodsInfoVO().getGoodsInfoId(),
                request.getDevanningGoodsInfoVO().getAddStep());

        DevanningGoodsInfoByInfoIdAndStepResponse devanningGoodsInfoByInfoIdAndStepResponse = new DevanningGoodsInfoByInfoIdAndStepResponse();
        if(Objects.nonNull(infoByIdAndStep)){
            devanningGoodsInfoByInfoIdAndStepResponse.setDevanningGoodsInfoVO(KsBeanUtil.convert(infoByIdAndStep, DevanningGoodsInfoVO.class));
        }

        return BaseResponse.success(devanningGoodsInfoByInfoIdAndStepResponse);
    }

    @Override
    public BaseResponse<DevanningGoodsInfoByIdResponse> getInfoById(DevanningGoodsInfoByIdRequest request) {
        Optional<DevanningGoodsInfo> byId = devanningGoodsInfoRepository.findById(request.getDevanningId());
        DevanningGoodsInfoByIdResponse devanningGoodsInfoByIdResponse = new DevanningGoodsInfoByIdResponse();
        byId.ifPresent(devanningGoodsInfo -> devanningGoodsInfoByIdResponse.setDevanningGoodsInfoVO(KsBeanUtil.convert(devanningGoodsInfo, DevanningGoodsInfoVO.class)));
        return BaseResponse.success(devanningGoodsInfoByIdResponse);
    }

    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> getInfoByIds(DevanningGoodsInfoByIdRequest request) {
        DevanningGoodsInfoListResponse devanningGoodsInfoListResponse = new DevanningGoodsInfoListResponse();
        List<DevanningGoodsInfo> devanningGoodsInfoByIds = devanningGoodsInfoRepository.getDevanningGoodsInfoByIds(request.getDevanningIds());
        devanningGoodsInfoListResponse.setDevanningGoodsInfoVOS(KsBeanUtil.convertList(devanningGoodsInfoByIds, DevanningGoodsInfoVO.class));
        return BaseResponse.success(devanningGoodsInfoListResponse);
    }

    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> listByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request) {
        List<DevanningGoodsInfo> goodsInfos = devanningGoodsInfoService.findByIds(request.getDevanningIds(), request.getWareId());
        return BaseResponse.success(DevanningGoodsInfoListResponse.builder()
                .devanningGoodsInfoVOS(KsBeanUtil.convertList(goodsInfos , DevanningGoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> listViewByIdsByMatchFlag(@Valid GoodsInfoViewByIdsRequest request) {
        DevanningGoodsInfoQueryRequest infoRequest = new DevanningGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        DevanningGoodsInfoResponse infoResponse = devanningGoodsInfoService.findSkuByIdsAndMatchFlag(infoRequest);
        return BaseResponse.success(
                DevanningGoodsInfoListResponse.builder()
                        .devanningGoodsInfoVOS(KsBeanUtil.convertList(infoResponse.getDevanningGoodsInfos(),DevanningGoodsInfoVO.class))
                        .goodses(KsBeanUtil.convert(infoResponse.getGoodses(),GoodsVO.class))
                        .build());
    }

    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> listViewByIdsByMatchFlagNoStock(@Valid GoodsInfoViewByIdsRequest request) {
        DevanningGoodsInfoQueryRequest infoRequest = new DevanningGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        DevanningGoodsInfoResponse infoResponse = devanningGoodsInfoService.findSkuByIdsAndMatchFlagNoStock(infoRequest);
        return BaseResponse.success(
                DevanningGoodsInfoListResponse.builder()
                        .devanningGoodsInfoVOS(devanningGoodsInfoService.getVOWithStoreName(infoResponse.getDevanningGoodsInfos()))
                        .goodses(KsBeanUtil.convert(infoResponse.getGoodses(),GoodsVO.class))
                        .build());
    }


    /**
     * 根据商品skuId批量查询商品sku视图列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoViewByIdsRequest}
     * @return 商品sku视图列表 {@link GoodsInfoViewByIdsResponse}
     */
    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> listViewByIds(@RequestBody @Valid GoodsInfoViewByIdsRequest
                                                                          request) {
        com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoRequest infoRequest = new com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        DevanningGoodsInfoResponse infoResponse = devanningGoodsInfoService.findSkuByIds(infoRequest);
        return BaseResponse.success(
                DevanningGoodsInfoListResponse.builder()
                        .devanningGoodsInfoVOS(KsBeanUtil.convertList(infoResponse.getDevanningGoodsInfos(), DevanningGoodsInfoVO.class))
                        .goodses(KsBeanUtil.convertList(infoResponse.getGoodses(), GoodsVO.class)).build());
    }


    /**
     * 根据动态条件查询商品sku列表
     *
     * @param request 根据动态条件查询结构 {@link GoodsInfoListByConditionRequest}
     * @return 商品sku列表 {@link GoodsInfoListByConditionResponse}
     */
    @Override

    public BaseResponse<DevanningGoodsInfoListResponse> listByCondition(@RequestBody @Valid DevanningGoodsInfoListByConditionRequest
                                                                                  request){
        DevanningGoodsInfoQueryRequest queryRequest = new DevanningGoodsInfoQueryRequest();

//        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);


        DevanningGoodsInfoListResponse response = DevanningGoodsInfoListResponse.builder()
                .devanningGoodsInfoVOS(KsBeanUtil.convertList(devanningGoodsInfoService.findByParams(queryRequest), DevanningGoodsInfoVO.class))
                .build();
        return BaseResponse.success(response);
    }



    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> listDevanningGoodsInfoAndStcokByIds(@Valid GoodsInfoAndStockListByIdsRequest request) {
        return BaseResponse.success(DevanningGoodsInfoListResponse.builder()
                .devanningGoodsInfoVOS(KsBeanUtil.convertList( devanningGoodsInfoService.findGoodsInfoAndStockByIds(request.getDevanningIds()
                        ,request.getGoodsInfoIds()
                        ,request.getMatchWareHouseFlag()) ,
                        DevanningGoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> listByParentId(DevanningGoodsInfoListByParentIdRequest request) {
        DevanningGoodsInfoQueryRequest queryRequest = DevanningGoodsInfoQueryRequest.builder().parentGoodsInfoId(request.getParentGoodsInfoId()).build();
        DevanningGoodsInfoListResponse response = DevanningGoodsInfoListResponse.builder()
                .devanningGoodsInfoVOS(KsBeanUtil.convertList(devanningGoodsInfoService.listByParentId(queryRequest), DevanningGoodsInfoVO.class))
                .build();
        return BaseResponse.success(response);
    }
    @Override
    public BaseResponse<DevanningGoodsInfoByIdResponse> findBySkuId(@RequestBody @Valid DevanningGoodsInfoRequest request) {
        List<String> goodsInfos=new ArrayList<>();
        goodsInfos.add(request.getDevanningGoodsInfoVO().getGoodsInfoId());
        List<DevanningGoodsInfo> byParams = devanningGoodsInfoService.findByParams(DevanningGoodsInfoQueryRequest.builder().goodsInfoIds(goodsInfos).build());
        DevanningGoodsInfoVO goodsInfoVO=new DevanningGoodsInfoVO();
        goodsInfoVO.setDevanningId(byParams.get(Constants.no).getDevanningId());
        DevanningGoodsInfoByIdResponse response =new DevanningGoodsInfoByIdResponse();
        response.setDevanningGoodsInfoVO(goodsInfoVO);
        return BaseResponse.success(response);
    }

    /**
     * 根据skuId集合批量查询拆箱表
     * @param request
     * @return 拆箱表集合(拆箱表id+skuId)
     */
    @Override
    public BaseResponse<DevanningGoodsInfoByIdResponse> findBySkuIds(@RequestBody @Valid GoodsInfoListByIdsRequest request) {
        List<DevanningGoodsInfo> devanningGoodsInfoList = devanningGoodsInfoService.findByParams(DevanningGoodsInfoQueryRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).build());
        DevanningGoodsInfoByIdResponse response = new DevanningGoodsInfoByIdResponse();
        List<DevanningGoodsInfoVO> devanningGoodsInfoVOList = devanningGoodsInfoList.stream().map(devanningGoodsInfo -> {
            DevanningGoodsInfoVO devanningGoodsInfoVO = new DevanningGoodsInfoVO();
            devanningGoodsInfoVO.setDevanningId(devanningGoodsInfo.getDevanningId());
            devanningGoodsInfoVO.setGoodsInfoId(devanningGoodsInfo.getGoodsInfoId());
            return devanningGoodsInfoVO;
        }).collect(Collectors.toList());
        response.setDevanningGoodsInfoVOList(devanningGoodsInfoVOList);
        return BaseResponse.success(response);
    }
}
