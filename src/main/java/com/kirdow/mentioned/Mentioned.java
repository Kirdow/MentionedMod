package com.kirdow.mentioned;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mentioned implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("ktnmentioned");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
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
