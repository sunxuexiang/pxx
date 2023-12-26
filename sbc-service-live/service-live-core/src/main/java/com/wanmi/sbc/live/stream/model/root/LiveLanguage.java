package com.wanmi.sbc.live.stream.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LiveLanguage extends BaseEntity {

    private Long languageId;

    private String message;

}
