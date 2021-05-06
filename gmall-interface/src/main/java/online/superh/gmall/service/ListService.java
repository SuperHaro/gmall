package online.superh.gmall.service;

import online.superh.gmall.bean.SkuLsInfo;
import online.superh.gmall.bean.SkuLsParams;
import online.superh.gmall.bean.SkuLsResult;

import java.io.IOException;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-29 12:58
 */
public interface ListService {
    /**
     *@Description: 保存数据到es中
     *@Creator: SuperH
     *@Date: 2021/4/29 13:00
     *@param skuLsInfo
     *@return: void
     *@throws:
     */
    void saveSkuLsInfo(SkuLsInfo skuLsInfo);

    /**
     * 根据用户输入的条件查询商品
     * @param skuLsParams
     * @return
     */
    SkuLsResult search(SkuLsParams skuLsParams) throws IOException;

    /**
     * 记录每个商品被访问的次数
     * @param skuId
     */
    void incrHotScore(String skuId);
}
