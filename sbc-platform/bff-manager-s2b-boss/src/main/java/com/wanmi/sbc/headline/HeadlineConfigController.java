package com.wanmi.sbc.headline;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.setting.api.provider.headline.HeadLineProvider;
import com.wanmi.sbc.setting.api.request.headline.HeadLineSaveRequest;
import com.wanmi.sbc.setting.api.response.headline.HeadLineResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/7 15:58
 */
@Api(description = "首页白鲸头条配置",tags = "HeadlineConfigController")
@RestController
@RequestMapping(value = "/home/page/headline")
public class HeadlineConfigController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private HeadLineProvider headLineProvider;

    @ApiOperation(value = "保存白鲸头条配置")
    @PostMapping("/save")
    public BaseResponse save(@RequestBody @Valid List<HeadLineSaveRequest> request){
        if (!CollectionUtils.isEmpty(request) && request.size() > 3) {
            return BaseResponse.error("最多支持三条头条配置");
        }
        Operator operator = commonUtil.getOperator();
        String account = operator.getAccount();
        LocalDateTime now = LocalDateTime.now();
        int sortNum = 1;
        for (HeadLineSaveRequest req : request) {
            String content = req.getContent();
            if (StringUtils.isBlank(content)) {
                return BaseResponse.error("第" + sortNum + "条内容不能为空");
            }
            if (content.length() > 50) {
                return BaseResponse.error("第" + sortNum + "条内容长度不能超过50");
            }
            req.setSortNum(sortNum);
            req.setCreatePerson(account);
            req.setUpdatePerson(account);
            req.setCreateTime(now);
            req.setUpdateTime(now);
            sortNum++;
        }
        return headLineProvider.save(request);
    }

    @ApiOperation(value = "头条列表")
    @GetMapping
    public BaseResponse<List<HeadLineResponse>> get(){
        return headLineProvider.get();
    }
}
