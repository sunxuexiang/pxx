package com.wanmi.sbc.companymall;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.companymall.response.CompanyStoreBorderImageRequest;
import com.wanmi.sbc.companymall.response.CompanyStoreBorderImageResponse;
import com.wanmi.sbc.redis.RedisService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-03 14:30
 **/

@RestController
@RequestMapping("/store-border-image")
public class CompanyStoreBorderImageController {

    @Autowired
    private RedisService redisService;
    private final static String KEY = "store-border-image";

    @ApiOperation(value = "list边框")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    BaseResponse<List<CompanyStoreBorderImageResponse>> listAll() {
        List<CompanyStoreBorderImageResponse> response;
        final String string = redisService.getString(KEY);
        if (StringUtils.isNotBlank(string)) {
            response = JSONObject.parseArray(string, CompanyStoreBorderImageResponse.class);
        } else {
            response = new ArrayList<>();
        }
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "add边框")
    @RequestMapping(value = "/add", method = RequestMethod.PUT)
    BaseResponse<List<CompanyStoreBorderImageResponse>> add(@RequestParam("image") String image) {
        final List<CompanyStoreBorderImageResponse> context = listAll().getContext();
        context.add(new CompanyStoreBorderImageResponse(image));
        redisService.setString(KEY,JSONObject.toJSONString(context));
        return BaseResponse.success(context);
    }

    @ApiOperation(value = "add边框批量")
    @RequestMapping(value = "/batch-edit", method = RequestMethod.POST)
    BaseResponse<List<CompanyStoreBorderImageResponse>> batchEdit(@RequestBody CompanyStoreBorderImageRequest request) {
        List<CompanyStoreBorderImageResponse> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getImages())){
            request.getImages().forEach(image -> {
                final CompanyStoreBorderImageResponse companyStoreBorderImageResponse = new CompanyStoreBorderImageResponse();
                list.add(companyStoreBorderImageResponse);
                companyStoreBorderImageResponse.setImage(image);
            });
        }
        redisService.setString(KEY,JSONObject.toJSONString(list));
        return BaseResponse.success(list);
    }


    @ApiOperation(value = "delete边框")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    BaseResponse<List<CompanyStoreBorderImageResponse>> delete(@RequestParam("image") String image) {
        final List<CompanyStoreBorderImageResponse> context = listAll().getContext();
        final Iterator<CompanyStoreBorderImageResponse> iterator = context.iterator();
        while (iterator.hasNext()){
            final CompanyStoreBorderImageResponse next = iterator.next();
            if (Objects.equals(image,next.getImage()))  iterator.remove();
        }
        redisService.setString(KEY,JSONObject.toJSONString(context));
        return BaseResponse.success(context);
    }
}
