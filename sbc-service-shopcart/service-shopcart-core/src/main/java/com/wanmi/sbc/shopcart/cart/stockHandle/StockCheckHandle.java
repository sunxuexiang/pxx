package com.wanmi.sbc.shopcart.cart.stockHandle;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.shopcart.cart.request.ShopCartRequest;
import com.wanmi.sbc.shopcart.redis.RedisCache;
import com.wanmi.sbc.shopcart.redis.RedisKeyConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 库存校验器
 */
@Log4j2
public class StockCheckHandle extends CheckHandle {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    public StockCheckHandle(CheckHandle next) {
        super(next);
    }
    @Override
    public boolean process(ShopCartRequest request) {
        log.info("库存校验器=======入参"+ JSON.toJSONString(request));
        //判断库存
        Map<Long, GoodsInfoDTO> nowGoodsInfo = request.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoDTO::getDevanningId, Function.identity(), (a, b) -> a));
        String key = RedisKeyConstants.GOODS_INFO_STOCK_HASH.concat(request.getGoodsInfoId());
        double stock = Double.parseDouble(redisCache.HashGet(key, RedisKeyConstants.GOODS_INFO_STOCK_HASH).toString());
        double parseDouble = Double.parseDouble(redisCache.HashGet(key, RedisKeyConstants.GOODS_INFO_YK_STOCK).toString());
        double re = stock-parseDouble; //库存余量
        GoodsInfoDTO goodsInfoDTO = nowGoodsInfo.get(request.getDevanningId());
        BigDecimal multiply = BigDecimal.valueOf(goodsInfoDTO.getBuyCount()).multiply(goodsInfoDTO.getDivisorFlag());//要购买数量
        //还需要查询购物车内这个商品对应的拆箱数据
        List<String> list =new LinkedList<>();
        list.add(request.getGoodsInfoId());
        List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().
                goodsInfoIds(list).wareId(request.getWareId())
                .build()).getContext().getDevanningGoodsInfoVOS();
        BigDecimal reduce = devanningGoodsInfoVOS.stream().filter(v -> {
            return !v.getIsExit();
        }).map(DevanningGoodsInfoVO::getDivisorFlag).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (BigDecimal.valueOf(re).compareTo(multiply.add(reduce))<0){
            throw new SbcRuntimeException("k-030301","系统库存校验失败请重新到购物车页面选择数量");
        }
        CheckHandle next = this.getNext();
        if (Objects.isNull(next)){
            return true;
        }else {
            return  next.process(request);
        }
    }
}
