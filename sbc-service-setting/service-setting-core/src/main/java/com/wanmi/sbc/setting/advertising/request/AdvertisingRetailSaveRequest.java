package com.wanmi.sbc.setting.advertising.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.advertising.model.root.AdvertisingRetail;
import com.wanmi.sbc.setting.advertising.model.root.AdvertisingRetailConfig;
import com.wanmi.sbc.setting.advertising.model.root.AdvertisingRetailGoodsConfig;
import com.wanmi.sbc.setting.advertising.model.validgroups.NotAdvertisingId;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
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
import java.util.Objects;

/**
 * @description: 散批广告位保存请求参数实体类
 * @author: XinJiang
 * @time: 2022/4/18 17:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisingRetailSaveRequest implements Serializable {

    private static final long serialVersionUID = 5494922075444015544L;

    /**
     * 散批广告位ID
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
     * 开启状态 0：否，1：是
     */
    @NotNull
    private DefaultFlag status;

    /**
     * 是否删除标志 0：否，1：是
     */
    @NotNull
    private DeleteFlag delFlag;

    /**
     * 排序顺序
     */
    private Integer sortNum;

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
     * 广告位配置信息
     */
    @Size(max = 5)
    private List<AdvertisingRetailConfig> advertisingRetailConfigs;

    public AdvertisingRetail generateAdvertisingRetail(){

        AdvertisingRetail advertisingRetail = new AdvertisingRetail();

        advertisingRetail.setAdvertisingName(advertisingName);
        advertisingRetail.setAdvertisingType(advertisingType);
        if (Objects.isNull(status)) {
            advertisingRetail.setStatus(DefaultFlag.NO);
        } else {
            advertisingRetail.setStatus(status);
        }
        if (Objects.isNull(sortNum)) {
            advertisingRetail.setSortNum(0);
        } else {
            advertisingRetail.setSortNum(sortNum);
        }
        advertisingRetail.setCreatePerson(createPerson);
        advertisingRetail.setCreateTime(createTime);

        return advertisingRetail;
    }

    public List<AdvertisingRetailConfig> generateAdvertisingRetailConfig(String addAdvertisingId){
        advertisingRetailConfigs.forEach(config -> {
            config.setAdvertisingId(addAdvertisingId);
            config.setAdvertisingConfigId(null);
        });
        return advertisingRetailConfigs;
    }

}
