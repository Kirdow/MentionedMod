package com.kirdow.mentioned;

import com.kirdow.mentioned.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mentioned implements ModInitializer {
	public static final String MOD_ID = "ktnmentioned";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize() {
		ModConfig.registerConfig();
	}

	private static long skip = -1L;
	private static final Object skipMutex = new Object();
	public static void skip() {
		synchronized (skipMutex) {
			skip = System.currentTimeMillis() + 1000L;
		}
	}
	public static boolean skips() {
		synchronized (skipMutex) {
			return System.currentTimeMillis() < skip;
		}
	}
}
