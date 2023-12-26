package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsCateBrandService;
import com.wanmi.sbc.es.elastic.request.EsCateDeleteRequest;
import com.wanmi.sbc.goods.api.provider.cate.*;
import com.wanmi.sbc.goods.api.provider.prop.GoodsPropQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.*;
import com.wanmi.sbc.goods.api.request.prop.GoodsPropListAllByCateIdRequest;
import com.wanmi.sbc.goods.api.response.cate.BossGoodsCateDeleteByIdResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateModifyResponse;
import com.wanmi.sbc.goods.api.response.prop.GoodsPropListAllByCateIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsCateSortDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsPropVO;
import com.wanmi.sbc.goods.request.GoodsCateModify;
import com.wanmi.sbc.goods.service.GoodsCateExcelService;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponMarketingScopeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponMarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponMarketingScopeBatchAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponMarketingScopeByScopeIdRequest;
import com.wanmi.sbc.marketing.bean.dto.CouponMarketingScopeDTO;
import com.wanmi.sbc.marketing.bean.vo.CouponMarketingScopeVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * 商品分类服务 Created by chenli on 17/10/30.
 */
@RequestMapping("/goods")
@RestController
@Validated
@Api(tags = "GoodsCateController", description = "商品分类服务")
public class GoodsCateController {

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private GoodsPropQueryProvider goodsPropQueryProvider;

    @Autowired
    private GoodsCateProvider goodsCateProvider;

    @Autowired
    private BossGoodsCateQueryProvider bossGoodsCateQueryProvider;

    @Autowired
    private EsCateBrandService esCateBrandService;

    @Autowired
    private GoodsCateExcelService goodsCateExcelService;

    @Autowired
    private BossGoodsCateProvider bossGoodsCateProvider;

    @Autowired
    private CouponMarketingScopeProvider couponMarketingScopeProvider;

    @Autowired
    private CouponMarketingScopeQueryProvider couponMarketingScopeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private GoodsCateExcelProvider goodsCateExcelProvider;

    @Autowired
    private RedisService redisService;

    /**
     * 查询有效分类平台分类（有3级分类的）
     * @return
     */
    @ApiOperation(value = "查询有效分类平台分类（有3级分类的）")
    @RequestMapping(value = "/goodsCatesTree", method = RequestMethod.GET)
    public BaseResponse<List<GoodsCateVO>> queryGoodsCate(){
        List<GoodsCateVO> goodsCateVOS = goodsCateQueryProvider.list().getContext().getGoodsCateVOList();
        //过滤默认分类
        if(CollectionUtils.isNotEmpty(goodsCateVOS)){
            if(goodsCateVOS.get(0).getCateId() == -102L){
                goodsCateVOS.remove(0);
            }
        }

        return BaseResponse.success(goodsCateVOS);
    }

