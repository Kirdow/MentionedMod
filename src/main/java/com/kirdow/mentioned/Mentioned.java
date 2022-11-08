package com.kirdow.mentioned;

import com.google.common.base.Ticker;
import com.kirdow.mentioned.config.ModConfig;
import com.kirdow.mentioned.config.gui.ConfigMenuHelper;
import com.kirdow.mentioned.config.gui.MentionedConfigMenu;
import com.kirdow.mentioned.input.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Path;

@Mod(Mentioned.MOD_ID)
public class Mentioned {

	private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(String.format("%s", Mentioned.MOD_ID));
	public static final String MOD_ID = "ktnmentioned";
	public static final String MOD_NAME = "Mentioned";
	public static final String MOD_VERSION = "1.1";

	private static Path modPath;

	public Mentioned() {
		Logger.setLogger(LOGGER::info, LOGGER::debug, LOGGER::error, LOGGER::warn);

		if (FMLEnvironment.dist != Dist.CLIENT) {
			Logger.info("Disabled because not running on the client.");
			return;
		}

		Logger.info("Preparing mod config directory");
		modPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
		File modFolder = modPath.toFile();
		if (!modFolder.exists()) {
			Logger.info("Creating config directory");
			if (!modFolder.mkdir())
				Logger.error("Failed creating config directory: %s", modFolder.getAbsolutePath());
		}

		KeyBindings.init();
		MinecraftForge.EVENT_BUS.register(new Object() {
			@SubscribeEvent
			public void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
				KeyBindings.register(event::register);
			}

			@SubscribeEvent
			public void onKeyPress(InputEvent.Key event) {
				if (event.getAction() != GLFW.GLFW_PRESS) return;
				Minecraft mc = Minecraft.getInstance();
				if (mc.player == null || mc.level == null || mc.screen != null) return;

				if (KeyBindings.keyOpenConfig.matches(event.getKey(), event.getScanCode())) {
					mc.setScreen(new MentionedConfigMenu());
				}
			}
		});

		ModConfig.registerConfig();

		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, ConfigMenuHelper::createConfigGuiFactory);
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
