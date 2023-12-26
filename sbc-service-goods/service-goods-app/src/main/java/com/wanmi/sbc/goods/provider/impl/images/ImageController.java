package com.wanmi.sbc.goods.provider.impl.images;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.image.ImageProvider;
import com.wanmi.sbc.goods.api.request.image.ImagesQueryRequest;
import com.wanmi.sbc.goods.api.response.image.ImagesResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import com.wanmi.sbc.goods.images.service.GoodsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class ImageController  implements ImageProvider {

    @Autowired
    private GoodsImageService goodsImageService;

    @Override
    public BaseResponse<ImagesResponse> listByGoodsIds(@RequestBody  @Valid ImagesQueryRequest imagesQueryRequest) {
        List<GoodsImageVO> byGoodsIds = goodsImageService.findByGoodsIds(imagesQueryRequest.getGoodsIds());
        return BaseResponse.success(ImagesResponse.builder().imageVOS(byGoodsIds).build());
    }
}
