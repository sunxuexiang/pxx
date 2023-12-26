package com.wanmi.sbc.live.rule.model.root;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString(callSuper = true)
public class LiveRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer liveRoomId;
        /**
     * 1 在线人数 2点赞数量
     */
         private Integer type;

        /**
     * 规则起始人数
     */
         private Integer beginNum;
         /**
     * 增长系数以,隔开
     */
         private String coefficient;

        /**
     * 固定增加数量
     */
         private Integer fixed;

    private Date createTime;

    private Date updateTime;



}
