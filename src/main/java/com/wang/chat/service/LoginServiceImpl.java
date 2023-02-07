package com.wang.chat.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginServiceImpl implements LoginService {


    private Map<String, String> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("zhangsan", "123");
        allUserMap.put("lisi", "123");
        allUserMap.put("wangwu", "123");
        allUserMap.put("zhaoliu", "123");
        allUserMap.put("qianqi", "123");
    }

    @Override
    public boolean login(String name, String password) {

        String pwd = allUserMap.get(name);
        if (pwd==null){
            return false;
        }

        return password.equals(pwd);
    }
}
