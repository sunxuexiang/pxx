package com.wanmi.sbc.returnorder.trade.model.root;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p>物流信息实体类</p>
 *
 * @author yuanfei
 * @date 2020-04-15 10:34:15
 */
@Data
@Entity
@Table(name = "order_logistics")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderLogistics {

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
     * 快递公司类型：1，百世
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 物流公司名称
     */
    @Column(name = "nick_name")
    private String nickName;


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
