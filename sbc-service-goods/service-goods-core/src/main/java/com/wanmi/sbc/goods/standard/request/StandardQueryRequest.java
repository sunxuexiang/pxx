package com.wanmi.sbc.goods.standard.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商品库查询请求
 * Created by daiyitian on 2017/3/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandardQueryRequest extends BaseQueryRequest {

    /**
     * 批量SPU编号
     */
    private List<String> goodsIds;

    /**
     * 模糊条件-商品名称
     */
    private String likeGoodsName;

    /**
     * 模糊条件-供应商名称
     */
    private String likeProviderName;

    /**
     * 商品来源，0供应商，1商家
     */
    private Integer goodsSource;

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
     * 批量品牌分类
     */
    private List<Long> brandIds;

    /**
     * 批量品牌分类，可与NULL以or方式查询
     */
    private List<Long> orNullBrandIds;

    /**
     * 删除标记
     */
    private Integer delFlag;

    /**
     * 非GoodsId
     */
    private String notGoodsId;

    /**
     * 价格范围最小
     */
    private BigDecimal specialPriceFirst;

    /**
     * 价格范围最大
     */
    private BigDecimal specialPriceLast;

    /**
     * 仓库Id
     */
    private Long wareId;

    /**
     * 商品销售类型 0批发，1零售
     */
    private Integer goodsSaleType;
    /**
     * erp 编码集合
     */

    private List<String> erpNos;
    /**
     * erp 编码集合
     */

    private List<String> ffskus;
    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<StandardGoods> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //批量商品编号
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                predicates.add(root.get("goodsId").in(goodsIds));
            }
            if (CollectionUtils.isNotEmpty(erpNos)) {
                    predicates.add(root.get("standardSku").get("erpGoodsInfoNo").in(erpNos));
                }
            if (CollectionUtils.isNotEmpty(ffskus)) {
                predicates.add(root.get("ffsku").in(ffskus));
            }
//            if (CollectionUtils.isNotEmpty(erpNos)) {
//                predicates.add(root.get("ffsku").in(erpNos));
//            }else{
//                if (CollectionUtils.isNotEmpty(erpNos)) {
//                    predicates.add(root.get("standardSku").get("erpGoodsInfoNo").in(erpNos));
//                }
//
//                }
            //查询品牌编号
            if (brandId != null && brandId > 0) {
                predicates.add(cbuild.equal(root.get("brandId"), brandId));
            }
            //查询分类编号
            if (cateId != null && cateId > 0) {
                predicates.add(cbuild.equal(root.get("cateId"), cateId));
            }
            //批量查询品牌编号
            if (CollectionUtils.isNotEmpty(brandIds)) {
                predicates.add(root.get("brandId").in(brandIds));
            }
            //批量查询分类编号
            if (CollectionUtils.isNotEmpty(cateIds)) {
                predicates.add(root.get("cateId").in(cateIds));
            }
            //模糊查询名称
            if (StringUtils.isNotEmpty(likeGoodsName)) {
                predicates.add(cbuild.like(root.get("goodsName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询供应商名称
            if (StringUtils.isNotEmpty(likeProviderName)) {
                predicates.add(cbuild.like(root.get("providerName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeProviderName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //批量查询品牌编号
//            if (CollectionUtils.isNotEmpty(orNullBrandIds)) {
//                predicates.add(cbuild.or(root.get("brandId").in(orNullBrandIds), root.get("brandId").isNull()));
//            }

            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //非商品编号
            if (StringUtils.isNotBlank(notGoodsId)) {
                predicates.add(cbuild.notEqual(root.get("goodsId"), notGoodsId));
            }

            //商品价格范围
            if (specialPriceFirst != null) {
                predicates.add(cbuild.ge(root.get("marketPrice"), specialPriceFirst));
            }
            if (specialPriceLast != null) {
                predicates.add(cbuild.le(root.get("marketPrice"), specialPriceLast));
            }
            //仓库筛选
            if (Objects.nonNull(wareId) && wareId > 0) {
                predicates.add(cbuild.equal(root.get("wareId"), wareId));
            }
            //销售类型筛选
            if (Objects.nonNull(goodsSaleType)) {
                predicates.add(cbuild.equal(root.get("goodsSaleType"), goodsSaleType));
            }
            //商品来源，0供应商，1商家
            /*if (goodsSource != null) {
                predicates.add(cbuild.equal(root.get("goodsSource"), goodsSource));
            }*/
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
    public Specification<StandardGoods> getStoreWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(ffskus)) {
                predicates.add(root.get("ffsku").in(ffskus));
            }
            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //仓库筛选
            if (Objects.nonNull(wareId) && wareId > 0) {
                predicates.add(cbuild.equal(root.get("wareId"), wareId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
