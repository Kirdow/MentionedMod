package com.kirdow.mentioned.util;

import com.kirdow.mentioned.Logger;

public class Utils {

    public static void runAsync(Runnable runnable) {
        try {
            (new Thread(() -> {
                try {
                    runnable.run();
                } catch (Throwable ex) {
                    Logger.error("Async Error: %s\n%s", ex.getMessage(), ex.toString());
                }
            })).start();
        } catch (Throwable ignored) {
        }
    }

}
