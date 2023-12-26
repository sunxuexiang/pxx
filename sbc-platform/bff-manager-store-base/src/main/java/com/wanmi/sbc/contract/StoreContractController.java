package com.wanmi.sbc.contract;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContactRelationBatchSaveRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabQueryRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContactRelationBatchSaveResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.bean.dto.StoreGoodsCatPublishGoodsDTO;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandProvider;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.ContractCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.contract.ContractProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandTransferByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.cate.*;
import com.wanmi.sbc.goods.api.request.contract.ContractSaveRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.bean.vo.ContractBrandVO;
import com.wanmi.sbc.goods.bean.vo.ContractCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 签约信息(品牌，)
 * Created by sunkun on 2017/11/2.
 */
@Api(tags = "StoreContractController", description = "签约信息 API")
@RestController
@RequestMapping("/contract")
@Validated
public class StoreContractController {

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private ContractProvider contractProvider;

    @Resource
    private CommonUtil commonUtil;

    @Autowired
    private ContractCateQueryProvider contractCateQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private ContractBrandProvider contractBrandProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    /**
     * 签约信息新增修改删除(签约分类，签约品牌)
     *
     * @return
     */
    @ApiOperation(value = "签约信息新增修改删除(签约分类，签约品牌)")
    @RequestMapping(value = "/renewal", method = RequestMethod.POST)
    public BaseResponse renewalAll(@Valid @RequestBody ContractSaveRequest contractRequest) {
        contractRequest.setStoreId(commonUtil.getStoreId());
        List<Long> ids = contractProvider.save(contractRequest).getContext().getBrandIds();
        //取消签约品牌的时候更新es
        if (CollectionUtils.isNotEmpty(ids) && CollectionUtils.isNotEmpty(contractRequest.getDelBrandIds())) {
            esGoodsInfoElasticService.delBrandIds(ids,contractRequest.getStoreId());
            esRetailGoodsInfoElasticService.delBrandIds(ids,contractRequest.getStoreId());
        }
        if (null != contractRequest.getStoreId()){
            contractBrandProvider.transferByStoreId(ContractBrandTransferByStoreIdRequest.builder().storeId(contractRequest.getStoreId()).build());
        }
        operateLogMQUtil.convertAndSend("设置","店铺信息","编辑店铺信息");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 校验签约分类是否有关联商品
     *
     * @param cateId
     * @return
     */
    @ApiOperation(value = "校验签约分类是否有关联商品")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "cateId", value = "分类Id", required = true)
    @RequestMapping(value = "/cate/del/verify/{cateId}", method = RequestMethod.GET)
    public BaseResponse cateDelVerify(@PathVariable Long cateId) {
        Long storeId = commonUtil.getStoreId();
        ContractCateDelVerifyRequest request = new ContractCateDelVerifyRequest();
        request.setStoreId(storeId);
        request.setCateId(cateId);
        contractCateQueryProvider.cateDelVerify(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商家签约分类列表
     *
     * @return
     */
    @ApiOperation(value = "获取商家签约分类列表")
    @RequestMapping(value = "/cate/list", method = RequestMethod.GET)
    public BaseResponse<List<ContractCateVO>> cateList() {
        ContractCateListRequest contractCateQueryRequest = new ContractCateListRequest();
        contractCateQueryRequest.setStoreId(commonUtil.getStoreId());
        return BaseResponse.success(contractCateQueryProvider.list(contractCateQueryRequest).getContext().getContractCateList());
    }

    /**
     * 获取商家签约品牌列表
     *
     * @return
     */
    @ApiOperation(value = "获取商家签约品牌列表")
    @RequestMapping(value = "/brand/list", method = RequestMethod.GET)
    public BaseResponse<List<ContractBrandVO>> brandList() {
        ContractBrandListRequest contractBrandQueryRequest = new ContractBrandListRequest();
        contractBrandQueryRequest.setStoreId(commonUtil.getStoreId());
        return BaseResponse.success(contractBrandQueryProvider.list(contractBrandQueryRequest).getContext().getContractBrandVOList());
    }

    /**
     * 查询商家签约的平台类目列表，包含所有的父级类目
     *
     * @return 平台类目列表
     */
    @ApiOperation(value = "查询商家签约的平台类目列表，包含所有的父级类目")
    @RequestMapping(value = "/goods/cate/list", method = RequestMethod.GET)
    public BaseResponse<List<GoodsCateVO>> listCate() {
        ContractCateListCateByStoreIdRequest request = new ContractCateListCateByStoreIdRequest();
        request.setStoreId(commonUtil.getStoreId());
        return BaseResponse.success(contractCateQueryProvider.listCateByStoreId(request).getContext().getGoodsCateList());
    }


    /**
     * 查询商家签约的平台品牌列表
     *
     * @return 商家签约的平台品牌列表
     */
    @ApiOperation(value = "查询商家签约的平台品牌列表")
    @RequestMapping(value = "/goods/brand/list", method = RequestMethod.GET)
    public BaseResponse<List<GoodsBrandVO>> listBrand() {
        ContractBrandListRequest request = new ContractBrandListRequest();
        request.setStoreId(commonUtil.getStoreId());
        List<GoodsBrandVO> brands = contractBrandQueryProvider.list(request).getContext().getContractBrandVOList()
                .stream().map(ContractBrandVO::getGoodsBrand)
                .filter(Objects::nonNull).collect(Collectors.toList());
        return BaseResponse.success(brands);
    }

}
