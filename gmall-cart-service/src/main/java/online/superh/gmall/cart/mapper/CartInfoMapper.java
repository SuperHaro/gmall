package online.superh.gmall.cart.mapper;

import online.superh.gmall.bean.CartInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-05-02 14:43
 */
public interface CartInfoMapper extends Mapper<CartInfo> {
    /**
     * 根据用户Id查询实时价格到CartInfo
     * @param userId
     * @return
     */
    List<CartInfo> selectCartListWithCurPrice(String userId);

}
