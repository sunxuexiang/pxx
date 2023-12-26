package com.wanmi.sbc.job;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageProvider;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageAddRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageListRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageListResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JobHandler(value = "stockoutRecordJobHandler")
@Component
@Slf4j
public class StockoutRecordJobHandler extends IJobHandler {

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private GoodsBrandProvider goodsBrandProvider;

    @Autowired
    private StockoutManageProvider stockoutManageProvider;

    @Autowired
    private StockoutManageQueryProvider stockoutManageQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("商品缺货生成记录定时任务开始........:::: {}", LocalDateTime.now());

        StockoutManageListRequest searchRequest = new StockoutManageListRequest();
        searchRequest.setSource(2);
        searchRequest.setReplenishmentFlagList(Lists.newArrayList(ReplenishmentFlag.NO, ReplenishmentFlag.NOT_ALERT));
        searchRequest.setDelFlag(DeleteFlag.NO);

        BaseResponse<StockoutManageListResponse> stockDbList = stockoutManageQueryProvider.list(searchRequest);
        log.info("商品缺货表里面的记录内容大小:======" + stockDbList.getContext().getStockoutManageVOList().size());

        // 查询所有库存为0的商品
        List<String> goodsIds = goodsInfoQueryProvider.listStockoutGoods().getContext();
        log.error("调用商品Feign服务查询所有库存为0的商品返回数据为:{}条" + goodsIds.size());
        if (CollectionUtils.isEmpty(goodsIds)) {
            log.info("商品缺货生成记录定时任务结束........:::: {}", LocalDateTime.now());
            return SUCCESS;
        }

        //通过商品信息id查询商品信息
        GoodsInfoListByConditionRequest request = new GoodsInfoListByConditionRequest();
        request.setGoodsIds(goodsIds);
        BaseResponse<GoodsInfoListByConditionResponse> goodsInfoResponse = goodsInfoQueryProvider.listByCondition(request);
        if (goodsInfoResponse == null ||
                goodsInfoResponse.getContext() == null ||
                CollectionUtils.isEmpty(goodsInfoResponse.getContext().getGoodsInfos())) {
            log.warn("商品缺货生成记录商品通过商品信息id查询商品信息为空");
            return SUCCESS;
        }
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getContext().getGoodsInfos();
        log.info("商品缺货生成记录调用商品InfoService返回数据内容:{}条" + goodsInfos.size());

        //通过仓库id查询仓库信息
        List<Long> wareIds = goodsInfos.stream().map(x -> x.getWareId()).collect(Collectors.toList());
        WareHouseListRequest wareHouseListRequest = new WareHouseListRequest();
        wareHouseListRequest.setWareIdList(wareIds);
        BaseResponse<WareHouseListResponse> wareHoseResponse = wareHouseQueryProvider.list(wareHouseListRequest);
        if (wareHoseResponse == null ||
                wareHoseResponse.getContext() == null ||
                CollectionUtils.isEmpty(wareHoseResponse.getContext().getWareHouseVOList())) {
            log.warn("商品缺货生成记录商品通过仓库id查询仓库信息为空");
            return SUCCESS;
        }
        List<WareHouseVO> wareHouseVOList = wareHoseResponse.getContext().getWareHouseVOList();
        log.info("商品缺货生成记录调用仓库InfoService返回数据内容:{}条", wareHouseVOList.size());

        //通过品牌id查询品牌数据
        List<Long> brandIds = goodsInfos.stream().map(x -> x.getBrandId()).collect(Collectors.toList());
        GoodsBrandListRequest goodsBrandRequest = new GoodsBrandListRequest();
        goodsBrandRequest.setBrandIds(brandIds);
        BaseResponse<List<GoodsBrandVO>> listBaseResponse = goodsBrandProvider.listByCondition(goodsBrandRequest);
        List<GoodsBrandVO> goodsBrandVOS = listBaseResponse.getContext();
        log.info("商品缺货生成记录调用品牌InfoService返回数据内容:{}条", listBaseResponse.getContext().size());

        List<Long> cateList = goodsInfos.stream().map(g -> g.getCateId()).collect(Collectors.toList());

        BaseResponse<GoodsCateListByConditionResponse> cates = goodsCateQueryProvider.listByCondition(new GoodsCateListByConditionRequest(cateList));
        Map<Long, String> cateMaps = Maps.newHashMap();
        if (cates == null ||
                cates.getContext() == null ||
                CollectionUtils.isEmpty(cates.getContext().getGoodsCateVOList())) {
            log.warn("商品缺货生成记录通过商品分类id查询商品分类信息为空");
        } else {
            cateMaps = cates.getContext().getGoodsCateVOList().stream().collect(Collectors.toMap(GoodsCateVO::getCateId, GoodsCateVO::getCateName, (oldValue, newValue) -> newValue));
        }


