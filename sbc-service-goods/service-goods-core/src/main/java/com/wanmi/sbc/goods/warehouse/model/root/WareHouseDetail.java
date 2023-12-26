package com.wanmi.sbc.goods.warehouse.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import lombok.Data;

import javax.persistence.*;

/**
 * <p>仓库表实体类</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Data
@Entity
@Table(name = "ware_house_detail")
public class WareHouseDetail extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * wareId
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;


	@Column(name = "ware_id")
	private Long wareId;


	/**
	 * 仓库名称
	 */
	@Column(name = "ware_name")
	private String wareName;



	/**
	 * 详细地址
	 */
	@Column(name = "ware_player_img")
	private String warePlayerImg;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;



}