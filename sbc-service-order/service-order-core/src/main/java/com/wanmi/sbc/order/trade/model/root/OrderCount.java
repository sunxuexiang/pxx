package com.wanmi.sbc.order.trade.model.root;


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
@Table(name = "s2b_statistics.replay_trade")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCount {


    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "sku_id")
    private String skuId;


    @Column(name = "count")
    private Integer count;




}
