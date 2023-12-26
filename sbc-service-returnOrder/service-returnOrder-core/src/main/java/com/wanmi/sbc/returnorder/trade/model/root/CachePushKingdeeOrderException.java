package com.wanmi.sbc.returnorder.trade.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 缓存销售订单推金蝶
 *
 * @author yitang
 * @version 1.0
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cache_push_kingdee_order_exception")
@EntityListeners(AuditingEntityListener.class)
public class CachePushKingdeeOrderException implements Serializable {

    /**
     * 推送金蝶订单id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_kingdee_log_id")
    private Long pushKingdeeId;


    /**
     * 销售订单编号
     */
    @Column(name = "order_code")
    private String orderCode;


    /**
     * 错误日志
     */
    @Column(name = "exception_info")
    private String exceptionInfo;




    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time",updatable = false,nullable = false)
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime creattime;

}
