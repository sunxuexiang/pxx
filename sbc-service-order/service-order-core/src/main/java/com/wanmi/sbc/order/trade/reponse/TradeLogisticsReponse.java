package com.wanmi.sbc.order.trade.reponse;

import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.order.trade.model.entity.PayInfo;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.TradeState;
import com.wanmi.sbc.order.trade.model.entity.value.Supplier;
import com.wanmi.sbc.order.trade.model.entity.value.TradePrice;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
