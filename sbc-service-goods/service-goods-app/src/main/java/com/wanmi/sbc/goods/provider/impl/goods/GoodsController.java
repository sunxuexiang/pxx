package com.wanmi.sbc.goods.provider.impl.goods;

import com.github.yitter.idgen.YitIdHelper;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchUpdateRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.ares.GoodsAresService;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.devanninggoodsinfo.response.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import com.wanmi.sbc.goods.goodsattribute.service.GoodsAttributeService;
import com.wanmi.sbc.goods.goodsimagestype.model.root.GoodsImageStype;
import com.wanmi.sbc.goods.goodsimagestype.repository.GoodsImagestypeRepository;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.info.model.root.*;
import com.wanmi.sbc.goods.info.request.BulkGoodsSaveRequest;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsSaveRequest;
import com.wanmi.sbc.goods.info.request.RetailGoodsSaveRequest;
import com.wanmi.sbc.goods.info.service.*;
import com.wanmi.sbc.goods.relationgoodsimages.model.root.RelationGoodsImages;
import com.wanmi.sbc.goods.relationgoodsimages.repository.RelationGoodsImagesRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * com.wanmi.sbc.goods.provider.impl.goods.GoodsController
 *
 * @author lipeng
 * @dateTime 2018/11/7 下午3:20
 */
@RestController
@Validated
@Slf4j
public class GoodsController implements GoodsProvider {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    private DevanningGoodsInfoService devanningGoodsInfoService;
    @Autowired
    private GoodsImagestypeRepository goodsImagestypeRepository;

    @Autowired
    private RelationGoodsImagesRepository relationGoodsImagesRepository;

    @Autowired
    private S2bGoodsService s2bGoodsService;

    @Autowired
    private GoodsAresService goodsAresService;

    @Autowired
    private GoodsModifyExcelService GoodsModifyExcelService;

    @Autowired
    private GoodsModifyExcelService goodsModifyExcelService;

    @Autowired
    private RetailGoodsService retailGoodsService;

    @Autowired
    private BulkGoodsService bulkGoodsService;
    @Autowired
    private GoodsAttributeService attributeService;

    @Value("${erpSync.goods.url}")
    private String erpSyncGoodsUrl;
    private int count = 0;

    @Autowired
    private GoodsTagRelService goodsTagRelService;

