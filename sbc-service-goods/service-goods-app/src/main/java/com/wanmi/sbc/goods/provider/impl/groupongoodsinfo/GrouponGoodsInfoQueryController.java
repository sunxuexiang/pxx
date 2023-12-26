package com.wanmi.sbc.goods.provider.impl.groupongoodsinfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.groupongoodsinfo.GrouponGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.groupongoodsinfo.*;
import com.wanmi.sbc.goods.api.response.groupongoodsinfo.*;
import com.wanmi.sbc.goods.bean.vo.GrouponGoodsInfoByActivityIdAndGoodsIdVO;
import com.wanmi.sbc.goods.bean.vo.GrouponGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GrouponGoodsVO;
import com.wanmi.sbc.goods.groupongoodsinfo.model.root.GrouponGoodsInfo;
import com.wanmi.sbc.goods.groupongoodsinfo.service.GrouponGoodsInfoService;
import com.wanmi.sbc.goods.groupongoodsinfo.service.GrouponGoodsInfoSqlCriteriaBuilder;
import com.wanmi.sbc.goods.groupongoodsinfo.service.GrouponGoodsWhereCriteriaBuilder;
import com.wanmi.sbc.goods.info.model.entity.GoodsInfoParams;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.spec.service.GoodsInfoSpecDetailRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>拼团活动商品信息表查询服务接口实现</p>
 *
 * @author groupon
 * @date 2019-05-15 14:49:12
 */
@RestController
@Validated
public class GrouponGoodsInfoQueryController implements GrouponGoodsInfoQueryProvider {
    @Autowired
    private GrouponGoodsInfoService grouponGoodsInfoService;

    @Autowired
    private GoodsInfoService goodsInfoService;

    @Autowired
    private GoodsInfoSpecDetailRelService goodsInfoSpecDetailRelService;

    @Override
    public BaseResponse<GrouponGoodsInfoPageResponse> page(@RequestBody @Valid GrouponGoodsInfoPageRequest
                                                                   grouponGoodsInfoPageReq) {
        GrouponGoodsInfoQueryRequest queryReq = new GrouponGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(grouponGoodsInfoPageReq, queryReq);
        Page<GrouponGoodsInfo> grouponGoodsInfoPage = grouponGoodsInfoService.page(queryReq);
        Page<GrouponGoodsInfoVO> newPage = grouponGoodsInfoPage.map(entity -> grouponGoodsInfoService.wrapperVo
                (entity));
        MicroServicePage<GrouponGoodsInfoVO> microPage = new MicroServicePage<>(newPage, grouponGoodsInfoPageReq
                .getPageable());
        GrouponGoodsInfoPageResponse finalRes = new GrouponGoodsInfoPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<GrouponGoodsInfoListResponse> list(@RequestBody @Valid GrouponGoodsInfoListRequest
                                                                   grouponGoodsInfoListReq) {

        // 1.查询拼团商品
        GrouponGoodsInfoQueryRequest queryReq = new GrouponGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(grouponGoodsInfoListReq, queryReq);
        List<GrouponGoodsInfo> grouponGoodsInfoList = grouponGoodsInfoService.list(queryReq);
        List<GrouponGoodsInfoVO> newList = grouponGoodsInfoList.stream().map(entity -> grouponGoodsInfoService
                .wrapperVo(entity)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newList)) {
            // 2.查询商品表，填充商品名称、市场价、规格值
            List<GoodsInfoParams> goodsInfoParams = goodsInfoService.findGoodsInfoParamsByIds(
                    newList.stream().map(GrouponGoodsInfoVO::getGoodsInfoId).collect(Collectors.toList())
            );
            newList.forEach(item -> {
                Optional<GoodsInfoParams> params = goodsInfoParams.stream()
                        .filter(i -> i.getGoodsInfoId().equals(item.getGoodsInfoId())).findFirst();
                if (params.isPresent()) {
                    item.setGoodsInfoName(params.get().getGoodsInfoName());
                    item.setGoodsInfoNo(params.get().getGoodsInfoNo());
                    item.setSpecText(params.get().getSpecText());
                    item.setMarketPrice(params.get().getMarketPrice());
                }
            });

        }
        return BaseResponse.success(new GrouponGoodsInfoListResponse(newList));
    }

    @Override
    public BaseResponse<GrouponGoodsInfoByIdResponse> getById(@RequestBody @Valid GrouponGoodsInfoByIdRequest
                                                                      grouponGoodsInfoByIdRequest) {
        GrouponGoodsInfo grouponGoodsInfo = grouponGoodsInfoService.getById(grouponGoodsInfoByIdRequest
                .getGrouponGoodsId());
        return BaseResponse.success(new GrouponGoodsInfoByIdResponse(grouponGoodsInfoService.wrapperVo
                (grouponGoodsInfo)));
    }

    @Override
    public BaseResponse<GrouponGoodsInfoBatchByActivityIdAndGoodsIdResponse> batchByActivityIdAndGoodsId(@RequestBody
                                                                                                         @Valid
                                                                                                                 GrouponGoodsInfoBatchByActivityIdAndGoodsIdRequest request) {
        List<GrouponGoodsInfoByActivityIdAndGoodsIdVO> result = grouponGoodsInfoService.batchByGrouponActivityIdAndGoodsId(request);
        return BaseResponse.success(new GrouponGoodsInfoBatchByActivityIdAndGoodsIdResponse(result));
    }

