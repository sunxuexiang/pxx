package com.wanmi.sbc.setting.api.response.imonlineservice;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllCustomerServiceLimitWordResponse implements Serializable {

    private List<CustomerServiceLimitWordResponse> numberList = new ArrayList<>();

    private List<CustomerServiceLimitWordResponse> wordList = new ArrayList<>();

    private List<CustomerServiceLimitWordResponse> formatList = new ArrayList<>();

}
