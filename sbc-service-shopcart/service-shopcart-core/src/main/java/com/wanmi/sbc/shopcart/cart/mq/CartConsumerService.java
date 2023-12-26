package com.wanmi.sbc.shopcart.cart.mq;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.shopcart.cart.*;
import com.wanmi.sbc.shopcart.constant.JmsDestinationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@EnableBinding(CartSink.class)
public class CartConsumerService {


    @Autowired
    private ShopCartRepository shopCartRepository;

    @Autowired
    private ShopCartNewPileTradeRepository shopCartNewPileTradeRepository;

    @Autowired
    private RetailShopCartRepository retailShopCartRepository;

    @Autowired
    private BulkShopCartRepository bulkShopCartRepository;


    @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_ADD_CUSTOMER)
    public void pushShopCarGoods(ShopCartVO shopCart){

        if(Objects.isNull(shopCart)){
            return;
        }
        log.info("购物车======="+ JSONObject.toJSONString(shopCart));
        if (!shopCart.getIsTunhuo()){
            if (Boolean.FALSE.equals(shopCart.getIsDelFlag())){
                ShopCart convert = KsBeanUtil.convert(shopCart, ShopCart.class);
                Optional<ShopCart> byId = shopCartRepository.findById(convert.getCartId());
                if(byId.isPresent()){
                    convert.setCreateTime(byId.get().getCreateTime());
                }else{
                    convert.setCreateTime(LocalDateTime.now());
                }
                convert.setIsCheck(DefaultFlag.YES);
                shopCartRepository.save(convert);
            } else {
                if(Objects.nonNull(shopCart.getCartId())){
                    shopCartRepository.deleteByCartId(shopCart.getCartId());
                }
            }
        }else {
            this.pushShopCarStoreGoods(shopCart);
        }

    }



    //    @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_STORE_ADD_CUSTOMER)
    public void pushShopCarStoreGoods(ShopCartVO shopCart){

        if(Objects.isNull(shopCart)){
            return;
        }
        log.info("囤货购物车======="+ JSONObject.toJSONString(shopCart));

        if (Boolean.FALSE.equals(shopCart.getIsDelFlag())){
            ShopCart convert = KsBeanUtil.convert(shopCart, ShopCart.class);
            Optional<ShopCartNewPileTrade> byId = shopCartNewPileTradeRepository.findById(convert.getCartId());
            if(byId.isPresent()){
                convert.setCreateTime(byId.get().getCreateTime());
            }else{
                convert.setCreateTime(LocalDateTime.now());
            }
            convert.setIsCheck(DefaultFlag.YES);
            shopCartNewPileTradeRepository.save(KsBeanUtil.convert(convert,ShopCartNewPileTrade.class));
        } else {
            if(Objects.nonNull(shopCart.getCartId())){
                shopCartNewPileTradeRepository.deleteByCartId(shopCart.getCartId());
            }
        }
    }




    @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_RETAIL_ADD_CUSTOMER)
    public void pushRetailShopCarGoods(RetailShopCartVo retailShopCartVo){

        if(Objects.isNull(retailShopCartVo)){
            return;
        }
        log.info("购物车======="+ JSONObject.toJSONString(retailShopCartVo));

        if (Boolean.FALSE.equals(retailShopCartVo.getIsDelFlag())){
            RetailShopCart convert = KsBeanUtil.convert(retailShopCartVo, RetailShopCart.class);
            Optional<RetailShopCart> byId = retailShopCartRepository.findById(convert.getCartId());
            if(byId.isPresent()){
                convert.setCreateTime(byId.get().getCreateTime());
            }else{
                convert.setCreateTime(LocalDateTime.now());
            }
            convert.setIsCheck(DefaultFlag.YES);
            log.info("最终插入数据========"+convert);
            if (Objects.isNull(convert.getCartId())){
                convert.setCartId(retailShopCartVo.getCartId());
            }
            retailShopCartRepository.save(convert);
        } else {
            if(Objects.nonNull(retailShopCartVo.getCartId())){
                retailShopCartRepository.deleteByCartId(retailShopCartVo.getCartId());
            }
        }
    }

    @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_BULK_ADD_CUSTOMER)
    public void pushShopCarGoods(BulkShopCartVO bulkShopCartVO){

        if(Objects.isNull(bulkShopCartVO)){
            return;
        }
        log.info("购物车======="+ JSONObject.toJSONString(bulkShopCartVO));

        if (Boolean.FALSE.equals(bulkShopCartVO.getIsDelFlag())){
            BulkShopCart bulkShopCart = KsBeanUtil.convert(bulkShopCartVO, BulkShopCart.class);

            Optional<BulkShopCart> byId = bulkShopCartRepository.findById(bulkShopCart.getCartId());
            if(byId.isPresent()){
                bulkShopCart.setCreateTime(byId.get().getCreateTime());
            }else{
                bulkShopCart.setCreateTime(LocalDateTime.now());
            }
            bulkShopCart.setIsCheck(DefaultFlag.YES);
            log.info("最终插入数据========"+bulkShopCart);
            if (Objects.isNull(bulkShopCart.getCartId())){
                bulkShopCart.setCartId(bulkShopCartVO.getCartId());
            }
            bulkShopCartRepository.save(bulkShopCart);
        } else {
            if(Objects.nonNull(bulkShopCartVO.getCartId())){
                bulkShopCartRepository.deleteByCartId(bulkShopCartVO.getCartId());
            }
        }
    }
}
