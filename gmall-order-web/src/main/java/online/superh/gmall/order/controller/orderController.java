package online.superh.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import online.superh.gmall.bean.UserAddress;
import online.superh.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-22 13:13
 */
@Controller
public class orderController {
    @Reference
    UserInfoService userInfoService;
//    @GetMapping("trade")
//    public String trade(){
//        return "index";
//    }
    @GetMapping("trade")
    @ResponseBody
    public List<UserAddress> trade(String userId){
        return userInfoService.getUserAddressList(userId);
    }
}
