package online.superh.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @version: 1.0
 * @author: SuperH
 * @description: 实体类
 * @date: 2021-04-20 20:29
 */
@Data
public class UserInfo implements Serializable {
    //通用mapper注解
    @Id//表示主键
    @Column//普通字段
    @GeneratedValue(strategy = GenerationType.IDENTITY)//获取数据库主键自增
    private String id;
    @Column
    private String loginName;
    @Column
    private String nickName;
    @Column
    private String passwd;
    @Column
    private String name;
    @Column
    private String phoneNum;
    @Column
    private String email;
    @Column
    private String headImg;
    @Column
    private String userLevel;

}