    /**
     * 查询分类下所有的属性信息
     * @return
     */
    @ApiOperation(value = "查询分类下所有的属性信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "cateId", value = "分类ID", required = true)
    @RequestMapping(value = "/goodsProp/{cateId}",method = RequestMethod.GET)
    public BaseResponse<List<GoodsPropVO>> listProp(@PathVariable Long cateId) {
        BaseResponse<GoodsPropListAllByCateIdResponse> baseResponse  = goodsPropQueryProvider.listAllByCateId(new GoodsPropListAllByCateIdRequest(cateId));
        GoodsPropListAllByCateIdResponse response = baseResponse.getContext();
        if (Objects.isNull(response)){
            return BaseResponse.success(Collections.emptyList());
        }
        return BaseResponse.success(response.getGoodsPropVOList());
    }

    /**
     * 查询商品分类
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品分类")
    @RequestMapping(value = "/goodsCates", method = RequestMethod.GET)
    public BaseResponse<List<GoodsCateVO>> list(GoodsCateListByConditionRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("isDefault", SortType.DESC.toValue());
        queryRequest.putSort("sort", SortType.ASC.toValue());
        queryRequest.putSort("createTime", SortType.DESC.toValue());
        return BaseResponse
                .success(goodsCateQueryProvider.listByCondition(queryRequest).getContext().getGoodsCateVOList());
    }

    /**
     * 新增商品分类
     */
    @ApiOperation(value = "新增商品分类")
    @RequestMapping(value = "/goodsCate", method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody GoodsCateAddRequest saveRequest) {
        GoodsCateVO goodsCate = goodsCateProvider.add(saveRequest).getContext().getGoodsCate();
        // 查询父分类是否关联优惠券
        List<CouponMarketingScopeVO> couponMarketingScopes = couponMarketingScopeQueryProvider
                .listByScopeId(CouponMarketingScopeByScopeIdRequest.builder()
                        .scopeId(String.valueOf(goodsCate.getCateParentId())).build())
                .getContext().getScopeVOList();
        if (CollectionUtils.isNotEmpty(couponMarketingScopes)) {
            couponMarketingScopes.stream().map(couponScope -> {
                couponScope.setMarketingScopeId(null);
                couponScope.setCateGrade(couponScope.getCateGrade() + 1);
                couponScope.setScopeId(String.valueOf(goodsCate.getCateId()));
                return couponScope;
            });
            couponMarketingScopeProvider.batchAdd(CouponMarketingScopeBatchAddRequest.builder()
                    .scopeDTOList(KsBeanUtil.convert(couponMarketingScopes, CouponMarketingScopeDTO.class)).build());
        }
        // 操作日志记录
        if (isNull(saveRequest.getGoodsCate().getCateId())) {
            operateLogMQUtil.convertAndSend("商品", "新增一级类目", "新增一级类目：" + saveRequest.getGoodsCate().getCateName());
        } else {
            operateLogMQUtil.convertAndSend("商品", "添加子类目", "添加子类目：" + saveRequest.getGoodsCate().getCateName());
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商品分类详情信息
     *
     * @param cateId 商品分类编号
     * @return 商品详情
     */
    @ApiOperation(value = "获取商品分类详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "cateId", value = "分类Id", required = true)
    @RequestMapping(value = "/goodsCate/{cateId}", method = RequestMethod.GET)
    public ResponseEntity<GoodsCateByIdResponse> list(@PathVariable Long cateId) {

        if (cateId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        GoodsCateByIdRequest goodsCateByIdRequest = new GoodsCateByIdRequest();
        goodsCateByIdRequest.setCateId(cateId);
        return ResponseEntity.ok(goodsCateQueryProvider.getById(goodsCateByIdRequest).getContext());
    }

    /**
     * 编辑商品分类
     */
    @ApiOperation(value = "编辑商品分类")
    @RequestMapping(value = "/goodsCate", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> edit(@Valid @RequestBody GoodsCateModify goodsCateModify) {
        if (Objects.isNull(goodsCateModify.getGoodsCate().getCateId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        BaseResponse<GoodsCateModifyResponse> modifyResponse = goodsCateProvider.modify(goodsCateModify.getGoodsCate());
        // 更新es信息
        esCateBrandService.updateToEs(modifyResponse.getContext().getGoodsCateListVOList());
        // 操作日志记录
        operateLogMQUtil.convertAndSend("商品", "编辑类目", "编辑类目：" + goodsCateModify.getGoodsCate().getCateName());
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 检测图片分类是否有子类
     */
    @ApiOperation(value = "检测图片分类是否有子类")
    @RequestMapping(value = "/goodsCate/child", method = RequestMethod.POST)
    public BaseResponse checkChild(@RequestBody BossGoodsCateCheckSignChildRequest queryRequest) {
        if (queryRequest == null || queryRequest.getCateId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return BaseResponse.success(bossGoodsCateQueryProvider.checkSignChild(queryRequest));
    }

    /**
     * 检测图片分类是否有商品
     */
    @ApiOperation(value = "检测图片分类是否有商品")
    @RequestMapping(value = "/goodsCate/goods", method = RequestMethod.POST)
    public BaseResponse checkGoods(@RequestBody BossGoodsCateCheckSignGoodsRequest queryRequest) {
        if (queryRequest == null || queryRequest.getCateId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return BaseResponse.success(bossGoodsCateQueryProvider.checkSignGoods(queryRequest));
    }

    /**
     * 删除商品分类
     */
    @ApiOperation(value = "删除商品分类")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "cateId", value = "分类Id", required = true)
    @RequestMapping(value = "/goodsCate/{cateId}", method = RequestMethod.DELETE)
    public BaseResponse delete(@PathVariable Long cateId) {
        if (cateId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        BossGoodsCateDeleteByIdRequest bossGoodsCateDeleteByIdRequest = new BossGoodsCateDeleteByIdRequest();
        bossGoodsCateDeleteByIdRequest.setCateId(cateId);
        BaseResponse<BossGoodsCateDeleteByIdResponse> baseResponse = bossGoodsCateProvider
                .deleteById(bossGoodsCateDeleteByIdRequest);
        esCateBrandService
                .deleteCateFromEs(new EsCateDeleteRequest(baseResponse.getContext().getLongList(), null, false));

        GoodsCateByIdRequest goodsCateByIdRequest = new GoodsCateByIdRequest();
        goodsCateByIdRequest.setCateId(cateId);
        GoodsCateByIdResponse goodsCate = goodsCateQueryProvider.getById(goodsCateByIdRequest).getContext();
        // 操作日志记录
        if (Objects.nonNull(goodsCate)) {
            operateLogMQUtil.convertAndSend("商品", "删除类目", "删除类目：" + goodsCate.getCateName());
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 拖拽排序商品分类
     */
    @ApiOperation(value = "拖拽排序商品分类")
    @RequestMapping(value = "/goods-cate/sort", method = RequestMethod.PUT)
    public BaseResponse goodsCateSort(@RequestBody List<GoodsCateSortDTO> goodsCateList) {
        operateLogMQUtil.convertAndSend("商品", "拖拽排序商品分类", "拖拽排序商品分类");
        return goodsCateProvider.batchModifySort(new GoodsCateBatchModifySortRequest(goodsCateList, null));
    }

    /**
     * 拖拽排序商品推荐分类
     */
    @ApiOperation(value = "拖拽排序商品推荐分类")
    @RequestMapping(value = "/recommend/goods-cate/sort", method = RequestMethod.PUT)
    public BaseResponse recommendGoodsCateSort(@RequestBody List<GoodsCateSortDTO> goodsCateList) {
        operateLogMQUtil.convertAndSend("商品", "拖拽排序商品推荐分类", "拖拽排序商品推荐分类");
        return goodsCateProvider.batchRecommendModifySort(new GoodsCateBatchModifySortRequest(goodsCateList, null));
    }

    /**
     * 下载模板
     */
    @ApiOperation(value = "下载模板")
    @RequestMapping(value = "/goodsCate/excel/template/{encrypted}", method = RequestMethod.GET)
    public void template(@PathVariable String encrypted) {
        goodsCateExcelService.exportTemplate();
        operateLogMQUtil.convertAndSend("商品", "下载模板", "操作成功");
    }

    /**
     * 下载错误文档
     */
    @ApiOperation(value = "下载错误文档")
    @RequestMapping(value = "/goodsCate/excel/err/{ext}/{decrypted}", method = RequestMethod.GET)
    public void downErrExcel(@PathVariable String ext, @PathVariable String decrypted) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsCateExcelService.downErrExcel(commonUtil.getOperatorId(), ext);
        operateLogMQUtil.convertAndSend("商品", "下载错误文档", "操作成功");
    }

    /**
     * 上传模板
     *
     * @param uploadFile
     * @return
     */
    @ApiOperation(value = "上传模板")
    @RequestMapping(value = "/goodsCate/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        operateLogMQUtil.convertAndSend("商品", "上传模板", "上传模板");
        return BaseResponse.success(goodsCateExcelService.upload(uploadFile, commonUtil.getOperatorId()));
    }

    /**
     * 类目导入
     *
     * @param ext
     * @return
     */
    @ApiOperation(value = "类目导入")
    @RequestMapping(value = "/goodsCate/import/{ext}", method = RequestMethod.GET)
    public BaseResponse<Boolean> importGoodsCate(@PathVariable String ext) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsCateExcelImportRequest request = new GoodsCateExcelImportRequest();
        request.setExt(ext);
        request.setUserId(commonUtil.getOperatorId());
        goodsCateExcelProvider.importGoodsCate(request);
        operateLogMQUtil.convertAndSend("商品", "类目导入", "操作成功");
        return BaseResponse.success(Boolean.TRUE);
    }

    /**
     * 新增商品推荐分类
     */
    @ApiOperation(value = "新增商品推荐分类")
    @RequestMapping(value = "/goodsCate/recommend", method = RequestMethod.POST)
    public BaseResponse addRecommend(@Valid @RequestBody GoodsCateAddRequest saveRequest) {
        goodsCateProvider.addGoodsCateRecommend(saveRequest);
        operateLogMQUtil.convertAndSend("商品", "新增商品推荐分类", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新增商品推荐分类
     */
    @ApiOperation(value = "删除商品推荐分类")
    @RequestMapping(value = "/goodsCate/recommend", method = RequestMethod.DELETE)
    public BaseResponse delRecommend(@Valid @RequestBody GoodsCateAddRequest saveRequest) {
        goodsCateProvider.delGoodsCateRecommend(saveRequest);
        operateLogMQUtil.convertAndSend("商品", "删除商品推荐分类", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商品推荐分类(缓存级别)
     *
     * @return
     */
    @ApiOperation(value = "获取商品推荐分类")
    @RequestMapping(value = "/goodsCate/recommend", method = RequestMethod.GET)
    public BaseResponse<List<GoodsCateVO>> getRecommend() {
        return BaseResponse.success(goodsCateQueryProvider.getRecommendByCache().getContext().getGoodsCateVOList());
    }

    /**
     * 设置零售超市分类图标
     *
     * @param retailCateImgRequest
     * @return
     */
    @ApiOperation(value = "设置零售超市分类图标")
    @RequestMapping(value = "/retailGoodsCate/set", method = RequestMethod.POST)
    public BaseResponse setRetailCateImg(@Valid @RequestBody RetailCateImgRequest retailCateImgRequest) {
        if (StringUtils.isBlank(retailCateImgRequest.getImgUrl())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        redisService.setString(RedisKeyConstant.RETAIL_GOODS_CATE_IMG, retailCateImgRequest.getImgUrl());
        operateLogMQUtil.convertAndSend("商品", "设置零售超市分类图标", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取零售超市分类图标
     *
     * @return
     */
    @ApiOperation(value = "获取零售超市分类图标")
    @RequestMapping(value = "/retailGoodsCateImg/get", method = RequestMethod.POST)
    public BaseResponse<String> getRetailCateImg() {
        return BaseResponse.success(redisService.getString(RedisKeyConstant.RETAIL_GOODS_CATE_IMG));
    }

    /**
     * 新增散批商品推荐分类
     */
    @ApiOperation(value = "新增散批商品推荐分类")
    @RequestMapping(value = "/retail/goodsCate/recommend", method = RequestMethod.POST)
    public BaseResponse addRetailRecommend(@Valid @RequestBody GoodsCateAddRequest saveRequest) {
        goodsCateProvider.addRetailGoodsCateRecommend(saveRequest);
        operateLogMQUtil.convertAndSend("商品", "新增散批商品推荐分类", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 拖拽排序散批推荐商品分类
     */
    @ApiOperation(value = "拖拽排序散批推荐商品分类")
    @RequestMapping(value = "/retail-recommend/goods-cate/sort", method = RequestMethod.PUT)
    public BaseResponse retailRecommendGoodsCateSort(@RequestBody List<GoodsCateSortDTO> goodsCateList) {
        operateLogMQUtil.convertAndSend("商品", "拖拽排序散批推荐商品分类", "拖拽排序散批推荐商品分类");
        return goodsCateProvider
                .batchRetailRecommendModifySort(new GoodsCateBatchModifySortRequest(goodsCateList, null));
    }

    /**
     * 删除散批商品推荐分类
     */
    @ApiOperation(value = "删除散批商品推荐分类")
    @RequestMapping(value = "/retail/goodsCate/recommend", method = RequestMethod.DELETE)
    public BaseResponse delRetailRecommend(@Valid @RequestBody GoodsCateAddRequest saveRequest) {
        goodsCateProvider.delRetailGoodsCateRecommend(saveRequest);
        operateLogMQUtil.convertAndSend("商品", "删除散批商品推荐分类", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取散批商品推荐分类(缓存级别)
     *
     * @return
     */
    @ApiOperation(value = "获取散批商品推荐分类")
    @RequestMapping(value = "/retail/goodsCate/recommend", method = RequestMethod.GET)
    public BaseResponse<List<GoodsCateVO>> getRetailRecommend() {
        return BaseResponse
                .success(goodsCateQueryProvider.getRetailRecommendByCache().getContext().getGoodsCateVOList());
    }

}

