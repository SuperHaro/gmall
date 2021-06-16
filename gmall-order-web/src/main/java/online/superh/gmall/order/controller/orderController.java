package online.superh.gmall.order.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import online.superh.gmall.bean.CartInfo;
import online.superh.gmall.bean.OrderDetail;
import online.superh.gmall.bean.OrderInfo;
import online.superh.gmall.bean.UserAddress;
import online.superh.gmall.service.CartService;
import online.superh.gmall.service.OrderService;
import online.superh.gmall.service.UserInfoService;
import online.superh.gmall.util.LoginRequie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-22 13:13
 */
@Controller
public class orderController {
    @Reference
    private UserInfoService userInfoService;
    @Reference
    private CartService cartService;
    @Reference
    private OrderService orderService;
    @RequestMapping("submitOrder")
    @LoginRequie
    public String submitOrder(HttpServletRequest request,OrderInfo orderInfo){
        String userId = (String) request.getAttribute("userId");
        String tradeNo = request.getParameter("tradeNo");
        boolean b = orderService.checkTradeCode(userId,tradeNo);
        if(!b){
            request.setAttribute("errMsg","订单已提交，不能重复提交！！！");
            return "tradeFail";
        }
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            // 从订单中去购物skuId，数量
            boolean result = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!result){
                request.setAttribute("errMsg","商品库存不足，请重新下单！");
                return "tradeFail";
            }
        }
        orderInfo.setUserId(userId);
        String orderId = orderService.saveOrder(orderInfo);
        orderService.delTradeCode(userId);
        return "redirect://payment.gmall.com/index?orderId="+orderId;
    }
    @RequestMapping("orderSplit")
    @ResponseBody
    public String orderSplit(HttpServletRequest request){
        String orderId = request.getParameter("orderId");
        String wareSkuMap = request.getParameter("wareSkuMap");
        // 定义订单集合
        List<OrderInfo> subOrderInfoList = orderService.splitOrder(orderId,wareSkuMap);
        List<Map> wareMapList=new ArrayList<>();
        for (OrderInfo orderInfo : subOrderInfoList) {
            Map map = orderService.initWareOrder(orderInfo);
            wareMapList.add(map);
        }
        return JSON.toJSONString(wareMapList);
    }
    @RequestMapping("trade")
    @LoginRequie
    public String trade(HttpServletRequest request){
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        String userId = (String) request.getAttribute("userId");
        List<UserAddress> userAddressList = userInfoService.getUserAddressList(userId);
        List<CartInfo> cartList = cartService.getCartListCk(userId);
        for (CartInfo cartInfo : cartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetails.add(orderDetail);
        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetails);
        orderInfo.sumTotalAmount();
        request.setAttribute("orderDetails",orderDetails);
        request.setAttribute("userAddressList",userAddressList);
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo",tradeNo);
        return "trade";
    }
//    @GetMapping("toTrade")
//    @LoginRequie
//    public String toTrade(){
//        return "tradeFail";
//    }
}
