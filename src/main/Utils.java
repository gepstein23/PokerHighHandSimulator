package main;

public class Utils {

    public static void log(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    public static void log(String msg) {
        System.out.println(msg);
    }
}
