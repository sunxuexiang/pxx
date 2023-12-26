package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-20 10:23
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class UserProfileItemDO {
    private String toAccount;
    private List<ProfileItemDO> profileItem;
    private int resultCode;
    private String resultInfo;
}
