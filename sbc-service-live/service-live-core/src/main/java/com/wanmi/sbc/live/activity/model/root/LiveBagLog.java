package com.wanmi.sbc.live.activity.model.root;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tools.49db.cn
 * @version 1.0
 * @date 2022-09-20
 */
@Data
@ToString(callSuper = true)
public class LiveBagLog implements Serializable {
    private static final long serialVersionUID = 1791727470875246381L;
    private Integer liveBagLogId;

    /**
     * 直播记录id
     */
    private Integer liveId;

    /**
     * 福袋id
     */
    private Integer bagId;

    /**
     * 中奖用户id
     */
    private String customerIds;

    /**
     * 中奖用户账号
     */
    private String customerAccounts;

    /**
     * 发放状态
     */
    private  Integer ticketStatus;

    /**
     * 福袋参与人数
     */
    private Integer joinNum;

    /**
     * 福袋发放时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date ticketTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

}