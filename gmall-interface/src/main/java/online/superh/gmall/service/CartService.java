package online.superh.gmall.service;

import online.superh.gmall.bean.CartInfo;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-05-02 14:49
 */
public interface CartService {
    public  void  addToCart(String skuId,String userId,Integer skuNum);

    List<CartInfo> getCartList(String userId);

    List<CartInfo> mergeToCartList(List<CartInfo> cartInfosck, String userId);

    void checkCart(String skuId, String isChecked, String  userId);

    List<CartInfo> getCartListCk(String userId);
}
