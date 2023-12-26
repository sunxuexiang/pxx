package com.wanmi.sbc.goods;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifySeqNumRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 商品服务
 */
@Api(tags = "SupplierGoodsController", description = "商品服务 Api")
@RestController
@RequestMapping("/goods")
public class SupplierGoodsController {
	
    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;
    
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;
    
    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    
    @Autowired
    private CommonUtil commonUtil;

    /**
     * 编辑商品排序序号
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑店铺内商品排序序号")
    @RequestMapping(value = "/supplier/spu/modifySeqNum", method = RequestMethod.PUT)
    public BaseResponse modifyGoodsSeqNum(@RequestBody GoodsModifySeqNumRequest request) {
        if (StringUtils.isBlank(request.getGoodsId()) || Objects.isNull(request.getGoodsSeqNum())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        if (request.getGoodsSeqNum() == 0) {
            request.setGoodsSeqNum(null);
        }
        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
        goodsByIdRequest.setGoodsId(request.getGoodsId());
        GoodsByIdResponse oldGoods = goodsQueryProvider.getById(goodsByIdRequest).getContext();
        
        request.setStoreId(commonUtil.getStoreId());
        goodsProvider.modifyGoodsSeqNum(request);

        GoodsVO goodsVO = new GoodsVO();
        KsBeanUtil.copyPropertiesThird(request, goodsVO);
        esGoodsInfoElasticService.modifyGoodsSeqNum(goodsVO);

        //ares埋点-商品-后台修改商品sku,迁移至goods微服务下
        operateLogMQUtil.convertAndSend("商品", "编辑店铺内排序序号",
                "SPU编码:" + oldGoods.getGoodsNo() +
                        "，操作前排序:" + oldGoods.getStoreGoodsSeqNum() +
                        "，操作后排序:" + request.getGoodsSeqNum() +
                        "，操作时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                        "，操作人:" + commonUtil.getOperator().getName());

        return BaseResponse.SUCCESSFUL();
    }

}