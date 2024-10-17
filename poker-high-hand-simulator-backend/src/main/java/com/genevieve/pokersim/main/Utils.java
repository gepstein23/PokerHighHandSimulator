package com.genevieve.pokersim.main;

import java.util.Arrays;

public class Utils {

    public static void debug(String msg, Object... args) {
        Object[] argStr = Arrays.stream(args).map(Object::toString).toArray();
     //  System.out.println(String.format(msg, argStr));
    }

    public static void log(String msg, Object... args) {
        Object[] argStr = Arrays.stream(args).map(Object::toString).toArray();
        System.out.println(String.format(msg, argStr));
    }

    public static void log(String msg) {
        System.out.println(msg);
    }
}
