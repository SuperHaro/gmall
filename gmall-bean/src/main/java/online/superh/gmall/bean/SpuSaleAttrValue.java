package online.superh.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-24 17:56
 */
@Data
public class SpuSaleAttrValue implements Serializable {
    @Id
    @Column
    String id ;

    @Column
    String spuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrValueName;
    //当前属性值是否被选中
    @Transient
    String isChecked;
}
