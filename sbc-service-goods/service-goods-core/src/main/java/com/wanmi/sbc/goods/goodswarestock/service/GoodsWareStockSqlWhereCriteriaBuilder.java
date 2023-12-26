package com.wanmi.sbc.goods.goodswarestock.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockPageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsWareStockSqlWhereCriteriaBuilder
 * @Description TODO
 * @Author lvzhenwei
 * @Date 2020/4/10 15:58
 **/
@Data
public class GoodsWareStockSqlWhereCriteriaBuilder extends BaseQueryRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 批量查询-主键List
     */
    @ApiModelProperty(value = "批量查询-主键List")
    private List<Long> idList;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    private String goodsInfoId;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsInfoName;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    private String goodsInfoNo;

    /**
     * 商品分类
     */
    @ApiModelProperty(value = "商品分类")
    private Long cateId;

    /**
     * 商品品牌
     */
    @ApiModelProperty(value = "商品品牌")
    private Long brandId;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID ")
    private Long wareId;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称 ")
    private String wareName;

    /**
     * 搜索条件:创建时间开始
     */
    @ApiModelProperty(value = "搜索条件:创建时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeBegin;
    /**
     * 搜索条件:创建时间截止
     */
    @ApiModelProperty(value = "搜索条件:创建时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeEnd;

    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty(value = "是否删除标志 0：否，1：是")
    private DeleteFlag delFlag;

    /**
     * 分类idList
     */
    List<Long> cateIds;

    /**
     * 拼接查询结果
     *
     * @return
     */
    public String getQueryResultInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT\n" +
                "\ts.id,\n" +
                "\ts.stock,\n" +
                "\ts.ware_id wareId,\n" +
                "\tg.goods_info_name goodsInfoName,\n" +
                "\ts.goods_info_no goodsInfoNo,\n" +
                "\ts.goods_info_id goodsInfoId,\n" +
                "\tgroup_concat(distinct(c.cate_name)) cateName,\n" +
                "\tc.store_cate_id cateId,\n" +
                "\tb.brand_name brandName,\n" +
                "\tb.brand_id brandId,\n" +
                "\tgroup_concat(distinct(sp.detail_name)) specName,\n" +
                "\tw.ware_name wareName\n");
        return sb.toString();
    }

    /**
     * 拼接查询结果条数
     *
     * @return
     */
    public String getQueryTotalInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT\n" +
                "\tcount(1) total \n" +
                "from (\n");
        return sb.toString();
    }

    /**
     * 拼接查询结果条数
     *
     * @return
     */
    public String getQueryTotalInfoEnd() {
        StringBuilder sb = new StringBuilder();
        sb.append(")res\n");
        return sb.toString();
    }

    /**
     * 拼接查询条件
     *
     * @return
     */
    public String getTableJoinInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("FROM\n" +
                "\tgoods_ware_stock s\n" +
                "\tLEFT JOIN goods_info g ON g.goods_info_id = s.goods_info_id\n" +
                "\tLEFT JOIN store_cate_goods_rela gr ON gr.goods_id = g.goods_id\n" +
                "\tLEFT JOIN store_cate c ON c.store_cate_id = gr.store_cate_id\n" +
                "\tLEFT JOIN goods_brand b ON b.brand_id = g.brand_id\n" +
                "\tLEFT JOIN goods_info_spec_detail_rel sp ON sp.goods_info_id = s.goods_info_id \n" +
                "\tLEFT JOIN ware_house w ON w.ware_id = s.ware_id \n" +
                "WHERE 1=1 \n");
        if (Objects.nonNull(storeId)) {
            sb.append("\tand s.store_id = ");
            sb.append(storeId + " \n");
        }
        if (StringUtils.isNotBlank(goodsInfoName)) {
            sb.append("\tand g.goods_info_name like '%");
            sb.append(goodsInfoName);
            sb.append("%'\n");
        }
        if (StringUtils.isNotBlank(goodsInfoNo)) {
            sb.append("\tand g.goods_info_no like '%");
            sb.append(goodsInfoNo);
            sb.append("%'\n");
        }
//        if (Objects.nonNull(cateId)) {
//            sb.append("\tand c.store_cate_id = ");
//            sb.append(cateId + " \n");
//        }

        if(CollectionUtils.isNotEmpty(cateIds)){
            sb.append("\tand c.store_cate_id in(  ");
            String cateIdListStr = Joiner.on(",").join(cateIds);
            sb.append(cateIdListStr+ ") \n");
        }

        if (Objects.nonNull(brandId)) {
            sb.append("\tand b.brand_id = ");
            sb.append(brandId + " \n");
        }
        if (Objects.nonNull(wareId)) {
            sb.append("\tand w.ware_id = ");
            sb.append(wareId + " \n");
        }
        if (Objects.nonNull(wareName)) {
            sb.append("\tand w.ware_name like '%");
            sb.append(wareName);
            sb.append("%'\n");
        }
        if (Objects.nonNull(idList)) {
            sb.append("\tand s.id in( ");
            String idListStr = Joiner.on(",").join(idList);
            sb.append(idListStr+ ") \n");
        }
        return sb.toString();
    }

    public String getGroupByInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tGROUP BY s.goods_info_id,s.ware_id order by s.create_time desc\n");
        return sb.toString();
    }

    /**
     * 查询对象转换
     *
     * @param sqlResult
     * @return
     */
    public static List<GoodsWareStockPageVO> converter(List<Map<String, Object>> sqlResult) {
        return sqlResult.stream().map(item ->
                GoodsWareStockPageVO.builder()
                        .goodsInfoId(toStr(item, "goodsInfoId"))
                        .goodsInfoName(toStr(item, "goodsInfoName"))
                        .goodsInfoNo(toStr(item, "goodsInfoNo"))
                        .cateName(toStr(item, "cateName"))
                        .brandName(toStr(item, "brandName"))
                        .specName(toStr(item, "specName"))
                        .wareName(toStr(item, "wareName"))
                        .wareId(toLong(item, "wareId"))
                        .id(toLong(item, "id"))
                        .stock(toLong(item, "stock"))
                        .cateId(toStr(item, "cateId"))
                        .brandId(toStr(item, "brandId"))
                        .build()
        ).collect(Collectors.toList());
    }

    private static String toStr(Map<String, Object> map, String key) {
        return map.get(key) != null ? map.get(key).toString() : null;
    }

    private static Integer toInteger(Map<String, Object> map, String key) {
        return map.get(key) != null ? Integer.parseInt(map.get(key).toString()) : null;
    }

    private static Long toLong(Map<String, Object> map, String key) {
        return map.get(key) != null ? Long.valueOf(map.get(key).toString()) : null;
    }

    private static BigDecimal toBigDecimal(Map<String, Object> map, String key) {
        return map.get(key) != null ? new BigDecimal(map.get(key).toString()) : null;
    }
}
