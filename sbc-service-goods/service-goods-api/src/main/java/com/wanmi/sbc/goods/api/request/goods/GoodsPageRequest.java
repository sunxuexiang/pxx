package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsSelectStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest
 * 商品分页请求对象
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午9:33 wwwwwww
 */
@ApiModel
@Data
public class GoodsPageRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = -7972557462976673056L;

    /**
     * 批量SPU编号
     */
    @ApiModelProperty(value = "批量SPU编号")
    private List<String> goodsIds;

    /**
     * 精准条件-SPU编码
     */
    @ApiModelProperty(value = "精准条件-SPU编码")
    private String goodsNo;

    /**
     * 精准条件-批量SPU编码
     */
    @ApiModelProperty(value = "精准条件-批量SPU编码")
    private List<String> goodsNos;

    /**
     * 模糊条件-SPU编码
     */
    @ApiModelProperty(value = "模糊条件-SPU编码")
    private String likeGoodsNo;

    /**
     * 模糊条件-SKU编码
     */
    @ApiModelProperty(value = "模糊条件-SKU编码")
    private String likeGoodsInfoNo;

    /**
     * 模糊条件-erp编码
     */
    @ApiModelProperty(value = "模糊条件-erp编码")
    private String likeErpNo;

    /**
     * 模糊条件-商品名称
     */
    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    /**
     * 模糊条件-供应商名称
     */
    @ApiModelProperty(value = "模糊条件-供应商名称")
    private String likeProviderName;

    /**
     * 模糊条件-关键词（商品名称、SPU编码）
     */
    @ApiModelProperty(value = "模糊条件-关键词", notes = "商品名称、SPU编码")
    private String keyword;

    /**
     * 商品分类
     */
    @ApiModelProperty(value = "商品分类")
    private Long cateId;

    /**
     * 批量商品分类
     */
    @ApiModelProperty(value = "批量商品分类")
    private List<Long> cateIds;

    /**
     * 品牌编号
     */
    @ApiModelProperty(value = "品牌编号")
    private Long brandId;

    /**
     * 批量品牌编号
     */
    @ApiModelProperty(value = "批量品牌编号")
    private List<Long> brandIds;

    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态", dataType = "com.wanmi.sbc.goods.bean.enums.AddedFlag")
    private Integer addedFlag;

    /**
     * 上下架状态-批量
     */
    @ApiModelProperty(value = "上下架状态-批量", dataType = "com.wanmi.sbc.goods.bean.enums.AddedFlag")
    private List<Integer> addedFlags;

    /**
     * 商家类型
     */
    @ApiModelProperty(value = "商家类型", dataType = "com.wanmi.sbc.common.enums.CompanyType")
    private CompanyType companyType;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记", dataType = "com.wanmi.sbc.common.enums.DeleteFlag")
    private Integer delFlag;

    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String customerId;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;
    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private List<Long> storeIds;
    /**
     * 非GoodsId
     */
    @ApiModelProperty(value = "非GoodsId")
    private String notGoodsId;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String likeSupplierName;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态", dataType = "com.wanmi.sbc.goods.bean.enums.CheckStatus")
    private CheckStatus auditStatus;

    /**
     * 批量审核状态
     */
    @ApiModelProperty(value = "批量审核状态", dataType = "com.wanmi.sbc.goods.bean.enums.CheckStatus")
    private List<CheckStatus> auditStatusList;

    /**
     * 店铺分类Id
     */
    @ApiModelProperty(value = "店铺分类Id")
    private Long storeCateId;

    /**
     * 店铺分类所关联的SpuIds
     */
    @ApiModelProperty(value = "店铺分类所关联的SpuIds")
    private List<String> storeCateGoodsIds;

    /**
     * 运费模板ID
     */
    @ApiModelProperty(value = "运费模板ID")
    private Long freightTempId;

    /**
     * 商品状态筛选
     */
    @ApiModelProperty(value = "商品状态筛选")
    private List<GoodsSelectStatus> goodsSelectStatuses;

    /**
     * 销售类别
     */
    @ApiModelProperty(value = "销售类别", dataType = "com.wanmi.sbc.goods.bean.enums.SaleType")
    private Integer saleType;

    /**
     * 商品类型，0:实体商品，1：虚拟商品
     */
    @ApiModelProperty(value = "商品类型")
    private Integer goodsType;

    /**
     * 商品来源，0品牌商城，1商家
     */
    @ApiModelProperty(value = "商品来源，0品牌商城，1商家")
    private Integer goodsSource;

    /**
     * 是否为特价商品
     */
    @ApiModelProperty(value = "是否为特价商品 ： 0否   1是")
    private Integer goodsInfoType;

    /**
     * 商品排序查询 1已排序 0未排序
     */
    @ApiModelProperty(value = "品排序查询 1已排序 0未排序")
    private Integer goodsSeqFlag;
    
    /**
     * 店铺内商品排序查询 1已排序 0未排序
     */
    @ApiModelProperty(value = "店铺内商品排序查询 1已排序 0未排序")
    private Integer storeGoodsSeqFlag;

    //三期更改：新增查询条件
    /**
     * 特价范围参数1
     */
    @ApiModelProperty(value = "第一个特价")
    private BigDecimal specialPriceFirst;

    /**
     * 特价范围参数2
     */
    @ApiModelProperty(value = "第二个特价")
    private BigDecimal specialPriceLast;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String goodsInfoBatchNo;

    /**
     * 是否为囤货 1：囤货中 ，2：已囤完 ，3：全部
     */
    @ApiModelProperty(value = "囤货筛选条件")
    private Long stockUp;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "商品创建时间条件")
    private String  create_timeStart;


    /**
     * 创建时间
     */
    @ApiModelProperty(value = "商品创建时间条件")
    private String  create_timeEnd;

    /**
     * 上架状态
     */
    @ApiModelProperty(value = "上下架状态,0:下架1:上架2:部分上架")
    private Integer added_flag;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

    /**
     * 匹配的分仓Id
     */
    @ApiModelProperty(value = "匹配的分仓Id")
    private Long wareId;


    /**
     * 商品规格 0为全部 1为单规格  2为多规格
     */
    private Integer manySpecs = 0;
    /**
     * 店铺名称
     */
    private String storeName;



}
