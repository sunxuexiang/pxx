package com.wanmi.sbc.setting.api.request.systemconfig;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * 大白鲸APP右上角文字配置
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeValueClerkRequest {

    /**
     * 一级文字配置列表
     */
    private List<String> levelOneList = new ArrayList<>();

    /**
     * 二级文字配置列表
     */
    private List<String> levelTwoList = new ArrayList<>();

}
