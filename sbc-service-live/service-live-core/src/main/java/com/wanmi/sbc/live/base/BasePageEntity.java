package com.wanmi.sbc.live.base;

import com.github.pagehelper.Page;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.Data;

@Data
public class BasePageEntity extends BaseEntity {

    BaseQueryRequest page = new BaseQueryRequest();
}
