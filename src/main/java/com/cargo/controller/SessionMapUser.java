package com.cargo.controller;

import com.cargo.model.Users;

import java.util.HashMap;
import java.util.Map;


public class SessionMapUser {

    private static Map<String, Users> sessionMap;

    private SessionMapUser(){
    }


    public static Map addSession(String key,Users value){
        if(sessionMap==null){
            sessionMap = new HashMap<>();
        }
        sessionMap.put(key,value);
        return sessionMap;
    }

    public static Users getSessionMap(String key) {
        return sessionMap.get(key);
    }

    public static void deleteSessionMap(String key){
        sessionMap.remove(key);
    }

    public static void deleteAllSession(){
        sessionMap = null;
    }
}
