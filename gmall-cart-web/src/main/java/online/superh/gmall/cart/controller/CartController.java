package online.superh.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import online.superh.gmall.bean.CartInfo;
import online.superh.gmall.bean.SkuInfo;
import online.superh.gmall.cart.component.CartCookieHandler;
import online.superh.gmall.service.CartService;
import online.superh.gmall.service.ManageService;
import online.superh.gmall.util.LoginRequie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-05-02 14:02
 */

@Controller
public class CartController {

    @Reference
    private CartService cartService;
    @Reference
    private ManageService manageService;
    @Autowired
    private CartCookieHandler cartCookieHandler;
    //toTrade
    @RequestMapping("toTrade")
    @LoginRequie
    public String toTrade(HttpServletRequest request, HttpServletResponse response){
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cookieHandlerCartList = cartCookieHandler.getCartList(request);
        if (cookieHandlerCartList!=null && cookieHandlerCartList.size()>0){
            cartService.mergeToCartList(cookieHandlerCartList, userId);
            cartCookieHandler.deleteCookie(request,response);
        }
        return "redirect://order.gmall.com/trade";
    }
    @RequestMapping("checkCart")
    @LoginRequie
    @ResponseBody
    //isChecked
    //skuId
    public void checkCart(HttpServletRequest request,HttpServletResponse response){
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        String  userId = (String) request.getAttribute("userId");
        if(userId!=null){
            cartService.checkCart(skuId,isChecked,userId);
        }else {
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }
    // 如何区分用户是否登录？只需要看userId
    @RequestMapping("addToCart")
    @LoginRequie
    public String addToCart(HttpServletRequest request, HttpServletResponse response){
        // 获取商品数量
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");

        // 获取userId
        String userId = (String) request.getAttribute("userId");
        if (userId!=null){
            // 调用登录添加购物车
            cartService.addToCart(skuId,userId,Integer.parseInt(skuNum));
        }else{
            // 调用未登录添加购物车
            cartCookieHandler.addToCart(request,response,skuId,userId,Integer.parseInt(skuNum));
        }
        // 根据skuId 查询skuInfo
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        request.setAttribute("skuNum",skuNum);
        request.setAttribute("skuInfo",skuInfo);
        return "success";
    }
    @LoginRequie
    @RequestMapping("cartList")
    public  String cartList(HttpServletRequest request,HttpServletResponse response){
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<>();
        if (userId!=null){
            List<CartInfo> cartInfosck = cartCookieHandler.getCartList(request);
            if(cartInfosck!=null&&cartInfosck.size()>0){
                cartInfos=cartService.mergeToCartList(cartInfosck,userId);
                cartCookieHandler.deleteCookie(request,response);
            }else {
                cartInfos = cartService.getCartList(userId);
            }
        }else{
            cartInfos = cartCookieHandler.getCartList(request);

        }
        request.setAttribute("cartInfos",cartInfos);
        return "cartList";
    }
}
