package com.wanmi.sbc.customer.api.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/22 9:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRelationBatchRequest implements Serializable {


    private static final long serialVersionUID = 8231715940198778809L;

    private List<CustomerModifyRequest> relationList;
}
