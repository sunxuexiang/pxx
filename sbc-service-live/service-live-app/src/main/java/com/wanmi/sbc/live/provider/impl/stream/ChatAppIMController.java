package com.wanmi.sbc.live.provider.impl.stream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.live.api.provider.stream.ChatAppIMProvider;
import com.wanmi.sbc.live.api.request.stream.IMAppRequest;
import com.wanmi.sbc.live.api.response.stream.IMAppResponse;
import com.wanmi.sbc.live.chat.service.ChatAppService;
import com.wanmi.sbc.live.redis.RedisService;
import com.wanmi.sbc.live.util.TencentImUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * <p>直播服务接口实现</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@RestController
@Validated
public class ChatAppIMController implements ChatAppIMProvider {
    @Autowired
    private ChatAppService appService;
    @Override
    public BaseResponse<IMAppResponse> getUserSig(@RequestBody IMAppRequest appRequest) {
        String userSig=appService.getUserSig(appRequest.getUserId());
        IMAppResponse imAppResponse=new IMAppResponse();
        imAppResponse.setUserSig(userSig);
        return BaseResponse.success(imAppResponse);
    }

}
