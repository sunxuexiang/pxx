package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByInfoIdAndStepResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 17/4/12.
 */

@Slf4j
@Api(tags = "GoodsInfoController", description = "商品服务 Api")
@RestController
public class GoodsInfoController {
    @Autowired
    GoodsAresProvider goodsAresProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    /**
     * 获取商品SKU详情信息
     *
     * @param goodsInfoId 商品编号
     * @return 商品详情
     */
    @ApiOperation(value = "获取商品SKU详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsInfoId", value = "sku Id", required = true)
    @RequestMapping(value = "/goods/sku/{goodsInfoId}", method = RequestMethod.GET)
    public BaseResponse<GoodsInfoViewByIdResponse> info(@PathVariable String goodsInfoId) {
        return goodsInfoQueryProvider.getViewById(GoodsInfoViewByIdRequest.builder().goodsInfoId(goodsInfoId).build());
    }

    /**
     * 编辑商品SKU
     */
    @ApiOperation(value = "编辑商品SKU")
    @RequestMapping(value = "/goods/sku", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> edit(@RequestBody GoodsInfoModifyRequest saveRequest) {

        this.checkGoodsInfoModifyRequest(saveRequest);

        GoodsInfoByIdResponse goodsInfoVo = goodsInfoQueryProvider.getById(
                GoodsInfoByIdRequest.builder().goodsInfoId(saveRequest.getGoodsInfo().getGoodsInfoId()).build()).getContext();
        if(StringUtils.isNotEmpty(goodsInfoVo.getProviderGoodsInfoId())){
            GoodsInfoByIdResponse providerGoodsInfoVo = goodsInfoQueryProvider.getById(
                    GoodsInfoByIdRequest.builder().goodsInfoId(goodsInfoVo.getProviderGoodsInfoId()).build()).getContext();
            if(providerGoodsInfoVo.getAddedFlag().equals(AddedFlag.NO.toValue())){
                throw new SbcRuntimeException("K-030506");
            }
        }

        //校验关联本品是否重复
        if (Objects.nonNull(saveRequest.getGoodsInfo().getChoseProductSkuId())) {
            List<GoodsInfoVO> goodsInfoVOList = goodsInfoQueryProvider.page(GoodsInfoPageRequest.builder()
                    .notGoodsInfoId(saveRequest.getGoodsInfo().getGoodsInfoId())
                    .choseProductSkuId(saveRequest.getGoodsInfo().getChoseProductSkuId())
                    .build()).getContext().getGoodsInfoPage().getContent();
            if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
                throw new SbcRuntimeException("K-030570","关联本品商品重复，请重新选择");
            }
        }

        goodsInfoProvider.modify(saveRequest);
        if (saveRequest.getGoodsInfo().getGoodsInfoId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }
        //持化至ES
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder()
                .skuIds(Collections.singletonList(saveRequest.getGoodsInfo().getGoodsInfoId())).build());

