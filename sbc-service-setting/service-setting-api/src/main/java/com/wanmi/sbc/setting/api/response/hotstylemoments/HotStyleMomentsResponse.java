package com.wanmi.sbc.setting.api.response.hotstylemoments;

import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 爆款时刻响应实体类
 * @author: XinJiang
 * @time: 2022/5/9 21:52
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
public class HotStyleMomentsResponse extends HotStyleMomentsVO {

    private static final long serialVersionUID = -3622949938761502475L;

}
