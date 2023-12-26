package com.wanmi.sbc.order.trade.model.root;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
