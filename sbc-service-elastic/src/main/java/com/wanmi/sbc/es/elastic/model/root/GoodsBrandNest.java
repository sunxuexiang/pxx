package com.wanmi.sbc.es.elastic.model.root;

import com.wanmi.sbc.common.util.EsConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 商品品牌实体类
 * Created by dyt on 2017/4/11.
 */
@Data
@ApiModel
public class GoodsBrandNest implements Serializable {

    /**
     * 品牌编号
     */
    @Id
    @ApiModelProperty(value = "品牌编号")
    private Long brandId;

    /**
     * 品牌名称
     */
    @ApiModelProperty(value = "品牌名称")
    @Field(searchAnalyzer = EsConstants.DEF_ANALYZER, analyzer = EsConstants.DEF_ANALYZER, type = FieldType.Text)
    private String brandName;

    /**
     * 拼音
     */
    @ApiModelProperty(value = "拼音")
    private String pinYin;

    /**
     * 简拼
     */
    @ApiModelProperty(value = "简拼")
    private String sPinYin;

    /**
     * 品牌排序序号
     */
    @ApiModelProperty(value = "品牌排序序号")
    private Integer brandSeqNum;

    /**
     * 品类关联品牌排序序号
     */
    @ApiModelProperty(value = "品类关联品牌排序序号")
    @Field( type = FieldType.Integer)
    private Integer brandRelSeqNum;
}
