package com.wanmi.sbc.goods.freight.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>配送到家范围实体类</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@Data
@Entity
@Table(name = "freight_template_delivery_area")
public class FreightTemplateDeliveryArea implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键标识
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 配送地id(逗号分隔)
	 */
	@Column(name = "destination_area")
	private String destinationArea;

	/**
	 * 配送地名称(逗号分隔)
	 */
	@Column(name = "destination_area_name")
	private String destinationAreaName;

	/**
	 * 店铺标识
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 公司信息ID
	 */
	@Column(name = "company_info_id")
	private Long companyInfoId;

	/**
	 * 免费店配类型（0常规，1乡镇满十件免配送区域） 2 第三方物流
	 */
	@Column(name = "destination_type")
	@Enumerated
	private freightTemplateDeliveryType destinationType;

	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 是否删除(0:否,1:是)
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	/**
	 * 仓库id
	 */
	@Column(name = "ware_id")
	private Long wareId;

	/**
	 * 免运费起始数量
	 */
	@Column(name = "freight_free_number")
	private Long freightFreeNumber;

	/**
	 * 是否启用(0:否,1:是)
	 */
	@Column(name = "open_flag")
	private Integer openFlag;

	/**
	 * @desc  自定义配置
	 * @author shiy  2023/9/22 10:17
	*/
	@Column(name = "custom_cfg")
	private String customCfg;
}