        if(DefaultFlag.YES.equals(saveRequest.getGoodsInfo().getIsDevanning())){

            if(Objects.nonNull(saveRequest.getGoodsInfo().getDevanningType())){
                DevanningGoodsInfoVO convert = KsBeanUtil.convert(saveRequest.getGoodsInfo(), DevanningGoodsInfoVO.class);
                DevanningGoodsInfoRequest devanningGoodsInfoRequest = new DevanningGoodsInfoRequest();
                devanningGoodsInfoRequest.setDevanningGoodsInfoVO(convert);
                //
                String[] split = saveRequest.getGoodsInfo().getDevanningType().split(",");
                ArrayList<String> type = Lists.newArrayList();
                Collections.addAll(type,split);

                type.forEach(t->{
                    //1/4箱规格
                    log.info("DefaultFlag.YES.toValue() == Integer.parseInt(t) ---- {}", DefaultFlag.YES.toValue() == Integer.parseInt(t));
                    if(DefaultFlag.YES.toValue() == Integer.parseInt(t)){
                        //查询当前规格是否存在
                        //设置步长
                        devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setAddStep(saveRequest.getGoodsInfo().getAddStep().divide(BigDecimal.valueOf(4)));
                        DevanningGoodsInfoByInfoIdAndStepResponse context = devanningGoodsInfoQueryProvider.getInfoByIdAndStep(devanningGoodsInfoRequest).getContext();

                        //查询商品
                        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
                        goodsByIdRequest.setGoodsId(goodsInfoVo.getGoodsId());
                        GoodsByIdResponse goods = goodsQueryProvider.getById(goodsByIdRequest).getContext();

                        if(Objects.isNull(context.getDevanningGoodsInfoVO())){
                            //设置价格
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setMarketPrice(saveRequest.getGoodsInfo().getMarketPrice().divide(BigDecimal.valueOf(4),BigDecimal.ROUND_UP));
                            if(Objects.nonNull(goodsInfoVo.getSupplyPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setSupplyPrice(goodsInfoVo.getSupplyPrice().divide(BigDecimal.valueOf(4),BigDecimal.ROUND_UP));
                            }
                            if(Objects.nonNull(goodsInfoVo.getRetailPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setRetailPrice(goodsInfoVo.getRetailPrice().divide(BigDecimal.valueOf(4),BigDecimal.ROUND_UP));
                            }
                            if(Objects.nonNull(goodsInfoVo.getCostPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setCostPrice(goodsInfoVo.getCostPrice().divide(BigDecimal.valueOf(4),BigDecimal.ROUND_UP));
                            }
                            if(Objects.nonNull(goodsInfoVo.getVipPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setVipPrice(goodsInfoVo.getVipPrice().divide(BigDecimal.valueOf(4),BigDecimal.ROUND_UP));
                            }
                            //创建时间
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setCreateTime(LocalDateTime.now());
                            //同步区域限购
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setAllowedPurchaseArea(goodsInfoVo.getAllowedPurchaseArea());
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setAllowedPurchaseAreaName(goodsInfoVo.getAllowedPurchaseAreaName());
                            //副标题
                            StringBuffer subtitle = new StringBuffer();
                            StringBuffer subtitleNew = new StringBuffer();
                            if(Objects.nonNull(goods.getGoodsSubtitle())){
                                // subtitle结构=》 1箱(24盒x11.50元/盒)
                                //单位
                                char unit = goods.getGoodsSubtitle().charAt(goods.getGoodsSubtitle().length() - 2);
                                // 箱
                                char unitAll = goods.getGoodsSubtitle().charAt(1);
                                //价格
                                String priceStr = goods.getGoodsSubtitle().substring(goods.getGoodsSubtitle().lastIndexOf("x")).replace("x","");
                                String price = priceStr.substring(0,priceStr.lastIndexOf("元"));
                                //格式化步长
                                BigDecimal addStep = devanningGoodsInfoRequest.getDevanningGoodsInfoVO().getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP);

                                subtitle.append("1/4").append(unitAll).append("(");
                                subtitle.append(addStep).append(unit);
                                subtitle.append("x").append(price)
                                        .append("元/").append(unit).append(")");

                                subtitleNew.append(price).append("/").append(unit);
                            }
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setGoodsSubtitleNew(subtitleNew.toString());
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setGoodsInfoSubtitle(subtitle.toString());
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setDivisorFlag(BigDecimal.valueOf(0.25));

                            devanningGoodsInfoProvider.add(devanningGoodsInfoRequest);
                        }else{
                            //查询当前规格
                            DevanningGoodsInfoVO devanningGoodsInfoVORequest = saveRequest.getDevanningGoodsInfos().stream().filter(d -> d.getDevanningId().equals(context.getDevanningGoodsInfoVO().getDevanningId())).findFirst().orElse(null);
                            //最小单位
                            if(Objects.nonNull(goods.getGoodsSubtitle())){
                                char unit = goods.getGoodsSubtitle().charAt(goods.getGoodsSubtitle().length() - 2);
                                // 箱
                                char unitAll = goods.getGoodsSubtitle().charAt(1);
                                BigDecimal marketPrice = devanningGoodsInfoVORequest.getMarketPrice();//拆箱价格
                                BigDecimal addStep = devanningGoodsInfoVORequest.getAddStep();//步长
                                BigDecimal subtitlePrice = marketPrice.divide(addStep, 2, BigDecimal.ROUND_UP);
                                String s = String.valueOf(unit);
                                String goodsSubtitle = "1/4"+unitAll+"=" + addStep + s + "x" + subtitlePrice + "元/" + s;
                                String goodsSubtitleNew = subtitlePrice + "/" + s;
                                devanningGoodsInfoVORequest.setGoodsInfoSubtitle(goodsSubtitle);
                                devanningGoodsInfoVORequest.setGoodsSubtitleNew(goodsSubtitleNew);
                            }
                            //同步区域限购
                            devanningGoodsInfoVORequest.setAllowedPurchaseArea(goodsInfoVo.getAllowedPurchaseArea());
                            devanningGoodsInfoVORequest.setAllowedPurchaseAreaName(goodsInfoVo.getAllowedPurchaseAreaName());
                            //修改
                            DevanningGoodsInfoRequest updaterRequest = new DevanningGoodsInfoRequest();
                            updaterRequest.setDevanningGoodsInfoVO(devanningGoodsInfoVORequest);
                            devanningGoodsInfoProvider.update(updaterRequest);
                        }
                    }else{
                        //查询商品
                        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
                        goodsByIdRequest.setGoodsId(goodsInfoVo.getGoodsId());
                        GoodsByIdResponse goods = goodsQueryProvider.getById(goodsByIdRequest).getContext();
                        //半箱规格
                        //设置步长
                        devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setAddStep(saveRequest.getGoodsInfo().getAddStep().divide(BigDecimal.valueOf(2)));

                        DevanningGoodsInfoByInfoIdAndStepResponse context = devanningGoodsInfoQueryProvider.getInfoByIdAndStep(devanningGoodsInfoRequest).getContext();

                        if(Objects.isNull(context.getDevanningGoodsInfoVO())){
                            //设置价格
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setMarketPrice(saveRequest.getGoodsInfo().getMarketPrice().divide(BigDecimal.valueOf(2),BigDecimal.ROUND_UP));
                            if(Objects.nonNull(goodsInfoVo.getSupplyPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setSupplyPrice(goodsInfoVo.getSupplyPrice().divide(BigDecimal.valueOf(2),BigDecimal.ROUND_UP));
                            }
                            if(Objects.nonNull(goodsInfoVo.getRetailPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setRetailPrice(goodsInfoVo.getRetailPrice().divide(BigDecimal.valueOf(2),BigDecimal.ROUND_UP));
                            }
                            if(Objects.nonNull(goodsInfoVo.getCostPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setCostPrice(goodsInfoVo.getCostPrice().divide(BigDecimal.valueOf(2),BigDecimal.ROUND_UP));
                            }
                            if(Objects.nonNull(goodsInfoVo.getVipPrice())){
                                devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setVipPrice(goodsInfoVo.getVipPrice().divide(BigDecimal.valueOf(2),BigDecimal.ROUND_UP));
                            }

                            //创建时间
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setCreateTime(LocalDateTime.now());
                            //同步区域限购
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setAllowedPurchaseArea(goodsInfoVo.getAllowedPurchaseArea());
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setAllowedPurchaseAreaName(goodsInfoVo.getAllowedPurchaseAreaName());
                            //副标题
                            StringBuffer subtitle = new StringBuffer();
                            StringBuffer subtitleNew = new StringBuffer();
                            if(Objects.nonNull(goods.getGoodsSubtitle())){
                                // subtitle结构=》 1箱(24盒x11.50元/盒)
                                //单位
                                char unit = goods.getGoodsSubtitle().charAt(goods.getGoodsSubtitle().length() - 2);
                                // 箱
                                char unitAll = goods.getGoodsSubtitle().charAt(1);
                                //价格
                                String priceStr = goods.getGoodsSubtitle().substring(goods.getGoodsSubtitle().lastIndexOf("x")).replace("x","");
                                String price = priceStr.substring(0,priceStr.lastIndexOf("元"));
                                //格式化步长
                                BigDecimal addStep = devanningGoodsInfoRequest.getDevanningGoodsInfoVO().getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP);

                                subtitle.append("半").append(unitAll).append("(");
                                subtitle.append(addStep).append(unit);
                                subtitle.append("x").append(price)
                                        .append("元/").append(unit).append(")");

                                subtitleNew.append(price).append("/").append(unit);
                            }
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setGoodsSubtitleNew(subtitleNew.toString());
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setGoodsInfoSubtitle(subtitle.toString());
                            devanningGoodsInfoRequest.getDevanningGoodsInfoVO().setDivisorFlag(BigDecimal.valueOf(0.5));
                            devanningGoodsInfoProvider.add(devanningGoodsInfoRequest);
                        }else{
                            //查询当前规格
                            DevanningGoodsInfoVO devanningGoodsInfoVORequest = saveRequest.getDevanningGoodsInfos().stream().filter(d -> d.getDevanningId().equals(context.getDevanningGoodsInfoVO().getDevanningId())).findFirst().orElse(null);
                            //最小单位
                            if(Objects.nonNull(goods.getGoodsSubtitle())){
                                char unit = goods.getGoodsSubtitle().charAt(goods.getGoodsSubtitle().length() - 2);
                                // 箱
                                char unitAll = goods.getGoodsSubtitle().charAt(1);
                                BigDecimal marketPrice = devanningGoodsInfoVORequest.getMarketPrice();//拆箱价格
                                BigDecimal addStep = devanningGoodsInfoVORequest.getAddStep();//步长
                                BigDecimal subtitlePrice = marketPrice.divide(addStep, 2, BigDecimal.ROUND_UP);
                                String s = String.valueOf(unit);
                                String goodsSubtitle = "半"+unitAll+"=" + addStep + s + "x" + subtitlePrice + "元/" + s ;
                                String goodsSubtitleNew = subtitlePrice + "/" + s;
                                devanningGoodsInfoVORequest.setGoodsInfoSubtitle(goodsSubtitle);
                                devanningGoodsInfoVORequest.setGoodsSubtitleNew(goodsSubtitleNew);
                            }
                            //同步区域限购
                            devanningGoodsInfoVORequest.setAllowedPurchaseArea(goodsInfoVo.getAllowedPurchaseArea());
                            devanningGoodsInfoVORequest.setAllowedPurchaseAreaName(goodsInfoVo.getAllowedPurchaseAreaName());
                            //修改
                            DevanningGoodsInfoRequest updaterRequest = new DevanningGoodsInfoRequest();
                            updaterRequest.setDevanningGoodsInfoVO(devanningGoodsInfoVORequest);
                            devanningGoodsInfoProvider.update(updaterRequest);
                        }
                    }
                });

            }
        }else{
            DevanningGoodsInfoVO convert = KsBeanUtil.convert(saveRequest.getGoodsInfo(), DevanningGoodsInfoVO.class);
            BaseResponse<DevanningGoodsInfoByIdResponse> devanningGoodsInfoByIdResponseBaseResponse = devanningGoodsInfoQueryProvider.findBySkuId(DevanningGoodsInfoRequest.builder().devanningGoodsInfoVO(convert).build());
            DevanningGoodsInfoRequest updaterRequest = new DevanningGoodsInfoRequest();
            convert.setDevanningId(devanningGoodsInfoByIdResponseBaseResponse.getContext().getDevanningGoodsInfoVO().getDevanningId());
            updaterRequest.setDevanningGoodsInfoVO(convert);
            devanningGoodsInfoProvider.update(updaterRequest);
        }
        operateLogMQUtil.convertAndSend("商品", "编辑商品", "编辑商品：SKU编码" + saveRequest.getGoodsInfo().getGoodsInfoNo());

        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 校验请求参数是否满足要求
     * @param saveRequest
     */
    private void checkGoodsInfoModifyRequest( GoodsInfoModifyRequest saveRequest) {
        if (Objects.isNull(saveRequest.getGoodsInfo()) || Objects.isNull(saveRequest.getGoodsInfo().getGoodsInfoId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        // 指定区域销售必须要填写单用户限购数量，未填写数量，保存需要提示
        GoodsInfoDTO goodsInfo = saveRequest.getGoodsInfo();
        String singleOrderAssignAreaName = goodsInfo.getSingleOrderAssignAreaName();
        Long singleOrderPurchaseNum = goodsInfo.getSingleOrderPurchaseNum();
        // 如果设置了单用户限购区域，单用户限购数量不能为空
        if(Strings.isNotBlank(singleOrderAssignAreaName) && Objects.isNull(singleOrderPurchaseNum)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }

    /**
     * 编辑商品SKU
     */
    @ApiOperation(value = "编辑商品SKU")
    @RequestMapping(value = "/goods/sku/price", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> price(@RequestBody GoodsInfoPriceModifyRequest saveRequest) {
        if (saveRequest.getGoodsInfo() == null || saveRequest.getGoodsInfo().getGoodsInfoId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsInfoProvider.modifyPrice(saveRequest);
        if (saveRequest.getGoodsInfo().getGoodsInfoId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }
        //持化至ES
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder()
                .skuIds(Collections.singletonList(saveRequest.getGoodsInfo().getGoodsInfoId())).build());

        operateLogMQUtil.convertAndSend("商品", "设价",
                "设价：SKU编码" + saveRequest.getGoodsInfo().getGoodsInfoNo());
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 编辑推荐商品排序
     */
    @ApiOperation(value = "编辑推荐商品排序")
    @PostMapping(value = "/sku/modify/recommend/sort")
    public BaseResponse modifyRecommendSort(@RequestBody List<GoodsInfoModifyRecommendSortRequest> request) {
        goodsInfoProvider.clearAllRecommendSort(request.get(0).getWareId());
        goodsInfoProvider.modifyRecommendSort(request);
        if (request.stream().map(GoodsInfoModifyRecommendSortRequest::getGoodsInfoId).collect(Collectors.toList()) == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }
        //持化至ES
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder()
                .skuIds(request.stream().map(GoodsInfoModifyRecommendSortRequest::getGoodsInfoId).collect(Collectors.toList())).build());
        //清除redis首页推荐商品缓存列表
        redisService.delete(RedisKeyConstant.RECOMMEND_PAGE_SETTING);
        operateLogMQUtil.convertAndSend("商品", "编辑推荐商品排序","操作成功");
        return BaseResponse.SUCCESSFUL();
    }
}
