package online.superh.gmall.user.controller;

import online.superh.gmall.bean.UserInfo;
import online.superh.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-21 23:00
 */
@RestController
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;
    @GetMapping("findAll")
    public List<UserInfo> findAll(){
        return userInfoService.findAll();

    }
}