        for (GoodsInfoVO goodsInfoVO : goodsInfos) {
            //统计时需要去除商品名称中含有T/NEW/星号标记的商品
            if (goodsInfoVO.getGoodsInfoName().contains("T") ||
                    goodsInfoVO.getGoodsInfoName().contains("t") ||
                    goodsInfoVO.getGoodsInfoName().contains("NEW") ||
                    goodsInfoVO.getGoodsInfoName().contains("new") ||
                    goodsInfoVO.getGoodsInfoName().contains("*")) {
                log.info("商品缺货生成记录去除商品名称中含有T/NEW/星号标记的商品GoodsInfoId:{}", goodsInfoVO.getGoodsInfoId());
                continue;
            }

            //缺货记录表已存在库存为0的商品不在做任何处理
            boolean existGoodInfo = isExistGoodInfo(goodsInfoVO.getGoodsInfoId(), stockDbList.getContext().getStockoutManageVOList());
            if (existGoodInfo) {
                continue;
            }

            StockoutManageAddRequest addStockoutManage = this.createStockoutManage(goodsInfoVO, goodsBrandVOS, cateMaps);
            stockoutManageProvider.add(addStockoutManage);
        }

        log.info("商品缺货生成记录定时任务结束时间........:::: {}", LocalDateTime.now());
        return SUCCESS;
    }

    /**
     * 创建缺货管理信息
     *
     * @param goodsInfoVO
     * @param goodsBrandVOS
     * @return
     */
    private StockoutManageAddRequest createStockoutManage(GoodsInfoVO goodsInfoVO, List<GoodsBrandVO> goodsBrandVOS, Map<Long, String> cateMaps) {
        log.info("商品缺货生成记录进入创建记录,goodsInfoId:{}", goodsInfoVO.getGoodsInfoId());
        StockoutManageAddRequest addRequest = new StockoutManageAddRequest();

        BaseResponse<GoodsByIdResponse> goodsResponse = goodsQueryProvider.getById(new GoodsByIdRequest(goodsInfoVO.getGoodsId()));
        if (goodsResponse == null ||
                goodsResponse.getContext() == null) {
            log.warn("商品缺货生成记录通过GoodsId：{}查询为空", goodsInfoVO.getGoodsId());
            addRequest.setGoodsInfoImg(goodsInfoVO.getGoodsInfoImg());
        } else {
            addRequest.setGoodsInfoImg(goodsResponse.getContext().getGoodsImg());
        }

        addRequest.setGoodsName(goodsInfoVO.getGoodsInfoName());
        addRequest.setGoodsInfoId(goodsInfoVO.getGoodsInfoId());
        addRequest.setGoodsInfoNo(goodsInfoVO.getGoodsInfoNo());
        addRequest.setErpGoodsInfoNo(goodsInfoVO.getErpGoodsInfoNo());
        log.info("商品缺货生成记录goodsInfoId：{},erpid{}", goodsInfoVO.getGoodsInfoId(), goodsInfoVO.getErpGoodsInfoNo());

        addRequest.setCateId(goodsInfoVO.getCateId());
        addRequest.setCateName(cateMaps.get(goodsInfoVO.getCateId()));
        addRequest.setAddedFlag(AddedFlag.get(goodsInfoVO.getAddedFlag()));
        addRequest.setBrandId(goodsInfoVO.getBrandId());
        addRequest.setWareId(goodsInfoVO.getWareId());


        addRequest.setSource(2);
        addRequest.setStockoutNum(0L);
        addRequest.setStoreId(0L);
        addRequest.setDelFlag(DeleteFlag.NO);
        LocalDateTime localDateTime = LocalDateTime.now();
        addRequest.setCreateTime(localDateTime);

        GoodsBrandVO brandInfo = this.getBrandInfo(addRequest.getBrandId(), goodsBrandVOS);
        if (brandInfo != null) {
            addRequest.setBrandName(brandInfo.getBrandName());
        } else {
            addRequest.setBrandName("湖南主仓");
        }

        addRequest.setAddedFlag(AddedFlag.get(goodsInfoVO.getAddedFlag()));
        addRequest.setStockoutTime(localDateTime);
        addRequest.setStockoutDay(1);
        addRequest.setReplenishmentFlag(ReplenishmentFlag.NOT_ALERT);

        return addRequest;
    }


    private boolean isExistGoodInfo(String goodsInfoId, List<StockoutManageVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.stream().filter(m -> m.getGoodsInfoId().equals(goodsInfoId)).findAny().isPresent();
    }

    private List<String> fillMatchGoodsIds(List<String> goodsIds, BaseResponse<StockoutManageListResponse> stockDbList) {
        List<StockoutManageVO> stockoutManageVOS = stockDbList.getContext().getStockoutManageVOList();
        if (CollectionUtils.isEmpty(stockDbList.getContext().getStockoutManageVOList())) {
            return goodsIds;
        }
        List<String> resultList = new ArrayList<>();
        for (String goodInfoId : goodsIds) {
            boolean isExist = stockoutManageVOS.stream().filter(m -> m.getGoodsInfoId().equals(goodInfoId)).findAny().isPresent();
            if (!isExist) {
                resultList.add(goodInfoId);
            }
        }
        return resultList;
    }

    private GoodsBrandVO getBrandInfo(Long brandId, List<GoodsBrandVO> brandList) {
        for (GoodsBrandVO brandVO : brandList) {
            if (brandVO.getBrandId().equals(brandId)) {
                return brandVO;
            }
        }
        return null;
    }
}
