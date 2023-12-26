package com.wanmi.sbc.setting.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统配置实体类
 * Created by dyt on 2017/4/23.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintConfigVO implements Serializable {

    /**
     * 编号
     */
    private Long printId;


    /**
     * 打印头部
     */
    private String printHead;


    /**
     * 打印尾部
     */
    private String printBottom;

}
