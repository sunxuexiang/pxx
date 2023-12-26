package com.wanmi.sbc.standard;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.ContractCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardImportProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.ContractCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateChildCateIdsByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsGetUsedStandardRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsListUsedGoodsIdRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardImportGoodsRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.standard.StandardGoodsIdsQueryResponse;
import com.wanmi.sbc.goods.api.response.standard.StandardGoodsListUsedGoodsIdResponse;
import com.wanmi.sbc.goods.api.response.standard.StandardGoodsPageResponse;
import com.wanmi.sbc.goods.api.response.standard.StandardImportGoodsResponse;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.intercepter.Resubmit;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品库服务
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "StoreStandardController", description = "商品库服务 API")
@RestController
@RequestMapping("/standard")
public class StoreStandardController {

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private StandardGoodsQueryProvider standardGoodsQueryProvider;

    @Autowired
    private ContractCateQueryProvider contractCateQueryProvider;

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    @Autowired
    private StandardImportProvider standardImportProvider;

    @Autowired
    private GoodsAresProvider goodsAresProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 查询商品
     *
     * @param pageRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<StandardGoodsPageResponse> list(@RequestBody StandardGoodsPageRequest pageRequest) {

        Long storeId = commonUtil.getStoreId();

        //自动填充当前商家签约的所有分类
        ContractCateListByConditionRequest cateQueryRequest = new ContractCateListByConditionRequest();
        cateQueryRequest.setStoreId(storeId);
        List<Long> cateIds = contractCateQueryProvider.listByCondition(cateQueryRequest).getContext()
                .getContractCateList().stream().map(ContractCateVO::getGoodsCate).map(GoodsCateVO::getCateId).collect
                        (Collectors.toList());
        if (Objects.nonNull(pageRequest.getCateId())) {
            //获取已签约的子分类
            GoodsCateChildCateIdsByIdRequest goodsCateChildCateIdsByIdRequest = new GoodsCateChildCateIdsByIdRequest();
            goodsCateChildCateIdsByIdRequest.setCateId(pageRequest.getCateId());
            List<Long> tempCateIds =
                    goodsCateQueryProvider.getChildCateIdById(goodsCateChildCateIdsByIdRequest).getContext().getChildCateIdList();
            tempCateIds.add(pageRequest.getCateId());
            cateIds = tempCateIds.stream().filter(cateIds::contains).collect(Collectors.toList());
            //不能列出所有子分类，所以置空
            pageRequest.setCateId(null);
        }
        if (CollectionUtils.isEmpty(cateIds)) {
            return BaseResponse.success(new StandardGoodsPageResponse());
        }
        pageRequest.setCateIds(cateIds);

        //如果为空，自动填充当前商家签约的所有分类品牌
        if (Objects.isNull(pageRequest.getBrandId())) {
            ContractBrandListRequest request = new ContractBrandListRequest();
            request.setStoreId(storeId);
            pageRequest.setOrNullBrandIds(contractBrandQueryProvider.list(request).getContext().getContractBrandVOList()
                    .stream().filter(contractBrand -> Objects.nonNull(contractBrand.getGoodsBrand()))
                    .map(ContractBrandVO::getGoodsBrand).map(GoodsBrandVO::getBrandId).collect(Collectors.toList()));
        }

        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        pageRequest.putSort("goodsId", SortType.ASC.toValue());
        BaseResponse<StandardGoodsPageResponse> response = standardGoodsQueryProvider.page(pageRequest);
        //列出已被导入的商品库ID
        StandardGoodsPageResponse context = response.getContext();
        if (CollectionUtils.isNotEmpty(context.getStandardGoodsPage().getContent())) {
            StandardGoodsGetUsedStandardRequest standardRequest = new StandardGoodsGetUsedStandardRequest();
            standardRequest.setStandardIds(context.getStandardGoodsPage().getContent().stream().map(StandardGoodsVO::getGoodsId).collect(Collectors.toList()));
            standardRequest.setStoreIds(Collections.singletonList(storeId));
            context.setUsedStandard(standardGoodsQueryProvider.getUsedStandard(standardRequest).getContext().getStandardIds());
        }
        return response;
    }

