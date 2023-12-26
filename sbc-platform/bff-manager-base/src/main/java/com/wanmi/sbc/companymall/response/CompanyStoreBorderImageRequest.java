package com.wanmi.sbc.companymall.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-03 14:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStoreBorderImageRequest implements Serializable {
    private static final long serialVersionUID = -8924672215720178515L;

    private List<String> images;
}
