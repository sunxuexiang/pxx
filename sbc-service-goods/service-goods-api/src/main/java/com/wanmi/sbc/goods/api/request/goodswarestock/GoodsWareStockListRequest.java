package com.wanmi.sbc.goods.api.request.goodswarestock;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>sku分仓库存表列表查询请求参数</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockListRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsInfoName;

    /**
     * 批量查询-主键List
     */
    @ApiModelProperty(value = "批量查询-主键List")
    private List<Long> idList;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    private String goodsInfoId;


    /**
     * sku IDS
     */
    @ApiModelProperty(value = "skuIds")
    private List<String> goodsInfoIds;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    private String goodsInfoNo;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID ")
    private Long wareId;

    /**
     * 货品库存
     */
    @ApiModelProperty(value = "货品库存")
    private Long stock;

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
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 搜索条件:修改时间开始
     */
    @ApiModelProperty(value = "搜索条件:修改时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeBegin;
    /**
     * 搜索条件:修改时间截止
     */
    @ApiModelProperty(value = "搜索条件:修改时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeEnd;

    /**
     * 编辑人
     */
    @ApiModelProperty(value = "编辑人")
    private String updatePerson;

    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty(value = "是否删除标志 0：否，1：是")
    private DeleteFlag delFlag;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * spu ID
     */
    @ApiModelProperty(value = "spu ID")
    private String goodsId;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称 ")
    private String wareName;

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
     * 分类idList
     */
    @ApiModelProperty(value = "商品分类IdList")
    List<Long> cateIds;

    private WareHouseType wareHouseType;

}