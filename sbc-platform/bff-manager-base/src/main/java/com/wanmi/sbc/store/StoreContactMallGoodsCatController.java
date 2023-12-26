package com.wanmi.sbc.store;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabQueryRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.bean.dto.StoreGoodsCatPublishGoodsDTO;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-18 17:54
 **/
@RestController
public class StoreContactMallGoodsCatController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;


    @RequestMapping(value = "/contract/goods/cate/listContractGoodsByMallTab", method = RequestMethod.POST)
    public BaseResponse<List<GoodsCateVO>> listContractGoodsByMallTab(@RequestBody StoreGoodsCatPublishGoodsDTO goodsDTO) {
        final String goodsId = goodsDTO.getGoodsId();
        final Long storeId = null == goodsDTO.getStoreId() ? commonUtil.getStoreId() : goodsDTO.getStoreId();
        if (null == storeId) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "店铺异常");
        }
        Long catId;
        if (StringUtils.isNotBlank(goodsId)) {
            final GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(goodsId);
            final BaseResponse<GoodsByIdResponse> goodsInfo = goodsQueryProvider.getById(goodsByIdRequest);
            catId = goodsInfo.getContext().getCateId();
        } else {
            catId = null;
        }
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageNum(0);
        relationPageRequest.setPageSize(1);
        relationPageRequest.setRelationType(MallContractRelationType.TAB.getValue());
        relationPageRequest.setStoreId(storeId);
        relationPageRequest.setDeleteFlag(DeleteFlag.NO);
        final BaseResponse<CompanyMallContractRelationPageResponse> contractRelation = companyIntoPlatformQueryProvider.pageContractRelation(relationPageRequest);
        final CompanyMallContractRelationVO companyMallContractRelationVO = contractRelation.getContext().getPage().getContent().get(0);
        final CompanyMallSupplierTabQueryRequest queryRequest = new CompanyMallSupplierTabQueryRequest();
        queryRequest.setId(Long.valueOf(companyMallContractRelationVO.getRelationValue()));
        final BaseResponse<CompanyMallSupplierTabResponse> byIdSupplierTab = companyIntoPlatformQueryProvider.getByIdSupplierTab(queryRequest);
        final CompanyMallSupplierTabResponse supplierTabContext = byIdSupplierTab.getContext();
        if (null == supplierTabContext || supplierTabContext.getRelCatId() == null) {
            return BaseResponse.success(new ArrayList<>());
        }
        GoodsCateListByConditionRequest request = new GoodsCateListByConditionRequest();
        request.setCateParentId(supplierTabContext.getRelCatId());
        request.setDelFlag(0);
        List<GoodsCateVO> goodsCateList = goodsCateQueryProvider.listByCondition(request).getContext().getGoodsCateVOList();
        if (null != catId) {
            final boolean addCatFlag = goodsCateList.stream().noneMatch(f -> Objects.equals(catId, f.getCateId()));
            if (addCatFlag) {
                final GoodsCateVO goodsCateVO = new GoodsCateVO();
                goodsCateVO.setCateId(catId);
                goodsCateVO.setCateName("待分类");
                goodsCateList.add(goodsCateVO);
            }
        }
        return BaseResponse.success(goodsCateList);
    }

}
