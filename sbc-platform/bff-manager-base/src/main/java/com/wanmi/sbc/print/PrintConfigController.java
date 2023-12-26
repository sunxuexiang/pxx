package com.wanmi.sbc.print;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.print.PrintConfigProvider;
import com.wanmi.sbc.setting.api.request.print.PrintConfigRequest;
import com.wanmi.sbc.setting.api.response.print.PrintConfigResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author baijianzhong
 * @ClassName PrintConfigController
 * @Date 2020-09-16 17:13
 * @Description TODO
 **/
@RestController
public class PrintConfigController {

    @Autowired
    private PrintConfigProvider printConfigProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 查询
     * @return
     */
    @RequestMapping(value = "/print/config/fetch", method = RequestMethod.GET)
    public BaseResponse<PrintConfigResponse> findOne(){
        return printConfigProvider.findOne();
    }


    /**
     * 修改modify
     * @return
     */
    @RequestMapping(value = "/print/config/modify", method = RequestMethod.POST)
    public BaseResponse<PrintConfigResponse> modify(@RequestBody @Valid PrintConfigRequest printConfigRequest){
        operateLogMQUtil.convertAndSend("打印", "打印配置修改", "打印配置修改");
        return printConfigProvider.modify(printConfigRequest);
    }
}
