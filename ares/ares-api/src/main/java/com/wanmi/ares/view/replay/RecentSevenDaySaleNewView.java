package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentSevenDaySaleNewView implements Serializable {

    private static final long serialVersionUID = -9094112446817646245L;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     *  省份名称
     */
    private String provinceName;

    private String dayTime;

    private Long totalNum;
}
