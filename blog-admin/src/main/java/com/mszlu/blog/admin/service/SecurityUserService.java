package com.mszlu.blog.admin.service;

import com.mszlu.blog.admin.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@SuppressWarnings({"all"})
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Override//登录认证功能
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //当你登录的时候，会把username 传递到这里
        //通过username 查询 admin 表，如果admin存在 将密码 告诉spring security
        // 如果不存在 返回null 认证失败了
        Admin admin = this.adminService.findAdminByUsername(username);
        if (admin == null) {
            //登录失败
            return null;
        }
        UserDetails userDetails = new User(username, admin.getPassword(), new ArrayList<>());
        return userDetails;
    }
}
