package com.wanmi.sbc.goods.bean.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc  
 * @author shiy  2023/10/10 15:06
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliverWayFrontNeed implements Serializable{
    private String name;
    private List<item> items = new ArrayList<>(2);
    private Boolean didsabled =false;
    private String type;

    public String getType() {
       return this.items.size() > 1 ? "single" : "normal";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class item implements Serializable{
        private Integer id;
        private String  name;
    }
}
