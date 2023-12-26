package com.wanmi.sbc.setting.videomanagement.model.root;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>视频搜索记录</p>
 * @author zzg
 * @date 2023-09-17 17:47:22
 */
@Data
@Entity
@Table(name = "video_search_record")
public class VideoSearchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    /**
     * 客户ID
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 搜索类容
     */
    @Column(name = "content")
    private String content;

    /**
     * 搜索时间
     */
    @Column(name = "search_time")
    private Long searchTime;
}
