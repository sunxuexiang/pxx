package com.wanmi.sbc.goods.icitem.model.root;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>配送到家实体类</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@Data
@Entity
@Table(name = "icitem")
@EntityListeners(AuditingEntityListener.class)
public class Icitem implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * sku
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "sku")
	private String sku;

	/**
	 * name
	 */
	@Column(name = "name")
	private String name;

	/**
	 * tiji
	 */
	@Column(name = "tiji")
	private BigDecimal tiji;

	/**
	 * weight
	 */
	@Column(name = "weight")
	private BigDecimal weight;

}