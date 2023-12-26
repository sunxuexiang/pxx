package com.wanmi.sbc.setting.iosappversionconfig.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>IOS基础服务体类</p>
 * @author zhou.jiang
 * @date 2021-09-15
 */
@Data
@Entity
@Table(name = "ios_app_version_config")
public class IosAppVersionConfig implements Serializable {
    private static final long serialVersionUID = 4564564652222L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 版本号
     */
    @Column(name = "version_no")
    private String versionNo;

    /**
     * 版本构建号
     */
    @Column(name = "build_no")
    private Long buildNo;

    /**
     * 更新提示状态：1-不提示更新，2-提示更新但不强制更新，3-强制更新
     */
    @Column(name = "update_prompt_status")
    private Integer updatePromptStatus;

    /**
     * 是否打开微信登录标志：0-否，1-是
     */
    @Column(name = "open_wechat_login_flag")
    private Integer openWechatLongFlag;

    /**
     * 最新版本更新时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "last_version_update_time")
    private LocalDateTime lastVersionUpdateTime;
}
