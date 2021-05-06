package online.superh.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import online.superh.gmall.bean.UserAddress;
import online.superh.gmall.bean.UserInfo;
import online.superh.gmall.config.RedisUtil;
import online.superh.gmall.service.UserInfoService;
import online.superh.gmall.user.mapper.UserAddressMapper;
import online.superh.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-21 22:57
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private RedisUtil redisUtil;
    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24;

    @Override
    public UserInfo verify(String useId) {
        Jedis jedis =null;

        try {
            jedis = redisUtil.getJedis();
            String key = userKey_prefix+useId+userinfoKey_suffix;
            String userInfoJson = jedis.get(key);
            jedis.expire(key,userKey_timeOut);
            if (!StringUtils.isEmpty(userInfoJson)){
                UserInfo userInfo = JSON.parseObject(userInfoJson, UserInfo.class);
                return userInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    @Override
    public List<UserInfo> findAll() {
      return userInfoMapper.selectAll();
    }

    /**
     * 根据userID查地址
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    /**
     * 用户登录
     *
     * @param userInfo
     * @return
     */
    @Override
    public UserInfo login(UserInfo userInfo) {
        String passwd = userInfo.getPasswd();
        String newpasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(newpasswd);
        UserInfo info = userInfoMapper.selectOne(userInfo);
        if(info!=null){
            Jedis jedis = redisUtil.getJedis();
            //起名字
            String userKey = userKey_prefix+info.getId()+userinfoKey_suffix;
            jedis.setex(userKey,userKey_timeOut, JSON.toJSONString(info));
            jedis.close();
            return info;
        }
        return null;
    }
}
