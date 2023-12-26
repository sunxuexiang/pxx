package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByGoodsResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateGoodsRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "StoreRetailGoodsController", description = "商品服务 API")
@RestController
@RequestMapping("/bulk/goods")
public class StoreBulkGoodsController {



    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private BulkGoodsQueryProvider retailGoodsQueryProvider;

    /**
     * 查询商品
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> list(@RequestBody GoodsPageRequest queryRequest) {
        queryRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        queryRequest.setStoreId(commonUtil.getStoreId());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        queryRequest.putSort("createTime", SortType.DESC.toValue());
        queryRequest.putSort("goodsId", SortType.ASC.toValue());


        //补充店铺分类
        if(queryRequest.getStoreCateId() != null) {
            BaseResponse<StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse>  baseResponse = storeCateQueryProvider.listGoodsRelByStoreCateIdAndIsHaveSelf(new StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfRequest(queryRequest.getStoreCateId(), true));
            StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse cateIdAndIsHaveSelfResponse = baseResponse.getContext();
            if (Objects.nonNull(cateIdAndIsHaveSelfResponse)) {
                List<StoreCateGoodsRelaVO> relas = cateIdAndIsHaveSelfResponse.getStoreCateGoodsRelaVOList();
                if (CollectionUtils.isEmpty(relas)) {
                    GoodsPageResponse response = new GoodsPageResponse();
                    response.setGoodsPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageable(), 0));
                    return BaseResponse.success(response);
                }
                queryRequest.setStoreCateGoodsIds(relas.stream().map(StoreCateGoodsRelaVO::getGoodsId).collect(Collectors.toList()));
            }else{
                GoodsPageResponse response = new GoodsPageResponse();
                response.setGoodsPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageable(), 0));
                return BaseResponse.success(response);
            }
        }

        BaseResponse<GoodsPageResponse> goodsPageBaseResponse = retailGoodsQueryProvider.bulkpage(queryRequest);
        GoodsPageResponse response = goodsPageBaseResponse.getContext();
        List<GoodsVO> goodses = response.getGoodsPage().getContent();
        if(CollectionUtils.isNotEmpty(goodses)) {
            List<String> goodsIds = goodses.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
            BaseResponse<StoreCateListByGoodsResponse> baseResponse = storeCateQueryProvider.listByGoods(new StoreCateListByGoodsRequest(goodsIds));
            StoreCateListByGoodsResponse cateListByGoodsResponse = baseResponse.getContext();
            if (Objects.isNull(cateListByGoodsResponse)){
                return BaseResponse.success(response);
            }
            Map<String, List<StoreCateGoodsRelaVO>> storeCateMap = cateListByGoodsResponse.getStoreCateGoodsRelaVOList()
                    .stream().collect(Collectors.groupingBy(StoreCateGoodsRelaVO::getGoodsId));
            //为每个spu填充店铺分类编号
            if(MapUtils.isNotEmpty(storeCateMap)){
                goodses.stream()
                        .filter(goods -> storeCateMap.containsKey(goods.getGoodsId()))
                        .forEach(goods -> {
                            goods.setStoreCateIds(storeCateMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRelaVO::getStoreCateId).filter(id -> id != null).collect(Collectors.toList()));
                        });
            }
        }
        return BaseResponse.success(response);
    }
}
