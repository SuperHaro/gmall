package online.superh.gmall.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-29 14:53
 */
@Data
public class SkuLsParams implements Serializable {

    String  keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo=1;

    int pageSize=20;
}
