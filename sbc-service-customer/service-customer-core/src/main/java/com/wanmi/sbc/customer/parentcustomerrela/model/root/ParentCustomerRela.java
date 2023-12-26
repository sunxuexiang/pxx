package com.wanmi.sbc.customer.parentcustomerrela.model.root;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author baijianzhong
 * @ClassName ParentCustomerRela
 * @Date 2020-05-26 15:31
 * @Description TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parent_customer_rela")
public class ParentCustomerRela implements Serializable {

	/**
	 * 子账号Id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "com.wanmi.sbc.customer.util.CustomerUUIDGenerator")
	@Column(name = "customer_id")
	private String customerId;

	/**
	 * 主账号Id
	 */
	@Column(name = "parent_id")
	private String parentId;

}