    private static void descartes(List<List<String>> dimvalue,
                                  List<List<String>> result, int layer, List<String> curList) {
        if (layer < dimvalue.size() - 1) {
            if (dimvalue.get(layer).size() == 0) {
                descartes(dimvalue, result, layer + 1, curList);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<String> list = new ArrayList<String>(curList);
                    list.add(dimvalue.get(layer).get(i));
                    descartes(dimvalue, result, layer + 1, list);
                }
            }
        } else if (layer == dimvalue.size() - 1) {
            if (dimvalue.get(layer).size() == 0) {
                result.add(curList);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<String> list = new ArrayList<String>(curList);
                    list.add(dimvalue.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }

    /**
     * 新增商品
     *
     * @param request {@link GoodsAddRequest}
     * @return 新增结果 {@link GoodsAddResponse}
     */
    @Override

    public BaseResponse<GoodsAddResponse> add(@RequestBody @Valid GoodsAddRequest request) {
        GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        String result = goodsService.add(goodsSaveRequest);

        GoodsAddResponse response = new GoodsAddResponse();
        response.setResult(result);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsAddResponse> addSpecial(@RequestBody @Valid GoodsSpecialRequest request) {
        String goodsId = goodsService.addSpecial(request);
        GoodsAddResponse response = new GoodsAddResponse();
        response.setResult(goodsId);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsAddResponse> modifySpecialGoods(@RequestBody @Valid GoodsSpecialRequest request) {
        String goodsId = goodsService.modifySpecialGoods(request);
        GoodsAddResponse response = new GoodsAddResponse();
        response.setResult(goodsId);
        return BaseResponse.success(response);
    }

    /**
     * 新增spu
     *
     * @return
     */
    @Override
    public BaseResponse<SpecialGoodsAddResponse> addSpu(@RequestBody @Valid SpecialGoodsSaveRequest specialGoodsSaveRequest) {
        SpecialGoodsAddResponse specialGoodsAddResponse = new SpecialGoodsAddResponse();
        List<Goods> goodsList = goodsService.addSpu(specialGoodsSaveRequest);
        specialGoodsAddResponse.setGoods(KsBeanUtil.convertList(goodsList, GoodsVO.class));
        return BaseResponse.success(specialGoodsAddResponse);
    }

    @Override
    public BaseResponse weightAndCubage(@Valid GoodsWeightRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request.getGoodsByConditionRequest(), GoodsQueryRequest.class);
        goodsService.updateBatch(goodsQueryRequest, request.getMap(), request.getGoodsStandardIds(), request.getStandardMap());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品
     *
     * @param request {@link GoodsModifyRequest}
     * @return 修改结果 {@link GoodsModifyResponse}
     */
    @Override
    @Transactional
    public BaseResponse<GoodsModifyResponse> modify(@RequestBody @Valid GoodsModifyRequest request) {
        GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        Goods goods = goodsService.getGoodsById(goodsSaveRequest.getGoods().getGoodsId());
        Map<String, Object> map;

        /**锁定库存空赋值*/
        if (Objects.isNull(goodsSaveRequest.getGoods().getLockStock())) {
            goodsSaveRequest.getGoods().setLockStock(0);
        }

        if (0 == goods.getGoodsSource() && StringUtils.isEmpty(goods.getProviderGoodsId())) {

            // 供应商编辑商品
            map = goodsService.edit(goodsSaveRequest);
            List<Goods> providerGoods = goodsService.findByProviderGoodsId(goods.getGoodsId());
            if (goods.getGoodsSource() != 1) {
                if (CollectionUtils.isNotEmpty(providerGoods)) {
                    //同步商家商品Info  和 商品库sku 的supplyPrice 和stock
                    goodsService.synStoreGoodsInfoAndStandardSkuForSupplyPrice(goodsSaveRequest, providerGoods);
                    if (CollectionUtils.isNotEmpty((List<String>) map.get("delInfoIds"))) {
                        //同步删除商家商品Info商品库sku 的supplyPrice
                        List<String> storeGoodsInfoIds = goodsService.synDeleteStoreGoodsInfoAndStandardSku((List<String>) map.get("delInfoIds"));
                        map.put("delStoreGoodsInfoIds", storeGoodsInfoIds);
                    }
                }
            }
        } else {

            // 商家编辑商品
            if (goodsSaveRequest.getGoods().getAddedFlag() == AddedFlag.YES.toValue()) {
                // 如果是上架，要判断所属供应商商品是否是上架状态
                if (StringUtils.isNotEmpty(goods.getProviderGoodsId())) {
                    Goods providerGoods = goodsService.getGoodsById(goods.getProviderGoodsId());
                    if (Objects.nonNull(providerGoods) && providerGoods.getAddedFlag() == AddedFlag.NO.toValue()) {
                        // 所属供应商商品是下架，商家商品也修改为下架
                        goodsSaveRequest.getGoods().setAddedFlag(AddedFlag.NO.toValue());
                    }
                }
                //goodsSaveRequest.getGoods().setAddedFlag(null);
            }
            map = goodsService.edit(goodsSaveRequest);
            if (goodsSaveRequest.getGoods().getAddedFlag() == AddedFlag.YES.toValue()) {
                List<GoodsInfo> newGoodsInfo = goodsSaveRequest.getGoodsInfos();
                if (CollectionUtils.isNotEmpty(newGoodsInfo)) {
                    for (GoodsInfo goodsInfo : newGoodsInfo) {
                        BigDecimal stock = goodsInfo.getGoodsWareStocks().stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (stock.compareTo(BigDecimal.ZERO) > 0) {
                            goodsService.sendMessage(goodsInfo, stock);
                        }
                    }
                }
            }
        }
        if (Objects.nonNull(map.get("checkImageId"))) {
            goodsSaveRequest.setCheckImageId(Long.valueOf(map.get("checkImageId").toString()));
        }


        insertHcImage(goodsSaveRequest);
        //ares埋点-商品-后台修改商品sku
        goodsAresService.dispatchFunction("editGoodsSku",
                new Object[]{map.get("newGoodsInfo"), map.get("delInfoIds"), map.get("oldGoodsInfos"), map.get("storeGoodsInfoIds")});
        GoodsModifyResponse response = new GoodsModifyResponse();
        response.setReturnMap(map);
        return BaseResponse.success(response);
    }

    void insertHcImage(GoodsSaveRequest goodsSaveRequest) {
        if (CollectionUtils.isNotEmpty(goodsSaveRequest.getGoodsImageVOS()) && Objects.nonNull(goodsSaveRequest.getCheckImageId())) {
            List<GoodsImageStypeVO> collect = goodsSaveRequest.getGoodsImageVOS().stream().filter(v -> {
                if (v.getType() == 1) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "没有合成图片");
            } else if (collect.size() > 1) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "合成图片只能为1");
            }
            //覆盖操作
            //判断关联关系图片表有没有数据
            List<RelationGoodsImages> byGoodsInfoId = relationGoodsImagesRepository.findByGoodsId(goodsSaveRequest.getGoods().getGoodsId());

            relationGoodsImagesRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoods().getGoodsId());
            goodsImagestypeRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoods().getGoodsId());

            //新增
            AtomicReference<Long> imagesTypeIdHC = new AtomicReference<>(0L);
            AtomicReference<Long> cx_image_id = new AtomicReference<>(0L);
            Long imageId = goodsSaveRequest.getCheckImageId();
            //赋值id
            goodsSaveRequest.getGoodsImageVOS().forEach(v -> {
                long l = YitIdHelper.nextId();
                if (v.getType() == 0 && v.getCheckFlag() == 1) {
                    cx_image_id.set(l);
                }
                if (v.getType() == 1) {
                    imagesTypeIdHC.set(l);
                }
                v.setImagesTypeId(l);
            });
            long l = YitIdHelper.nextId();
            RelationGoodsImages build = RelationGoodsImages.builder().cxImageId(cx_image_id.get()).imageId(imageId).relationId(l)
                    .delFlag(0).goods_info_id("").goodsId(goodsSaveRequest.getGoods().getGoodsId())
                    .imagesTypeId(imagesTypeIdHC.get()).build();
            relationGoodsImagesRepository.save(build);
            goodsSaveRequest.getGoodsImageVOS().forEach(v -> {
                if (v.getType() == 1 && v.getCheckFlag() == 1) {
                    v.setRelationId(l);
                }
            });
            List<GoodsImageStype> convert = KsBeanUtil.convert(goodsSaveRequest.getGoodsImageVOS(), GoodsImageStype.class);
            goodsImagestypeRepository.saveAll(convert);

        } else {
            //如果集合为null 批量删除图片类型表 和关系表
            if (CollectionUtils.isEmpty(goodsSaveRequest.getGoodsImageVOS())) {
                relationGoodsImagesRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoods().getGoodsId());
                goodsImagestypeRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoods().getGoodsId());
            } else {
                List<GoodsImageStypeVO> goodsImageVOS = goodsSaveRequest.getGoodsImageVOS();
                List<GoodsImageStypeVO> collect = goodsImageVOS.stream().filter(v -> {
                    if (v.getType() == 1) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                List<GoodsImageStypeVO> collect1 = goodsImageVOS.stream().filter(v -> {
                    if (v.getType() == 0) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    //如何合成图片是空 删除图片类型表的合成图片  和删除关联关系表
                    goodsImagestypeRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoods().getGoodsId());
                    relationGoodsImagesRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoods().getGoodsId());
                    if (CollectionUtils.isNotEmpty(collect1)) {
                        //赋值id
                        goodsSaveRequest.getGoodsImageVOS().forEach(v -> {
                            long l = YitIdHelper.nextId();
                            v.setImagesTypeId(l);
                        });
                        //保存
                        List<GoodsImageStype> convert = KsBeanUtil.convert(goodsSaveRequest.getGoodsImageVOS(), GoodsImageStype.class);
                        goodsImagestypeRepository.saveAll(convert);
                    }
                }
                //判断是否删除促销图片

            }

            //判断促销图片是否为空
        }
    }

    @Override
    public BaseResponse<GoodsModifyResponse> modifyRetailGoods(@Valid GoodsModifyRequest request) {
        //  GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        RetailGoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, RetailGoodsSaveRequest.class);
        RetailGoods goods = retailGoodsService.getGoodsById(goodsSaveRequest.getGoods().getGoodsId());
        Map<String, Object> map;

        /**锁定库存空赋值*/
        if (Objects.isNull(goodsSaveRequest.getGoods().getLockStock())) {
            goodsSaveRequest.getGoods().setLockStock(0);
        }

        if (0 == goods.getGoodsSource() && StringUtils.isEmpty(goods.getProviderGoodsId())) {
            // 供应商编辑商品
            map = retailGoodsService.edit(goodsSaveRequest);
            List<RetailGoods> providerGoods = retailGoodsService.findByProviderGoodsId(goods.getGoodsId());
            if (goods.getGoodsSource() != 1) {
                if (CollectionUtils.isNotEmpty(providerGoods)) {
                    //同步商家商品Info  和 商品库sku 的supplyPrice 和stock
                    retailGoodsService.synStoreGoodsInfoAndStandardSkuForSupplyPrice(goodsSaveRequest, providerGoods);
                    if (CollectionUtils.isNotEmpty((List<String>) map.get("delInfoIds"))) {
                        //同步删除商家商品Info商品库sku 的supplyPrice
                        List<String> storeGoodsInfoIds = retailGoodsService.synDeleteStoreGoodsInfoAndStandardSku((List<String>) map.get("delInfoIds"));
                        map.put("delStoreGoodsInfoIds", storeGoodsInfoIds);
                    }
                }
            }
        } else {
            // 商家编辑商品
            if (goodsSaveRequest.getGoods().getAddedFlag() == AddedFlag.YES.toValue()) {
                // 如果是上架，要判断所属供应商商品是否是上架状态
                if (StringUtils.isNotEmpty(goods.getProviderGoodsId())) {
                    RetailGoods providerGoods = retailGoodsService.getGoodsById(goods.getProviderGoodsId());
                    if (providerGoods.getAddedFlag() == AddedFlag.NO.toValue()) {
                        // 所属供应商商品是下架，商家商品也修改为下架
                        goodsSaveRequest.getGoods().setAddedFlag(AddedFlag.NO.toValue());
                    }
                }
                //goodsSaveRequest.getGoods().setAddedFlag(null);
            }
            map = retailGoodsService.edit(goodsSaveRequest);
            if (goodsSaveRequest.getGoods().getAddedFlag() == AddedFlag.YES.toValue()) {
                List<RetailGoodsInfo> newGoodsInfo = goodsSaveRequest.getGoodsInfos();
                if (CollectionUtils.isNotEmpty(newGoodsInfo)) {
                    for (RetailGoodsInfo goodsInfo : newGoodsInfo) {
                        BigDecimal stock = goodsInfo.getGoodsWareStocks().stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (stock.compareTo(BigDecimal.ZERO) > 0) {
                            goodsService.sendMessage(goodsInfo, stock.setScale(0, BigDecimal.ROUND_DOWN).longValue());
                        }
                    }
                }
            }
        }

        //ares埋点-商品-后台修改商品sku
        goodsAresService.dispatchFunction("editGoodsSku",
                new Object[]{map.get("newGoodsInfo"), map.get("delInfoIds"), map.get("oldGoodsInfos"), map.get("storeGoodsInfoIds")});
        GoodsModifyResponse response = new GoodsModifyResponse();
        response.setReturnMap(map);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsModifyResponse> modifyBulkGoods(@Valid GoodsModifyRequest request) {
        //  GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        BulkGoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, BulkGoodsSaveRequest.class);
        BulkGoods goods = bulkGoodsService.getGoodsById(goodsSaveRequest.getGoods().getGoodsId());
        Map<String, Object> map;

        /**锁定库存空赋值*/
        if (Objects.isNull(goodsSaveRequest.getGoods().getLockStock())) {
            goodsSaveRequest.getGoods().setLockStock(0);
        }

        if (0 == goods.getGoodsSource() && StringUtils.isEmpty(goods.getProviderGoodsId())) {
            // 供应商编辑商品
            map = bulkGoodsService.edit(goodsSaveRequest);
            List<BulkGoods> providerGoods = bulkGoodsService.findByProviderGoodsId(goods.getGoodsId());
            if (goods.getGoodsSource() != 1) {
                if (CollectionUtils.isNotEmpty(providerGoods)) {
                    //同步商家商品Info  和 商品库sku 的supplyPrice 和stock
                    bulkGoodsService.synStoreGoodsInfoAndStandardSkuForSupplyPrice(goodsSaveRequest, providerGoods);
                    if (CollectionUtils.isNotEmpty((List<String>) map.get("delInfoIds"))) {
                        //同步删除商家商品Info商品库sku 的supplyPrice
                        List<String> storeGoodsInfoIds = bulkGoodsService.synDeleteStoreGoodsInfoAndStandardSku((List<String>) map.get("delInfoIds"));
                        map.put("delStoreGoodsInfoIds", storeGoodsInfoIds);
                    }
                }
            }
        } else {
            // 商家编辑商品
            if (goodsSaveRequest.getGoods().getAddedFlag() == AddedFlag.YES.toValue()) {
                // 如果是上架，要判断所属供应商商品是否是上架状态
                if (StringUtils.isNotEmpty(goods.getProviderGoodsId())) {
                    BulkGoods providerGoods = bulkGoodsService.getGoodsById(goods.getProviderGoodsId());
                    if (providerGoods.getAddedFlag() == AddedFlag.NO.toValue()) {
                        // 所属供应商商品是下架，商家商品也修改为下架
                        goodsSaveRequest.getGoods().setAddedFlag(AddedFlag.NO.toValue());
                    }
                }
                //goodsSaveRequest.getGoods().setAddedFlag(null);
            }
            map = bulkGoodsService.edit(goodsSaveRequest);
            if (goodsSaveRequest.getGoods().getAddedFlag() == AddedFlag.YES.toValue()) {
                List<BulkGoodsInfo> newGoodsInfo = goodsSaveRequest.getGoodsInfos();
                if (CollectionUtils.isNotEmpty(newGoodsInfo)) {
                    for (BulkGoodsInfo goodsInfo : newGoodsInfo) {
                        BigDecimal stock = goodsInfo.getGoodsWareStocks().stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (stock.compareTo(BigDecimal.ZERO) > 0) {
                            goodsService.sendBulkMessage(goodsInfo, stock.setScale(0, BigDecimal.ROUND_DOWN).longValue());
                        }
                    }
                }
            }
        }

        //ares埋点-商品-后台修改商品sku
        goodsAresService.dispatchFunction("editGoodsSku",
                new Object[]{map.get("newGoodsInfo"), map.get("delInfoIds"), map.get("oldGoodsInfos"), map.get("storeGoodsInfoIds")});
        GoodsModifyResponse response = new GoodsModifyResponse();
        response.setReturnMap(map);
        return BaseResponse.success(response);
    }

    /**
     * 新增商品定价
     *
     * @param request {@link GoodsAddPriceRequest}
     */
    @Override

    public BaseResponse addPrice(@RequestBody @Valid GoodsAddPriceRequest request) {
        GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        goodsService.savePrice(goodsSaveRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新增商品基本信息、基价
     *
     * @param request {@link GoodsAddAllRequest}
     * @return 商品编号 {@link GoodsAddAllResponse}
     */
    @Override

    public BaseResponse<GoodsAddAllResponse> addAll(@RequestBody @Valid GoodsAddAllRequest request) {
        GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        String goodsId = goodsService.addAll(goodsSaveRequest);

        GoodsAddAllResponse response = new GoodsAddAllResponse();
        response.setGoodsId(goodsId);
        return BaseResponse.success(response);
    }

    /**
     * 修改商品基本信息、基价
     *
     * @param request {@link GoodsModifyAllRequest}
     * @return 修改结果 {@link GoodsModifyAllResponse}
     */
    @Override

    public BaseResponse<GoodsModifyAllResponse> modifyAll(@RequestBody @Valid GoodsModifyAllRequest request) {
        GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        Map<String, Object> map = goodsService.editAll(goodsSaveRequest);

        //ares埋点-商品-后台修改商品sku
        goodsAresService.dispatchFunction("editGoodsSku", new Object[]{map.get
                ("newGoodsInfo"), map.get("delInfoIds"), map.get("oldGoodsInfos")});

        GoodsModifyAllResponse response = new GoodsModifyAllResponse();
        response.setResultMap(map);
        return BaseResponse.success(response);
    }

    /**
     * 修改商品基本信息、基价
     *
     * @param request {@link GoodsDeleteByIdsRequest}
     */
    @Override
    public BaseResponse deleteByIds(@RequestBody @Valid GoodsDeleteByIdsRequest request) {
        List<String> goodsIdList = request.getGoodsIds();
        goodsService.delete(goodsIdList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除商品零售商品
     *
     * @param request {@link GoodsDeleteByIdsRequest}
     * @return
     */
    @Override
    public BaseResponse deleteRetailByIds(@Valid GoodsDeleteByIdsRequest request) {
        List<String> goodsIdList = request.getGoodsIds();
        retailGoodsService.delete(goodsIdList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除商品散批商品
     *
     * @param request {@link GoodsDeleteByIdsRequest}
     * @return
     */
    @Override
    public BaseResponse deleteBulkByIds(@Valid GoodsDeleteByIdsRequest request) {
        List<String> goodsIdList = request.getGoodsIds();
        bulkGoodsService.delete(goodsIdList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品上下架状态
     *
     * @param request {@link GoodsModifyAddedStatusRequest}
     */
    @Override

    public BaseResponse modifyAddedStatus(@RequestBody @Valid GoodsModifyAddedStatusRequest request) {
        Integer addedFlag = request.getAddedFlag();
        List<String> goodsIdList = request.getGoodsIds();
        goodsService.updateAddedStatus(addedFlag, goodsIdList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品上下架状态
     *
     * @param request {@link GoodsModifyAddedStatusRequest}
     */
    @Override

    public BaseResponse updateAddedFlagByGoodsInfoIds(@RequestBody @Valid GoodsModifyAddedStatusRequest request) {
        Integer addedFlag = request.getAddedFlag();
        List<String> goodsIdList = request.getGoodsIds();
        goodsService.updateAddedFlagByGoodsInfoIds(addedFlag, goodsIdList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品分类
     *
     * @param request {@link GoodsModifyCateRequest}
     */
    @Override

    public BaseResponse modifyCate(@RequestBody @Valid GoodsModifyCateRequest request) {
        List<String> goodsIds = request.getGoodsIds();
        List<Long> storeCateIds = request.getStoreCateIds();
        goodsService.updateCate(goodsIds, storeCateIds);

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品商家名称
     *
     * @param request {@link GoodsModifySupplierNameRequest}
     */
    @Override

    public BaseResponse modifySupplierName(@RequestBody @Valid GoodsModifySupplierNameRequest request) {
        String supplierName = request.getSupplierName();
        Long companyInfoId = request.getCompanyInfoId();
        goodsService.updateSupplierName(supplierName, companyInfoId);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品运费模板
     *
     * @param request {@link GoodsModifyFreightTempRequest}
     */
    @Override

    public BaseResponse modifyFreightTemp(@RequestBody @Valid GoodsModifyFreightTempRequest request) {
        Long freightTempId = request.getFreightTempId();
        List<String> goodsIds = request.getGoodsIds();
        goodsService.updateFreight(freightTempId, goodsIds);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 商品审核
     *
     * @param request {@link GoodsCheckRequest}
     */
    @Override

    public BaseResponse checkGoods(@RequestBody @Valid GoodsCheckRequest request) {
        com.wanmi.sbc.goods.info.request.GoodsCheckRequest goodsCheckRequest =
                KsBeanUtil.convert(request, com.wanmi.sbc.goods.info.request.GoodsCheckRequest.class);
        s2bGoodsService.check(goodsCheckRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateGoodsCollectNum(@RequestBody @Valid GoodsModifyCollectNumRequest
                                                      goodsModifyCollectNumRequest) {
        goodsService.updateGoodsCollectNum(goodsModifyCollectNumRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateGoodsSalesNum(@RequestBody @Valid GoodsModifySalesNumRequest goodsModifySalesNumRequest) {
        goodsService.updateGoodsSalesNum(goodsModifySalesNumRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateGoodsFavorableCommentNum(@RequestBody @Valid GoodsModifyEvaluateNumRequest request) {
        goodsService.updateGoodsFavorableCommentNum(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<GoodsInfoUpdateResponse> batchUpdate(@RequestBody @Valid GoodsCommonBatchUpdateRequest request) {
        List<String> strings = goodsModifyExcelService.batchUpdate(request);
        GoodsInfoUpdateResponse goodsInfoUpdateResponse = new GoodsInfoUpdateResponse();
        goodsInfoUpdateResponse.setSkuIds(strings);
        return BaseResponse.success(goodsInfoUpdateResponse);
    }

    /**
     * 编辑商品排序
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse modifyGoodsSeqNum(@RequestBody @Valid GoodsModifySeqNumRequest request) {
        if (request.getStoreId() != null) {
            goodsService.modifyStoreGoodsSeqNum(request.getGoodsId(), request.getGoodsSeqNum(), request.getStoreId());
        } else {
            goodsService.modifyGoodsSeqNum(request.getGoodsId(), request.getGoodsSeqNum());
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量编辑商品排序
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse modifyBatchGoodsSeqNum(@RequestBody @Valid GoodsBatchModifySeqNumRequest request) {
        request.getBatchRequest().forEach(batch -> goodsService.modifyGoodsSeqNum(batch.getGoodsId(), batch.getGoodsSeqNum()));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchModifyCate(GoodsBatchModifyCateRequest request) {
        goodsService.batchModifyCate(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchModifyRetailCate(GoodsBatchModifyCateRequest request) {
        retailGoodsService.batchModifyRetailCate(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchModifyBulkCate(GoodsBatchModifyCateRequest request) {
        bulkGoodsService.batchModifyBulkCate(request);
        return BaseResponse.SUCCESSFUL();

    }

    /**
     * 新增商品
     *
     * @param request {@link GoodsAddRequest}
     * @return 新增结果 {@link GoodsAddResponse}
     */
    @Override

    public BaseResponse<GoodsAddResponse> merchantGoodsAdd(@RequestBody @Valid GoodsAddRequest request) {
        GoodsSaveRequest goodsSaveRequest = KsBeanUtil.convert(request, GoodsSaveRequest.class);
        String result = goodsService.merchantGoodsAdd(goodsSaveRequest);
        GoodsAddResponse response = new GoodsAddResponse();
        response.setResult(result);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsSkuInfoResponse> getSkuInfo(@RequestBody @Valid GoodsGetSkuRequest request) {
        GoodsSkuInfoResponse goodsSkuInfoResponse = new GoodsSkuInfoResponse();
        List<GoodsInfoVO> goodsInfoList = new ArrayList<>();

        List<List<String>> dimvalue = new ArrayList<>();
        List<List<String>> result = new ArrayList<>();
        for (GoodsAttributeVo goodsAttributeVo : request.getAttributeList()) {
            if (ObjectUtils.isEmpty(goodsAttributeVo.getAttributes())) {
                continue;
            }
            List<String> itemList = new ArrayList<>();
            for (String item : goodsAttributeVo.getAttributes()) {
                itemList.add(goodsAttributeVo.getAttributeId() + "-#-" + goodsAttributeVo.getAttribute() + "-#-" + item);
            }
            dimvalue.add(itemList);
        }
        descartes(dimvalue, result, 0, new ArrayList<>());
        for (List<String> item : result) {
            GoodsInfoVO vo = new GoodsInfoVO();
            String skuNo = '8' + String.valueOf(new Date().getTime()).substring(4, 10) + String.valueOf(Math.random()).substring(2, 5);
            vo.setGoodsInfoNo(skuNo);
            vo.setMarketPrice(BigDecimal.ZERO);
            vo.setLockStock(BigDecimal.ZERO);
            vo.setAddedFlag(AddedFlag.NO.toValue());
            vo.setCount(new Long(count));
            List<GoodsAttributeKeyVO> keyList = new ArrayList<>();
            for (String value : item) {
                String[] array = value.split("-#-");
                GoodsAttributeKeyVO keyVO = new GoodsAttributeKeyVO();
                keyVO.setAttributeId(array[0]);
                keyVO.setAttributeName(array[1]);
                keyVO.setGoodsAttributeValue(array[2]);
                keyList.add(keyVO);
            }
            vo.setGoodsAttributeKeys(keyList);
            vo.setGoodsInfoAttribute(keyList);
            goodsInfoList.add(vo);
        }
        goodsSkuInfoResponse.setGoodsInfoList(goodsInfoList);
        return BaseResponse.success(goodsSkuInfoResponse);
    }

    /**
     * 推送第三方商品到erp
     */
    @Override
    @Async
    public BaseResponse sysnErp(@RequestBody @Valid GoodsAddRequest goodsAddRequest) {
        //推送第三方商品到erp

        DevanningGoodsInfoQueryRequest devanningGoodsInfoQueryRequest = new DevanningGoodsInfoQueryRequest();
        devanningGoodsInfoQueryRequest.setGoodsId(goodsAddRequest.getGoods().getGoodsId());
        devanningGoodsInfoQueryRequest.setWareId(Long.valueOf(1));
        List<DevanningGoodsInfo> byParams = devanningGoodsInfoService.findByGoodsId(devanningGoodsInfoQueryRequest);

        if (CollectionUtils.isNotEmpty(byParams)) {
            byParams.stream().forEach(v -> {
                GoodsInfo one = goodsInfoService.findOne(v.getGoodsInfoId());
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("sku", v.getErpGoodsInfoNo());
                requestMap.put("skuDescr1", v.getGoodsInfoName());
                requestMap.put("skuBarcode", v.getGoodsInfoBarcode());
                requestMap.put("cube", one.getGoodsInfoCubage());
                if (Objects.nonNull(v) && Objects.nonNull(v.getAddStep()) && Objects.nonNull(one.getGoodsInfoUnit()) && Objects.nonNull(v.getDevanningUnit())) {
                    //1箱=10包
                    requestMap.put("alternateSkuB", "1" + one.getGoodsInfoUnit() + "=" + v.getAddStep() + v.getDevanningUnit());
                } else {
                    requestMap.put("alternateSkuB", "1箱" + "=1箱");
                }
                requestMap.put("price", v.getMarketPrice().toString());
                requestMap.put("skuGroup1", v.getDevanningUnit());
                requestMap.put("skuGroup2", one.getGoodsInfoUnit());
                requestMap.put("skuGroup3", v.getDevanningUnit());
                requestMap.put("skuGroup4", v.getErpGoodsInfoNo());
                requestMap.put("vipPrice", v.getVipPrice().toString());
                try {
                    HttpCommonResult post = HttpCommonUtil.post(erpSyncGoodsUrl, requestMap);
                    log.info(" 推送第三方商品到erp - erpSyncGoodsUrl result1:{}", post);
                } catch (Exception e) {
                    log.warn(" 推送第三方商品到erp异常 - erpSyncGoodsUrl result1:{}", requestMap);
                }

            });
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 推送第三方商品到erp
     */
    @Override
    @Async
    public BaseResponse sysnErpSku(@RequestBody @Valid GoodsAddRequest goodsAddRequest) {
        //推送第三方商品到erp

        DevanningGoodsInfoRequest devanningGoodsInfoQueryRequest = new DevanningGoodsInfoRequest();
        devanningGoodsInfoQueryRequest.setGoodsInfoIds(goodsAddRequest.getGoods().getGoodsInfoIds());
        devanningGoodsInfoQueryRequest.setWareId(Long.valueOf(1));
        DevanningGoodsInfoResponse skuByIds = devanningGoodsInfoService.findSkuByIds(devanningGoodsInfoQueryRequest);
        if (CollectionUtils.isNotEmpty(skuByIds.getDevanningGoodsInfos())) {
            skuByIds.getDevanningGoodsInfos().stream().forEach(v -> {
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("sku", v.getErpGoodsInfoNo());
                requestMap.put("skuDescr1", v.getGoodsInfoName());
                requestMap.put("skuBarcode", v.getGoodsInfoBarcode());
                requestMap.put("alternateSkuB", v.getGoodsSubtitleNew());
                if (Objects.nonNull(v) && Objects.nonNull(v.getAddStep()) && Objects.nonNull(v.getDevanningUnit()) && Objects.nonNull(v.getDevanningUnit())) {
                    //1箱=10包
                    requestMap.put("cube", "1" + v.getGoodsUnit() + "=" + v.getAddStep() + v.getDevanningUnit());
                } else {
                    requestMap.put("cube", "1" + v.getGoodsUnit() + "=" + v.getAddStep() + v.getGoodsUnit());
                }
                requestMap.put("price", v.getMarketPrice().toString());
                requestMap.put("skuGroup1", v.getGoodsUnit());
                requestMap.put("skuGroup2", v.getGoodsUnit());
                requestMap.put("skuGroup3", v.getDevanningUnit());
                requestMap.put("skuGroup4", v.getErpGoodsInfoNo());
                requestMap.put("vipPrice", v.getVipPrice().toString());
                try {
                    HttpCommonResult post = HttpCommonUtil.post(erpSyncGoodsUrl, requestMap);
                    log.info(" 推送第三方商品到erp - erpSyncGoodsUrl result1:{}", post);
                } catch (Exception e) {
                    log.warn(" 推送第三方商品到erp异常 - erpSyncGoodsUrl result1:{}", requestMap);
                }
            });

        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Boolean> batchGoodsTagRel(GoodsTagRelReOperateRequest request) {
        return BaseResponse.success(goodsTagRelService.batchGoodsTagRel(request));
    }
}
