package com.wanmi.sbc.init;

import com.tencentcloudapi.tiia.v20190529.models.*;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.ImageDetectService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsimage.GoodsImageProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsCountByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoCountByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPageRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/init")
@Slf4j
@Api(description = "初始化图片库服务",tags ="InitImageDataController")
public class InitImageDataController {

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Autowired
    private GoodsQueryProvider goodsQueryProvider;
    @Autowired
    private ImageDetectService imageDetectService;
    @Autowired
    private GoodsImageProvider goodsImageProvider;

    /**
     * 将mysql 商品信息 同步到-> 图片库中
     */
    @ApiOperation(value = "将mysql 商品图片 同步到-> 图片库中")
    @RequestMapping(value = "/syncImage1", method = RequestMethod.GET)
    public BaseResponse syncImage(){
//批量查询所有SKU信息列表
        GoodsInfoCountByConditionRequest infoQueryRequest = new GoodsInfoCountByConditionRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsSource(1);
        Long totalCount = goodsInfoQueryProvider.countByCondition(infoQueryRequest).getContext().getCount();
        if (totalCount > 0) {
            int pageSize = 500;//每批查询500个GoodsID
            long pageCount = 0L;
            long m = totalCount % pageSize;
            if (m > 0) {
                pageCount = totalCount / pageSize + 1;
            } else {
                pageCount = totalCount / pageSize;
            }
            GoodsInfoPageRequest pageRequest = new GoodsInfoPageRequest();
            KsBeanUtil.copyPropertiesThird(infoQueryRequest, pageRequest);
            GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
            goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            goodsQueryRequest.setGoodsSource(1);
            DescribeGroupsRequest groupsRequest=new DescribeGroupsRequest();
            groupsRequest.setGroupId("xiyayaOnline");
            Long PicCount=imageDetectService.DescribeGroups(groupsRequest);
            for (int i = 0; i < pageCount; i++) {
                pageRequest.setPageNum(i);
                pageRequest.setPageSize(pageSize);
                pageRequest.setGoodsSource(1);
                MicroServicePage<GoodsInfoVO> goodsInfopage =
                        goodsInfoQueryProvider.page(pageRequest).getContext().getGoodsInfoPage();
                log.info("图片库开始上传-----------第"+i+"页");
                log.info("图片数量："+ goodsInfopage.getContent().size());
                CreateImageRequest req = new CreateImageRequest();//创建图片
                DescribeImagesRequest describeImagesReq=new DescribeImagesRequest();//查询图片
                DeleteImagesRequest deleteImagesReq=new DeleteImagesRequest();//删除图片
                req.setGroupId("xiyayaOnline");
                describeImagesReq.setGroupId("xiyayaOnline");
                deleteImagesReq.setGroupId("xiyayaOnline");
                if (CollectionUtils.isNotEmpty(goodsInfopage.getContent())) {
                    goodsInfopage.getContent().stream().forEach(goodsInfoVO -> {
                        describeImagesReq.setEntityId(goodsInfoVO.getGoodsInfoId());
                        try {
                            if(PicCount>0) {
                                DescribeImagesResponse describeImagesResponse = imageDetectService.DescribeImages(describeImagesReq);
                                ImageInfo[] ImageInfos = describeImagesResponse.getImageInfos();
                                if (ImageInfos.length > 0) {
                                    deleteImagesReq.setEntityId(goodsInfoVO.getGoodsInfoId());
                                    imageDetectService.DeleteImages(deleteImagesReq);
                                    log.info(goodsInfoVO.getGoodsInfoId() + "--图片已删除");
                                }
                            }
                        }catch (Exception e){
                            e.getMessage();
                        }
                        req.setEntityId(goodsInfoVO.getGoodsInfoId());
                        if(goodsInfoVO.getGoodsInfoName().length()>63){
                            req.setPicName(goodsInfoVO.getGoodsInfoName().substring(0,40));
                        }else{
                            req.setPicName(goodsInfoVO.getGoodsInfoName());
                        }
                        if(Objects.nonNull(goodsInfoVO.getGoodsInfoImg())){
                            log.info("图片----"+goodsInfoVO.getGoodsInfoImg());
                            req.setImageUrl(goodsInfoVO.getGoodsInfoImg());
                            try {
                                imageDetectService.CreateImage(req);
                            }catch (Exception e){
                                e.getMessage();
                            }
                        }
                        if(Objects.nonNull(goodsInfoVO.getGoodsId())) {
                            List<String> goodsIds = new ArrayList<>();
                            goodsIds.add(goodsInfoVO.getGoodsId());
                            List<GoodsImageVO> context = goodsImageProvider.getGoodsImagesByGoodsIds(goodsIds).getContext();
                            if (CollectionUtils.isNotEmpty(context)) {
                                if (context.size() > 9) {
                                    for (int j = 0; j < 9; j++) {
                                        req.setImageUrl(context.get(j).getArtworkUrl());
                                        try {
                                            imageDetectService.CreateImage(req);
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    }
                                } else if (context.size() > 0 && context.size() < 9) {
                                    context.stream().forEach(goodsImageVO -> {
                                        req.setImageUrl(goodsImageVO.getArtworkUrl());
                                        try {
                                            imageDetectService.CreateImage(req);
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    });
                                }
                            }
                        }
                       /* if(Objects.nonNull(goodsInfoVO.getGoodsImages())) {
                            goodsInfoVO.getGoodsImages().stream().forEach(goodsImageVO -> {
                                req.setImageUrl(goodsImageVO.getArtworkUrl());
                                req.setImageUrl(goodsInfoVO.getGoodsInfoImg());
                                try {
                                    imageDetectService.CreateImage(req);
                                }catch (Exception e){
                                    e.getMessage();
                                }
                            });
                        }*/
                    });
                }
            }
        }
        log.info("图片库同步成功-----------");
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 将mysql 商品信息 同步到-> 图片库中
     */
    @ApiOperation(value = "将mysql 商品图片 同步到-> 图片库中")
    @RequestMapping(value = "/syncImage", method = RequestMethod.GET)
    public BaseResponse syncImage1(){
        GoodsCountByConditionRequest infoQueryRequest = new GoodsCountByConditionRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsSource(1);
        Long totalCount = goodsQueryProvider.countByCondition(infoQueryRequest).getContext().getCount();
        if (totalCount > 0) {
            int pageSize = 500;//每批查询500个GoodsID
            long pageCount = 0L;
            long m = totalCount % pageSize;
            if (m > 0) {
                pageCount = totalCount / pageSize + 1;
            } else {
                pageCount = totalCount / pageSize;
            }
            GoodsPageRequest pageRequest = new GoodsPageRequest();
            KsBeanUtil.copyPropertiesThird(infoQueryRequest, pageRequest);
            GoodsByConditionRequest goodsQueryRequest = new GoodsByConditionRequest();
            goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            goodsQueryRequest.setGoodsSource(1);

            CreateImageRequest req = new CreateImageRequest();//创建图片
            DescribeImagesRequest describeImagesReq=new DescribeImagesRequest();//查询图片
            DeleteImagesRequest deleteImagesReq=new DeleteImagesRequest();//删除图片
            req.setGroupId("xiyayaOnline");
            describeImagesReq.setGroupId("xiyayaOnline");
            deleteImagesReq.setGroupId("xiyayaOnline");

            for (int i = 0; i < pageCount; i++) {
                pageRequest.setPageNum(i);
                pageRequest.setPageSize(pageSize);
                pageRequest.setGoodsSource(1);
                MicroServicePage<GoodsVO> goodsPage =
                        goodsQueryProvider.page(pageRequest).getContext().getGoodsPage();

                if (CollectionUtils.isNotEmpty(goodsPage.getContent())) {
                    goodsPage.getContent().stream().forEach(goodsVO -> {

                        req.setEntityId(goodsVO.getGoodsId());
                        if(goodsVO.getGoodsName().length()>21){
                            req.setPicName(goodsVO.getGoodsName().substring(0,21));
                        }else{
                            req.setPicName(goodsVO.getGoodsName());
                        }
                        if(Objects.nonNull(goodsVO.getGoodsImg())){
                            log.info("图片----"+goodsVO.getGoodsImg());
                            req.setImageUrl(goodsVO.getGoodsImg());
                            try {
                                imageDetectService.CreateImage(req);
                            }catch (Exception e){
                                log.info(e.getMessage());
                            }
                        }

                        if(Objects.nonNull(goodsVO.getGoodsId())) {
                            List<String> goodsIds = new ArrayList<>();
                            goodsIds.add(goodsVO.getGoodsId());
                            List<GoodsImageVO> context = goodsImageProvider.getGoodsImagesByGoodsIds(goodsIds).getContext();
                            if (CollectionUtils.isNotEmpty(context)) {
                                if (context.size() > 9) {
                                    for (int j = 0; j < 9; j++) {
                                        req.setImageUrl(context.get(j).getArtworkUrl());
                                        try {
                                            imageDetectService.CreateImage(req);
                                        } catch (Exception e) {
                                            log.info(e.getMessage());
                                        }
                                    }
                                } else if (context.size() > 0 && context.size() < 9) {
                                    context.stream().forEach(goodsImageVO -> {
                                        req.setImageUrl(goodsImageVO.getArtworkUrl());
                                        try {
                                            imageDetectService.CreateImage(req);
                                        } catch (Exception e) {
                                            log.info(e.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        }

        log.info("图片库同步成功-----------");
        return BaseResponse.SUCCESSFUL();
    }


}
