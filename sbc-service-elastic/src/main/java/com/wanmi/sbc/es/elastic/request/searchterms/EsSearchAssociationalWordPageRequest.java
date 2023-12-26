package com.wanmi.sbc.es.elastic.request.searchterms;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author houshuai
 * @date 2020/12/17 14:25
 * @description <p> </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class EsSearchAssociationalWordPageRequest extends BaseQueryRequest {


    private static final long serialVersionUID = 6875631224899349826L;
}
