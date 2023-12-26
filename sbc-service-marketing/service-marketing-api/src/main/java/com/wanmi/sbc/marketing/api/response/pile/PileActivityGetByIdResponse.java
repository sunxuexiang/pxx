package com.wanmi.sbc.marketing.api.response.pile;

import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
public class PileActivityGetByIdResponse extends PileActivityVO {

    private static final long serialVersionUID = 992870583338752205L;
}
