package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.request.goods.GoodsAddRequest
 * 新增商品请求对象
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午10:02
 */
@ApiModel
@Data
public class GoodsCopyByStoreRequest implements Serializable {

    private static final long serialVersionUID = -8933154540285476077L;

    private Long storeId;

    private List<String> goodsIds;


    public void checkParams() {
        if (null == storeId || CollectionUtils.isEmpty(goodsIds))
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "参数异常");
    }
}
