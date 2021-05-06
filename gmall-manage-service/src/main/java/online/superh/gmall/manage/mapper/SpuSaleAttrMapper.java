package online.superh.gmall.manage.mapper;

import online.superh.gmall.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-24 18:12
 */
public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    /**
     * 根据spuId查询销售属性集合
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

    /**
     * 根据skuId 和spuId获取销售属性的集合
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrlistChecked(String skuId, String spuId);
}
