package com.wanmi.sbc.goods.catebrandsortrel.model.root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * @author lvheng
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandRelId   implements Serializable  {


    private Long cateId;

    private Long brandId;
}
