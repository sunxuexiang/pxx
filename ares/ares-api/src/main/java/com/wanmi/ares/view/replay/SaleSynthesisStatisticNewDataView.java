package com.wanmi.ares.view.replay;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@Data
public class SaleSynthesisStatisticNewDataView implements Serializable {

    private static final long serialVersionUID = -4811117041123573195L;

    private List<SaleSynthesisNewDataView> saleSynthesisDataViewList;
}
