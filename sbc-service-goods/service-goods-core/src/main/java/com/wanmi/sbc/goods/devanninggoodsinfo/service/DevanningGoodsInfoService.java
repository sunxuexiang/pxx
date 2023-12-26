package com.wanmi.sbc.goods.devanninggoodsinfo.service;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreNameListByStoreIdsResquest;
import com.wanmi.sbc.customer.bean.vo.StoreNameVO;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsInsidePageResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsBatchNoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.response.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodslabel.service.GoodsLabelService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStockVillages;
import com.wanmi.sbc.goods.goodswarestock.repository.GoodsWareStockRepository;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockVillagesService;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.redis.RedisCache;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("DevanningGoodsInfpService")
public class DevanningGoodsInfoService {
    @Autowired
	private DevanningGoodsInfoRepository devanningGoodsInfoRepository;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;

    @Autowired
    private GoodsLabelService goodsLabelService;

    @Autowired
    private GoodsWareStockVillagesService goodsWareStockVillagesService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private GoodsWareStockRepository goodsWareStockRepository;

    @Autowired
    private StoreQueryProvider storeQueryProvider;



    public GoodsInsidePageResponse getGoodsNorms(GoodsInsidePageResponse goodsPage, Integer manySpecs,Integer pageNum,Integer pageSize){
        List<Object> GoodsNorms = null;
        List<String> Skuids = goodsPage.getContent().stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
        if (manySpecs==1){
            GoodsNorms=devanningGoodsInfoRepository.getGoodsNorms(Skuids);
        }else {
            GoodsNorms=devanningGoodsInfoRepository.getBigGoodsNorms(Skuids);
        }
        if (CollectionUtils.isNotEmpty(GoodsNorms)){
            List<GoodsVO> GoodsVOS = this.resultToGoods(GoodsNorms);
            List<String> collect = GoodsVOS.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
            List<GoodsVO> content = goodsPage.getContent();
            List<GoodsVO> content1 = new LinkedList<>();
            Iterator<GoodsVO> iterator = content.iterator();
            for (GoodsVO goodsVO:content){
                if (collect.contains(goodsVO.getGoodsId())){
                    content1.add(goodsVO);
                }
            }
            goodsPage.setContent(content1);
            int allpagesize=goodsPage.getContent().size();
            goodsPage.setContent(goodsPage.getContent().stream().skip(pageNum*pageSize).limit(pageSize).collect(Collectors.toList()));
            goodsPage.setTotal(allpagesize);
            goodsPage.setTotalElements(goodsPage.getTotal());
            goodsPage.setTotalPages(allpagesize%pageSize>0?allpagesize/pageSize+1:allpagesize/pageSize);
            return goodsPage;
        }
        goodsPage.setContent(new ArrayList<>());
        goodsPage.setTotal(0);
        goodsPage.setTotalElements(0);
        goodsPage.setTotalPages(0);
        return goodsPage;

    }


