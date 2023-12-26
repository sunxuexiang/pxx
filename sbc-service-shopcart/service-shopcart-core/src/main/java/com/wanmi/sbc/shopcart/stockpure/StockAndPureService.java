package com.wanmi.sbc.shopcart.stockpure;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import com.wanmi.sbc.shopcart.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.shopcart.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.shopcart.cart.ChainHandle.StockAndPureChainNode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Log4j2
public class StockAndPureService {
    @Resource(name = "areaNodeList")
    private List<StockAndPureChainNode> areaNodeList;
    public void checkStockPure(StockAndPureChainNodeRequeest requeest){
        if (CollectionUtils.isEmpty(areaNodeList)) {
            throw new SbcRuntimeException("调用链为null");
        }
        for (StockAndPureChainNode checkNode : areaNodeList) {
            StockAndPureChainNodeRsponse result = checkNode.checkStockPure(requeest);
            if (CollectionUtils.isNotEmpty(result.getCheckPure())) {
                DevanningGoodsInfoPureVO devanningGoodsInfoPureVO = result.getCheckPure().stream().findAny().orElse(null);
                if (Objects.nonNull(devanningGoodsInfoPureVO)) {
                    if (Objects.nonNull(devanningGoodsInfoPureVO.getType())) {
                        switch (devanningGoodsInfoPureVO.getType()) {
                            case 0:
                                throw new SbcRuntimeException("k-250001");
                            case 1:
                                throw new SbcRuntimeException("k-250002");
                            case 2:
                                throw new SbcRuntimeException("k-250003");
                            case 3:
                                throw new SbcRuntimeException("k-250004");
                            case -1:
                                throw new SbcRuntimeException("k-250005");
                        }
                    }
                }
            }
        }
    }
}
