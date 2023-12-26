package com.wanmi.sbc.live.provider.impl.host;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.host.LiveHostProvider;
import com.wanmi.sbc.live.api.request.host.*;
import com.wanmi.sbc.live.bean.vo.LiveHostVO;
import com.wanmi.sbc.live.host.model.root.LiveHost;
import com.wanmi.sbc.live.host.service.LiveHostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>主播服务接口实现</p>
 * @author 王冬明（1010331559@qq.com） Automatic Generator
 * @date 2022-09-19 11:37:24
 * @version 1.0
 * @package com.wanmi.sbc.live.provider.impl.host
 */
@RestController
@Validated
public class LiveHostController implements LiveHostProvider {

    @Autowired
    private LiveHostService liveHostService;

    @Override
    public BaseResponse add(@RequestBody LiveHostAddRequest addRequest) {
        liveHostService.add(addRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(@RequestBody LiveHostModifyRequest modifyRequest) {
        liveHostService.modify(modifyRequest);
        return BaseResponse.SUCCESSFUL();
    }


    @Override
    public BaseResponse delete(@RequestBody LiveHostDeleteRequest request) {
       int deleteFalg= liveHostService.delete(request);
       if(deleteFalg==11){
           return BaseResponse.error("请解绑当前绑定的直播账号");
       }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse enable(@RequestBody LiveHostEnableRequest request) {
        liveHostService.enable(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse leave(@RequestBody LiveHostLeaveRequest request) {
        liveHostService.leave(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse getInfo(Integer hostId) {
        LiveHost liveHost = liveHostService.getInfo(hostId);
        return BaseResponse.success(liveHost);
    }

    @Override
    public BaseResponse getInfoByCustomer(LiveHostInfoRequest request) {
        LiveHost liveHost = liveHostService.getInfo(request);
        return BaseResponse.success(liveHost);
    }

    @Override
    public BaseResponse<List<String>> getEnableCustomerAccountList() {
        List<String> customerAccountList = liveHostService.getEnableCustomerAccountList();
        return BaseResponse.success(customerAccountList);
    }

    @Override
    public BaseResponse getPage(LiveHostPageRequest pageRequest) {
        pageRequest.setPageNum((pageRequest.getPageNum())*pageRequest.getPageSize());
        Page<LiveHostVO> page = liveHostService.getPage(pageRequest);
        return BaseResponse.success(page);
    }
}