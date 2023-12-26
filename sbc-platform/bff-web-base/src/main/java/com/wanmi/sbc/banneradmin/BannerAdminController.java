package com.wanmi.sbc.banneradmin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.banneradmin.BannerAdminQueryProvider;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminListRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminListResponse;
import com.wanmi.sbc.setting.bean.vo.BannerAdminVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 轮播管理
 * @author EDZ
 */
@Api(description = "轮播管理管理API", tags = "BannerAdminController")
@RestController
@RequestMapping(value = "/banneradmin")
public class BannerAdminController {

    @Autowired
    private BannerAdminQueryProvider bannerAdminQueryProvider;



    @ApiOperation(value = "列表查询轮播管理")
    @PostMapping("/list")
    public BaseResponse<BannerAdminListResponse> getList(@RequestBody @Valid BannerAdminListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("bannerSort", "asc");
        List<BannerAdminVO> bannerAdminVOList =
                bannerAdminQueryProvider.list(listReq).getContext().getBannerAdminVOList();
        for (BannerAdminVO bannerAdminVO : bannerAdminVOList) {
            String bannerImg = bannerAdminVO.getBannerImg();
            JSONArray objects;
            try {
                objects = JSON.parseArray(bannerImg);
                if (Objects.nonNull(objects)) {
                    Map<String, String> map = (Map<String, String>) objects.get(0);
                    bannerAdminVO.setBannerImg(map.get("url"));
                }
            } catch (Exception e) {
                bannerAdminVO.setBannerImg("");
            }


        }
        return BaseResponse.success(BannerAdminListResponse.builder().bannerAdminVOList(bannerAdminVOList).build());
    }






}
