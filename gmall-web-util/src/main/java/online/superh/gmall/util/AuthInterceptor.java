package online.superh.gmall.util;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import online.superh.gmall.commom.util.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static online.superh.gmall.util.WebConst.COOKIE_MAXAGE;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-05-01 12:24
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    //多个拦截器执行顺序，先进后后出
    //进入控制器前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("newToken");
        //把token保存到cookie
        if(token!=null){
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
        }
        if(token==null){
            token = CookieUtil.getCookieValue(request, "token", false);
        }

        if(token!=null) {
            //读取token
            Map<String,Object> map = getUserMapByToken(token);
            String nickName = (String)map.get("nickName");
            request.setAttribute("nickName", nickName);

        }
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        LoginRequie methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequie.class);
        if(methodAnnotation!=null){
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token);
            if ("success".equals(result)){
                Map<String,Object> map = getUserMapByToken(token);
                String userId = (String)map.get("userId");
                request.setAttribute("userId",userId);
                return true;
            }else {
                if(methodAnnotation.autoRedirect()){
                    String  requestURL = request.getRequestURL().toString();
                    String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
                    response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
                    return false;
                }
            }
        }
        return true;
    }

    private Map getUserMapByToken(String token) {
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] tokenBytes = base64UrlCodec.decode(tokenUserInfo);
        String tokenJson = null;
        try {
            tokenJson = new String(tokenBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String,Object> map = JSON.parseObject(tokenJson, Map.class);
        return map;
    }

    //进入控制器后，试图渲染前
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    //试图渲染前后
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
