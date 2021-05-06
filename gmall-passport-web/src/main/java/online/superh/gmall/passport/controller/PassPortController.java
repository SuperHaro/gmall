package online.superh.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import online.superh.gmall.bean.UserInfo;
import online.superh.gmall.passport.util.JwtUtil;
import online.superh.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-30 17:30
 */
@Controller
public class PassPortController {
    @Reference
    private UserInfoService userInfoService;
    @Value("${token.key}")
    private String key;
    @RequestMapping("index")
    public String index(HttpServletRequest request){
        //获取url
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo,HttpServletRequest request){
        UserInfo info = userInfoService.login(userInfo);
        if(info!=null){
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId",info.getId());
            map.put("nickName",info.getNickName());
            String header = request.getHeader("X-forwarded-for");
            String token = JwtUtil.encode(key, map, header);
            return token;
        }else {
            return "fail";
        }
    }
    @RequestMapping("verify")
    @ResponseBody
    public  String verify(HttpServletRequest request){
        String salt = request.getHeader("X-forwarded-for");
        String token = request.getParameter("token");
        Map<String, Object> map = JwtUtil.decode(token, key, salt);
        if(map!=null&&map.size()>0){
            String userId = (String) map.get("userId");
            UserInfo userInfo = userInfoService.verify(userId);
            if (userInfo!=null){
                return "success";
            }else {
                return "fail";
            }
        }
        return "fail";
    }
}
