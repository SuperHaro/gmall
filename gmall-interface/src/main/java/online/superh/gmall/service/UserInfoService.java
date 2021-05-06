package online.superh.gmall.service;

import online.superh.gmall.bean.UserAddress;
import online.superh.gmall.bean.UserInfo;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-21 22:12
 */
public interface UserInfoService {
    UserInfo verify(String useId);

    /**
     * 查询所有数据
     * @return
     */
    List<UserInfo> findAll();

    /**
     * 根据userID查地址
     * @param userId
     * @return
     */
    List<UserAddress> getUserAddressList(String userId);

    /**
     * 用户登录
     * @param userInfo
     * @return
     */
    UserInfo login(UserInfo userInfo);
}
