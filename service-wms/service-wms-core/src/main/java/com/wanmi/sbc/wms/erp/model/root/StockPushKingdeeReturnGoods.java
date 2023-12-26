package com.wanmi.sbc.wms.erp.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 推金蝶囤货退货单
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock_push_kingdee_return_goods")
public class StockPushKingdeeReturnGoods {

      /**
       * id
       */
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "push_kingdee_return_goods_id")
      private Long pushKingdeeReturnGoodsId;

      /**
       * 囤货退货编号
       */
      @Column(name = "stock_return_goods_code")
      private String stockReturnGoodsCode;

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
       * 囤货订单号
       */
      @Column(name = "stock_order_code")
      private String stockOrderCode;

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
