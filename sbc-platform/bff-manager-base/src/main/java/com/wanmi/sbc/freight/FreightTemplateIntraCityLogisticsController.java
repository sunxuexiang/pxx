package com.wanmi.sbc.freight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.NoDeleteStoreByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyTypeRequest;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.freight.*;
import com.wanmi.sbc.goods.api.request.freight.*;
import com.wanmi.sbc.goods.api.response.freight.*;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateGoodsVO;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateStoreVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * 运费模板控制器
 * Created by sunkun on 2018/5/2.
 */
@Api(tags = "FreightTemplateIntraCityLogisticsController", description = "同城配送运费模板服务API")
@RestController
@RequestMapping("/freightTemplateIntraCityLogistics")
public class FreightTemplateIntraCityLogisticsController {

    @Autowired
    private FreightTemplateGoodsQueryProvider freightTemplateGoodsQueryProvider;

    @Autowired
    private FreightTemplateGoodsProvider freightTemplateGoodsProvider;

    @Autowired
    private FreightTemplateStoreProvider freightTemplateStoreProvider;

    @Autowired
    private FreightTemplateStoreQueryProvider freightTemplateStoreQueryProvider;

    @Autowired
    private FreightTemplateGoodsExpressQueryProvider freightTemplateGoodsExpressQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 查询店铺下所有单品运费模板
     *
     * @return
     */
    @ApiOperation(value = "查询店铺下所有单品运费模板")
    @RequestMapping(value = "/freightTemplateGoods", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateGoodsVO>> getFreightTemplateGoodsAll() {
        Long storeId = commonUtil.getStoreIdWithDefault();
        if (storeId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(commonUtil.getStoreIdWithDefault()))
                .getContext().getStoreVO();
        if(Objects.nonNull(store.getCompanyType())){
            if(store.getCompanyType().toValue()==4) {
                store = storeQueryProvider.findByCompanyType(new StoreByCompanyTypeRequest(CompanyType.fromValue(CompanyType.PLATFORM.toValue()))).getContext().getStoreVO();
                storeId=store.getStoreId();
            }
        }
        List<FreightTemplateGoodsVO> voList = freightTemplateGoodsQueryProvider.listByStoreId(
                FreightTemplateGoodsListByStoreIdRequest.builder().storeId(storeId).deliverWay(DeliverWay.EXPRESS.toValue()).build()
        ).getContext().getFreightTemplateGoodsVOList();

        return BaseResponse.success(voList);
    }

    @ApiOperation(value = "查询配送到店下所有单品运费模板")
    @RequestMapping(value = "/freightTemplateDeliveryToStore", method = RequestMethod.GET)
    public BaseResponse<List<FreightTemplateGoodsVO>> getFreightTemplateDeliveryToStore() {
        Long storeId = commonUtil.getStoreIdWithDefault();
        if (storeId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightTemplateGoodsByIdsResponse freightTemplateGoodsByIdsResponse = freightTemplateGoodsQueryProvider.queryTmplistDeliveryToStore().getContext();
        if(null!=freightTemplateGoodsByIdsResponse){
            return BaseResponse.success(freightTemplateGoodsByIdsResponse.getFreightTemplateGoodsVOList());
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "查询配送到店下所有单品运费模板")
    @RequestMapping(value = "/freightTemplateDeliveryToStoreOpened", method = RequestMethod.GET)
    public BaseResponse<FreightDefaultDeliveryToStoreRes> getfreightTemplateDeliveryToStoreOpened() {
        Long storeId = commonUtil.getStoreIdWithDefault();
        if (storeId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        FreightDefaultDeliveryToStoreRes res = new FreightDefaultDeliveryToStoreRes();
        FreightTemplateDeliveryAreaListRequest queryRequest=FreightTemplateDeliveryAreaListRequest
                .builder().companyInfoId(Constants.BOSS_DEFAULT_COMPANY_INFO_ID)
                .storeId(Constants.BOSS_DEFAULT_STORE_ID).build();
        List<FreightTemplateDeliveryAreaVO> deliveryAreaVOList = freightTemplateDeliveryAreaQueryProvider.queryDeliveryToStore(queryRequest).getContext();
        if(CollectionUtils.isNotEmpty(deliveryAreaVOList)){
            res.setFreightTemplateDeliveryAreaVO(deliveryAreaVOList.get(0));
        }
        FreightTemplateGoodsByIdsResponse freightTemplateGoodsByIdsResponse = freightTemplateGoodsQueryProvider.queryTmplistDeliveryToStoreOpened().getContext();
        if(null!=freightTemplateGoodsByIdsResponse){
            res.setFreightTemplateGoodsVOList(freightTemplateGoodsByIdsResponse.getFreightTemplateGoodsVOList());
        }
        return BaseResponse.success(res);
    }

    /**
     * 查询单品运费模板
     *
     * @param freightTempId
     * @return
     */
    @ApiOperation(value = "查询单品运费模板")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "freightTempId", value = "运费模板Id",
            required = true)
    @RequestMapping(value = "/freightTemplateGoods/{freightTempId}", method = RequestMethod.GET)
    public BaseResponse<FreightTemplateGoodsVO> getFreightTemplateGoodsById(@PathVariable Long freightTempId) {
        FreightTemplateGoodsVO freightTemplateGoods = freightTemplateGoodsQueryProvider.getById(
                FreightTemplateGoodsByIdRequest.builder().freightTempId(freightTempId).build()
        ).getContext();
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(commonUtil.getStoreIdWithDefault()))
                .getContext().getStoreVO();
        if(Objects.nonNull(store.getCompanyType())){
            if(store.getCompanyType().toValue()!=4) {
                if (!Objects.equals(freightTemplateGoods.getStoreId(), commonUtil.getStoreIdWithDefault())) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
            }
        }
        return BaseResponse.success(freightTemplateGoods);
    }

    /**
     * 更新单品运费模板
     *
     * @return
     */
    @ApiOperation(value = "更新单品运费模板")
    @RequestMapping(value = "/freightTemplateGoods", method = RequestMethod.POST)
    public BaseResponse renewalFreightTemplateGoods(@Valid @RequestBody FreightTemplateGoodsSaveRequest freightTemplateGoodsSaveRequest) {
        if (commonUtil.getStoreIdWithDefault() == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        freightTemplateGoodsSaveRequest.setCompanyInfoId(commonUtil.getCompanyInfoIdWithDefault());
        freightTemplateGoodsSaveRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        if (isNull(freightTemplateGoodsSaveRequest.getFreightTempId())) {
            operateLogMQUtil.convertAndSend("设置", "新增单品运费模板",
                    "新增单品运费模板：" + freightTemplateGoodsSaveRequest.getFreightTempName());
        } else {
            operateLogMQUtil.convertAndSend("设置", "编辑单品运费模板",
                    "编辑单品运费模板：" + freightTemplateGoodsSaveRequest.getFreightTempName());
        }
        return freightTemplateGoodsProvider.save(freightTemplateGoodsSaveRequest);

    }

    /**
     * 更新店铺运费模板
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "更新店铺运费模板")
    @RequestMapping(value = "/freightTemplateStore", method = RequestMethod.POST)
    public BaseResponse renewalFreightTemplateStore(@Valid @RequestBody FreightTemplateStoreSaveRequest request) {
        request.setCompanyInfoId(commonUtil.getCompanyInfoIdWithDefault());
        request.setStoreId(commonUtil.getStoreIdWithDefault());

        if (isNull(request.getFreightTempId())) {
            operateLogMQUtil.convertAndSend("设置", "新增店铺运费模板",
                    "新增店铺运费模板：" + request.getFreightTempName());
        } else {
            operateLogMQUtil.convertAndSend("设置", "编辑店铺运费模板",
                    "编辑店铺运费模板：" + request.getFreightTempName());
        }

        return freightTemplateStoreProvider.save(request);
    }

    /**
     * 根据id获取店铺运费模板
     *
     * @param freightTempId
     * @return
     */
    @ApiOperation(value = "根据id获取店铺运费模板")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "freightTempId", value = "运费模板Id",
            required = true)
    @RequestMapping(value = "/freightTemplateStore/{freightTempId}", method = RequestMethod.GET)
    public BaseResponse<FreightTemplateStoreVO> getFreightTemplateStore(@PathVariable Long freightTempId) {
        FreightTemplateStoreVO freightTemplateStore = freightTemplateStoreQueryProvider.getById(
                FreightTemplateStoreByIdRequest.builder().freightTempId(freightTempId).build()
        ).getContext();
        Long storeId = commonUtil.getStoreIdWithDefault();
        if (!Objects.equals(freightTemplateStore.getStoreId(), storeId)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        freightTemplateStore.setSelectedAreas(freightTemplateStoreQueryProvider.queryAreaIdsByIdAndStoreId(
                FreightTemplateStoreAreaIdByIdAndStoreIdRequest.builder()
                        .freightTempId(freightTempId).storeId(storeId).build()
        ).getContext().getAreaIds());
        return BaseResponse.success(freightTemplateStore);
    }


    /**
     * 查询店铺运费模板已选区域
     *
     * @return
     */
    @ApiOperation(value = "查询店铺运费模板已选区域")
    @RequestMapping(value = "/freightTemplateStore/selected/area", method = RequestMethod.GET)
    public BaseResponse<List<Long>> getFreigthTemplateStoreSelectedArea() {
        if (commonUtil.getStoreIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return BaseResponse.success(freightTemplateStoreQueryProvider.queryAreaIdsByIdAndStoreId(
                FreightTemplateStoreAreaIdByIdAndStoreIdRequest.builder()
                        .freightTempId(0L).storeId(commonUtil.getStoreIdWithDefault()).build()
        ).getContext().getAreaIds());
    }

    /**
     * 查询店铺下所有店铺运费模板
     *
     * @return
     */
    @ApiOperation(value = "查询店铺下所有店铺运费模板")
    @RequestMapping(value = "/freightTemplateStore/list", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<FreightTemplateStoreVO>> getFreightTemplateStoreAll(@RequestBody FreightTemplateStorePageRequest request) {
        Long storeId = commonUtil.getStoreIdWithDefault();
        if (storeId == null || commonUtil.getCompanyInfoIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setStoreId(storeId);
        MicroServicePage<FreightTemplateStoreVO> page =new MicroServicePage<>();
        if(Objects.nonNull(freightTemplateStoreQueryProvider.page(request))){
            page = freightTemplateStoreQueryProvider.page(request).getContext().getFreightTemplateStorePage();
            if (request.getPageNum() > 0 && CollectionUtils.isEmpty(page.getContent())) {
                request.setPageNum(0);
                page = freightTemplateStoreQueryProvider.page(request).getContext().getFreightTemplateStorePage();
            }
        }
        return BaseResponse.success(page);
    }

    /**
     * 根据主键删除单品运费模板
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据主键删除单品运费模板")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "运费模板Id", required = true)
    @RequestMapping(value = "/freightTemplateGoods/{id}", method = RequestMethod.DELETE)
    public BaseResponse delFreightTemplateGoods(@PathVariable Long id) {
        String name = getFreightTemplateGoodsName(id);

        operateLogMQUtil.convertAndSend("设置", "删除单品运费模板",
                "删除单品运费模板：" + name);
        return freightTemplateGoodsProvider.deleteByIdAndStoreId(
                FreightTemplateGoodsDeleteByIdAndStoreIdRequest.builder()
                        .freightTempId(id).storeId(commonUtil.getStoreIdWithDefault()).deliverWay(DeliverWay.EXPRESS.toValue()).build());
    }

    @ApiOperation(value = "根据主键删除单品运费模板")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "运费模板Id", required = true)
    @RequestMapping(value = "/freightTemplateGoods/updateDelFlag/{id}", method = RequestMethod.DELETE)
    public BaseResponse updateDelFlagById(@PathVariable Long id) {
        String name = getFreightTemplateGoodsName(id);

        operateLogMQUtil.convertAndSend("设置", "删除单品运费模板",
                "删除单品运费模板：" + name);
        return freightTemplateGoodsProvider.updateDelFlagById(
                FreightTemplateGoodsDeleteByIdAndStoreIdRequest.builder()
                        .freightTempId(id).storeId(commonUtil.getStoreIdWithDefault()).build());
    }


    @ApiOperation(value = "更新单品运费模板")
    @RequestMapping(value = "/updateDefaultFlag", method = RequestMethod.POST)
    public BaseResponse updateFreightDefaultFlag(@Valid @RequestBody FreightTemplateGoodsModifyRequest freightTemplateGoodsSaveRequest) {
        if (Objects.isNull(freightTemplateGoodsSaveRequest) || Objects.isNull(freightTemplateGoodsSaveRequest.getFreightTempId()) ||Objects.isNull(freightTemplateGoodsSaveRequest.getDefaultFlag())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        freightTemplateGoodsSaveRequest.setStoreId(commonUtil.getStoreIdWithDefault());
        if (freightTemplateGoodsSaveRequest.getDefaultFlag()==DefaultFlag.YES.toValue()) {
            operateLogMQUtil.convertAndSend("设置", "启用配送到店运费模板",
                    "启用配送到店运费模板：" + freightTemplateGoodsSaveRequest.getFreightTempId());
        } else {
            operateLogMQUtil.convertAndSend("设置", "停用配送到店运费模板",
                    "停用配送到店运费模板：" + freightTemplateGoodsSaveRequest.getFreightTempId());
        }
        return freightTemplateGoodsProvider.updateTemplateDefaultFlag(freightTemplateGoodsSaveRequest);

    }

    /**
     * 复制单品运费模板
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "复制单品运费模板")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "运费模板Id", required = true)
    @RequestMapping(value = "/freightTemplateGoods/{id}", method = RequestMethod.PUT)
    public BaseResponse copyFreightTemplateGoods(@PathVariable Long id) {

        operateLogMQUtil.convertAndSend("设置", "复制单品运费模板",
                "复制单品运费模板：" + getFreightTemplateGoodsName(id));
        return freightTemplateGoodsProvider.copyByIdAndStoreId(
                FreightTemplateGoodsCopyByIdAndStoreIdRequest.builder()
                        .freightTempId(id)
                        .storeId(commonUtil.getStoreIdWithDefault()).deliverWay(DeliverWay.EXPRESS).build());
    }

    /**
     * 根据主键删除店铺运费模板
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据主键删除店铺运费模板")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "运费模板Id", required = true)
    @RequestMapping(value = "/freightTemplateStore/{id}", method = RequestMethod.DELETE)
    public BaseResponse delFreightTemplateStore(@PathVariable Long id) {
        FreightTemplateStoreByIdResponse store =
                freightTemplateStoreQueryProvider.getById(FreightTemplateStoreByIdRequest.builder().freightTempId(id).build()).getContext();
        operateLogMQUtil.convertAndSend("设置", "删除店铺运费模板",
                "删除店铺运费模板：" + (Objects.nonNull(store) ? store.getFreightTempName() : ""));

        BaseResponse response = freightTemplateStoreProvider.deleteByIdAndStoreId(
                FreightTemplateStoreDeleteByIdAndStoreIdRequest.builder()
                        .freightTempId(id).storeId(commonUtil.getStoreIdWithDefault()).build());
        return response;
    }

    @ApiOperation(value = "根据主键查询单品运费模板")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "freightTempId", value = "运费模板Id", required = true)
    @RequestMapping(value = "/freightTemplateExpress/{freightTempId}", method = RequestMethod.GET)
    public BaseResponse<FreightTemplateGoodsExpressByIdResponse> findByFreightTempIdAndDelFlag(@PathVariable Long freightTempId) {
        BaseResponse<FreightTemplateGoodsExpressByIdResponse> baseResponse =
                freightTemplateGoodsExpressQueryProvider.getById(new FreightTemplateGoodsExpressByIdRequest(freightTempId));
        return BaseResponse.success(baseResponse.getContext());
    }

    /**
     * 公共方法获取运费模板名称
     *
     * @param id
     * @return
     */
    private String getFreightTemplateGoodsName(Long id) {
        FreightTemplateGoodsByIdResponse goodsByIdResponse =
                freightTemplateGoodsQueryProvider.getById(FreightTemplateGoodsByIdRequest.builder().freightTempId(id).build()).getContext();
        return Objects.nonNull(goodsByIdResponse) ? goodsByIdResponse.getFreightTempName() : " ";
    }
}
