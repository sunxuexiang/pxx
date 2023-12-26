package com.wanmi.sbc.setting.videomanagement.model.root;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>视频关注表实体类</p>
 * @author 江舟
 * @date 2021-08-06 17:47:22
 */
@Data
@Entity
@IdClass(VideoFollow.class)
@Table(name = "video_follow")
public class VideoFollow implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;
    /**
     * 关注人id
     */
    @Column(name = "follow_customer_id")
    private String followCustomerId;
    /**
     * 被关注人id
     */
    @Column(name = "cover_follow_customer_id")
    private String coverFollowCustomerId;

    /**
     * 关注时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "store_id")
    private Long storeId;

//    @JoinColumn(name = "cover_follow_customer_id", insertable = false, updatable = false)
//    @OneToMany(cascade=CascadeType.ALL)
//    @JoinColumn(name="cover_follow_customer_id")//注释的是另一个表指向本表的外键。
//    @JsonManagedReference
//    private List<VideoManagement> videoManagement;
}
