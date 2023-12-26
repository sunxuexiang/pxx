package com.wanmi.sbc.goods.goodsBackups.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendBackupsVO;
import com.wanmi.sbc.goods.goodsBackups.repository.GoodsRecommendBackupsReponsitory;
import com.wanmi.sbc.goods.goodsBackups.request.GoodsBackupsQueryRequest;
import com.wanmi.sbc.goods.goodsBackups.root.GoodsRecommendBackups;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 商品推荐备份
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true, timeout = 10)
public class GoodsRecommendBackupsService {

    @Autowired
    private GoodsRecommendBackupsReponsitory goodsRecommendBackupsReponsitory;

    /**
     * 新增推荐备份
     *
     * @param unit 推荐备份信息
     * @throws SbcRuntimeException 业务异常
     */
    @Transactional
    public GoodsRecommendBackups add(GoodsRecommendBackups unit) throws SbcRuntimeException {
        goodsRecommendBackupsReponsitory.save(unit);
        return unit;
    }
    

    /**
     * 条件查询商品推荐备份
     *
     * @param request 参数
     * @return list
     */
    public List<GoodsRecommendBackups> query(GoodsBackupsQueryRequest request) {
        List<GoodsRecommendBackups> list;
        Sort sort = request.getSort();
        if (Objects.nonNull(sort)) {
            list = goodsRecommendBackupsReponsitory.findAll(request.getWhereCriteria(), sort);
        } else {
            list = goodsRecommendBackupsReponsitory.findAll(request.getWhereCriteria());
        }
        return ListUtils.emptyIfNull(list);
    }


    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public GoodsRecommendBackupsVO wrapperVo(GoodsRecommendBackups unit) {
        if (unit != null) {
            GoodsRecommendBackupsVO pointsGoodsVO = new GoodsRecommendBackupsVO();
            KsBeanUtil.copyPropertiesThird(unit, pointsGoodsVO);

            return pointsGoodsVO;
        }
        return null;
    }
    /**
     * 单个删除
     *
     * @author sgy
     */
    @Transactional
    public void deleteById(Long id) {
        goodsRecommendBackupsReponsitory.deleteById(id);
    }
    /**
     * 删除全部
     *
     * @author sgy
     */
    @Transactional
    public void deleteAll() {
        goodsRecommendBackupsReponsitory.deleteAll();
    }
}
