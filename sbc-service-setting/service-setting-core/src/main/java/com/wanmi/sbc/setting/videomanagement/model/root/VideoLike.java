package com.wanmi.sbc.setting.videomanagement.model.root;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wanmi.sbc.setting.expresscompany.model.root.ExpressCompany;
import lombok.Data;

import javax.persistence.*;

import com.wanmi.sbc.common.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>视频管理实体类</p>
 *
 * @author zhaowei
 * @date 2021-04-19 14:24:26
 */
@Data
@Entity
@IdClass(VideoLike.class)
@Table(name = "video_like")
public class VideoLike implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 视频id
     */
    @Id
    @Column(name = "video_id")
    private Long videoId;

    /**
     * 用户id
     */
    @Id
    @Column(name = "customer_id")
    private String customerId;


    @JoinColumn(name = "video_id", insertable = false, updatable = false)
	@OneToOne
    @JsonManagedReference
	private VideoManagement videoManagement;

}