package com.wanmi.sbc.goods.standard.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.StandardGoodsErrorCode;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.model.root.StandardSku;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRepository;
import com.wanmi.sbc.goods.standard.repository.StandardSkuRepository;
import com.wanmi.sbc.goods.standard.request.StandardSkuSaveRequest;
import com.wanmi.sbc.goods.standard.response.StandardSkuEditResponse;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSkuSpecDetailRel;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSpecDetail;
import com.wanmi.sbc.goods.standardspec.repository.StandardSkuSpecDetailRelRepository;
import com.wanmi.sbc.goods.standardspec.repository.StandardSpecDetailRepository;
import com.wanmi.sbc.goods.standardspec.repository.StandardSpecRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
public class StandardSkuService {

    @Autowired
    private StandardGoodsRepository standardGoodsRepository;

    @Autowired
    private StandardSkuRepository standardInfoRepository;

    @Autowired
    private StandardSpecRepository standardSpecRepository;

    @Autowired
    private StandardSpecDetailRepository standardSpecDetailRepository;

    @Autowired
    private StandardSkuSpecDetailRelRepository standardInfoSpecDetailRelRepository;

    /**
     * 根据ID查询商品SKU
     *
     * @param standardInfoId 商品SKU编号
     * @return list
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public StandardSkuEditResponse findById(String standardInfoId) throws SbcRuntimeException {
        StandardSkuEditResponse response = new StandardSkuEditResponse();
        StandardSku standardSku = standardInfoRepository.findById(standardInfoId).orElse(null);
        if (standardSku == null || DeleteFlag.YES.toValue() == standardSku.getDelFlag().toValue()) {
            throw new SbcRuntimeException(StandardGoodsErrorCode.NOT_EXIST);
        }
        StandardGoods standard = standardGoodsRepository.findById(standardSku.getGoodsId()).orElse(null);
        if (standard == null) {
            throw new SbcRuntimeException(StandardGoodsErrorCode.NOT_EXIST);
        }

        //如果是多规格
        if (Constants.yes.equals(standard.getMoreSpecFlag())) {
            response.setGoodsSpecs(standardSpecRepository.findByGoodsId(standard.getGoodsId()));
            response.setGoodsSpecDetails(standardSpecDetailRepository.findByGoodsId(standard.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().stream().forEach(standardSpec -> {
                standardSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(standardSpec.getSpecId())).map(StandardSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            List<StandardSkuSpecDetailRel> standardInfoSpecDetailRels = standardInfoSpecDetailRelRepository.findByGoodsId(standard.getGoodsId());
            standardSku.setMockSpecIds(standardInfoSpecDetailRels.stream().filter(detailRel -> detailRel.getGoodsInfoId().equals(standardSku.getGoodsInfoId())).map(StandardSkuSpecDetailRel::getSpecId).collect(Collectors.toList()));
            standardSku.setMockSpecDetailIds(standardInfoSpecDetailRels.stream().filter(detailRel -> detailRel.getGoodsInfoId().equals(standardSku.getGoodsInfoId())).map(StandardSkuSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            standardSku.setSpecText(StringUtils.join(standardInfoSpecDetailRels.stream().filter(specDetailRel -> standardSku.getGoodsInfoId().equals(specDetailRel.getGoodsInfoId())).map(StandardSkuSpecDetailRel::getDetailName).collect(Collectors.toList()), " "));
        }

        response.setGoodsInfo(standardSku);
        response.setGoods(standard);
        return response;
    }

    /**
     * 商品SKU更新
     *
     * @param saveRequest 参数
     * @throws SbcRuntimeException 业务异常
     */
    @Transactional
    public StandardSku edit(StandardSkuSaveRequest saveRequest) throws SbcRuntimeException {
        StandardSku newStandardSku = saveRequest.getGoodsInfo();
        StandardSku oldStandardSku = standardInfoRepository.findById(newStandardSku.getGoodsInfoId()).orElse(null);
        if (oldStandardSku == null || oldStandardSku.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(StandardGoodsErrorCode.NOT_EXIST);
        }
        newStandardSku.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(newStandardSku, oldStandardSku);
        standardInfoRepository.save(oldStandardSku);
        return oldStandardSku;
    }

    /**
     * 获取详情
     * @param skuId 商品库sku编号
     * @return 商品库信息
     */
    public StandardSku findOne(String skuId){
        return this.standardInfoRepository.findById(skuId).orElse(null);
    }

    /**
     * 批量查询
     * @param skuIds 编号
     * @return 商品库列表
     */
    public List<StandardSku> findAll(List<String> skuIds){
        return this.standardInfoRepository.findAllById(skuIds);
    }

    public StandardSku findByErpNo(String erpNo){
        return standardInfoRepository.findFirstByErpGoodsInfoNo(erpNo);
    }


    public Map<String,String> findListByErpNo(List<String> erpIds){
        List<StandardSku> byErpGoodsInfoNo = standardInfoRepository.findByErpGoodsInfoNo(erpIds);
        Map<String,String> result=new HashMap<>(500);
        byErpGoodsInfoNo.forEach(param->{
            result.put(param.getGoodsId(),param.getErpGoodsInfoNo());
        });
        return  result;
    };
}
