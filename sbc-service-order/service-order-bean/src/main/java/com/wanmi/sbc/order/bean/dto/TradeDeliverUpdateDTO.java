package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.order.bean.vo.TradeDeliverVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @desc  
 * @author shiy  2023/6/20 9:22
*/
@Data
@ApiModel
public class TradeDeliverUpdateDTO implements Serializable {


    /**
     * @desc  原来的物流信息
     * @author shiy  2023/6/20 8:23
    */
    @ApiModelProperty(value = "旧的物流信息")
    TradeDeliverRequestDTO oldData;

    /**
     * @desc  待保存的物流信息
     * @author shiy  2023/6/20 8:23
    */
    @ApiModelProperty(value = "新的物流信息")
    TradeDeliverRequestDTO newData;

    @ApiModelProperty(value = "待保存的物流信息")
    private TradeDeliverVO tradeDeliverVO;
}
