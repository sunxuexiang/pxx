package com.wanmi.sbc.goods.activitygoodspicture.service;

import com.wanmi.sbc.goods.activitygoodspicture.model.root.ActivityGoodsPicture;
import com.wanmi.sbc.goods.activitygoodspicture.repository.ActivityGoodsPictureRepository;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureGetRequest;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ActivityGoodsPictureService {

    @Autowired
    private ActivityGoodsPictureRepository activityGoodsPictureRepository;

    public void addOrUpdateActivityGoodsPicture(ActivityGoodsPictureRequest request) {
        List<ActivityGoodsPicture> byGoodsInfoIdAnGoodsId = activityGoodsPictureRepository.findAllByGoodsInfoIdAndGoodsId(request.getGoodsInfoId(), request.getGoodsId());
        if (CollectionUtils.isNotEmpty(byGoodsInfoIdAnGoodsId)) {
            ActivityGoodsPicture activityGoodsPicture = byGoodsInfoIdAnGoodsId.get(0);
            activityGoodsPicture.setUpdateTime(LocalDateTime.now());
            activityGoodsPicture.setImgPath(request.getImgPath());
            activityGoodsPicture.setGoodsInfoId(request.getGoodsInfoId());
            activityGoodsPicture.setGoodsId(request.getGoodsId());
            activityGoodsPicture.setId(activityGoodsPicture.getId());
            activityGoodsPictureRepository.save(activityGoodsPicture);
        } else {
            ActivityGoodsPicture activityGoodsPicture = new ActivityGoodsPicture();
            activityGoodsPicture.setCreateTime(LocalDateTime.now());
            activityGoodsPicture.setGoodsId(request.getGoodsId());
            activityGoodsPicture.setGoodsInfoId(request.getGoodsInfoId());
            activityGoodsPicture.setImgPath(request.getImgPath());
            activityGoodsPicture.setUpdateTime(LocalDateTime.now());
            activityGoodsPictureRepository.save(activityGoodsPicture);
        }
    }

    /**
     * 根据goodsInfoid批量获取
     */
    public List<ActivityGoodsPicture> getAllByGoodsInfoIds(ActivityGoodsPictureGetRequest request) {
        if (CollectionUtils.isNotEmpty(request.getGoodsInfoIds())) {
            return activityGoodsPictureRepository.findByGoodsInfoIdIn(request.getGoodsInfoIds());
        } else if (CollectionUtils.isNotEmpty(request.getGoodsInfoIds())) {
            return activityGoodsPictureRepository.findByGoodsIdIn(request.getGoodsIds());
        } else {
            return null;
        }
    }
}
