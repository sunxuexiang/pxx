package com.wanmi.sbc.returnorder.api.request.purchase;

import com.wanmi.sbc.returnorder.bean.dto.PurchaseSaveDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-30
 */
@Data
@ApiModel
public class PurchaseAddFollowRequest extends PurchaseSaveDTO {

    private static final long serialVersionUID = 7125861920651273650L;

}
