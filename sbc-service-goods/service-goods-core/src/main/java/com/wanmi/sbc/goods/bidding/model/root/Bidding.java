package com.wanmi.sbc.goods.bidding.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.enums.ActivityStatus;
import com.wanmi.sbc.goods.bean.enums.BiddingType;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * <p>竞价配置实体类</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@Data
@Entity
@Table(name = "bidding")
public class Bidding {
	private static final long serialVersionUID = 1L;

	/**
	 * 竞价配置主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "bidding_id")
	private String biddingId;

	/**
	 * 关键字,分类
	 */
	@Column(name = "keywords")
	private String keywords;

	/**
	 * 竞价类型0:关键字，1:分类
	 */
	@Column(name = "bidding_type")
	private BiddingType biddingType;

	/**
	 * 竞价的状态：0:未开始，1:进行中，2:已结束
	 */
	@Column(name = "bidding_status")
	private ActivityStatus biddingStatus;

	/**
	 * 开始时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "start_time")
	private LocalDateTime startTime;

	/**
	 * 修改时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "modify_time")
	private LocalDateTime modifyTime;

	/**
	 * 结束时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "end_time")
	private LocalDateTime endTime;

	/**
	 * 删除标志位
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}