package com.wanmi.sbc.job;

import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPlusStockByIdRequest;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsMinusStockRequest;
import com.wanmi.sbc.goods.bean.vo.PointsGoodsVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yang
 * @since 2019/5/24
 */
@JobHandler(value = "pointsGoodsJobHandler")
@Component
@Slf4j
public class PointsGoodsJobHandler extends IJobHandler {

    @Autowired
    private PointsGoodsQueryProvider pointsGoodsQueryProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private PointsGoodsSaveProvider pointsGoodsSaveProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("积分商品定时任务执行 " + LocalDateTime.now());
        List<PointsGoodsVO> pointsGoodsVOList = pointsGoodsQueryProvider.queryOverdueList().getContext().getPointsGoodsVOList();
        int total = pointsGoodsVOList.size();
        pointsGoodsVOList.forEach(pointsGoodsVO -> {
            // 过期积分商品兑换库存归0并停用
            pointsGoodsSaveProvider.resetStockById(PointsGoodsMinusStockRequest.builder()
                    .pointsGoodsId(pointsGoodsVO.getPointsGoodsId())
                    .build());
            // 过期积分商品回退商品库存
            GoodsInfoPlusStockByIdRequest goodsInfoPlusStockByIdRequest = new GoodsInfoPlusStockByIdRequest();
            goodsInfoPlusStockByIdRequest.setGoodsInfoId(pointsGoodsVO.getGoodsInfoId());
            goodsInfoPlusStockByIdRequest.setStock(pointsGoodsVO.getStock());
            goodsInfoProvider.plusStockById(goodsInfoPlusStockByIdRequest);
        });
        XxlJobLogger.log("积分商品定时任务执行结束： " + LocalDateTime.now() + ",处理总数为：" + total);
        return SUCCESS;
    }
}
