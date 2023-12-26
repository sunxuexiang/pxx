package com.wanmi.sbc.job;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageQueryProvider;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManagePageRequest;
import com.wanmi.sbc.goods.bean.vo.StockoutManageVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: CheckInventory
 * @Description: TODO
 * @Date: 2020/5/30 10:25
 * @Version: 1.0
 */
@JobHandler(value = "arrivalPushHandler")
@Component
@Slf4j
public class ArrivalPushHandler extends IJobHandler {

    @Autowired
    private StockoutManageQueryProvider stockoutManageQueryProvider;

    private Logger logger = LoggerFactory.getLogger(ArrivalPushHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("短信推送开始........");
        int pageSize=100;
        int pageNum=1;
        while (true){
            StockoutManagePageRequest request=new StockoutManagePageRequest();
            request.setDelFlag(DeleteFlag.NO);
            request.setReplenishmentFlag(ReplenishmentFlag.NO);
            request.setPageSize(pageSize);
            request.setPageNum(pageNum-1);
            List<StockoutManageVO> stockOutManageVOList = stockoutManageQueryProvider.pushGoodsStockPage(request).getContext().getStockoutManageVOList();
            pageNum++;
            if (CollectionUtils.isEmpty(stockOutManageVOList)) {
                break;
            }
        }
        return SUCCESS;
    }
}
