package com.wanmi.sbc.goods.biddinggoods.model.root;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * <p>竞价商品实体类</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@Data
@Entity
@Table(name = "bidding_goods")
public class BiddingGoods {
	private static final long serialVersionUID = 1L;

	/**
	 * 竞价商品的Id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "bidding_goods_id")
	private String biddingGoodsId;

	/**
	 * 竞价的Id
	 */
	@Column(name = "bidding_id")
	private String biddingId;

	/**
	 * 排名
	 */
	@Column(name = "sort")
	private Integer sort;

	/**
	 * skuId
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * SKU信息
	 */
	@OneToOne
	@JoinColumn(name = "goods_info_id", insertable = false, updatable = false)
	private GoodsInfo goodsInfo;

	/**
	 * 修改时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "modify_time")
	private LocalDateTime modifyTime;

	/**
	 * 删除时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "del_time")
	private LocalDateTime delTime;

	/**
	 * 删除标志
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}