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
public class SalesUserDataView extends BaseDataView implements Serializable {

    /*整箱批发用户数*/
    private Long userCount;

    /*散批用户*/
    private Long userSPCount;
}
