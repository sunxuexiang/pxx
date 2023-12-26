package com.wanmi.sbc.setting.viewversionconfig.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "view_version_config")
public class ViewVersionConfig implements Serializable {

    private static final long serialVersionUID = 4564564652222L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 系统类型：android、ios
     */
    @Column(name = "system_type")
    private String systemType;

    /**
     * 更新提示状态：1-不提示更新，2-提示更新但不强制更新，3-强制更新
     */
    @Column(name = "update_prompt_status")
    private Integer updatePromptStatus;

    /**
     * 版本号
     */
    @Column(name = "version_no")
    private String versionNo;

    /**
     * 下载地址
     */
    @ApiModelProperty(value = "下载地址")
    @Column(name = "download_address")
    private String downloadAddress;

    /**
     * 更新安装包大小（MB）
     */
    @ApiModelProperty(value = "更新安装包大小（MB）")
    @Column(name = "package_size")
    private BigDecimal packageSize;

    /**
     * 更新描述
     */
    @ApiModelProperty(value = "更新描述")
    @Column(name = "upgrade_desc")
    private String upgradeDesc;

    /**
     * 最新版本更新时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "last_version_update_time")
    private LocalDateTime lastVersionUpdateTime;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Column(name = "state")
    private Integer state;


}
