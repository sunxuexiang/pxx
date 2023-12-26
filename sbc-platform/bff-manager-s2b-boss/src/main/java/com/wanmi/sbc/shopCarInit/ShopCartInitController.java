package com.wanmi.sbc.shopCarInit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.provider.storelevel.StoreLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.request.store.validGroups.StoreUpdate;
import com.wanmi.sbc.customer.api.request.storelevel.StoreLevelByStoreIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreBaseInfoResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.api.response.storelevel.StroeLevelInfoResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.customer.bean.vo.StoreCustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoDeleteByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsSwitchRequest;
import com.wanmi.sbc.goods.bean.vo.PointsGoodsVO;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.store.StoreBaseService;
import com.wanmi.sbc.store.StoreSelfService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Api(tags = "ShopCartInitController")
@RestController("shopCartInitController")
@RequestMapping("/shopCart")
public class ShopCartInitController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ShopCartProvider shopCartProvider;

    /**
     * 编辑店铺结算日期
     */
    @ApiOperation(value = "初始化购物车")
    @RequestMapping(value = "/initshopcar", method = RequestMethod.GET)
    public BaseResponse initShopCar() {
        return  shopCartProvider.refreshShopCarRedis();
    }



}
