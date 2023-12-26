package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesOrderDataView extends BaseDataView implements Serializable {

    /*整箱批发*/
    private Long todayOrderCount;

    /*拆箱散批*/
    private Long totalSPOrderCount;
}
