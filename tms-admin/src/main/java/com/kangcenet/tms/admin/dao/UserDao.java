package com.kangcenet.tms.admin.dao;

import com.kangcenet.tms.admin.core.model.User;

import java.util.HashMap;
import java.util.List;

/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface UserDao {

    public List<User> pageList();

    public User select(HashMap map);

    public User loadUserInfo(String auth);

    public int create(User user);

    public int delete(String user);

    public int setToken(HashMap map);
}
