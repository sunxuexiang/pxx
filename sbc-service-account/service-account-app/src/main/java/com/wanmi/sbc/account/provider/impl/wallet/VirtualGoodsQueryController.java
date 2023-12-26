package com.wanmi.sbc.account.provider.impl.wallet;

import com.wanmi.sbc.account.api.provider.wallet.VirtualGoodsQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.VirtualGoodsRequest;
import com.wanmi.sbc.account.api.response.wallet.VirtualGoodsResponse;
import com.wanmi.sbc.account.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.account.wallet.model.root.VirtualGoods;
import com.wanmi.sbc.account.wallet.service.VirtualGoodsService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-21 17:11
 */


@RestController
@Validated
public class VirtualGoodsQueryController implements VirtualGoodsQueryProvider {

    @Autowired
    private VirtualGoodsService virtualGoodsService;

    /**
     * 查询单个虚拟商品
     *
     * @return
     */
    @Override
    public BaseResponse<VirtualGoodsResponse> getVirtualGoods(VirtualGoodsRequest virtualGoodsRequest) {
        VirtualGoods virtualGoods = virtualGoodsService.getVirtualGoods(virtualGoodsRequest.getGoodsId());
        VirtualGoodsVO virtualGoodsVO = KsBeanUtil.convert(virtualGoods, VirtualGoodsVO.class);
        return BaseResponse.success(VirtualGoodsResponse.builder().virtualGoods(virtualGoodsVO).build());
    }

    /**
     * 查询多个虚拟商品
     *
     * @param virtualGoodsRequest
     * @return
     */

    @Override
    public BaseResponse<VirtualGoodsResponse> getVirtualGoodsList(VirtualGoodsRequest virtualGoodsRequest) {
        List<VirtualGoods> list = virtualGoodsService.getVirtualGoodsList(virtualGoodsRequest.getGoodsIdList());
        List<VirtualGoodsVO> virtualGoodsVOS = KsBeanUtil.convertList(list, VirtualGoodsVO.class);
        return BaseResponse.success(VirtualGoodsResponse.builder().virtualGoodsList(virtualGoodsVOS).build());
    }

    /**
     * 查询全部虚拟商品(分页)
     *
     * @return
     */
    @Override
    public BaseResponse<VirtualGoodsResponse> getPageVirtualGoodsList(VirtualGoodsRequest request) {
        Page<VirtualGoods> newPage = virtualGoodsService.getPageVirtualGoodsList(request);
        MicroServicePage<VirtualGoods> microPage = new MicroServicePage<>(newPage, request.getPageable());
        MicroServicePage<VirtualGoodsVO> virtualGoodsVOS = KsBeanUtil.convertPage(microPage, VirtualGoodsVO.class);
        return BaseResponse.success(VirtualGoodsResponse.builder().pageVirtualGoodsList(virtualGoodsVOS).build());
    }

    /**
     * 增加或者修改一个虚拟商品
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse putVirtualGoods(VirtualGoodsRequest request) {
        Boolean flag = virtualGoodsService.saveVirturalGoods(request);
        if (flag) {
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.FAILED();
    }

    /**
     * 删除一个虚拟商品(根据goodsId)
     *
     * @return
     */
    @Override
    public BaseResponse deleteVirtualGoodsByGoodsId(VirtualGoodsRequest request) {
        virtualGoodsService.deleteVirtualGoodsByGoodsId(request.getGoodsId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除多个虚拟商品(根据goodsId)
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse deleteVirtualGoodsByGoodsIds(VirtualGoodsRequest request) {
        virtualGoodsService.deleteVirtualGoodsByGoodsIds(request.getGoodsIdList());
        return BaseResponse.SUCCESSFUL();
    }
}
