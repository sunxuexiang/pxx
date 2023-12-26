package com.wanmi.sbc.order.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.order.trade.model.entity.value.Supplier;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author: lq
 * @CreateTime:2019-08-16 15:34
 * @Description:todo
 */

@Component
public class OrderCommonService {

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 查询订单可退时间
     */
    public LocalDateTime queryReturnTime() {
        LocalDateTime returnTime = LocalDateTime.now();
        // 查询已完成订单允许申请退单天数配置
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        // 是否支持退货，不支持退货时，订单可退时间设置为当前时间
        if (Objects.nonNull(config)&& (config.getStatus() != 0)) {
            JSONObject content = JSON.parseObject(config.getContext());
            Long day = content.getObject("day", Long.class);
            returnTime = returnTime.plusDays(day);
        }
        return returnTime;
    }

    /**
     * @desc  订单能否推ERP
     * @author shiy  2023/7/20 10:36
    */
    public boolean erpCanTrade(Trade trade){
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && !isSelf(trade.getSupplier().getCompanyType())){
            logger.info("商家不推ERP.订单ID[{}]商家ID[{}]", trade.getId(),trade.getSupplier().getStoreId());
            return false;
        }
        return true;
    }

    /**
     * @desc  囤货能否推ERP
     * @author shiy  2023/7/20 10:37
    */
    public boolean erpCanPileTrade(NewPileTrade trade){
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && !isSelf(trade.getSupplier().getCompanyType())){
            logger.info("商家不推ERP.订单ID[{}]商家ID[{}]", trade.getId(),trade.getSupplier().getStoreId());
            return false;
        }
        return true;
    }

    /**
     * @desc  订单能否推WMS
     * @author shiy  2023/7/20 10:36
     */
    public boolean wmsCanTrade(Trade trade){
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && !isSelf(trade.getSupplier().getCompanyType())){
            logger.info("商家不推WMS.订单ID[{}]商家ID[{}]", trade.getId(),trade.getSupplier().getStoreId());
            return false;
        }
        return true;
    }

    /**
     * @desc  囤货能否推WMS
     * @author shiy  2023/7/20 10:37
     */
    public boolean wmsCanPileTrade(NewPileTrade trade){
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && !isSelf(trade.getSupplier().getCompanyType())){
            logger.info("商家不推WMS.订单ID[{}]商家ID[{}]", trade.getId(),trade.getSupplier().getStoreId());
            return false;
        }
        return true;
    }

    /**
     * @desc  囤货能否推WMS
     * @author shiy  2023/7/20 10:37
     */
    public boolean wmsCanPileTrade(PileTrade trade){
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && !isSelf(trade.getSupplier().getCompanyType())){
            logger.info("商家不推WMS.订单ID[{}]商家ID[{}]", trade.getId(),trade.getSupplier().getStoreId());
            return false;
        }
        return true;
    }

    /**
     * @desc  自营商家CompanyType
     * @author shiy  2023/7/20 10:36
     */
    public Boolean isSelf(CompanyType companyType){
        if(Objects.isNull(companyType)){
            logger.info("不是自营类型");
            return false;
        }
        boolean isTrue = (CompanyType.PLATFORM.toValue()==companyType.toValue() || companyType.toValue()==CompanyType.UNIFIED.toValue());
        if(!isTrue){
            logger.info("不是自营类型");
            return false;
        }
        return true;
    }


    /**
     * @desc  自营商家Supplier
     * @author shiy  2023/7/20 10:36
     */
    public boolean isSelf(Supplier supplier){
        if(Objects.nonNull(supplier) && Objects.nonNull(supplier.getCompanyType()) && !isSelf(supplier.getCompanyType())){
            logger.info("不是自营商家ID[{}]", supplier.getStoreId());
            return false;
        }
        return true;
    }


    /**
     * @desc  自营商家order
     * @author shiy  2023/7/20 10:36
     */
    public boolean selfOrder(Trade trade){
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && !isSelf(trade.getSupplier().getCompanyType())){
            logger.info("不是自营订单.订单ID[{}]商家ID[{}]", trade.getId(),trade.getSupplier().getStoreId());
            return false;
        }
        return true;
    }

    /**
     * @desc  自营商家订单WMS标识
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsSoReference5(Trade trade){
        String str = "电商订单";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType())) {
            if (trade.getSupplier().getCompanyType() == CompanyType.UNIFIED){
                str = "商家订单";
            }else if (trade.getSupplier().getCompanyType() == CompanyType.SUPPLIER) {
                str = "外部订单";
            }
        }
        return str;
    }

    /**
     * @desc  自营商家订单WMS标识
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsSoReference5(NewPileTrade trade){
        String str = "电商订单";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType())) {
            if (trade.getSupplier().getCompanyType() == CompanyType.UNIFIED){
                str = "商家订单";
            }else if (trade.getSupplier().getCompanyType() == CompanyType.SUPPLIER){
                str = "外部订单";
            }
        }
        return str;
    }

    /**
     * @desc  自营商家订单WMS标识
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsSoReference5(PileTrade trade){
        String str = "电商订单";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && (trade.getSupplier().getCompanyType()== CompanyType.UNIFIED)){
            str = "商家订单";
        }
        return str;
    }


    /**
     * @desc  只有是 自营商家订单，，才改成 SJDD，  超级大白鲸的  还是XSCK/WBDD
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsDocType(Trade trade){
        String str = "XSCK";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType())) {
            if (trade.getSupplier().getCompanyType() == CompanyType.UNIFIED){
                str = "SJDD";
            }else if (trade.getSupplier().getCompanyType() == CompanyType.SUPPLIER){
                str = "WBDD";
            }
        }
        return str;
    }

    /**
     * @desc  只有是 自营商家订单，，才改成 SJDD，  超级大白鲸的  还是XSCK
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsDocType(NewPileTrade trade){
        String str = "XSCK";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType())) {
            if (trade.getSupplier().getCompanyType() == CompanyType.UNIFIED){
                str = "SJDD";
            }else if (trade.getSupplier().getCompanyType() == CompanyType.SUPPLIER){
                str = "WBDD";
            }
        }
        return str;
    }

    /**
     * @desc  只有是 自营商家订单，，才改成 SJDD，  超级大白鲸的  还是XSCK
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsDocType(PileTrade trade){
        String str = "XSCK";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType())) {
            if (trade.getSupplier().getCompanyType() == CompanyType.UNIFIED){
                str = "SJDD";
            }else if (trade.getSupplier().getCompanyType() == CompanyType.SUPPLIER){
                str = "WBDD";
            }
        }
        return str;
    }





    /**
     * @desc  自营商家囤货WMS标识
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsSoReference4(Trade trade){
        String str = "电商退单";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && (trade.getSupplier().getCompanyType()== CompanyType.UNIFIED)){
            str = "商家退单";
        }
        return str;
    }

    /**
     * @desc  自营商家囤货WMS标识
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsSoReference4(NewPileTrade trade){
        String str = "电商退单";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && (trade.getSupplier().getCompanyType()== CompanyType.UNIFIED)){
            str = "商家退单";
        }
        return str;
    }

    /**
     * @desc  自营商家囤货WMS标识
     * @author shiy  2023/7/20 10:36
     */
    public String getWmsSoReference4(PileTrade trade){
        String str = "电商退单";
        if(Objects.nonNull(trade.getSupplier()) && Objects.nonNull(trade.getSupplier().getCompanyType()) && (trade.getSupplier().getCompanyType()== CompanyType.UNIFIED)){
            str = "商家退单";
        }
        return str;
    }



}
