package online.superh.gmall.manage.mapper;


import online.superh.gmall.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue>{
    /**
     * 根据spuId查询销售属性值得集合
     *
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> selectSpuSaleAttrValuelistBySpu(String spuId);
}
