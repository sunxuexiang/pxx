package com.wanmi.sbc.goods.provider.impl.postionpicture;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsExcelProvider;
import com.wanmi.sbc.goods.api.provider.postionPicture.PostionPictureProvider;
import com.wanmi.sbc.goods.api.response.pointsgoods.PointsGoodsExcelResponse;
import com.wanmi.sbc.goods.bean.vo.PositionPictureVO;
import com.wanmi.sbc.goods.pointsgoods.service.PointsGoodsExcelService;
import com.wanmi.sbc.goods.positionpicture.model.root.PositionPicture;
import com.wanmi.sbc.goods.positionpicture.repository.PostionPictureRepository;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@Validated
@RestController
public class PostionPictureController implements PostionPictureProvider {

    @Autowired
    private PostionPictureRepository postionPictureRepository;

    @Autowired
    private WareHouseRepository wareHouseRepository;


    /**
     * type:0是批发1是散批2是零售
     * allTyep: 0是全查 1是加仓库id查 默认是全查
     * wareId :仓库id
     * @param param
     * @return
     */
    @Override
    public BaseResponse<List<PositionPictureVO>> getImages(Map<String, Object> param) {
        Object o = param.get("allTyep");
        Object type = param.get("type");
        Object wareId = param.get("wareId");
        Integer integerType = null;
        try {
            integerType = Integer.valueOf(type.toString());
        }
        catch (Exception e) {
        }
        Object storeIdObj = param.get("storeId");
        Long storeId = null;
        try {
            if (storeIdObj != null) {
                storeId = Long.parseLong(storeIdObj.toString());
            }
        }
        catch (Exception e) {
        }

        List<PositionPicture> pictureList = new ArrayList<>();
        // 如果指定类型查询：0是批发1是散批2是零售
        if (Objects.isNull(o) || Integer.valueOf(o.toString()).compareTo(0)==0){
            if (storeId == null) {
                pictureList = postionPictureRepository.getAllImage(integerType);
            }
            else {
                // 按店铺、以及类型查询
                pictureList = postionPictureRepository.getImageByStoreAndType(storeId, integerType);
            }
        }
        // 按店铺查询用户须知
        else if (storeId != null) {
            pictureList = postionPictureRepository.getImageByStore(storeId);
        }
        else {
            if (wareId != null) {
                Long aLong = Long.valueOf(wareId.toString());
                pictureList = postionPictureRepository.getImageByWareId(aLong, integerType);
            }
        }
        List<PositionPictureVO> convert = KsBeanUtil.convert(pictureList, PositionPictureVO.class);
        convert.forEach(v->{
            if (v.getWareId() != null && v.getWareId() > 0) {
                WareHouse one = wareHouseRepository.getOne(v.getWareId());
                if (one != null) {
                    v.setWareName(one.getWareName());
                }
            }
        });
        return BaseResponse.success(convert);
    }

    /**
     * 插入和修改
     * @param positionPictureVO
     * @return
     */
    @Override
    public BaseResponse addAndFlush(PositionPictureVO positionPictureVO) {
        PositionPicture convert = KsBeanUtil.convert(positionPictureVO, PositionPicture.class);
        if (Objects.isNull(convert.getDelFlag())){
            convert.setDelFlag(0);
        }
        postionPictureRepository.saveAndFlush(convert);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse add(PositionPictureVO positionPictureVO) {
        PositionPicture convert = KsBeanUtil.convert(positionPictureVO, PositionPicture.class);
        if (Objects.isNull(convert.getDelFlag())){
            convert.setDelFlag(0);
        }
        /**   2023-07-01 注释仓库ID重复检查，用户须知废弃仓库ID字段，新增店铺ID，以店铺来判断重复
        if (Objects.isNull(convert.getPositionId())){
            //新增
            List<PositionPicture> imageByWareId = postionPictureRepository.getImageByWareId(convert.getWareId(),convert.getType());
            if (CollectionUtils.isNotEmpty(imageByWareId)){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "仓库已经存在");
            }
        }else {
            //修改
            List<PositionPicture> imageByWareId = postionPictureRepository.getImageByWareId(convert.getWareId(),convert.getType());
            if (CollectionUtils.isNotEmpty(imageByWareId) && imageByWareId.get(0).getPositionId()!=convert.getPositionId()){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "仓库已经存在");
            }
        }*/

        // 如果没有主键，为新增用户须知
        if (Objects.isNull(convert.getPositionId())) {
            // 新增用户须知，检查门店不能重复添加用户须知
            Integer count = postionPictureRepository.countByStore(convert.getStoreId());
            if (count != null && count > 0) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "已设置用户须知，请勿重新添加");
            }
        }
        else {
            // 查询数据库中店铺添加的用户须知（一个店铺只能添加唯一一条用户须知）
            PositionPicture dbPicture = postionPictureRepository.getOneByStore(convert.getStoreId());
            // 如果数据中店铺须知的主键，不等于修改参数的主键，表示用户修改属于自己店铺的用户须知，不允许修改
            if (dbPicture != null && !dbPicture.getPositionId().equals(convert.getPositionId())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "没有修改权限");
            }
        }

        postionPictureRepository.saveAndFlush(convert);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<PositionPictureVO> getImage(Long positionId) {
        Optional<PositionPicture> byId = postionPictureRepository.findById(positionId);
        if (byId.isPresent()){
            if (byId.get().getDelFlag()==0){
                return BaseResponse.success(KsBeanUtil.convert(byId.get(),PositionPictureVO.class));
            }
            return BaseResponse.success(null);
        }
        return BaseResponse.success(null);
    }
}
