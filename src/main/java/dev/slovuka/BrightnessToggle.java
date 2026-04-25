package dev.slovuka;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import dev.slovuka.classes.DataClass;

public class BrightnessToggle implements ClientModInitializer {
	public static final String MOD_ID = "brightness-toggle";
	public static KeyMapping toggleKey;
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);


	private static boolean wasDown = false;

	private static double getMaxGamma() {
		return (double) DataClass.configData.brightnessValue / 100.0;
	}

	public static boolean isGammaEnabled() {
		return DataClass.configData.gammaEnabled;
	}

	public static void onGammaChanged(double newGamma) {
		if (DataClass.configData.gammaEnabled) {
			DataClass.configData.gammaEnabled = false;
			net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
			if (client.player != null) {
				client.player.displayClientMessage(
						new TranslatableComponent("key.brightness-toggle.gamma-disabled").withStyle(ChatFormatting.WHITE),
						true
				);
			}
		}

		DataClass.configData.oldGamma = newGamma;
		DataClass.save();
	}

	@Override
	public void onInitializeClient() {
		DataClass.load();

		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.brightness-toggle.toggle",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"category.brightness-toggle"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client == null || client.options == null || client.player == null) return;

			boolean isDown = toggleKey.isDown();

			if (isDown && !wasDown) {
				if (client.player != null) {
					toggleBrightness(client);
				}
			}

			wasDown = isDown;
		});
	}

	private void toggleBrightness(net.minecraft.client.Minecraft client) {
		String translationKey;
		double maxGamma = getMaxGamma();
		boolean isActive = isGammaEnabled();

		if (!isActive) {
			DataClass.configData.oldGamma = client.options.gamma;
			client.options.gamma = maxGamma;
			DataClass.configData.gammaEnabled = true;
			translationKey = "key.brightness-toggle.gamma-enabled";
		} else {
			client.options.gamma = DataClass.configData.oldGamma;
			DataClass.configData.gammaEnabled = false;
			translationKey = "key.brightness-toggle.gamma-disabled";
		}

		DataClass.save();
		client.options.save();

		if (client.player != null) {
			client.player.displayClientMessage(
					new TranslatableComponent(translationKey).withStyle(ChatFormatting.WHITE),
					true
			);
		}
	}
}