package com.wanmi.sbc.returnorder.trade.model.root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author lm
 * @date 2022/11/19 16:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "push_fail_log")
public class PushFailLog implements Serializable {


    @Id
    @Column(name = "tid")
    private String tid;

    @Column(name = "push_state")
    private String pushState;

    @Column(name = "order_time")
    private String orderTime;


    @Column(name = "create_time")
    private String createTime;

    @Column(name = "fail_reason")
    private String failReason;

    @Column(name = "retry_count")
    private Integer retryCount;

}
