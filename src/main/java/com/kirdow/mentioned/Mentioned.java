package com.kirdow.mentioned;

import com.kirdow.mentioned.config.ModConfig;
import com.kirdow.mentioned.config.gui.MentionedConfigMenu;
import com.kirdow.mentioned.input.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class Mentioned implements ClientModInitializer {
	public static final String MOD_ID = "ktnmentioned";
	public static final String MOD_NAME = "Mentioned";
	public static final String MOD_VERSION = "1.1";
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static Path modPath;

	@Override
	public void onInitializeClient() {
		Logger.setLogger(LOGGER::info, LOGGER::debug, LOGGER::error, LOGGER::warn);

		Logger.info("Preparing mod config directory");
		modPath = FabricLoader.getInstance().getConfigDir().resolve(Mentioned.MOD_ID);
		File modFolder = modPath.toFile();
		if (!modFolder.exists()) {
			Logger.info("Creating config directory");
			if (!modFolder.mkdir())
				Logger.error("Failed creating config directory: %s", modFolder.getAbsolutePath());
		}
		KeyBindings.init();
		KeyBindings.register(KeyBindingHelper::registerKeyBinding);

		ModConfig.registerConfig();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (KeyBindings.keyOpenConfig.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new MentionedConfigMenu());
				}
			}
		});
	}

	public static Path getModPath() {
		return modPath;
	}

	public static File getModFolder() {
		return modPath.toFile();
	}

	private static long skip = -1L;
	private static final Object skipMutex = new Object();
	public static void skip(long delay) {
		synchronized (skipMutex) {
			skip = System.currentTimeMillis() + delay;
		}
	}
	public static boolean skips() {
		synchronized (skipMutex) {
			return System.currentTimeMillis() < skip;
		}
	}
}
