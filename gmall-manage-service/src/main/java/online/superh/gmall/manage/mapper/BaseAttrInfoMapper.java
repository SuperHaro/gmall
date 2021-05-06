package online.superh.gmall.manage.mapper;

import online.superh.gmall.bean.BaseAttrInfo;
import online.superh.gmall.bean.BaseCatalog1;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-23 13:15
 */
public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo>{
    /**
     * 多表联查查询AttrInfoList
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(String catalog3Id);

    List<BaseAttrInfo> getAttrInfoListBy(@Param("valueIds") String valueIds);
}
