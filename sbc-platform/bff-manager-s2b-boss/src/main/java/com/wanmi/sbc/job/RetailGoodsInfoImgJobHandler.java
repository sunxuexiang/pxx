package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsImagesBySpuIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoUpdateImgRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 白鲸散批图片批处理
 * @author: XinJiang
 * @time: 2022/4/9 15:40
 */
@JobHandler(value = "retailGoodsInfoImgJobHandler")
@Component
@Slf4j
public class RetailGoodsInfoImgJobHandler extends IJobHandler {

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Override
    public ReturnT<String> execute(String erpNos) throws Exception {
        XxlJobLogger.log("批量处理白鲸散批图片定时任务开始：" + LocalDateTime.now());
        log.info("批量处理白鲸散批图片定时任务开始：{}",LocalDateTime.now());

        if (StringUtils.isNotBlank(erpNos)) {
            log.info("请求参数erpNos:::{}",erpNos);
            //todo 通过erp编码处理商品图片

        } else {
            List<GoodsInfoVO> goodsInfoVOS = retailGoodsInfoQueryProvider.listViewByGoodsInfoImgIsNull().getContext().getGoodsInfos();
            if (CollectionUtils.isNotEmpty(goodsInfoVOS)) {
                List<String> spuIds = goodsInfoVOS.stream().map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
                Map<String, List<GoodsImageVO>> goodsImageOfMap = retailGoodsInfoQueryProvider.getGoodsImageOfMap(GoodsImagesBySpuIdsRequest.builder().spuIds(spuIds).build()).getContext();
                goodsInfoVOS.forEach(goodsInfoVO -> {
                    List<GoodsImageVO> goodsImageVOS = goodsImageOfMap.getOrDefault(goodsInfoVO.getGoodsId(), Collections.emptyList());
                    if (CollectionUtils.isNotEmpty(goodsImageVOS)) {
                        goodsInfoVO.setGoodsInfoImg(goodsImageVOS.get(0).getArtworkUrl());
                    }
                });
                retailGoodsInfoQueryProvider.updateGoodsImg(GoodsInfoUpdateImgRequest.builder().goodsInfoVOList(goodsInfoVOS).build());
            } else {
                log.info("无商品图片需要处理！！！");
            }
        }

        log.info("批量处理白鲸散批图片定时任务结束：{}",LocalDateTime.now());
        XxlJobLogger.log("批量处理白鲸散批图片定时任务结束：" + LocalDateTime.now());
        return SUCCESS;
    }

}
