package com.wanmi.sbc.live.roomrela.model.root;

import com.wanmi.sbc.live.base.BasePageEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LiveRoomRela extends BasePageEntity {

 	private static final long serialVersionUID = 3791968282029077106L;

	/**'关联类型,品牌**/
 	public static final String RELATYPE_BRAND = "B";

	/**'关联类型,直播账号 **/
	public static final String RELATYPE_ACCOUNT = "A";

	/**'关联类型, 运营账号 **/
	public static final String RELATYPE_OPERATE = "O";

	/**'关联类型,  商品**/
//	public static final String RELATYPE_GOODS = "G";

	/**'关联类型, 福袋**/
//	public static final String RELATYPE_BAG = "BG";


	/**live_room_rela_id**/
	private Long liveRoomRelaId;

	/**'直播间id**/
	private Long liveRoomId;

	/**'关联类型,b:品牌,a:直播账号,o:运营账号,g:商品,b:福袋**/
	private String relaType;

	/**'关联id**/
	private String relaId;

	/** 关联内容 */
	private String relaContent;

	/**'状态**/
	private Long status;


	/**'删除标识,0:未删除1:已删除 **/
	private Long delFlag;


	public LiveRoomRela(){}

}