package com.wanmi.sbc.order.trade.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.order.enums.PushKingdeeOrderStatusEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推送金蝶销售单记录
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
@Entity
@Table(name = "push_kingdee_order")
public class TradePushKingdeeOrder implements Serializable{

  /**
   * 推送金蝶订单id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "push_kingdee_id")
  private Long pushKingdeeId;

  /**
   * 销售订单编号
   */
  @Column(name = "order_code")
  private String orderCode;

  /**
   * 推送给金蝶状态，0：创建 1：推送成功 2推送失败
   */
  @Column(name = "push_status")
  private Integer pushStatus;

  /**
   * 说明
   */
  @Column(name = "instructions")
  private String instructions;

  /**
   * 订单状态
   */
  @Column(name = "order_status")
  private Integer orderStatus;

  /**
   * 取消备注
   */
  @Column(name = "cancel_operation")
  private String cancelOperation;

  /**
   * 创建时间
   */
  @Column(name = "create_time")
  @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @Column(name = "update_time")
  @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
  @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  private LocalDateTime updateTime;
}
