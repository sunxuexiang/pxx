package com.wanmi.sbc.marketing.api.request.coupon;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/1 16:14
 */
@ApiModel
@Data
public class CoinRecordPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 3870614670900070555L;

    private String activityId;

    private String customerAccount;

    private String orderNo;
}
