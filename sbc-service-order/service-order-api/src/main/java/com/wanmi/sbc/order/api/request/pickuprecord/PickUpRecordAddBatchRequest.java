package com.wanmi.sbc.order.api.request.pickuprecord;

import com.wanmi.sbc.order.api.request.OrderBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * <p>批量删除测试代码生成请求参数</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordAddBatchRequest extends OrderBaseRequest {


	private static final long serialVersionUID = 3556305586547589041L;
	/**
	 * 批量删除-主键List
	 */
	@ApiModelProperty(value = "批量新增")
	private List<PickUpRecordAddRequest> pickUpRecordAddRequestList;

}
