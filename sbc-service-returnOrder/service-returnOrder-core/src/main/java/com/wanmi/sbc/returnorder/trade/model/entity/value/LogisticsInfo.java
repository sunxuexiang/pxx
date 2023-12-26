package com.wanmi.sbc.returnorder.trade.model.entity.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: LogisticsInfo
 * @Description: TODO
 * @Date: 2020/11/6 14:35
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogisticsInfo implements Serializable {
    private static final long serialVersionUID = 3633145346221381124L;
    //公司id
    private String id;
    //公司编号
    private String companyNumber;
    //公司名称
    private String logisticsCompanyName;
    //公司电话
    private String logisticsCompanyPhone;
    //物流公司地址
    private String logisticsAddress;
    //收货点
    private String receivingPoint;
    /**
     * 是否是客户自建标志位，0:否，1：是
     */
    private Integer insertFlag;
}
