package com.wanmi.sbc.live.api.response;

import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@NoArgsConstructor
public class LiveBasePageResponse<E>  implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<E> content;
    public boolean first; // required
    public boolean last; // required
    public int number; // required
    public int numberOfElements; // required
    public int size; // required
    public int totalElements; // required
    public int totalPages; // required
}
