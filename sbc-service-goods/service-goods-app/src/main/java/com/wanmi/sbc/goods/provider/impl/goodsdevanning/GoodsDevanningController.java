package com.wanmi.sbc.goods.provider.impl.goodsdevanning;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goodsdevanning.GoodsDevanningProvider;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchUpdateRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.ares.GoodsAresService;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.GoodsDevanningVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.goodsdevanning.service.GoodsDevanningService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsSaveRequest;
import com.wanmi.sbc.goods.info.request.RetailGoodsSaveRequest;
import com.wanmi.sbc.goods.info.service.GoodsModifyExcelService;
import com.wanmi.sbc.goods.info.service.GoodsService;
import com.wanmi.sbc.goods.info.service.RetailGoodsService;
import com.wanmi.sbc.goods.info.service.S2bGoodsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController
@Validated
public class GoodsDevanningController implements GoodsDevanningProvider {

    @Autowired
    private GoodsDevanningService goodsDevanningService;

    @Override
    public BaseResponse<GoodsDevanningResponse> getmaxdata(@Valid GoodsDevanningQueryRequest request) {
        List<GoodsDevanningVO> goodsDevanningMaxstep = goodsDevanningService.getGoodsDevanningMaxstep(request);
        GoodsDevanningResponse goodsDevanningResponse =new GoodsDevanningResponse();
        goodsDevanningResponse.setGoodsDevanningVOS(goodsDevanningMaxstep);
        return BaseResponse.success(goodsDevanningResponse);
    }
}