    /**
     * 导入商品
     *
     * @param request 导入参数
     * @return 成功结果
     */
    @ApiOperation(value = "导入商品")
    //@Resubmit(delaySeconds = 1000) //加了防止重复提交的注解
    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public BaseResponse importGoods(@RequestBody StandardImportGoodsRequest request) {

        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        if (companyInfo == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        request.setCompanyType(companyInfo.getCompanyType());
        request.setStoreId(commonUtil.getStoreId());
        request.setSupplierName(companyInfo.getSupplierName());

        BaseResponse<StandardImportGoodsResponse> baseResponse = standardImportProvider.importGoods(request);
        StandardImportGoodsResponse response = baseResponse.getContext();
        List<String> skuIds = Optional.ofNullable(response)
                .map(StandardImportGoodsResponse::getSkuIdList)
                .orElse(null);

        //加入ES
        if (CollectionUtils.isNotEmpty(skuIds)) {
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(skuIds).build());
        }
        if (CollectionUtils.isNotEmpty(response.getRetailSkuIds())) {
            esRetailGoodsInfoElasticService.initEsRetailGoodsInfo(EsGoodsInfoRequest.builder().skuIds(response.getRetailSkuIds()).build());
        }
        if (CollectionUtils.isNotEmpty(response.getBulkSkuIds())) {

            esRetailGoodsInfoElasticService.initEsBulkGoodsInfo(EsGoodsInfoRequest.builder().skuIds(response.getBulkSkuIds()).build());
        }

//        //ares埋点-商品-后台导入商品sku
//        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("addGoodsSkuIds", skuIds.toArray()));

        if (1 == request.getGoodsIds().size()) {
            List<String> standardIds = new ArrayList<String>();
            standardIds.add(request.getGoodsIds().get(0));
            List<Long> stores = new ArrayList<Long>();

            stores.add(request.getStoreId());
            StandardGoodsListUsedGoodsIdRequest standardGoodsListUsedGoodsIdRequest =
                    new StandardGoodsListUsedGoodsIdRequest();
            standardGoodsListUsedGoodsIdRequest.setStandardIds(standardIds);
            standardGoodsListUsedGoodsIdRequest.setStoreIds(stores);
            BaseResponse<StandardGoodsListUsedGoodsIdResponse> baseResponse1 =
                    standardGoodsQueryProvider.listUsedGoodsId(standardGoodsListUsedGoodsIdRequest);
            StandardGoodsListUsedGoodsIdResponse standardGoodsListUsedGoodsIdResponse = baseResponse1.getContext();
            if (Objects.nonNull(standardGoodsListUsedGoodsIdResponse)) {
                standardGoodsListUsedGoodsIdResponse.getGoodsIds().forEach(goodsId -> {
                            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
                            goodsByIdRequest.setGoodsId(goodsId);
                            BaseResponse<GoodsByIdResponse> baseResponse2 =
                                    goodsQueryProvider.getById(goodsByIdRequest);
                            GoodsByIdResponse goodsByIdResponse = baseResponse2.getContext();
                            if (Objects.nonNull(goodsByIdResponse)) {
                                operateLogMQUtil.convertAndSend("商品", "商品库导入",
                                        "商品库导入:" + goodsByIdResponse.getGoodsName());
                            }

                        }
                );
            }
        } else {
            operateLogMQUtil.convertAndSend("商品", "商品库批量导入", "商品库批量导入");
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询商品
     *
     * @return
     */
    @ApiOperation(value = "商品全部库导入")
    @RequestMapping(value = "/syncGoods", method = RequestMethod.GET)
    @MultiSubmit
    public BaseResponse syncGoods() {
        StandardGoodsPageRequest pageRequest =new StandardGoodsPageRequest();
        int pageNo=0;
        pageRequest.setPageSize(500);
        logger.info("商品库开始导入........:::: {}", LocalDateTime.now());
        Long storeId = commonUtil.getStoreId();
        //自动填充当前商家签约的所有分类
        ContractCateListByConditionRequest cateQueryRequest = new ContractCateListByConditionRequest();
        cateQueryRequest.setStoreId(storeId);
        List<Long> cateIds = contractCateQueryProvider.listByCondition(cateQueryRequest).getContext()
                .getContractCateList().stream().map(ContractCateVO::getGoodsCate).map(GoodsCateVO::getCateId).collect
                        (Collectors.toList());
        if (CollectionUtils.isEmpty(cateIds)) {
            return BaseResponse.success(new StandardGoodsPageResponse());
        }
        pageRequest.setCateIds(cateIds);
        while (true){
            pageRequest.setPageNum(pageNo);
            pageNo++;
            BaseResponse<StandardGoodsIdsQueryResponse> response = standardGoodsQueryProvider.queryGoodsIds(pageRequest);
            StandardGoodsIdsQueryResponse context = response.getContext();
            if (CollectionUtils.isEmpty(context.getGoodsIds())){
                break;
            }
            //列出已被导入的商品库ID
            StandardGoodsGetUsedStandardRequest standardRequest = new StandardGoodsGetUsedStandardRequest();
            standardRequest.setStandardIds(context.getGoodsIds());
            standardRequest.setStoreIds(Collections.singletonList(storeId));
            List<String> standardIds = standardGoodsQueryProvider.getUsedStandard(standardRequest).getContext().getStandardIds();
            List<String> goodsIds=context.getGoodsIds().stream().filter(param->!standardIds.contains(param)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                StandardImportGoodsRequest request=new StandardImportGoodsRequest();
                request.setGoodsIds(goodsIds);
                importGoods(request);
            }
        }
        logger.info("商品库导入结束........:::: {}", LocalDateTime.now());
        operateLogMQUtil.convertAndSend("商品", "商品全部库导入", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }
}
