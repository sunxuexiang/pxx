package com.wanmi.sbc.setting.provider.impl.headline;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.headline.HeadLineProvider;
import com.wanmi.sbc.setting.api.request.headline.HeadLineSaveRequest;
import com.wanmi.sbc.setting.api.response.headline.HeadLineResponse;
import com.wanmi.sbc.setting.headline.model.root.HeadlineConfig;
import com.wanmi.sbc.setting.headline.service.HeadlineConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/7 15:19
 */
@RestController
@Validated
public class HeadLineController implements HeadLineProvider {

    @Autowired
    private HeadlineConfigService headlineConfigService;
    @Override
    public BaseResponse save(@RequestBody @Valid List<HeadLineSaveRequest> request) {
        headlineConfigService.save(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<HeadLineResponse>> get() {
        List<HeadlineConfig> allHeadline = headlineConfigService.findAllHeadline();
        List<HeadLineResponse> headLineResponses = KsBeanUtil.convertList(allHeadline, HeadLineResponse.class);
        int size = headLineResponses.size();
        BigDecimal totalSeconds = getTotalSeconds(size, headLineResponses);
        for (HeadLineResponse response : headLineResponses) {
            response.setTotalSeconds(totalSeconds);
        }
        return BaseResponse.success(headLineResponses);
    }

    private static BigDecimal getTotalSeconds(int size, List<HeadLineResponse> headLineResponses) {
        BigDecimal totalSeconds = new BigDecimal("0.000");
        for (int i = 0; i < size; i++) {
            HeadLineResponse headLineResponse = headLineResponses.get(i);
            int length = headLineResponse.getContent().length();
            // 判断是否为最后一个元素
            if (i == size - 1) {
                totalSeconds = totalSeconds.add(headLineResponse.getSpeeds().multiply(new BigDecimal(length)));
            } else {
                totalSeconds = totalSeconds.add(headLineResponse.getSpeeds().multiply(new BigDecimal((length + 1))));
            }
        }
        return totalSeconds;
    }

}
