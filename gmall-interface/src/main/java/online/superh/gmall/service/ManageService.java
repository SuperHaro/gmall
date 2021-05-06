package online.superh.gmall.service;

import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import online.superh.gmall.bean.*;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description: 后台管理接口
 * @date: 2021-04-23 13:20
 */
public interface ManageService {
    /**
     * 根据spuId获取所有spu图片
     * @return
     */
    List<SpuImage> getspuImageList(SpuImage spuImage);
    /**
     * 获取所有的一级分类
     * @return
     */
    List<BaseCatalog1> getCatalog1();

    /**
     * 获取所有的二级分类
     * @param catalog1Id
     * @return
     */
    List<BaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 获取所有的三级分类
     * @param catalog2Id
     * @return
     */
    List<BaseCatalog3> getCatalog3(String catalog2Id);

    /**
     * 根据三级分类获取平台属性
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfo(String catalog3Id);

    /**
     * 根据平台属性ID获取平台属性值
     * @param attrInfoId
     * @return
     */
    List<BaseAttrValue> getAttrValue(String attrInfoId);

    /**
     * 添加平台属性
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    /**
     * 根据平台属性Id获取平台属性
     * @param attrInfoId
     */
    BaseAttrInfo getAttrInfoById(String attrInfoId);

    /**
     * 获取SpuInfo
     * @param spuInfo
     * @return
     */
    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    /**
     * 获取销售属性
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存spuInfo
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 获取销售属性
     * @param spuSaleAttr
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuSaleAttr);

    /**
     * 保存sku
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 根据skuid获取skuinfo
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(String skuId);

    /**
     * 获取销售属性回显并锁定
     * @param skuInfo
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrlistChecked(SkuInfo skuInfo);

    /**
     * 根据spuId查询销售属性值得集合
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> getSpuSaleAttrValuelistBySpu(String spuId);

    /**
     * 根据平台属性值Id
     * @param attrValueIdList
     * @return
     */
    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
