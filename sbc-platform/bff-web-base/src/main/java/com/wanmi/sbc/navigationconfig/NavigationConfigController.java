package com.wanmi.sbc.navigationconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.navigationconfig.NavigationConfigProvider;
import com.wanmi.sbc.setting.api.provider.navigationconfig.NavigationConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigListRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigListResponse;
import com.wanmi.sbc.setting.bean.vo.NavigationConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;


@Api(description = "导航配置管理API", tags = "NavigationConfigController")
@RestController
@RequestMapping(value = "/navigationconfig")
public class NavigationConfigController {

    @Autowired
    private NavigationConfigQueryProvider navigationConfigQueryProvider;

    @Autowired
    private NavigationConfigProvider navigationConfigProvider;

    @Autowired
    private RedisService redisService;

    @ApiOperation(value = "列表查询导航配置")
    @GetMapping("/get")
    public BaseResponse<NavigationConfigListResponse> getList() {
        List<NavigationConfigVO> list;
        if (redisService.hasKey(RedisKeyConstant.NAVIGATION_TAB_CONFIG_DATA)) {
            list = redisService.getList(RedisKeyConstant.NAVIGATION_TAB_CONFIG_DATA, NavigationConfigVO.class);
        }else {
            NavigationConfigListRequest navigationConfigListRequest = new NavigationConfigListRequest();
            list = navigationConfigQueryProvider.list(navigationConfigListRequest).getContext().getNavigationConfigVOList();
            redisService.setObj(RedisKeyConstant.NAVIGATION_TAB_CONFIG_DATA, list, Duration.ofDays(30).getSeconds());
        }
        return BaseResponse.success(NavigationConfigListResponse.builder().navigationConfigVOList(list).build());
    }


}
