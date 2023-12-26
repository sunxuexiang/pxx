package com.wanmi.sbc.goods.goodslabel.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;

import javax.persistence.*;

/**
 * <p>导航配置实体类</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@Data
@Entity
@Table(name = "goods_label")
public class GoodsLabel extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 标签id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 标签名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 商品列表展示开关 0: 关闭 1:开启
	 */
	@Column(name = "visible")
	@Enumerated
	private DefaultFlag visible;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Integer sort;

	/**
	 * 标签图片
	 */
	@Column(name = "image")
	private String image;


	/**
	 * 删除标识 0:未删除1:已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}