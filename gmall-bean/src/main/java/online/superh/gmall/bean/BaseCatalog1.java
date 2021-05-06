package online.superh.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @version: 1.0
 * @author: SuperH
 * @description: 一级分类
 * @date: 2021-04-23 13:09
 */
@Data
public class BaseCatalog1 implements Serializable {
    @Id
    @Column
    private String id;
    @Column
    private String name;
}
