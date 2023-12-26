package com.wanmi.sbc.goods.provider.impl.freight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.freight.*;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateGoodsByIdsResponse;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateGoodsDefaultByStoreIdResponse;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateGoodsListByStoreIdResponse;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateGoodsVO;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoods;
import com.wanmi.sbc.goods.freight.service.FreightTemplateGoodsService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseWhereCriteriaBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * <p>对单品运费模板查询接口</p>
 * Created by daiyitian on 2018-10-31-下午6:23.
 */
@RestController
@Validated
public class FreightTemplateGoodsQueryController implements FreightTemplateGoodsQueryProvider {

    @Autowired
    private FreightTemplateGoodsService freightTemplateGoodsService;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    /**
     * 根据店铺id查询单品运费模板
     *
     * @param request 包含店铺id的查询请求结构 {@link FreightTemplateGoodsListByStoreIdRequest}
     * @return 单品运费模板列表 {@link FreightTemplateGoodsListByStoreIdResponse}
     */
    @Override
    public BaseResponse<FreightTemplateGoodsListByStoreIdResponse> listByStoreId(@RequestBody @Valid
                                                                                  FreightTemplateGoodsListByStoreIdRequest request){
        List<FreightTemplateGoods> templateGoods = freightTemplateGoodsService.queryAll(request.getStoreId(),request.getDeliverWay());
        List<FreightTemplateGoodsVO> voList = FreightTemplateGoodsConvert.convertFreightTemplateGoodsListToVoList(templateGoods);
        FreightTemplateGoodsListByStoreIdResponse build = FreightTemplateGoodsListByStoreIdResponse.builder()
                .freightTemplateGoodsVOList(voList).build();

        if (CollectionUtils.isNotEmpty(build.getFreightTemplateGoodsVOList())){
            List<WareHouse> wareHouseList = wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(WareHouseQueryRequest.builder().build()));
            build.getFreightTemplateGoodsVOList().forEach(freightTemplate -> {
                if(Objects.nonNull(freightTemplate.getWareId())){
                    freightTemplate.setWareName(wareHouseList.stream().filter(wareHouse -> wareHouse.getWareId().equals(freightTemplate.getWareId())).findFirst().get().getWareName());
                }
            });
        }
        return BaseResponse.success(build);
    }

    /**
     * 根据批量单品运费模板id查询单品运费模板列表
     *
     * @param request 包含批量ids的查询请求结构 {@link FreightTemplateGoodsListByIdsRequest}
     * @return 单品运费模板列表 {@link FreightTemplateGoodsByIdsResponse}
     */
    @Override
    public BaseResponse<FreightTemplateGoodsByIdsResponse> listByIds(@RequestBody @Valid
                                                                      FreightTemplateGoodsListByIdsRequest request){
        List<FreightTemplateGoods> templateGoods = freightTemplateGoodsService.queryAllByIds(request.getFreightTempIds());
        List<FreightTemplateGoodsVO> voList = FreightTemplateGoodsConvert.convertFreightTemplateGoodsListToVoList(templateGoods);
        return BaseResponse.success(FreightTemplateGoodsByIdsResponse.builder()
                .freightTemplateGoodsVOList(voList).build());
    }

    /**
     * 根据单品运费模板id查询单品运费模板
     *
     * @param request 包含id的查询请求结构 {@link FreightTemplateGoodsByIdRequest}
     * @return 单品运费模板 {@link FreightTemplateGoodsByIdResponse}
     */
    @Override
    public BaseResponse<FreightTemplateGoodsByIdResponse> getById(@RequestBody @Valid
                                                                   FreightTemplateGoodsByIdRequest request){
        FreightTemplateGoods templateGoods = freightTemplateGoodsService.queryById(request.getFreightTempId());
        FreightTemplateGoodsByIdResponse freightTemplateGoodsByIdResponse = FreightTemplateGoodsConvert.convertFreightTemplateGoodsToResponse(templateGoods);


        if (Objects.nonNull(freightTemplateGoodsByIdResponse.getWareId())){
            List<WareHouse> wareHouseList = wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(WareHouseQueryRequest.builder().build()));
            freightTemplateGoodsByIdResponse.setWareName(wareHouseList.stream().filter(wareHouse -> wareHouse.getWareId().equals(freightTemplateGoodsByIdResponse.getWareId())).findFirst().get().getWareName());
        }
        return BaseResponse.success(freightTemplateGoodsByIdResponse);
    }


    /**
     * 根据单品运费模板id验证单品运费模板
     *
     * @param request 包含id的验证请求结构 {@link FreightTemplateGoodsExistsByIdRequest}
     * @return {@link BaseResponse}
     */
    @Override
    public BaseResponse existsById(@RequestBody @Valid FreightTemplateGoodsExistsByIdRequest request){
        freightTemplateGoodsService.hasFreightTemp(request.getFreightTempId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据店铺id查询默认单品运费模板请求
     *
     * @param request 包含店铺id的查询请求结构 {@link FreightTemplateGoodsDefaultByStoreIdRequest}
     * @return 单品运费模板列表 {@link FreightTemplateGoodsDefaultByStoreIdResponse}
     */
    @Override
    public BaseResponse<FreightTemplateGoodsDefaultByStoreIdResponse> getDefaultByStoreId(@RequestBody @Valid
                                                                                                  FreightTemplateGoodsDefaultByStoreIdRequest request) {
        List<FreightTemplateGoods> templateGoods = freightTemplateGoodsService.queryByDefaultByStoreId(request.getStoreId(),request.getDeliverWay());
        if(CollectionUtils.isNotEmpty(templateGoods)){
            FreightTemplateGoodsDefaultByStoreIdResponse defultFreightTemplate = new FreightTemplateGoodsDefaultByStoreIdResponse();
            List<FreightTemplateGoodsVO> convert = KsBeanUtil.convert(templateGoods, FreightTemplateGoodsVO.class);
            defultFreightTemplate.setDefultFreightTemplate(convert);
            return BaseResponse.success(defultFreightTemplate);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<FreightTemplateGoodsByIdsResponse> queryTmplistDeliveryToStore() {
        return queryDeliveryToStoreTempList(false);
    }

    private BaseResponse queryDeliveryToStoreTempList(Boolean onlyDefault) {
        List<FreightTemplateGoods> templateGoods =freightTemplateGoodsService.queryPlatformDeliveryToStoreTempList(onlyDefault);
        if(CollectionUtils.isNotEmpty(templateGoods)){
            FreightTemplateGoodsByIdsResponse defultFreightTemplate = new FreightTemplateGoodsByIdsResponse();
            List<FreightTemplateGoodsVO> convert = KsBeanUtil.convert(templateGoods, FreightTemplateGoodsVO.class);
            defultFreightTemplate.setFreightTemplateGoodsVOList(convert);
            return BaseResponse.success(defultFreightTemplate);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<FreightTemplateGoodsByIdsResponse> queryTmplistDeliveryToStoreOpened() {
        return queryDeliveryToStoreTempList(true);
    }
}