    private List<GoodsVO> resultToGoods(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            GoodsVO goodsVO = new GoodsVO();
            Object[] results = StringUtil.cast(item, Object[].class);
            goodsVO.setGoodsId(StringUtil.cast(results, 0, String.class));

            return goodsVO;
        }).collect(Collectors.toList());
    }

    public List<DevanningGoodsInfoVO> getGoodsDevanningMaxstep(DevanningGoodsInfoPageRequest devanningGoodsInfoPageRequest){
        List<Object> goodsDevanningMaxstep = null;
        if (!CollectionUtils.isEmpty(devanningGoodsInfoPageRequest.getGoodsIds())){
            goodsDevanningMaxstep = devanningGoodsInfoRepository.getGoodsDevanningMaxstep(devanningGoodsInfoPageRequest.getGoodsIds());
        }
        if (!CollectionUtils.isEmpty(devanningGoodsInfoPageRequest.getGoodsInfoIds())){
            goodsDevanningMaxstep = devanningGoodsInfoRepository.getGoodsDevanningMaxstepbysku(devanningGoodsInfoPageRequest.getGoodsInfoIds());
        }
        if (CollectionUtils.isNotEmpty(goodsDevanningMaxstep)){
            List<DevanningGoodsInfoVO> goodsDevanningVOS = this.resultToGoodsInfoList(goodsDevanningMaxstep);
            return goodsDevanningVOS;
        }
        return Collections.emptyList();
    }

    /**
     * 根据id批量查询SKU数据
     *
     * @param devanningIds 拆箱商品skuId
     * @return 商品sku列表
     */
    public List<DevanningGoodsInfo> findByIds(List<Long> devanningIds, Long wareId) {
        List<DevanningGoodsInfo> goodsInfoList = this.devanningGoodsInfoRepository.findAllById(devanningIds);
        //设置库存
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.getGoodsStockByAreaIdAndGoodsInfoIds(
                goodsInfoList.stream().map(g->g.getGoodsInfoId()).collect(Collectors.toList()),
                wareId);

        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfoList.stream().map(g -> g.getGoodsInfoId()).collect(Collectors.toList()));

        if (Objects.nonNull(wareId) && CollectionUtils.isNotEmpty(goodsWareStocks)) {
            Map<String, GoodsWareStock> goodsWareStockMap = goodsWareStocks.stream()
                    .collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId, g -> g));

            goodsInfoList.forEach(g -> g.setStock(getskusstock.getOrDefault(g.getGoodsInfoId(),BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_DOWN)));

//            goodsInfoList.forEach(g -> g.setStock(goodsWareStockMap.get(g.getGoodsInfoId()).getStock().setScale(2,BigDecimal.ROUND_DOWN)));
        }
//        Map<String, Long> goodsNumsMap = getGoodsPileNumBySkuIds(skuIds);
//        goodsInfoList.forEach(g -> {
//            if (Objects.isNull(g.getStock())) {
//                g.setStock(0L);
//                if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                    g.setStock(g.getPurchaseNum());
//                }
//            }

//            if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                g.setStock(g.getPurchaseNum());
//            }else{
        //计算库存 加上虚拟库存 减去囤货数量
