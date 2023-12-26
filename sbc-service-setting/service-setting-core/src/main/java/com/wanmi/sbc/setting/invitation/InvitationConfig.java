package com.wanmi.sbc.setting.invitation;


import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * <p>
 * 邀新配置
 * </p>
 *
 * @author author
 * @since 2022-11-25
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(name = "invitation_config")
@ApiModel(value="InvitationConfig对象", description="邀新配置")
public class InvitationConfig implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_config_id")
    private Long invitationConfigId;

    @ApiModelProperty(value = "新客限购")
    @Column(name = "new_customers_buy_limit")
    private Integer newCustomersBuyLimit;

    @ApiModelProperty(value = "老客限购")
    @Column(name = "old_customers_buy_limit")
    private Integer oldCustomersBuyLimit;

    @ApiModelProperty(value = "邀请规则")
    @Column(name = "invitation_rules")
    private String invitationRules;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改人")
    @Column(name = "update_by")
    private String updateBy;

    @ApiModelProperty(value = "修改时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;


}
