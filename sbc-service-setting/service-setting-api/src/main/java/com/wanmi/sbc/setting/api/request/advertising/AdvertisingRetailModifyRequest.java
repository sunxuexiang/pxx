package com.wanmi.sbc.setting.api.request.advertising;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailConfigVO;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailGoodsConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 修改散批广告位信息请求参数实体类
 * @author: XinJiang
 * @time: 2022/4/18 18:31
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisingRetailModifyRequest implements Serializable {

    private static final long serialVersionUID = -8735223309825643568L;

    /**
     * 散批广告位ID
     */
    @ApiModelProperty(value = "散批广告位ID")
    private String advertisingId;

    /**
     * 广告名称
     */
    @ApiModelProperty(value = "广告名称")
    @NotBlank
    @Length(max = 40)
    private String advertisingName;

    /**
     * 广告位类型：0-通栏推荐位，1-分栏推荐位，2-轮播推荐位
     */
    @ApiModelProperty(value = "广告位类型：0-通栏推荐位，1-分栏推荐位，2-轮播推荐位")
    @NotNull
    private AdvertisingType advertisingType;

    /**
     * 开启状态 0：否，1：是
     */
    @ApiModelProperty(value = "开启状态 0：否，1：是")
    private DefaultFlag status;

    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty(value = "是否删除标志 0：否，1：是")
    private DeleteFlag delFlag;

    /**
     * 排序顺序
     */
    @ApiModelProperty(value = "排序顺序")
    private Integer sortNum;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String delPerson;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updatePerson;

    /**
     * 广告位配置信息
     */
    @ApiModelProperty(value = "广告位配置信息")
    private List<AdvertisingRetailConfigVO> advertisingRetailConfigs;

}
