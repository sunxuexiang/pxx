package com.wanmi.sbc.setting.advertising.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.advertising.model.root.Advertising;
import com.wanmi.sbc.setting.advertising.model.root.AdvertisingConfig;
import com.wanmi.sbc.setting.advertising.model.validgroups.NotAdvertisingId;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 首页广告位保存请求类
 * @author: XinJiang
 * @time: 2022/2/18 10:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisingSaveRequest implements Serializable {

    private static final long serialVersionUID = 5356234827795749520L;

    /**
     * 首页广告位ID
     */
    @NotNull(groups = {NotAdvertisingId.class})
    private String advertisingId;

    /**
     * 广告名称
     */
    @NotBlank
    @Length(max = 40)
    private String advertisingName;

    /**
     * 广告位类型：0-通栏推荐位，1-分栏推荐位，2-轮播推荐位
     */
    @NotNull
    private AdvertisingType advertisingType;

    /**
     * 是否套装商品广告页 0否，1是
     */
    private DefaultFlag isSuit;

    /**
     * 是否删除标志 0：否，1：是
     */
    @NotNull
    private DeleteFlag delFlag;

    /**
     * 排序顺序
     */
    @NotNull
    private Integer sortNum;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createPerson;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updatePerson;

    /**
     * 删除时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    private String delPerson;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 广告位配置信息
     */
    @Size(max = 5)
    private List<AdvertisingConfig> advertisingConfigList;

    public Advertising generateAdvertising(){
        Advertising advertising = new Advertising();

        advertising.setAdvertisingName(advertisingName);
        advertising.setAdvertisingType(advertisingType);

        advertising.setDelFlag(delFlag);
        advertising.setSortNum(sortNum);
        advertising.setCreateTime(createTime);
        advertising.setCreatePerson(createPerson);
        advertising.setWareId(wareId);
        advertising.setStoreId(storeId);
        return advertising;
    }

    public List<AdvertisingConfig> generateAdvertisingConfig(String advertisingId) {
        advertisingConfigList.forEach(config -> config.setAdvertisingId(advertisingId));
        return advertisingConfigList;
    }
}
