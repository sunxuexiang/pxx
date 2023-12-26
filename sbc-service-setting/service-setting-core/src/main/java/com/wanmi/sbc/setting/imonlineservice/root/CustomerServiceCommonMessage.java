package com.wanmi.sbc.setting.imonlineservice.root;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p>客服快捷回复常用语表</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Data
@Entity
@Table(name = "customer_service_common_message")
public class CustomerServiceCommonMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id")
    private Long msgId;

    /**
     * 公司ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 消息内容
     */
    @Column(name = "message")
    private String message;

    /**
     * 排序，升序
     */
    @Column(name = "sort_num")
    private Integer sortNum;

    /**
     * 一级分组ID
     */
    @Column(name = "one_group_id")
    private Long oneGroupId;

    /**
     * 二级分组ID
     */
    @Column(name = "second_group_id")
    private Long secondGroupId;

    /**
     * 删除时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
