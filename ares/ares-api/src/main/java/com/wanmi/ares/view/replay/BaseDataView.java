package com.wanmi.ares.view.replay;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseDataView implements Serializable {

    private String wareHouse;

    private Long wareId;

    private String wareCode;
}
