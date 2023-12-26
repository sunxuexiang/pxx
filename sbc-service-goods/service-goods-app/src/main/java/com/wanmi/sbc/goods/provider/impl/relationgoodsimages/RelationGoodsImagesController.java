package com.wanmi.sbc.goods.provider.impl.relationgoodsimages;

import com.github.yitter.idgen.YitIdHelper;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsImagestype.GoodsImageStypeProvider;
import com.wanmi.sbc.goods.api.provider.relationgoodsimages.RelationGoodsImagesProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsImageTypeAddRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsImageStypeVO;
import com.wanmi.sbc.goods.bean.vo.RelationGoodsImagesVO;
import com.wanmi.sbc.goods.goodsimagestype.model.root.GoodsImageStype;
import com.wanmi.sbc.goods.goodsimagestype.repository.GoodsImagestypeRepository;
import com.wanmi.sbc.goods.info.request.GoodsSaveRequest;
import com.wanmi.sbc.goods.relationgoodsimages.model.root.RelationGoodsImages;
import com.wanmi.sbc.goods.relationgoodsimages.repository.RelationGoodsImagesRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@Validated
public class RelationGoodsImagesController implements RelationGoodsImagesProvider {

	@Autowired
	private RelationGoodsImagesRepository relationGoodsImagesRepository;

    @Autowired
    private GoodsImagestypeRepository goodsImagestypeRepository;

	@Override
	public BaseResponse<RelationGoodsImagesVO> getRelationByGoodsId(String goodsid) {
		List<RelationGoodsImages> byGoodsId = relationGoodsImagesRepository.findByGoodsId(goodsid);
		Optional<RelationGoodsImages> first = byGoodsId.stream().findFirst();
		return first.map(relationGoodsImages -> BaseResponse.success(KsBeanUtil.convert(relationGoodsImages, RelationGoodsImagesVO.class))).orElseGet(() -> BaseResponse.success(null));
	}

    @Override
    @Transactional
    public BaseResponse addList(GoodsImageTypeAddRequest goodsImageTypeAddRequest) {
        insertHcImage(goodsImageTypeAddRequest);
        return BaseResponse.SUCCESSFUL();
    }

    void insertHcImage(GoodsImageTypeAddRequest request) {
        request.getBaseGoodsImageTypeAddList().forEach(goodsSaveRequest->{
            if (CollectionUtils.isNotEmpty(goodsSaveRequest.getGoodsImageVOS()) && Objects.nonNull(goodsSaveRequest.getCheckImageId()) ){
                List<GoodsImageStypeVO> collect = goodsSaveRequest.getGoodsImageVOS().stream().filter(v -> {
                    if (v.getType() == 1) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)){
                    throw new SbcRuntimeException("没有合成图片");
                }
                else if (collect.size()>1){
                    throw new SbcRuntimeException("合成图片只能为1");
                }
                //覆盖操作
                //判断关联关系图片表有没有数据
                List<RelationGoodsImages> byGoodsInfoId = relationGoodsImagesRepository.findByGoodsId(goodsSaveRequest.getGoodsId());

                relationGoodsImagesRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoodsId());
                goodsImagestypeRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoodsId());

                //新增
                AtomicReference<Long> imagesTypeIdHC = new AtomicReference<>(0L);
                AtomicReference<Long> cx_image_id = new AtomicReference<>(0L);
                Long imageId = goodsSaveRequest.getCheckImageId();
                //赋值id
                goodsSaveRequest.getGoodsImageVOS().forEach(v->{
                    long l = YitIdHelper.nextId();
                    if (v.getType()==0 && v.getCheckFlag()==1){
                        cx_image_id.set(l);
                    }
                    if (v.getType()==1){
                        imagesTypeIdHC.set(l);
                    }
                    v.setImagesTypeId(l);
                });
                long l = YitIdHelper.nextId();
                RelationGoodsImages build = RelationGoodsImages.builder().cxImageId(cx_image_id.get()).imageId(imageId).relationId(l)
                        .delFlag(0).goods_info_id("").goodsId(goodsSaveRequest.getGoodsId())
                        .imagesTypeId(imagesTypeIdHC.get()).build();
                relationGoodsImagesRepository.save(build);
                goodsSaveRequest.getGoodsImageVOS().forEach(v->{
                    if (v.getType()==1 && v.getCheckFlag() == 1){
                        v.setRelationId(l);
                    }
                });
                List<GoodsImageStype> convert = KsBeanUtil.convert(goodsSaveRequest.getGoodsImageVOS(), GoodsImageStype.class);
                goodsImagestypeRepository.saveAll(convert);

            }
//            else {
//                //如果集合为null 批量删除图片类型表 和关系表
//                if (CollectionUtils.isEmpty(goodsSaveRequest.getGoodsImageVOS())){
//                    relationGoodsImagesRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoodsId());
//                    goodsImagestypeRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoodsId());
//                }else {
//                    List<GoodsImageStypeVO> goodsImageVOS = goodsSaveRequest.getGoodsImageVOS();
//                    List<GoodsImageStypeVO> collect = goodsImageVOS.stream().filter(v -> {
//                        if (v.getType() == 1) {
//                            return true;
//                        }
//                        return false;
//                    }).collect(Collectors.toList());
//                    List<GoodsImageStypeVO> collect1 = goodsImageVOS.stream().filter(v -> {
//                        if (v.getType() == 0) {
//                            return true;
//                        }
//                        return false;
//                    }).collect(Collectors.toList());
//                    if (CollectionUtils.isEmpty(collect)){
//                        //如何合成图片是空 删除图片类型表的合成图片  和删除关联关系表
//                        goodsImagestypeRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoodsId());
//                        relationGoodsImagesRepository.deleteByGoodsInfoId(goodsSaveRequest.getGoodsId());
//                        if (CollectionUtils.isNotEmpty(collect1)){
//                            //赋值id
//                            goodsSaveRequest.getGoodsImageVOS().forEach(v->{
//                                long l = YitIdHelper.nextId();
//                                v.setImagesTypeId(l);
//                            });
//                            //保存
//                            List<GoodsImageStype> convert = KsBeanUtil.convert(goodsSaveRequest.getGoodsImageVOS(), GoodsImageStype.class);
//                            goodsImagestypeRepository.saveAll(convert);
//                        }
//                    }
//
//                }
//
//                //判断促销图片是否为空
//            }
        });



    }
}
