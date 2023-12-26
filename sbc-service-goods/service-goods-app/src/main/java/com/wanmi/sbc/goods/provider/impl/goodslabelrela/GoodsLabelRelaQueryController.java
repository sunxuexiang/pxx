package com.wanmi.sbc.goods.provider.impl.goodslabelrela;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodslabelrela.GoodsLabelRelaQueryProvider;
import com.wanmi.sbc.goods.api.request.goodslabelrela.*;
import com.wanmi.sbc.goods.api.response.goodslabelrela.*;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelRelaVO;
import com.wanmi.sbc.goods.goodslabelrela.model.root.GoodsLabelRela;
import com.wanmi.sbc.goods.goodslabelrela.service.GoodsLabelRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>邀新统计查询服务接口实现</p>
 *
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@RestController
@Validated
public class GoodsLabelRelaQueryController implements GoodsLabelRelaQueryProvider {
    @Autowired
    private GoodsLabelRelaService goodsLabelRelaService;

    @Override
    public BaseResponse<GoodsLabelRelaPageResponse> page(@RequestBody @Valid GoodsLabelRelaPageRequest goodsLabelRelaPageReq) {
        GoodsLabelRelaQueryRequest queryReq = KsBeanUtil.convert(goodsLabelRelaPageReq, GoodsLabelRelaQueryRequest.class);
        Page<GoodsLabelRela> goodsLabelRelaPage = goodsLabelRelaService.page(queryReq);
        Page<GoodsLabelRelaVO> newPage = goodsLabelRelaPage.map(entity -> goodsLabelRelaService.wrapperVo(entity));
        MicroServicePage<GoodsLabelRelaVO> microPage = new MicroServicePage<>(newPage, goodsLabelRelaPageReq.getPageable());
        GoodsLabelRelaPageResponse finalRes = new GoodsLabelRelaPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<GoodsLabelRelaListResponse> list(@RequestBody @Valid GoodsLabelRelaListRequest goodsLabelRelaListReq) {
        GoodsLabelRelaQueryRequest queryReq = KsBeanUtil.convert(goodsLabelRelaListReq, GoodsLabelRelaQueryRequest.class);
        List<GoodsLabelRela> goodsLabelRelaList = goodsLabelRelaService.list(queryReq);
        List<GoodsLabelRelaVO> newList = goodsLabelRelaList.stream().map(entity -> goodsLabelRelaService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new GoodsLabelRelaListResponse(newList));
    }

    @Override
    public BaseResponse<GoodsLabelRelaByIdResponse> getById(@RequestBody @Valid GoodsLabelRelaByIdRequest goodsLabelRelaByIdRequest) {
        GoodsLabelRela goodsLabelRela =
                goodsLabelRelaService.getOne(goodsLabelRelaByIdRequest.getId());
        return BaseResponse.success(new GoodsLabelRelaByIdResponse(goodsLabelRelaService.wrapperVo(goodsLabelRela)));
    }

    @Override
    public BaseResponse<GoodsLabelRelaByLabelIdResponse> findByLabelId(GoodsLabelRelaByLabelIdRequest request) {
        Optional<List<GoodsLabelRela>> byLabelId = goodsLabelRelaService.findByLabelId(request.getLabelId());
        GoodsLabelRelaByLabelIdResponse goodsLabelRelaByLabelIdResponse = new GoodsLabelRelaByLabelIdResponse();
        if (byLabelId.isPresent()) {
            List<GoodsLabelRela> goodsLabelRelas = byLabelId.get();
            goodsLabelRelaByLabelIdResponse.setGoodsLabelRelaVOList(KsBeanUtil.convert(goodsLabelRelas, GoodsLabelRelaVO.class));
        }
        return BaseResponse.success(goodsLabelRelaByLabelIdResponse);
    }

    @Override
    public BaseResponse<GoodsLabelRelaByGoodsIdsResponse> findByGoodsIds(@Valid GoodsLabelRelaByGoodsIdsRequest request) {
        List<GoodsLabelRela> byGoodsIds = goodsLabelRelaService.findByGoodsIds(request.getGoodsIds());
        List<GoodsLabelRelaVO> convert = KsBeanUtil.convert(byGoodsIds, GoodsLabelRelaVO.class);
        return BaseResponse.success(GoodsLabelRelaByGoodsIdsResponse.builder().goodsLabelRelaVOList(convert).build());

    }

}

