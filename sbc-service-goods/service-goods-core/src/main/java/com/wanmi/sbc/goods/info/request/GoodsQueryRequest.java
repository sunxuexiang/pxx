package com.wanmi.sbc.goods.info.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsSelectStatus;
import com.wanmi.sbc.goods.info.model.root.BulkGoods;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import com.wanmi.sbc.goods.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商品查询请求
 * Created by daiyitian on 2017/3/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsQueryRequest extends BaseQueryRequest {

    /**
     * 批量SPU编号
     */
    private List<String> goodsIds;

    /**
     * 精准条件-SPU编码
     */
    private String goodsNo;

    /**
     * 精准条件-批量SPU编码
     */
    private List<String> goodsNos;

    /**
     * 模糊条件-SPU编码
     */
    private String likeGoodsNo;

    /**
     * 模糊条件-SKU编码
     */
    private String likeGoodsInfoNo;

    /**
     * 模糊条件-商品名称
     */
    private String likeGoodsName;

    /**
     * 模糊条件-供应商名称
     */
    private String likeProviderName;

    /**
     * 模糊条件-关键词（商品名称、SPU编码）
     */
    private String keyword;

    /**
     * 商品分类
     */
    private Long cateId;

    /**
     * 批量商品分类
     */
    private List<Long> cateIds;

    /**
     * 品牌编号
     */
    private Long brandId;

    /**
     * 批量品牌编号
     */
    private List<Long> brandIds;

    /**
     * 上下架状态
     */
    private Integer addedFlag;

    /**
     * 上下架状态-批量
     */
    private List<Integer> addedFlags;

    /**
     * 删除标记
     */
    private Integer delFlag;

    /**
     * 客户编号
     */
    private String customerId;

    /**
     * 公司信息ID
     */
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺ID
     */
    private List<Long> storeIds;
    /**
     * 非GoodsId
     */
    private String notGoodsId;

    /**
     * 商家名称
     */
    private String likeSupplierName;

    /**
     * 审核状态
     */
    private CheckStatus auditStatus;

    /**
     * 批量审核状态
     */
    private List<CheckStatus> auditStatusList;

    /**
     * 店铺分类Id
     */
    private Long storeCateId;

    /**
     * 店铺分类所关联的SpuIds
     */
    private List<String> storeCateGoodsIds;

    /**
     * 运费模板ID
     */
    private Long freightTempId;

    /**
     * 商品状态筛选
     */
    private List<GoodsSelectStatus> goodsSelectStatuses;

    /**
     * 销售类别
     */
    private Integer saleType;

    /**
     * 商品类型，0：实体商品，1：虚拟商品
     */
    private Integer goodsType;

    /**
     * 商品类型，0：实体商品，1：虚拟商品
     */
    private Integer goodsSource;

    /**
     * 批量供应商商品id
     */
    private List<String> providerGoodsIds;

    /**
     * 模糊条件-erp编码
     */
    @ApiModelProperty(value = "模糊条件-erp编码")
    private String likeErpNo;

    /**
     * 是否为特价商品
     */
    @ApiModelProperty(value = "是否为特价商品 ： 0否   1是")
    private Integer goodsInfoType;

    /**
     * 商品排序查询 1已排序 0未排序
     */
    private Integer goodsSeqFlag;
    
    /**
     * 店铺内商品排序查询 1已排序 0未排序
     */
    @ApiModelProperty(value = "店铺内商品排序查询 1已排序 0未排序")
    private Integer storeGoodsSeqFlag;

    //喜丫丫迭代三期商城端商品列表新增查询条件
    /**
     * 特价范围参数1
     */
    private BigDecimal specialPriceFirst;

    /**
     * 特价范围参数2
     */
    private BigDecimal specialPriceLast;

    /**
     * 批次号
     */
    private String goodsInfoBatchNo;

    /**
     * 是否为囤货 1：囤货中 ，2：已囤完
     */
    private Long stockUp;


    /**
     * 创建时间
     */

    private String  create_timeStart;


    /**
     * 创建时间
     */

    private String  create_timeEnd;



    /**
     * 上架状态
     */
    private Integer added_flag;


    /**
     * 商品规格 0为全部 1为单规格  2为多规格
     */
    private Integer manySpecs;

    /**
     * 仓库ID
     */
    private Long wareId;

    /**
     * 商家类型
     */
    private CompanyType companyType;

    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<Goods> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //批量商品编号
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                predicates.add(root.get("goodsId").in(goodsIds));
            }

            if (Objects.nonNull(wareId) && wareId > 0l) {
                predicates.add(cbuild.equal(root.get("wareId"), wareId));
            }

            //批量店铺分类关联商品编号
            if (CollectionUtils.isNotEmpty(storeCateGoodsIds)) {
                predicates.add(root.get("goodsId").in(storeCateGoodsIds));
            }
            //查询SPU编码
            if (StringUtils.isNotBlank(goodsNo)) {
                predicates.add(cbuild.equal(root.get("goodsNo"), goodsNo));
            }
            //批量查询SPU编码
            if (CollectionUtils.isNotEmpty(goodsNos)) {
                predicates.add(root.get("goodsNo").in(goodsNos));
            }
            //查询品牌编号
            if (brandId != null && brandId > 0) {
                predicates.add(cbuild.equal(root.get("brandId"), brandId));
            }
            //查询分类编号
            if (cateId != null && cateId > 0) {
                predicates.add(cbuild.equal(root.get("cateId"), cateId));
            }
            //批量查询分类编号
            if (CollectionUtils.isNotEmpty(cateIds)) {
                predicates.add(root.get("cateId").in(cateIds));
            }
            //批量查询分类编号
            if (CollectionUtils.isNotEmpty(brandIds)) {
                predicates.add(root.get("brandId").in(brandIds));
            }
            //公司信息ID
            if (companyInfoId != null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            //店铺ID
            if (storeId != null) {
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            //批量店铺ID
            if(CollectionUtils.isNotEmpty(storeIds)){
                predicates.add(root.get("storeId").in(storeIds));
            }
            //模糊查询SPU编码
            if (StringUtils.isNotEmpty(likeGoodsNo)) {
                predicates.add(cbuild.like(root.get("goodsNo"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //模糊查询名称
            if (StringUtils.isNotEmpty(likeGoodsName)) {
                predicates.add(cbuild.like(root.get("goodsName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询商家名称
            if (StringUtils.isNotBlank(likeSupplierName)) {
                predicates.add(cbuild.like(root.get("supplierName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeSupplierName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询供应商名称
            if (StringUtils.isNotBlank(likeProviderName)) {
                predicates.add(cbuild.like(root.get("providerName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeProviderName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //批量供应商商品编号
            if (CollectionUtils.isNotEmpty(providerGoodsIds)) {
                predicates.add(root.get("providerGoodsId").in(providerGoodsIds));
            }

            //关键词搜索
            if (StringUtils.isNotBlank(keyword)) {
                String str = StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(keyword.trim())).concat(StringUtil.SQL_LIKE_CHAR);
                predicates.add(cbuild.or(cbuild.like(root.get("goodsName"), str), cbuild.like(root.get("goodsNo"), str)));
            }

            //审核状态
            if (auditStatus != null) {
                predicates.add(cbuild.equal(root.get("auditStatus"), auditStatus));
            }

            //上下架状态
            if (addedFlag != null) {
                predicates.add(cbuild.equal(root.get("addedFlag"), addedFlag));
            }

            //多个上下架状态
            if (CollectionUtils.isNotEmpty(addedFlags)) {
                predicates.add(root.get("addedFlag").in(addedFlags));
            }

            //商家类型
            if(companyType != null){
                predicates.add(cbuild.equal(root.get("companyType"), companyType.toValue()));
            }

            /**
             * 批量审核状态
             */
            if (CollectionUtils.isNotEmpty(auditStatusList)) {
                predicates.add(root.get("auditStatus").in(auditStatusList));
            }

            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //非商品编号
            if (StringUtils.isNotBlank(notGoodsId)) {
                predicates.add(cbuild.notEqual(root.get("goodsId"), notGoodsId));
            }

            //销售类型
            if (!ObjectUtils.isEmpty(saleType)) {
                predicates.add(cbuild.equal(root.get("saleType"), saleType));
            }

            //商品类型
            if (!ObjectUtils.isEmpty(goodsType)) {
                predicates.add(cbuild.equal(root.get("goodsType"), goodsType));
            }

            //商品来源
            if (!ObjectUtils.isEmpty(goodsSource)) {
                predicates.add(cbuild.equal(root.get("goodsSource"), goodsSource));
            }

            //运费模板ID
            if (freightTempId != null) {
                predicates.add(cbuild.equal(root.get("freightTempId"), freightTempId));
            }
            //是否筛选特价商品
            if (goodsInfoType != null) {
                if (goodsInfoType == 1) {
                    predicates.add(cbuild.equal(root.get("goodsType"), 2));
                } else {
                    predicates.add(cbuild.or(cbuild.notEqual(root.get("goodsType"), 2), cbuild.isNull(root.get("goodsType"))));
                }
            }
            //特价范围参数1
            if (specialPriceFirst != null) {
                predicates.add(cbuild.ge(root.get("marketPrice"), specialPriceFirst));
            }
            //特价范围参数2
            if (specialPriceLast != null) {
                predicates.add(cbuild.le(root.get("marketPrice"), specialPriceLast));
            }
            //查询商品是否排序
            if (goodsSeqFlag != null) {
                if (goodsSeqFlag == 1) {
                    predicates.add(cbuild.isNotNull(root.get("goodsSeqNum")));
                } else {
                    predicates.add(cbuild.isNull(root.get("goodsSeqNum")));
                }
            }
            
			// 查询商品是否店铺内排序
            if (storeGoodsSeqFlag != null) {
    			if (storeGoodsSeqFlag == 1) {
    				predicates.add(cbuild.isNotNull(root.get("storeGoodsSeqNum")));
    			} else {
    				predicates.add(cbuild.isNull(root.get("storeGoodsSeqNum")));
    			}
            }
            
            //商品状态筛选
            if (CollectionUtils.isNotEmpty(goodsSelectStatuses)) {
                List<Predicate> orPredicate = new ArrayList<>();
                goodsSelectStatuses.forEach(goodsInfoSelectStatus -> {
                    if (goodsInfoSelectStatus != null) {
                        if (goodsInfoSelectStatus == GoodsSelectStatus.ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.YES.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.NOT_ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.NO.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.PART_ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.PART.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.OTHER) {
                            orPredicate.add(root.get("auditStatus").in(CheckStatus.FORBADE, CheckStatus.NOT_PASS, CheckStatus.WAIT_CHECK));
                        }
                    }
                });
                predicates.add(cbuild.or(orPredicate.toArray(new Predicate[orPredicate.size()])));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    /**
     * 封装公共条件 零售
     *
     * @return
     */
    public Specification<RetailGoods> getRetailWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //批量商品编号
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                predicates.add(root.get("goodsId").in(goodsIds));
            }
            //批量店铺分类关联商品编号
            if (CollectionUtils.isNotEmpty(storeCateGoodsIds)) {
                predicates.add(root.get("goodsId").in(storeCateGoodsIds));
            }
            //查询SPU编码
            if (StringUtils.isNotBlank(goodsNo)) {
                predicates.add(cbuild.equal(root.get("goodsNo"), goodsNo));
            }
            //批量查询SPU编码
            if (CollectionUtils.isNotEmpty(goodsNos)) {
                predicates.add(root.get("goodsNo").in(goodsNos));
            }
            //查询品牌编号
            if (brandId != null && brandId > 0) {
                predicates.add(cbuild.equal(root.get("brandId"), brandId));
            }
            //查询分类编号
            if (cateId != null && cateId > 0) {
                predicates.add(cbuild.equal(root.get("cateId"), cateId));
            }
            //批量查询分类编号
            if (CollectionUtils.isNotEmpty(cateIds)) {
                predicates.add(root.get("cateId").in(cateIds));
            }
            //批量查询分类编号
            if (CollectionUtils.isNotEmpty(brandIds)) {
                predicates.add(root.get("brandId").in(brandIds));
            }
            //公司信息ID
            if (companyInfoId != null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            //店铺ID
            if (storeId != null) {
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            //批量店铺ID
            if(CollectionUtils.isNotEmpty(storeIds)){
                predicates.add(root.get("storeId").in(storeIds));
            }
            //模糊查询SPU编码
            if (StringUtils.isNotEmpty(likeGoodsNo)) {
                predicates.add(cbuild.like(root.get("goodsNo"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //模糊查询名称
            if (StringUtils.isNotEmpty(likeGoodsName)) {
                predicates.add(cbuild.like(root.get("goodsName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询商家名称
            if (StringUtils.isNotBlank(likeSupplierName)) {
                predicates.add(cbuild.like(root.get("supplierName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeSupplierName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询供应商名称
            if (StringUtils.isNotBlank(likeProviderName)) {
                predicates.add(cbuild.like(root.get("providerName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeProviderName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //批量供应商商品编号
            if (CollectionUtils.isNotEmpty(providerGoodsIds)) {
                predicates.add(root.get("providerGoodsId").in(providerGoodsIds));
            }

            //关键词搜索
            if (StringUtils.isNotBlank(keyword)) {
                String str = StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(keyword.trim())).concat(StringUtil.SQL_LIKE_CHAR);
                predicates.add(cbuild.or(cbuild.like(root.get("goodsName"), str), cbuild.like(root.get("goodsNo"), str)));
            }

            //审核状态
            if (auditStatus != null) {
                predicates.add(cbuild.equal(root.get("auditStatus"), auditStatus));
            }

            //上下架状态
            if (addedFlag != null) {
                predicates.add(cbuild.equal(root.get("addedFlag"), addedFlag));
            }

            //多个上下架状态
            if (CollectionUtils.isNotEmpty(addedFlags)) {
                predicates.add(root.get("addedFlag").in(addedFlags));
            }

            /**
             * 批量审核状态
             */
            if (CollectionUtils.isNotEmpty(auditStatusList)) {
                predicates.add(root.get("auditStatus").in(auditStatusList));
            }

            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //非商品编号
            if (StringUtils.isNotBlank(notGoodsId)) {
                predicates.add(cbuild.notEqual(root.get("goodsId"), notGoodsId));
            }

            //销售类型
            if (!ObjectUtils.isEmpty(saleType)) {
                predicates.add(cbuild.equal(root.get("saleType"), saleType));
            }

            //商品类型
            if (!ObjectUtils.isEmpty(goodsType)) {
                predicates.add(cbuild.equal(root.get("goodsType"), goodsType));
            }

            //商品来源
            if (!ObjectUtils.isEmpty(goodsSource)) {
                predicates.add(cbuild.equal(root.get("goodsSource"), goodsSource));
            }

            //运费模板ID
            if (freightTempId != null) {
                predicates.add(cbuild.equal(root.get("freightTempId"), freightTempId));
            }
            //是否筛选特价商品
            if (goodsInfoType != null) {
                if (goodsInfoType == 1) {
                    predicates.add(cbuild.equal(root.get("goodsType"), 2));
                } else {
                    predicates.add(cbuild.or(cbuild.notEqual(root.get("goodsType"), 2), cbuild.isNull(root.get("goodsType"))));
                }
            }
            //特价范围参数1
            if (specialPriceFirst != null) {
                predicates.add(cbuild.ge(root.get("marketPrice"), specialPriceFirst));
            }
            //特价范围参数2
            if (specialPriceLast != null) {
                predicates.add(cbuild.le(root.get("marketPrice"), specialPriceLast));
            }
            //查询商品是否排序
            if (goodsSeqFlag != null) {
                if (goodsSeqFlag == 1) {
                    predicates.add(cbuild.isNotNull(root.get("goodsSeqNum")));
                } else {
                    predicates.add(cbuild.isNull(root.get("goodsSeqNum")));
                }
            }
            //商品状态筛选
            if (CollectionUtils.isNotEmpty(goodsSelectStatuses)) {
                List<Predicate> orPredicate = new ArrayList<>();
                goodsSelectStatuses.forEach(goodsInfoSelectStatus -> {
                    if (goodsInfoSelectStatus != null) {
                        if (goodsInfoSelectStatus == GoodsSelectStatus.ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.YES.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.NOT_ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.NO.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.PART_ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.PART.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.OTHER) {
                            orPredicate.add(root.get("auditStatus").in(CheckStatus.FORBADE, CheckStatus.NOT_PASS, CheckStatus.WAIT_CHECK));
                        }
                    }
                });
                predicates.add(cbuild.or(orPredicate.toArray(new Predicate[orPredicate.size()])));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    /**
     * 封装公共条件 零售
     *
     * @return
     */
    public Specification<BulkGoods> getBulkWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(wareId) && wareId > 0l) {
                predicates.add(cbuild.equal(root.get("wareId"), wareId));
            }

            //批量商品编号
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                predicates.add(root.get("goodsId").in(goodsIds));
            }
            //批量店铺分类关联商品编号
            if (CollectionUtils.isNotEmpty(storeCateGoodsIds)) {
                predicates.add(root.get("goodsId").in(storeCateGoodsIds));
            }
            //查询SPU编码
            if (StringUtils.isNotBlank(goodsNo)) {
                predicates.add(cbuild.equal(root.get("goodsNo"), goodsNo));
            }
            //批量查询SPU编码
            if (CollectionUtils.isNotEmpty(goodsNos)) {
                predicates.add(root.get("goodsNo").in(goodsNos));
            }
            //查询品牌编号
            if (brandId != null && brandId > 0) {
                predicates.add(cbuild.equal(root.get("brandId"), brandId));
            }
            //查询分类编号
            if (cateId != null && cateId > 0) {
                predicates.add(cbuild.equal(root.get("cateId"), cateId));
            }
            //批量查询分类编号
            if (CollectionUtils.isNotEmpty(cateIds)) {
                predicates.add(root.get("cateId").in(cateIds));
            }
            //批量查询分类编号
            if (CollectionUtils.isNotEmpty(brandIds)) {
                predicates.add(root.get("brandId").in(brandIds));
            }
            //公司信息ID
            if (companyInfoId != null) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            //店铺ID
            if (storeId != null) {
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            //批量店铺ID
            if(CollectionUtils.isNotEmpty(storeIds)){
                predicates.add(root.get("storeId").in(storeIds));
            }
            //模糊查询SPU编码
            if (StringUtils.isNotEmpty(likeGoodsNo)) {
                predicates.add(cbuild.like(root.get("goodsNo"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //模糊查询名称
            if (StringUtils.isNotEmpty(likeGoodsName)) {
                predicates.add(cbuild.like(root.get("goodsName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询商家名称
            if (StringUtils.isNotBlank(likeSupplierName)) {
                predicates.add(cbuild.like(root.get("supplierName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeSupplierName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询供应商名称
            if (StringUtils.isNotBlank(likeProviderName)) {
                predicates.add(cbuild.like(root.get("providerName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeProviderName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //批量供应商商品编号
            if (CollectionUtils.isNotEmpty(providerGoodsIds)) {
                predicates.add(root.get("providerGoodsId").in(providerGoodsIds));
            }

            //关键词搜索
            if (StringUtils.isNotBlank(keyword)) {
                String str = StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(keyword.trim())).concat(StringUtil.SQL_LIKE_CHAR);
                predicates.add(cbuild.or(cbuild.like(root.get("goodsName"), str), cbuild.like(root.get("goodsNo"), str)));
            }

            //审核状态
            if (auditStatus != null) {
                predicates.add(cbuild.equal(root.get("auditStatus"), auditStatus));
            }

            //上下架状态
            if (addedFlag != null) {
                predicates.add(cbuild.equal(root.get("addedFlag"), addedFlag));
            }

            //多个上下架状态
            if (CollectionUtils.isNotEmpty(addedFlags)) {
                predicates.add(root.get("addedFlag").in(addedFlags));
            }

            /**
             * 批量审核状态
             */
            if (CollectionUtils.isNotEmpty(auditStatusList)) {
                predicates.add(root.get("auditStatus").in(auditStatusList));
            }

            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //非商品编号
            if (StringUtils.isNotBlank(notGoodsId)) {
                predicates.add(cbuild.notEqual(root.get("goodsId"), notGoodsId));
            }

            //销售类型
            if (!ObjectUtils.isEmpty(saleType)) {
                predicates.add(cbuild.equal(root.get("saleType"), saleType));
            }

            //商品类型
            if (!ObjectUtils.isEmpty(goodsType)) {
                predicates.add(cbuild.equal(root.get("goodsType"), goodsType));
            }

            //商品来源
            if (!ObjectUtils.isEmpty(goodsSource)) {
                predicates.add(cbuild.equal(root.get("goodsSource"), goodsSource));
            }

            //运费模板ID
            if (freightTempId != null) {
                predicates.add(cbuild.equal(root.get("freightTempId"), freightTempId));
            }
            //是否筛选特价商品
            if (goodsInfoType != null) {
                if (goodsInfoType == 1) {
                    predicates.add(cbuild.equal(root.get("goodsType"), 2));
                } else {
                    predicates.add(cbuild.or(cbuild.notEqual(root.get("goodsType"), 2), cbuild.isNull(root.get("goodsType"))));
                }
            }
            //特价范围参数1
            if (specialPriceFirst != null) {
                predicates.add(cbuild.ge(root.get("marketPrice"), specialPriceFirst));
            }
            //特价范围参数2
            if (specialPriceLast != null) {
                predicates.add(cbuild.le(root.get("marketPrice"), specialPriceLast));
            }
            //查询商品是否排序
            if (goodsSeqFlag != null) {
                if (goodsSeqFlag == 1) {
                    predicates.add(cbuild.isNotNull(root.get("goodsSeqNum")));
                } else {
                    predicates.add(cbuild.isNull(root.get("goodsSeqNum")));
                }
            }
            //商品状态筛选
            if (CollectionUtils.isNotEmpty(goodsSelectStatuses)) {
                List<Predicate> orPredicate = new ArrayList<>();
                goodsSelectStatuses.forEach(goodsInfoSelectStatus -> {
                    if (goodsInfoSelectStatus != null) {
                        if (goodsInfoSelectStatus == GoodsSelectStatus.ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.YES.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.NOT_ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.NO.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.PART_ADDED) {
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.PART.toValue())));
                        } else if (goodsInfoSelectStatus == GoodsSelectStatus.OTHER) {
                            orPredicate.add(root.get("auditStatus").in(CheckStatus.FORBADE, CheckStatus.NOT_PASS, CheckStatus.WAIT_CHECK));
                        }
                    }
                });
                predicates.add(cbuild.or(orPredicate.toArray(new Predicate[orPredicate.size()])));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