    /**
     * 分页查询拼团活动商品SPU信息
     *
     * @param grouponGoodsPageRequest 分页请求参数和筛选对象 {@link GrouponGoodsPageRequest}
     * @return 分页查询拼团活动商品SPU信息 {@link GrouponGoodsPageResponse}
     * @author groupon
     */
    @Override
    public BaseResponse<GrouponGoodsPageResponse> pageGrouponGoods(@RequestBody
                                                                   @Valid GrouponGoodsPageRequest grouponGoodsPageRequest) {
        GrouponGoodsWhereCriteriaBuilder requestBuilder = KsBeanUtil.convert(grouponGoodsPageRequest, GrouponGoodsWhereCriteriaBuilder.class);
        Page<GrouponGoodsVO> grouponGoodsVOS = grouponGoodsInfoService.pageGrouponGoods(requestBuilder);
        List<String> goodsInfoIds = grouponGoodsVOS.stream().map(GrouponGoodsVO::getGoodsInfoId).collect(Collectors.toList());
        //默认先取SKU的图片，没有才取SPU的图片
        if (CollectionUtils.isNotEmpty(goodsInfoIds)){
            List<GoodsInfo> goodsInfos = goodsInfoService.queryByGoodsInfoIds(goodsInfoIds);
            for (GrouponGoodsVO param : grouponGoodsVOS.getContent()) {
                for (GoodsInfo goodsInfo : goodsInfos) {
                    if (goodsInfo.getGoodsInfoId().equals(param.getGoodsInfoId())
                            && StringUtils.isNotBlank(goodsInfo.getGoodsInfoImg())) {
                        param.setGoodsImg(goodsInfo.getGoodsInfoImg());
                        break;
                    }
                }
            }
        }
        return BaseResponse.success(GrouponGoodsPageResponse.builder().grouponGoodsVOS(KsBeanUtil.convertPage(grouponGoodsVOS, GrouponGoodsVO.class)).build());
    }

    /**
     * 分页查询拼团活动商品SKU信息
     *
     * @param grouponGoodsPageRequest 分页请求参数和筛选对象 {@link GrouponGoodsInfoSimplePageRequest}
     * @return 分页查询拼团活动商品SPU信息 {@link GrouponGoodsPageResponse}
     * @author groupon
     */
    @Override
    public BaseResponse<GrouponGoodsInfoSimplePageResponse> pageGrouponGoodsInfo(@RequestBody @Valid GrouponGoodsInfoSimplePageRequest
                                                                                         grouponGoodsPageRequest) {
        GrouponGoodsInfoSqlCriteriaBuilder requestBuilder = KsBeanUtil.convert(grouponGoodsPageRequest, GrouponGoodsInfoSqlCriteriaBuilder.class);
        Page<GrouponGoodsVO> grouponGoodsVOS = grouponGoodsInfoService.pageGrouponGoodsInfo(requestBuilder);
        MicroServicePage<GrouponGoodsVO> skus = KsBeanUtil.convertPage(grouponGoodsVOS, GrouponGoodsVO.class);
        //填充规格
        if(Boolean.TRUE.equals(grouponGoodsPageRequest.getHavSpecTextFlag())
                && CollectionUtils.isNotEmpty(skus.getContent())){
            Map<String,String> specMap = goodsInfoSpecDetailRelService.textByGoodsInfoIds(
                    skus.getContent().stream().map(GrouponGoodsVO::getGoodsInfoId).collect(Collectors.toList()));
            if(MapUtils.isNotEmpty(specMap)){
                skus.getContent().forEach(s -> s.setSpecText(specMap.get(s.getGoodsInfoId())));
            }
        }
        return BaseResponse.success(GrouponGoodsInfoSimplePageResponse.builder().grouponGoodsVOS(skus).build());
    }

    /**
     * 根据活动ID、SKU编号查询拼团商品信息
     * @param request
     * @return
     */
    @Override
    public BaseResponse<GrouponGoodsByGrouponActivityIdAndGoodsInfoIdResponse> getByGrouponActivityIdAndGoodsInfoId(@RequestBody @Valid GrouponGoodsByGrouponActivityIdAndGoodsInfoIdRequest request){
        GrouponGoodsInfo grouponGoodsInfo = grouponGoodsInfoService.findByGrouponActivityIdAndGoodsInfoId(request.getGrouponActivityId(),request.getGoodsInfoId());
        if (Objects.isNull(grouponGoodsInfo)){
            return BaseResponse.success(new GrouponGoodsByGrouponActivityIdAndGoodsInfoIdResponse());
        }
        GrouponGoodsInfoVO grouponGoodsInfoVO = KsBeanUtil.convert(grouponGoodsInfo,GrouponGoodsInfoVO.class);
        return BaseResponse.success(new GrouponGoodsByGrouponActivityIdAndGoodsInfoIdResponse(grouponGoodsInfoVO));
    }


}

