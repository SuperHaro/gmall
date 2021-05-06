package online.superh.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @version: 1.0
 * @author: SuperH
 * @description: 平台属性值
 * @date: 2021-04-23 13:13
 */
@Data
public class BaseAttrValue implements Serializable {
    @Id
    @Column
    private String id;
    @Column
    private String valueName;
    @Column
    private String attrId;
    @Transient
    private String urlParam;
}
