package com.wanmi.sbc.live.room.model.root;

import com.wanmi.sbc.live.base.BasePageEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LiveRoom extends BasePageEntity {

 	private static final long serialVersionUID = 5989165149145471856L;

	/**'平台标志,平台**/
	public static final Long SYSFLAG_YES = Long.valueOf(0);
	/**'平台标志,非平台**/
	public static final Long SYSFLAG_NO = Long.valueOf(1);


	/**'直播间id**/
	private Long liveRoomId;

	/**'直播间名称**/
	private String liveRoomName;
	
	/** 直播间图片 **/
	private String imgPath;

	/**'平台标志，0：平台，1：非平台**/
	private Long sysFlag;

	/**'厂商id**/
	private Long companyId;



	/**'删除标识,0:未删除1:已删除 **/
	private Long delFlag;

	/** 店铺ID */
	private Long storeId;

	/** 直播间类型：0：非自营直播间；1、自营直播间；2：平台直播间 */
	private Integer roomType;


	public LiveRoom(){}


}