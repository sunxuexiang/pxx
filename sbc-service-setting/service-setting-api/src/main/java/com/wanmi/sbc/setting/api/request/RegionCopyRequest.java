package com.wanmi.sbc.setting.api.request;

import com.wanmi.sbc.setting.bean.vo.RegionCopyVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RegionCopyRequest implements Serializable {

    private List<RegionCopyVO> regionCopyVOList;
}
