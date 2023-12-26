package com.wanmi.sbc.returnorder.api.response.returnorder;

import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 根据userId获取退单快照响应结构
 * @Author: daiyitian
 * @Description:
 * @Date: 2018-11-16 16:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class ReturnOrderTransferByUserIdResponse extends ReturnOrderVO implements Serializable {

    private static final long serialVersionUID = -1076979847505660373L;
}
