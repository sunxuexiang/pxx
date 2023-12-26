package com.wanmi.sbc.live.bean.vo;

import lombok.Data;

@Data
public class LiveStreamPageVO {
    public java.util.List<LiveStreamVO> content;
    public boolean first; // required
    public boolean last; // required
    public int number; // required
    public int numberOfElements; // required
    public int size; // required
    public int totalElements; // required
    public int totalPages; // required
}
