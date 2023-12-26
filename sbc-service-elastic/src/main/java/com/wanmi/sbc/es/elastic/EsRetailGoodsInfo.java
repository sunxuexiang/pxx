package com.wanmi.sbc.es.elastic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.EsConstants;
import com.wanmi.sbc.es.elastic.model.root.*;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ES商品实体类
 * 以SKU维度
 * Created by dyt on 2017/4/21.
 */
@Document(indexName = EsConstants.DOC_RETAIL_GOODS_INFO_TYPE, type = EsConstants.DOC_RETAIL_GOODS_INFO_TYPE)
@Data
public class EsRetailGoodsInfo implements Serializable {

    @Id
    private String id;

    /**
     * 转化为小写
     */
    @Field(type = FieldType.Text)
    private String lowGoodsName;

    /**
     * SKU信息
     */
    private GoodsInfoNest goodsInfo;

    /**
     * 商品分类信息
     */
    private GoodsCateNest goodsCate;

    /**
     * 商品品牌信息
     */
    private GoodsBrandNest goodsBrand;

    /**
     * 上下架时间
     */
    @Field(index = false, type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime addedTime;

    /**
     * SKU相关规格
     */
    @Field(type = FieldType.Nested)
    private List<GoodsInfoSpecDetailRelNest> specDetails;

    /**
     * 多对多关系，SPU相关属性
     */
    @Field(type = FieldType.Nested)
    private List<GoodsPropDetailRelNest> propDetails;

    /**
     * 等级价数据
     */
    private List<GoodsLevelPriceNest> goodsLevelPrices;

    /**
     * 客户价数据
     */
    private List<GoodsCustomerPriceNest> customerPrices;

    /**
     * 签约开始日期
     */
    @Field( type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractStartDate;

    /**
     * 签约结束日期
     */
    @Field( type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractEndDate;

    /**
     * 店铺状态 0、开启 1、关店
     */
    @Field( type = FieldType.Integer)
    private Integer storeState;

    /**
     * 禁售状态
     */
    @Field( type = FieldType.Integer)
    private Integer forbidStatus;

    /**
     * 审核状态
     */
    @Field( type = FieldType.Integer)
    private Integer auditStatus;

    /**
     * 多对多关系，多个店铺分类编号
     */
    @Field( type = FieldType.Long)
    private List<Long> storeCateIds;

    /**
     * 分销商品状态，配合分销开关使用
     */
    @Field(type = FieldType.Integer)
    private Integer distributionGoodsStatus;


    @ApiModelProperty(value = "商品绑定的规格数据")
    private List<GoodsLabelVO> goodsLabels = new ArrayList<>();
}
