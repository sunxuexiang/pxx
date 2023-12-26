package com.wanmi.sbc.order.trade.model.newPileTrade;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "new_pile_old_data")
@EqualsAndHashCode()
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewPileOldData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "goods_info_id")
    private String goodsInfoId;

    @Column(name = "account")
    private String account;

    @Column(name = "goods_name")
    private String goodsName;

    @Column(name = "erp_no")
    private String erpNo;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "goods_num")
    private  Integer goodsNum;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "area")
    private String area;

    @Column(name = "address")
    private String address;

    @Column(name = "ware_id")
    private Integer wareId;

    //0 正常数据，1，异常数据，2正常数据已处理
    @Column(name = "state")
    private Integer state;


    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

}
