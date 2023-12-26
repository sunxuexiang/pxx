package com.wanmi.sbc.advertising.api.request.slot;

import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
@Builder
@NoArgsConstructor
public class SlotSaveRequest extends AdSlotDTO {


}
