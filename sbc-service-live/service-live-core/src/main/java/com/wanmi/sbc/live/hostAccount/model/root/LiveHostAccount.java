package com.wanmi.sbc.live.hostAccount.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LiveHostAccount extends BaseEntity {

 	private static final long serialVersionUID = 3002494589752708418L;

	 private Integer hostAccountId;

	/**主播ID**/
	private Integer hostId;

	/**直播账号id**/
	private String customerId;

	/**直播账号**/
	private String customerAccount;

	/**删除标识,0:未删除1:已删除 **/
	private Integer delFlag;

	/**数据唯一编号**/
	private String dataSn;

	public LiveHostAccount(){}

}