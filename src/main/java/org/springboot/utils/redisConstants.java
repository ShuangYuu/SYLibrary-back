package org.springboot.utils;

public class redisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 5L;

    public static final String LOGIN_LOCK_KEY = "login:lock:";
    public static final Long LOGIN_LOCK_TTL = 60L;
}
