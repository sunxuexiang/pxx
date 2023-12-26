package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsevaluate.GoodsEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsevaluate.GoodsEvaluateSaveProvider;
import com.wanmi.sbc.goods.api.provider.goodsevaluateimage.GoodsEvaluateImageSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsevaluate.GoodsEvaluateByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsevaluate.GoodsEvaluateModifyRequest;
import com.wanmi.sbc.goods.api.request.goodsevaluate.GoodsEvaluatePageRequest;
import com.wanmi.sbc.goods.api.request.goodsevaluateimage.EvaluateImgUpdateIsShowReq;
import com.wanmi.sbc.goods.api.response.goodsevaluate.GoodsEvaluateByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsevaluate.GoodsEvaluateModifyResponse;
import com.wanmi.sbc.goods.api.response.goodsevaluate.GoodsEvaluatePageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsEvaluateVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author liutao
 * @date 2019/2/25 3:35 PM
 */
@Api(description = "boss商品评价Api",tags = "BossGoodsEvaluateController")
@RestController
@RequestMapping("/goods/evaluate")
public class GoodsEvaluateController {

    @Autowired
    private GoodsEvaluateQueryProvider goodsEvaluateQueryProvider;

    @Autowired
    private GoodsEvaluateSaveProvider goodsEvaluateSaveProvider;

    @Autowired
    GoodsEvaluateImageSaveProvider goodsEvaluateImageSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 分页查询商品评价列表
     *
     * @param goodsEvaluatePageRequest
     * @return
     */
    @ApiOperation(value = "分页查询商品评价列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<GoodsEvaluatePageResponse> page(@RequestBody @Valid GoodsEvaluatePageRequest
                                                                    goodsEvaluatePageRequest) {
        goodsEvaluatePageRequest.setStoreId(commonUtil.getStoreId());
        return goodsEvaluateQueryProvider.page(goodsEvaluatePageRequest);
    }

    /**
     * 获取商品评价详情
     * @param goodsEvaluateByIdRequest
     * @return
     */
    @ApiOperation(value = "获取商品评价详情信息")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public BaseResponse<GoodsEvaluateByIdResponse> info(@RequestBody @Valid GoodsEvaluateByIdRequest
                                                                    goodsEvaluateByIdRequest) {
        BaseResponse<GoodsEvaluateByIdResponse> response = goodsEvaluateQueryProvider.getById(goodsEvaluateByIdRequest);
        return  response;
    }

    /**
     * 商品评价回复
     * @param modifyRequest
     * @return
     */
    @ApiOperation(value = "回复商品评价")
    @RequestMapping(value = "/answer", method = RequestMethod.POST)
    public BaseResponse<GoodsEvaluateModifyResponse> answer(@RequestBody @Valid  GoodsEvaluateModifyRequest
                                                                        modifyRequest) {
        GoodsEvaluateByIdRequest goodsEvaluateByIdRequest = new GoodsEvaluateByIdRequest();
        goodsEvaluateByIdRequest.setEvaluateId(modifyRequest.getEvaluateId());
        GoodsEvaluateVO goodsEvaluateDetail =
               goodsEvaluateQueryProvider.getById(goodsEvaluateByIdRequest).getContext().getGoodsEvaluateVO();
        //如果有过回复的话，把以前的回复移到历史回复
        GoodsEvaluateModifyRequest newModifyRequest = new GoodsEvaluateModifyRequest();
        KsBeanUtil.copyPropertiesThird(goodsEvaluateDetail,newModifyRequest);
        if(Objects.nonNull(goodsEvaluateDetail) && StringUtils.isNotEmpty(goodsEvaluateDetail
        .getEvaluateAnswer())){
            newModifyRequest.setHistoryEvaluateAnswerTime(goodsEvaluateDetail.getEvaluateAnswerTime());
            newModifyRequest.setHistoryEvaluateAnswer(goodsEvaluateDetail.getEvaluateAnswer());
            newModifyRequest.setHistoryEvaluateAnswerEmployeeId(goodsEvaluateDetail.getEvaluateAnswerEmployeeId());
            newModifyRequest.setHistoryEvaluateAnswerAccountName(goodsEvaluateDetail.getEvaluateAnswerAccountName());
        }
        newModifyRequest.setIsShow(modifyRequest.getIsShow());
        newModifyRequest.setEvaluateAnswer(modifyRequest.getEvaluateAnswer());
        newModifyRequest.setEvaluateAnswerAccountName(commonUtil.getAccountName());
        newModifyRequest.setEvaluateAnswerEmployeeId(commonUtil.getOperator().getAdminId());
        newModifyRequest.setEvaluateAnswerTime(LocalDateTime.now());
        newModifyRequest.setIsAnswer(modifyRequest.getIsAnswer());
        newModifyRequest.setBuyTime(goodsEvaluateDetail.getBuyTime());
        newModifyRequest.setGoodsImg(goodsEvaluateDetail.getGoodsImg());
        goodsEvaluateImageSaveProvider.updateIsShowBygoodsId(EvaluateImgUpdateIsShowReq.builder()
                .evaluateId(modifyRequest.getEvaluateId()).isShow(modifyRequest.getIsShow()).build());
        BaseResponse<GoodsEvaluateModifyResponse> response = goodsEvaluateSaveProvider.modify(newModifyRequest);
        operateLogMQUtil.convertAndSend("商品", "回复商品评价","回复商品评价：评价ID" + (Objects.nonNull(modifyRequest) ? modifyRequest.getEvaluateId() : ""));
        return  response;
    }
}