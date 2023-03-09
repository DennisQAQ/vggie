package com.dw.vggie.common;

/**
 * 基于ThreadLocal封装工具栏，用户保存和获取当前用户id
 * 实现步骤:
 * 1、编写BaseContext工具类，基于ThreadLocal封装的工具类
 * 2、在LoginCheckFilter的doFilter方法中调用BaseContext来设置当前登录用户的id3、在MyMeta0bjectHandler的方法中调用BaseContext获取登录用户的id
 */
public class BaseContext {
    private static ThreadLocal <Long> ThreadLocal = new ThreadLocal();

    public static void setCurrentId(Long id){
        ThreadLocal.set(id);
    }

    public static Long getCurrentId(){
        return ThreadLocal.get();
    }
}
