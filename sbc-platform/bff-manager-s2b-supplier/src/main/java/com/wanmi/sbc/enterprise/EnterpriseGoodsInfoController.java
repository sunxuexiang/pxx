package com.wanmi.sbc.enterprise;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.enterprise.EnterpriseGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.enterprise.EnterpriseGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.enterprise.goods.*;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseBatchDeleteResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseCheckResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.bean.dto.BatchEnterPrisePriceDTO;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.vas.bean.vo.IepSettingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author baijianzhong
 * @ClassName EnterPriseGoodsInfoController
 * @Date 2020-03-03 16:19
 * @Description TODO
 **/
@Api
@RestController
@RequestMapping("/enterprise")
public class EnterpriseGoodsInfoController {

    @Autowired
    private EnterpriseGoodsInfoQueryProvider enterpriseGoodsInfoQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private EnterpriseGoodsInfoProvider enterpriseGoodsInfoProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "分页查询企业购商品")
    @PostMapping("/goodsInfo/page")
    public BaseResponse<EnterpriseGoodsInfoPageResponse> pageEnterpriseGoodsInfo(@RequestBody @Valid EnterpriseGoodsInfoPageRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        return enterpriseGoodsInfoQueryProvider.page(request);
    }

    /**
     * 批量新增企业购商品
     *
     * @param batchUpdateRequest
     * @return
     */
    @ApiOperation(value = "批量新增企业购商品")
    @PostMapping(value = "/batchAdd")
    public BaseResponse batchAddEnterpriseGoodsInfo(@RequestBody @Valid EnterprisePriceBatchUpdateRequest batchUpdateRequest) {
        //判断是否购买了企业购服务
        IepSettingVO iepSettingVO = commonUtil.getIepSettingInfo();
        //审核开关入参
        batchUpdateRequest.setEnterpriseGoodsAuditFlag(iepSettingVO.getEnterpriseGoodsAuditFlag());
        EnterpriseGoodsAddResponse response = enterpriseGoodsInfoProvider.batchUpdateEnterprisePrice(batchUpdateRequest).getContext();
        if (CollectionUtils.isNotEmpty(response.getGoodsInfoIds())) {
            return BaseResponse.info(GoodsErrorCode.ENTERPRISE_INVALID_ERROR, "存在失效的商品，请删除后再保存", response.getGoodsInfoIds());
        }
        //入日志
        operateLogMQUtil.convertAndSend("应用", "添加企业购商品",
                "skuIds: " + Arrays.toString(batchUpdateRequest.getBatchEnterPrisePriceDTOS().stream().map(BatchEnterPrisePriceDTO::getGoodsInfoId).toArray()) +
                        "\n 审核状态：" + iepSettingVO.getEnterpriseGoodsAuditFlag());
        if (DefaultFlag.NO.equals(iepSettingVO.getEnterpriseGoodsAuditFlag())) {
            //更新es
            esGoodsInfoElasticService.updateEnterpriseGoodsInfo(batchUpdateRequest.getBatchEnterPrisePriceDTOS(), EnterpriseAuditState.CHECKED);
        } else {
            esGoodsInfoElasticService.updateEnterpriseGoodsInfo(batchUpdateRequest.getBatchEnterPrisePriceDTOS(), EnterpriseAuditState.WAIT_CHECK);
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 单个修改企业购商品的价格
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "单个修改企业购商品的价格")
    @PostMapping(value = "/modify")
    @LcnTransaction
    public BaseResponse modifyEnterpriseGoodsInfoPrice(@RequestBody @Valid EnterprisePriceUpdateRequest request) {
        //判断是否购买了企业购服务
        commonUtil.getIepSettingInfo();
        GoodsInfoByIdResponse goodsInfoByIdResponse = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                .goodsInfoId(request.getGoodsInfoId()).build()).getContext();
        if(Objects.isNull(goodsInfoByIdResponse)) {
            throw new RuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        //判断如果是已审核的就更新为审核通过，其他的更新为待审核
        request.setEnterpriseGoodsAuditFlag(EnterpriseAuditState.CHECKED.equals(goodsInfoByIdResponse.getEnterPriseAuditState())
                ? DefaultFlag.YES : DefaultFlag.NO);
        //更新库
        enterpriseGoodsInfoProvider.updateEnterprisePrice(request);
        //更新es
        List<BatchEnterPrisePriceDTO> batchEnterPrisePriceDTOS = new ArrayList<>();
        batchEnterPrisePriceDTOS.add(new BatchEnterPrisePriceDTO(request.getGoodsInfoId(), request.getEnterPrisePrice()));
        //修改价格后更新es
        esGoodsInfoElasticService.updateEnterpriseGoodsInfo(batchEnterPrisePriceDTOS,
                EnterpriseAuditState.CHECKED.equals(goodsInfoByIdResponse.getEnterPriseAuditState())
                        ? EnterpriseAuditState.CHECKED : EnterpriseAuditState.WAIT_CHECK);
        //入日志
        operateLogMQUtil.convertAndSend("应用", "修改企业专享价",
                "skuId: " + request.getGoodsInfoId() +
                        "\n 修改后的价格：" + request.getEnterPrisePrice());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除企业购商品
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "单个删除企业购商品")
    @PostMapping(value = "/delete")
    public BaseResponse deleteEnterpriseGoodsInfoPrice(@RequestBody @Valid EnterpriseSkuDeleteRequest request) {
        BaseResponse response = enterpriseGoodsInfoProvider.deleteEnterpriseGoods(request);
        //入日志
        operateLogMQUtil.convertAndSend("应用", "删除企业购商品",
                "skuId: " + request.getGoodsInfoId());
        if (CommonErrorCode.SUCCESSFUL.equals(response.getCode())) {
            //更新es
            List<BatchEnterPrisePriceDTO> batchEnterPrisePriceDTOS = new ArrayList<>();
            batchEnterPrisePriceDTOS.add(new BatchEnterPrisePriceDTO(request.getGoodsInfoId(), BigDecimal.ZERO));
            esGoodsInfoElasticService.updateEnterpriseGoodsInfo(batchEnterPrisePriceDTOS, EnterpriseAuditState.INIT);
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量删除企业购商品
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "批量删除企业购商品")
    @PostMapping(value = "/batchDelete")
    public BaseResponse batchDeleteEnterpriseGoodsInfoPrice(@RequestBody @Valid EnterpriseSpuDeleteRequest request) {
        EnterpriseBatchDeleteResponse response = enterpriseGoodsInfoProvider.batchDeleteEnterpriseGoods(request).getContext();
        //入日志
        operateLogMQUtil.convertAndSend("应用", "批量删除企业购商品",
                "skuId: " + response.getGoodsInfoIds().toString());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 检查商品中是否有企业购货品
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "检查商品中是否有企业购货品")
    @PostMapping(value = "/enterprise-check")
    public BaseResponse<EnterpriseCheckResponse> checkEnterpriseInSku(@RequestBody @Valid EnterpriseGoodsChangeRequest request) {
        return enterpriseGoodsInfoQueryProvider.checkEnterpriseInSku(request);
    }

    /**
     * 检查商品中是否有企业购货品
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "检查商品中是否有企业购货品")
    @PostMapping(value = "/retail/enterprise-check")
    public BaseResponse<EnterpriseCheckResponse> checkRetailEnterpriseInSku(@RequestBody @Valid EnterpriseGoodsChangeRequest request) {
        return enterpriseGoodsInfoQueryProvider.checkRetailEnterpriseInSku(request);
    }


    /**
     * 分页查询商家的企业购商品——用于选择商品时的接口
     *
     * @param pageRequest 商品 {@link EnterpriseGoodsInfoPageRequest}
     * @return 企业购商品分页
     */
    @ApiOperation(value = "分页查询商家的企业购商品")
    @RequestMapping(value = "/enterprise-sku", method = RequestMethod.POST)
    public BaseResponse<EnterpriseGoodsInfoPageResponse> page(@RequestBody EnterpriseGoodsInfoPageRequest pageRequest) {
        pageRequest.setStoreId(commonUtil.getStoreId());
        pageRequest.setGoodsStatus(BoolFlag.YES.toValue());
        return goodsInfoQueryProvider.enterpriseGoodsInfoPage(pageRequest);
    }

}
