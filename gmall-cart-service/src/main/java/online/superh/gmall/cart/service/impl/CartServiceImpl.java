package online.superh.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.superh.gmall.bean.CartInfo;
import online.superh.gmall.bean.SkuInfo;
import online.superh.gmall.cart.constant.CartConst;
import online.superh.gmall.cart.mapper.CartInfoMapper;
import online.superh.gmall.config.RedisUtil;
import online.superh.gmall.service.CartService;
import online.superh.gmall.service.ManageService;
import org.elasticsearch.search.aggregations.metrics.percentiles.tdigest.InternalTDigestPercentileRanks;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-05-02 14:50
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CartInfoMapper cartInfoMapper;
    @Reference
    ManageService manageService;
    /**
     * 登录时添加购物车
     * @param skuId
     * @param userId
     * @param skuNum
     */
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        log.info("加入购物车");
        //先查询购物车有没有相同的商品，有则数量增加
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userId);
        CartInfo info = cartInfoMapper.selectOne(cartInfo);
        if(info!=null){
            info.setSkuNum(info.getSkuNum()+skuNum);
            //skuprice初始化操作
            info.setSkuPrice(info.getCartPrice());
            //更新数据
            cartInfoMapper.updateByPrimaryKeySelective(info);
            //同步缓存
        }else {
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuNum(skuNum);
            cartInfoMapper.insertSelective(cartInfo);
            //同步缓存
//            info = cartInfo;
        }
        loadCartCache(userId);
//        Jedis jedis = redisUtil.getJedis();
//        //定义key
//        String cartkey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
//        jedis.hset(cartkey,skuId, JSON.toJSONString(info));
//        jedis.close();
    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        String cartkey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals(cartkey);
        List<CartInfo> cartInfoList = new ArrayList<>();
        jedis.close();
        if (hvals!=null&&hvals.size()>0){
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            //排序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return Integer.parseInt(o1.getId()) - Integer.parseInt(o2.getId());
                }
            });
            return cartInfoList;
        }else {
            cartInfoList =loadCartCache(userId);
            return cartInfoList;
        }
    }

    /**
     * 合并购物车
     * @param cartInfosck
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartInfosck, String userId) {
        List<CartInfo> cartInfoList= cartInfoMapper.selectCartListWithCurPrice(userId);
        for (CartInfo cartInfock : cartInfosck) {
            boolean isMatch = false;
            for (CartInfo cartInfo : cartInfoList) {
                if(cartInfo.getSkuId().equals(cartInfock.getSkuId())){
                    cartInfo.setSkuNum(cartInfo.getSkuNum()+cartInfock.getSkuNum());
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfo);
                    isMatch=true;
                    break;
                }
            }
            if(!isMatch){
                cartInfock.setUserId(userId);
               cartInfoMapper.insertSelective(cartInfock);
            }
        }
        // 从新在数据库中查询并返回数据
        List<CartInfo> cartInfoList1 = loadCartCache(userId);

        for (CartInfo cartInfo : cartInfoList1) {
            for (CartInfo info : cartInfosck) {
                if (cartInfo.getSkuId().equals(info.getSkuId())){
                    // 只有被勾选的才会进行更改
                    if (info.getIsChecked().equals("1")){
                        cartInfo.setIsChecked(info.getIsChecked());
                        // 更新redis中的isChecked
                        checkCart(cartInfo.getSkuId(),info.getIsChecked(),userId);
                    }
                }
            }
        }
        return cartInfoList1;
    }

    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        Jedis jedis = redisUtil.getJedis();
        String cartkey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        String cartInfoJson = jedis.hget(cartkey, skuId);
        CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
        cartInfo.setIsChecked(isChecked);
        jedis.hset(cartkey,skuId,JSON.toJSONString(cartInfo));
        String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        if (isChecked.equals("1")){
            jedis.hset(userCheckedKey,skuId,JSON.toJSONString(cartInfo));
        }else{
            jedis.hdel(userCheckedKey,skuId);
        }
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartListCk(String userId) {
        ArrayList<CartInfo> cartInfos = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        List<String> hvals = jedis.hvals(userCheckedKey);
        if(hvals!=null&&hvals.size()>0){
            for (String hval : hvals) {
                cartInfos.add(JSON.parseObject(hval,CartInfo.class));
            }
        }
        jedis.close();
        return cartInfos;
    }

    private List<CartInfo> loadCartCache(String userId) {
        String cartkey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        List<CartInfo> cartInfoList= cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList==null||cartInfoList.size()==0){
            return null;
        }
        for (CartInfo cartInfo : cartInfoList) {
            jedis.hset(cartkey,cartInfo.getSkuId(),JSON.toJSONString(cartInfo));
        }
        jedis.close();
        return cartInfoList;
    }
}
