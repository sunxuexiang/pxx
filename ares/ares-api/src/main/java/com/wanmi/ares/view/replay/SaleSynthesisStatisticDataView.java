package com.wanmi.ares.view.replay;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SaleSynthesisStatisticDataView implements Serializable {

    private List<SaleSynthesisDataView> saleSynthesisDataViewList;
}
