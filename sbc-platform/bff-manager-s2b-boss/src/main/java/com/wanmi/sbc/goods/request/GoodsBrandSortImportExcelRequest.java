package com.wanmi.sbc.goods.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yang
 * @since 2020/12/31
 */
@Data
public class GoodsBrandSortImportExcelRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String ext;

    /**
     * 操作员id
     */
    private String userId;
}
