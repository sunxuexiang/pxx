package com.wanmi.sbc.customer.invitationstatistics.model.root;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * <p>邀新统计实体类ID</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class InvitationStatisticsId implements Serializable {

	private String employeeId;


	private String data;
}