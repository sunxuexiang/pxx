package com.wanmi.sbc.returnorder.trade.reponse;

import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 物流返回对象
 * Created by sunkun on 2017/9/20.
 */
@Data
public class TradeLogisticsReponse {

    /**
     * 外部电商订单号
     */
    @Id
    @Column(name = "logistic_id")
    private String logisticId;
    /**
     * 物流公司ID（填：BESTQJT）
     */
    @Column(name = "logistic_company_id")
    private String logisticCompanyId;
    /**
     * 电商编号（外部电商编号）
     */
    @Column(name = "ec_id")
    private String ecId;
    /**
     * 物流信息
     */
    @Column(name = "logistics_data")
    private String logisticsData;
    /**
     * 物流更新时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    /**
     * 物流创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

}
