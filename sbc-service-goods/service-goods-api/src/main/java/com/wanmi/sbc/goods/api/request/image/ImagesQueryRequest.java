package com.wanmi.sbc.goods.api.request.image;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagesQueryRequest {
	private static final long serialVersionUID = 1L;

	List<String> goodsIds;
}