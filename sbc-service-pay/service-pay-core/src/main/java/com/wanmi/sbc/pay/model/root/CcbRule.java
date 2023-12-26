package com.wanmi.sbc.pay.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 建行分账规则
 */
@Data
@Entity
@Table(name = "ccb_rule")
@Accessors(chain = true)
public class CcbRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "rule_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    /**
     * 市场编号
     */
    @Column(name = "mkt_id")
    private String mktId;

    /**
     * 市场名称
     */
    @Column(name = "mkt_nm")
    private String mktNm;

    /**
     * 清算规则编号
     */
    @Column(name = "clrg_rule_id")
    private String clrgRuleId;

    /**
     * 规则名称
     */
    @Column(name = "rule_nm")
    private String ruleNm;

    /**
     * 规则描述
     */
    @Column(name = "rule_dsc")
    private String ruleDsc;

    /**
     * 分账周期(1-日；2-月；3-月末)
     */
    @Column(name = "sub_acc_cyc")
    private String subAccCyc;

    /**
     * 清算后延天数(当分账周期为1-日，2-月时必输；
     * 1-日，取值为1-100；
     * 2-月，取值为1-31；)
     */
    @Column(name = "clrg_dlay_dys")
    private String clrgDlayDys;

    /**
     * 清算模式(2-滚动式；1-翻盘式 3-时间自定义式)
     */
    @Column(name = "clrg_mode")
    private String clrgMode;

    /**
     * 清算方式代码(1固定金额
     * 2固定比例
     * 3先固定金额再比例)
     */
    @Column(name = "clrg_mtdcd")
    private String clrgMtdcd;

    /**
     * 生效日期
     */
    @Column(name = "efdt")
    private LocalDateTime efdt;

    /**
     * 失效日期
     */
    @Column(name = "expdt")
    private LocalDateTime expdt;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

}