//                calGoodsInfoStock(g,goodsNumsMap);
//            }
//        });
        return goodsInfoList;
    }

    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public DevanningGoodsInfoResponse findSkuByIdsAndMatchFlag(DevanningGoodsInfoQueryRequest infoRequest) {
        Long wareId = infoRequest.getWareId();
        if(Objects.isNull(wareId)){
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        if (CollectionUtils.isEmpty(infoRequest.getDevanningIds())) {
            return DevanningGoodsInfoResponse.builder().devanningGoodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
        }
        //批量查询拆箱SKU信息列表
//        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
//        queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
//        queryRequest.setStoreId(infoRequest.getStoreId());
//        if (infoRequest.getDelFlag() != null) {
//            queryRequest.setDelFlag(infoRequest.getDelFlag());
//        }
        List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.findAll(infoRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(devanningGoodsInfos)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{devanningGoodsInfos});
        }

        //设置仓库的库存
        Map<String, GoodsWareStock> collect = goodsWareStockRepository.findGoodsDefaultStockByGoodsInfoIds(wareId,
                        devanningGoodsInfos.stream().map(DevanningGoodsInfo::getGoodsInfoId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId, Function.identity(), (a, b) -> a));

        for (DevanningGoodsInfo g : devanningGoodsInfos) {
            GoodsWareStock goodsWareStock = collect.get(g.getGoodsInfoId());
            if (Objects.isNull(goodsWareStock)){
                g.setStock(BigDecimal.ZERO);
                g.setLockStock(BigDecimal.ZERO);
            }else {
                g.setStock(goodsWareStock.getStock());
                g.setLockStock(Objects.isNull(goodsWareStock.getLockStock()) ? BigDecimal.ZERO : goodsWareStock.getLockStock());
            }
        }


        //批量查询SPU信息列表
        List<String> goodsIds = devanningGoodsInfos.stream().map(DevanningGoodsInfo::getGoodsId).collect(Collectors.toList());
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<Goods> goodses = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(goodses)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        Map<String, Goods> goodsMap = goodses.stream().collect(Collectors.toMap(Goods::getGoodsId, g -> g));


        List<String> skuIds = devanningGoodsInfos.stream().map(DevanningGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        //对每个SKU填充规格和规格值关系
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
        //如果需要规格值，则查询
//        if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
//            specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));
//        }

        //遍历SKU，填充销量价、商品状态
        devanningGoodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            if (goods != null) {
                //建立扁平化数据
                if (goods.getGoodsInfoIds() == null) {
                    goods.setGoodsInfoIds(new ArrayList<>());
                }
                goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                goodsInfo.setFreightTempId(goods.getFreightTempId());
                goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                //填充规格值
                if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                    goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                            .map(GoodsInfoSpecDetailRel::getDetailName)
                            .collect(Collectors.toList()), " "));
                }

                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        //定义响应结果
        DevanningGoodsInfoResponse responses = new DevanningGoodsInfoResponse();
        responses.setDevanningGoodsInfos(devanningGoodsInfos);
        responses.setGoodses(goodses);
        return responses;
    }

    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public DevanningGoodsInfoResponse findSkuByIdsAndMatchFlagNoStock(DevanningGoodsInfoQueryRequest infoRequest) {
        Long wareId = infoRequest.getWareId();
        if(Objects.isNull(wareId)){
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        if (CollectionUtils.isEmpty(infoRequest.getDevanningIds())) {
            return DevanningGoodsInfoResponse.builder().devanningGoodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
        }
        //批量查询拆箱SKU信息列表
//        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
//        queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
//        queryRequest.setStoreId(infoRequest.getStoreId());
//        if (infoRequest.getDelFlag() != null) {
//            queryRequest.setDelFlag(infoRequest.getDelFlag());
//        }
        List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.findAll(infoRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(devanningGoodsInfos)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{devanningGoodsInfos});
        }
        // log.info("商品集合数据============="+devanningGoodsInfos);

        //批量查询SPU信息列表
        List<String> goodsIds = devanningGoodsInfos.stream().map(DevanningGoodsInfo::getGoodsId).collect(Collectors.toList());
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<Goods> goodses = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());
        if (CollectionUtils.isEmpty(goodses)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        Map<String, Goods> goodsMap = goodses.stream().collect(Collectors.toMap(Goods::getGoodsId, g -> g));

        List<String> skuIds = devanningGoodsInfos.stream().map(DevanningGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        //对每个SKU填充规格和规格值关系
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
        //如果需要规格值，则查询
//        if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
//            specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));
//        }

        //遍历SKU，填充销量价、商品状态
        devanningGoodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            Goods goods = goodsMap.get(goodsInfo.getGoodsId());
            if (goods != null) {
                //建立扁平化数据
                if (goods.getGoodsInfoIds() == null) {
                    goods.setGoodsInfoIds(new ArrayList<>());
                }
                goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                goodsInfo.setFreightTempId(goods.getFreightTempId());
                goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                //填充规格值
                if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                    goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                            .map(GoodsInfoSpecDetailRel::getDetailName)
                            .collect(Collectors.toList()), " "));
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        //定义响应结果
        DevanningGoodsInfoResponse responses = new DevanningGoodsInfoResponse();
        responses.setDevanningGoodsInfos(devanningGoodsInfos);
        responses.setGoodses(goodses);
        return responses;
    }



    private List<DevanningGoodsInfoVO> resultToGoodsInfoList(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            DevanningGoodsInfoVO devanningGoodsInfoVO = new DevanningGoodsInfoVO();
            Object[] results = StringUtil.cast(item, Object[].class);
            devanningGoodsInfoVO.setDevanningId(StringUtil.cast(results, 0, Long.class));
            devanningGoodsInfoVO.setGoodsInfoId(StringUtil.cast(results, 1, String.class));
            devanningGoodsInfoVO.setGoodsId(StringUtil.cast(results, 2, String.class));
            devanningGoodsInfoVO.setAddStep(StringUtil.cast(results, 3, BigDecimal.class));
            devanningGoodsInfoVO.setDevanningUnit(StringUtil.cast(results, 4, String.class));
            devanningGoodsInfoVO.setSalePrice(StringUtil.cast(results, 5, BigDecimal.class));
            devanningGoodsInfoVO.setMarketPrice(StringUtil.cast(results, 5, BigDecimal.class));
            return devanningGoodsInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID批量查询商品SKU
     *
     * @param infoRequest 查询参数
     * @return 商品列表响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public DevanningGoodsInfoResponse findSkuByIds(DevanningGoodsInfoRequest infoRequest) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase findSkuByIds start");

        try {
            if (CollectionUtils.isEmpty(infoRequest.getDevanningIds())) {
                return DevanningGoodsInfoResponse.builder().devanningGoodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
            }


            //批量查询SKU信息列表
            DevanningGoodsInfoQueryRequest queryRequest = new DevanningGoodsInfoQueryRequest();
            queryRequest.setDevanningIds(infoRequest.getDevanningIds());
            queryRequest.setStoreId(infoRequest.getStoreId());
            if (infoRequest.getDeleteFlag() != null) {
                queryRequest.setDelFlag(infoRequest.getDeleteFlag().toValue());
            }
            //关联goodsLabels
            List<GoodsLabel> goodsLabelList = goodsLabelService.findTopByDelFlag();
            List<DevanningGoodsInfo> devanningGoodsInfos = devanningGoodsInfoRepository.findAll(queryRequest.getWhereCriteria());

            sb.append(",goodsInfoRepository.findAll end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

            if (CollectionUtils.isEmpty(devanningGoodsInfos)) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{devanningGoodsInfos});
            }

            //设置仓库的库存
        /*List<GoodsWareStock> goodsWareStocks = goodsWareStockService.getGoodsStockByAreaIdAndGoodsInfoIds(
                goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()),
                infoRequest.getWareId());*/
            List<Long> unOnline = new ArrayList<>(10);
            List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(devanningGoodsInfos.stream()
                    .map(DevanningGoodsInfo::getGoodsInfoId).collect(Collectors.toList()));

            sb.append(",goodsWareStockService.findByGoodsInfoIdIn end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

//            Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(goodsInfos
//                    .stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsNumsMap();

            if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
                if (Objects.nonNull(infoRequest.getMatchWareHouseFlag()) && !infoRequest.getMatchWareHouseFlag()) {
                    List<Long> storeList = goodsWareStocks.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
                    unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE).stream()
                            .map(WareHouseVO::getWareId).collect(Collectors.toList());

                    sb.append(",queryWareHouses end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                }
                for (DevanningGoodsInfo goodsInfo : devanningGoodsInfos) {
                    List<GoodsWareStock> stockList;
                    if (CollectionUtils.isNotEmpty(unOnline)) {
                        List<Long> finalUnOnline = unOnline;
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                        && finalUnOnline.contains(goodsWareStock.getWareId())).
                                collect(Collectors.toList());
                    } else {
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                                collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,BigDecimal.ROUND_DOWN);
                        goodsInfo.setStock(sumStock);
//                        if(Objects.isNull(goodsInfo.getMarketingId()) || Objects.isNull(goodsInfo.getPurchaseNum()) || goodsInfo.getPurchaseNum() == -1){
//                            goodsInfo.setStock(sumStock);
//                        }
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }else{
//                            //计算库存 加上虚拟库存 减去囤货数量
//                            calGoodsInfoStock(goodsInfo,goodsNumsMap);
//                        }
                    } else {
                        goodsInfo.setStock(BigDecimal.ZERO);
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }else{
//                            //计算库存 加上虚拟库存 减去囤货数量
//                            calGoodsInfoStock(goodsInfo,goodsNumsMap);
//                        }
                    }
                }

            /*Map<String,GoodsWareStock> goodsWareStockMap = goodsWareStocks.stream().collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId,g->g));
            goodsInfos.stream().forEach(g->{
                if(Objects.nonNull(goodsWareStockMap.get(g.getGoodsInfoId()))){
                    g.setStock(goodsWareStockMap.get(g.getGoodsInfoId()).getStock());
                }
            });*/
            }


            //2020-05-15  商品库存 通过WMS第三方查询
      /*  goodsInfos.forEach((goodsInfo)->{
            BaseResponse<InventoryQueryResponse> response = queryStorkByWMS(InventoryQueryRequest.builder()
                    .customerID(AbstarctWMSConstant.CUSTOMER_ID)
                    .sku(goodsInfo.getGoodsInfoNo()).build());
            goodsInfo.setStock(response.getContext().getInventoryQueryReturnVO().getQty());
        });*/


            // 商户库存转供应商库存
            // this.turnProviderStock(goodsInfos);

            //批量查询SPU信息列表
            List<String> goodsIds = devanningGoodsInfos.stream().map(DevanningGoodsInfo::getGoodsId).collect(Collectors.toList());
            GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
            goodsQueryRequest.setGoodsIds(goodsIds);
            List<Goods> goodses = goodsRepository.findAll(goodsQueryRequest.getWhereCriteria());


            //标签组装进goods
            goodses.stream().forEach(item -> {
                if (StringUtils.isNotBlank(item.getLabelIdStr())) {
                    List<Long> goodsLabelIds = Arrays.stream(item.getLabelIdStr().split(",")).map(Long::parseLong)
                            .collect(Collectors.toList());
                    //过滤除符合条件的标签对象
                    item.setGoodsLabels(goodsLabelList.stream()
                            .filter(label -> goodsLabelIds.contains(label.getId()))
                            .collect(Collectors.toList()));

                }
            });

            HashMap<String, List<GoodsLabel>> goodsLabelMap = new HashMap<>();
            goodses.forEach(item -> {
                goodsLabelMap.put(item.getGoodsId(), item.getGoodsLabels());
            });

            sb.append(",goodsRepository.findAll end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

            if (CollectionUtils.isEmpty(goodses)) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
            }
            Map<String, Goods> goodsMap = goodses.stream().collect(Collectors.toMap(Goods::getGoodsId, g -> g));


            List<String> skuIds = devanningGoodsInfos.stream().map(DevanningGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
            //对每个SKU填充规格和规格值关系
            Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
            //如果需要规格值，则查询
            if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
                specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));

                sb.append(",goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds end time=");
                sb.append(System.currentTimeMillis() - sTm);
                sTm = System.currentTimeMillis();
            }

            //遍历SKU，填充销量价、商品状态
            devanningGoodsInfos.forEach(goodsInfo -> {
                goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
                Goods goods = goodsMap.get(goodsInfo.getGoodsId());
                if (goods != null) {
                    //建立扁平化数据
                    if (goods.getGoodsInfoIds() == null) {
                        goods.setGoodsInfoIds(new ArrayList<>());
                    }
                    goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                    goodsInfo.setPriceType(goods.getPriceType());
                    goodsInfo.setGoodsUnit(goodsInfo.getDevanningUnit());
                    goodsInfo.setCateId(goods.getCateId());
                    goodsInfo.setBrandId(goods.getBrandId());
                    goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                    goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                    goodsInfo.setFreightTempId(goods.getFreightTempId());
                    goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                    goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                    goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                    goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                    //sku标签
                    goodsInfo.setGoodsLabels(goodsLabelMap.get(goodsInfo.getGoodsId()));

                    //为空，则以商品主图
                    if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                        goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                    }

                    //填充规格值
                    if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                        goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                                .map(GoodsInfoSpecDetailRel::getDetailName)
                                .collect(Collectors.toList()), " "));
                    }

                    if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OK);

                        if ((Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) && (Objects.isNull(goodsInfo.getMarketingId()) || Objects.isNull(goodsInfo.getPurchaseNum()) || goodsInfo.getPurchaseNum() == -1)) {
                            goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        }
                        //限购商品
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() != -1){
//                            if(goodsInfo.getPurchaseNum() < 1){
//                                goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                            }
//                        }

                    } else {
                        goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                    }
                } else {//不存在，则做为删除标记
                    goodsInfo.setDelFlag(DeleteFlag.YES);
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            });

            sb.append(",goodsInfos.forEach end time=");
            sb.append(System.currentTimeMillis() - sTm);

            //定义响应结果
            DevanningGoodsInfoResponse responses = new DevanningGoodsInfoResponse();
            responses.setDevanningGoodsInfos(devanningGoodsInfos);
            responses.setGoodses(goodses);
            return responses;
        } finally {
            log.info(sb.toString());
        }

    }


    /**
     * 条件查询SKU数据
     *
     * @param request 查询条件
     * @return 商品sku列表
     */
    public List<DevanningGoodsInfo> findByParams(DevanningGoodsInfoQueryRequest request) {
        List<DevanningGoodsInfo> goodsInfoList = this.devanningGoodsInfoRepository.findAll(request.getWhereCriteria());
        // 商户库存转供应商库存
        //this.turnProviderStock(goodsInfoList); //2020 5/15  喜丫丫 库存由WMS第三方提供无需转供应库存
        return goodsInfoList;
    }

    /**
     * 条件查询SKU数据
     *
     * @param request 查询条件
     * @return 商品sku列表
     */
    public List<DevanningGoodsInfo> listByParentId(DevanningGoodsInfoQueryRequest request) {
        List<DevanningGoodsInfo> goodsInfoList = this.devanningGoodsInfoRepository.queryGoodsInfoByParentId(request.getParentGoodsInfoId());
        // 商户库存转供应商库存
        //this.turnProviderStock(goodsInfoList); //2020 5/15  喜丫丫 库存由WMS第三方提供无需转供应库存
        return goodsInfoList;
    }

    /**
     * 只查门店ID
     * @param request
     * @return
     */
    public List<Long> getIdsByStoreId(List<Long> devanningIds, Long storeId) {
        List<Long> ids = this.devanningGoodsInfoRepository.getIdsByStoreId(devanningIds,storeId);
        return ids;
    }


    /**
     * 功能描述: <br>
     * 〈〉组装商品信息
     *
     * @Param: [resultsObj]
     * @Return: java.util.List<com.wanmi.sbc.goods.info.model.root.GoodsInfo>
     * @Author: yxb
     * @Date: 2021/2/2 14:26
     */
    private List<GoodsInfo> resultToGoodsInfoListtwo(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            GoodsInfo goodsInfo = new GoodsInfo();
            Object[] results = StringUtil.cast(item, Object[].class);
            goodsInfo.setGoodsId(StringUtil.cast(results, 0, String.class));
            goodsInfo.setGoodsInfoId(StringUtil.cast(results, 1, String.class));
            String goodsInfoImg = StringUtil.cast(results, 2, String.class);
            if (StringUtils.isNotBlank(goodsInfoImg)) {
                goodsInfo.setGoodsInfoImg(goodsInfoImg);
            } else {
                String goodsImg = StringUtil.cast(results, 3, String.class);
                if (StringUtils.isNotBlank(goodsImg)) {
                    goodsInfo.setGoodsInfoImg(goodsImg);
                } else {
                    goodsInfo.setGoodsInfoImg("");
                }
            }
            goodsInfo.setMarketPrice(StringUtil.cast(results, 4, BigDecimal.class));
            goodsInfo.setGoodsUnit(StringUtil.cast(results, 5, String.class));
            goodsInfo.setGoodsInfoName(StringUtil.cast(results, 6, String.class));
            int delFlag = StringUtil.cast(results, 7, Byte.class).intValue();
            goodsInfo.setDelFlag(DeleteFlag.fromValue(delFlag));
            int addFlag = StringUtil.cast(results, 8, Byte.class).intValue();
            int auditFlag = StringUtil.cast(results, 9, Byte.class).intValue();
            int checkedAddFlag = StringUtil.cast(results,10,Byte.class).intValue();
            goodsInfo.setCheckedAddedFlag(checkedAddFlag);
            goodsInfo.setAddedFlag(addFlag);
            goodsInfo.setAuditStatus(CheckStatus.fromValue(auditFlag));
            Long cast = StringUtil.cast(results, 11, BigInteger.class).longValue();
            goodsInfo.setDevanningId(cast);
            goodsInfo.setDivisorFlag(StringUtil.cast(results,12,BigDecimal.class));
            return goodsInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询商品信息
     *
     * @param devanningIds
     * @return
     */
    public List<GoodsInfo> findGoodsInfoByIds(List<Long> devanningIds,List<String> goodsInfoIds, Boolean matchWareHouseFlag) {
        List<Object> goodsInfoIdObj = devanningGoodsInfoRepository.findDevanningGoodsInfoByDids(devanningIds);
        if (CollectionUtils.isNotEmpty(goodsInfoIdObj)) {
            List<GoodsInfo> goodsInfoList = resultToGoodsInfoListtwo(goodsInfoIdObj);
            List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIds);
            List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(goodsInfoIds);
            Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfoIds);
            if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
                List<Long> unOnline = null;
                if (Objects.nonNull(matchWareHouseFlag) && !matchWareHouseFlag) {
                    List<Long> storeList = goodsWareStocks.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
                    unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE).stream()
                            .map(WareHouseVO::getWareId).collect(Collectors.toList());
                }
                for (GoodsInfo goodsInfo : goodsInfoList) {
                    List<GoodsWareStock> stockList;
                    List<GoodsWareStockVillages> villagesList;
                    if (CollectionUtils.isNotEmpty(unOnline)) {
                        List<Long> finalUnOnline = unOnline;
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                        && finalUnOnline.contains(goodsWareStock.getWareId())).
                                collect(Collectors.toList());
                        villagesList = goodsWareStockVillagesList.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                        && finalUnOnline.contains(goodsWareStock.getWareId())).
                                collect(Collectors.toList());
                    } else {
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                                collect(Collectors.toList());
                        villagesList = goodsWareStockVillagesList.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                                collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(stockList)) {
//                        BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
//                        if (CollectionUtils.isNotEmpty(villagesList)) {
//                            BigDecimal sumVillagesStock = villagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
//                            if (sumStock.subtract(sumVillagesStock).compareTo(BigDecimal.ZERO)  <= 0) {
//                                goodsInfo.setStock(BigDecimal.ZERO);
//                            } else {
//                                goodsInfo.setStock(sumStock.subtract(sumVillagesStock) );
//                            }
//                        } else {
//                            goodsInfo.setStock(sumStock);
//                        }
                        goodsInfo.setStock(getskusstock.getOrDefault(goodsInfo.getGoodsInfoId(),BigDecimal.ZERO));


//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }
                    } else {
                        goodsInfo.setStock(BigDecimal.ZERO);
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }
                    }
                    if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())
                            && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OK);
                        if (goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                            goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        }
                    } else {
                        goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                    }
                }

                return goodsInfoList;
            }
        } else {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }


    /**
     * 批量更新商品的批次号
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse batchUpdateGoodsInfoBatchNo(List<GoodsBatchNoDTO> goodsBatchNoDTOS) {
        goodsBatchNoDTOS.stream().forEach(g ->
                this.updateGoodsInfoBatchNo(g.getGoodsInfoBatchNo(), g.getGoodsInfoId()));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 条件查询SKU数据
     *
     * @param goodsInfoBatchNo 批次号
     * @param goodsInfoId 商品id
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsInfoBatchNo(String goodsInfoBatchNo, String goodsInfoId) {
        devanningGoodsInfoRepository.updateGoodsInfoBatchNo(goodsInfoBatchNo, goodsInfoId);
    }




    /**
     * 根据id批量查询SKU数据
     *
     * @param skuIds 商品skuId
     * @return 商品sku列表
     */
    public List<DevanningGoodsInfo> findGoodsInfoAndStockByIds(List<Long> devanning,List<String> skuIds, Boolean matchWareHouseFlag) {
        List<DevanningGoodsInfo> devanningGoodsInfos = this.devanningGoodsInfoRepository.findAllById(devanning);
//        Map<String, Long> goodsNumsMap = getGoodsPileNumBySkuIds(skuIds);
        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList = goodsWareStockService.findByGoodsInfoIdIn(skuIds);
        //获取对应的商品乡镇件库存
        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(skuIds);
        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(skuIds);
        Map<String, BigDecimal> stockMap = new HashMap<>();
        Map<String, List<GoodsWareStock>> goodsWareStockMap = new HashMap<>();
        List<Long> unOnline = new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(matchWareHouseFlag) && !matchWareHouseFlag) {
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            for (String goodsInfoId : skuIds) {
                List<GoodsWareStock> stockList;
                List<GoodsWareStockVillages> villagesList;
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    stockMap.put(goodsInfoId, sumStock);
//                    long sumStock = stockList.stream().mapToLong(GoodsWareStock::getStock).sum();
                    if (CollectionUtils.isNotEmpty(villagesList)) {
                        BigDecimal sumVillagesStock = villagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                        if (sumStock.subtract(sumVillagesStock).compareTo(BigDecimal.ZERO)  <= 0) {
                            stockMap.put(goodsInfoId, BigDecimal.ZERO);
                        } else {
                            stockMap.put(goodsInfoId, sumStock.subtract(sumVillagesStock) );
                        }
                    } else {
                        stockMap.put(goodsInfoId, sumStock);
                    }
                    goodsWareStockMap.put(goodsInfoId,stockList);
                } else {
                    stockMap.put(goodsInfoId, BigDecimal.ZERO);
                }
            }
        }
        devanningGoodsInfos.forEach(goodsInfo -> {
            if (Objects.nonNull(getskusstock.get(goodsInfo.getGoodsInfoId()))) {
                goodsInfo.setStock(getskusstock.get(goodsInfo.getGoodsInfoId()));
//                if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                    goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                }else{
//                    calGoodsInfoStock(goodsInfo,goodsNumsMap);
//                }
            } else {
                goodsInfo.setStock(BigDecimal.ZERO);
//                if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                    goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                }
            }
            if (CollectionUtils.isNotEmpty(goodsWareStockMap.get(goodsInfo.getGoodsInfoId()))) {
                goodsInfo.setGoodsWareStocks(goodsWareStockMap.get(goodsInfo.getGoodsInfoId()));
            }
        });
        return devanningGoodsInfos;
    }

    /**
     * @description  返回的DevanningGoodsInfoVO增加店铺名称
     * @author  shiy
     * @date    2023/5/20 16:33
     * @params  [java.util.List<com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo>]
     * @return  java.util.List<com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO>
    */
    public List<DevanningGoodsInfoVO> getVOWithStoreName(List<DevanningGoodsInfo> devanningGoodsInfoList) {
        List<DevanningGoodsInfoVO> voList = new ArrayList<>(1);
        if (CollectionUtils.isEmpty(devanningGoodsInfoList)) {
            return voList;
        }
        voList = KsBeanUtil.convertList(devanningGoodsInfoList, DevanningGoodsInfoVO.class);
        formatDevanningGoodsInfoVo(voList);
        return voList;
    }

    public void formatDevanningGoodsInfoVo(List<DevanningGoodsInfoVO> voList) {
        List<Long> storeIdList = voList.stream().filter(p->null!=p.getStoreId()).map(DevanningGoodsInfoVO::getStoreId).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(storeIdList)){
            return;
        }
        StoreNameListByStoreIdsResquest storeIdsResquest = new StoreNameListByStoreIdsResquest();
        storeIdsResquest.setStoreIds(storeIdList);
        List<StoreNameVO> storeNameList = storeQueryProvider.listStoreNameByStoreIds(storeIdsResquest).getContext().getStoreNameList();

        voList.forEach(devanningGoodsInfoVO->{
            for(StoreNameVO storeNameVO:storeNameList){
                if(storeNameVO.getStoreId().compareTo(devanningGoodsInfoVO.getStoreId())==0){
                    devanningGoodsInfoVO.setStoreName(storeNameVO.getStoreName());
                    break;
                }
            }
        });
    }

    public List<DevanningGoodsInfo> findByGoodsId(DevanningGoodsInfoQueryRequest devanningGoodsInfoQueryRequest) {
       return this.devanningGoodsInfoRepository.findByGoodsId(devanningGoodsInfoQueryRequest.getGoodsId());
    }


    public void addImport(DevanningGoodsInfo devanningGoodsInfoQueryRequest) {
         this.devanningGoodsInfoRepository.save(devanningGoodsInfoQueryRequest);
    }

}

