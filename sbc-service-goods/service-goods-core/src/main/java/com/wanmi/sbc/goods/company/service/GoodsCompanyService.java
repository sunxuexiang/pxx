package com.wanmi.sbc.goods.company.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.GoodsBrandErrorCode;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.company.model.root.GoodsCompany;
import com.wanmi.sbc.goods.company.repository.GoodsCompanyReponsitory;
import com.wanmi.sbc.goods.company.request.GoodsCompanyQueryRequest;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 商品厂商服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class GoodsCompanyService {

    @Autowired
    private GoodsCompanyReponsitory goodsCompanyRepository;
    /**
     * 条件查询商品厂商
     *
     * @param request 参数
     * @return list
     */
    public Page<GoodsCompany> page(GoodsCompanyQueryRequest request) {
        return goodsCompanyRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    /**
     * 条件查询商品厂商组件
     *
     * @param companyId 厂商ID
     * @return list
     */
    public GoodsCompany findById(Long companyId) {
        return goodsCompanyRepository.findById(companyId).orElse(null);
    }

    /**
     * 新增厂商
     *
     * @param goodsCompany 厂商信息
     * @throws SbcRuntimeException 业务异常
     */
    @Transactional
    public GoodsCompany add(GoodsCompany goodsCompany) throws SbcRuntimeException {
        GoodsCompanyQueryRequest request = new GoodsCompanyQueryRequest();
        request.setDelFlag(DeleteFlag.NO.toValue());
        //限制重复名称
        request.setCompanyName(goodsCompany.getCompanyName());
        if (goodsCompanyRepository.count(request.getWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsBrandErrorCode.NAME_ALREADY_EXISTS);
        }
        goodsCompany.setStatus(1);
        goodsCompany.setDelFlag(DeleteFlag.NO);
        goodsCompany.setCreateTime(LocalDateTime.now());
        goodsCompany.setUpdateTime(LocalDateTime.now());
        goodsCompanyRepository.save(goodsCompany);
        return goodsCompany;
    }

    @Transactional
    public GoodsCompany edit(GoodsCompany goodsCompany) throws SbcRuntimeException {
        GoodsCompany oldGoodsCompany = goodsCompanyRepository.findById(goodsCompany.getCompanyId()).orElse(null);
        //更新品牌
        goodsCompany.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(goodsCompany, oldGoodsCompany);
        goodsCompanyRepository.save(oldGoodsCompany);
        return oldGoodsCompany;
    }

    @Transactional
    public GoodsCompany set(GoodsCompany goodsCompany) throws SbcRuntimeException {
        GoodsCompany oldGoodsCompany = goodsCompanyRepository.findById(goodsCompany.getCompanyId()).orElse(null);
        if (oldGoodsCompany == null || oldGoodsCompany.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsBrandErrorCode.NOT_EXIST);
        }
        //更新品牌
        goodsCompany.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(goodsCompany, oldGoodsCompany);
        goodsCompanyRepository.save(oldGoodsCompany);
        return oldGoodsCompany;
    }


    /**
     * 条件查询商品品牌
     *
     * @param request 参数
     * @return list
     */
    public List<GoodsCompany> query(GoodsCompanyQueryRequest request) {
        List<GoodsCompany> list;
        Sort sort = request.getSort();
        if (Objects.nonNull(sort)) {
            list = goodsCompanyRepository.findAll(request.getWhereCriteria(), sort);
        } else {
            list = goodsCompanyRepository.findAll(request.getWhereCriteria());
        }
        return ListUtils.emptyIfNull(list);
    }
}
