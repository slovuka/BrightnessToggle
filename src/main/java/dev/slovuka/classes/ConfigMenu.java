package dev.slovuka.classes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.slovuka.BrightnessToggle;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.slovuka.BrightnessToggle.MOD_ID;

public class ConfigMenu implements ModMenuApi {
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MyConfigScreen::new;
    }
    public static class MyConfigScreen extends Screen {
        private final Screen parent;
        private OptionsList buttonList;

        public MyConfigScreen(Screen parent) {
            super(new TranslatableComponent("options.title"));
            this.parent = parent;
        }

        @Override
        protected void init() {

            this.addButton(new BrightnessSlider(this.width / 2 - 100, 60, 200, 20, DataClass.configData.brightnessValue));

            this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20,
                    new TranslatableComponent("gui.done"),
                    button -> {
                        this.onClose();
                    }));

            net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
            if (client == null || client.options == null || client.gameRenderer == null) return;

            client.options.gamma = (double) DataClass.configData.brightnessValue / 100.0;
            client.gameRenderer.lightTexture().tick();
        }

        @Override
        public void onClose() {
            DataClass.save();
            if (!BrightnessToggle.isGammaEnabled()) {
                net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
                if (client == null || client.options == null || client.gameRenderer == null) return;

                client.options.gamma = DataClass.configData.oldGamma;
                client.gameRenderer.lightTexture().tick();
            }

            if (this.minecraft != null) {
                this.minecraft.setScreen(parent);
            }
        }

        @Override
        public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
            if (this.minecraft == null) return;

            int width = this.width;
            int height = this.height;

            int topBarSizeY = 20 * 2 + 7;
            int bottomBarSizeY = (this.height - (40 * 2)) + 7;

            if (this.minecraft.level != null) {
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder buffer = tesselator.getBuilder();

                this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);


                    /* =======================
                       TOP PANEL
                    ======================= */

                buffer.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);

                //top-left
                buffer.vertex(width, 0, 0)
                        .uv(0, topBarSizeY / 32f)
                        .color(64, 64, 64, 255)
                        .endVertex();

                //top-right
                buffer.vertex(0, 0, 0)
                        .uv(width / 32f, topBarSizeY / 32f)
                        .color(64, 64, 64, 255)
                        .endVertex();

                //bottom-right
                buffer.vertex(0, topBarSizeY, 0)
                        .uv(width / 32f, 0)
                        .color(64, 64, 64, 255)
                        .endVertex();

                //bottom-left
                buffer.vertex(width, topBarSizeY, 0)
                        .uv(0, 0)
                        .color(64, 64, 64, 255)
                        .endVertex();

                tesselator.end();

                        /* =======================
                           BOTTOM PANEL
                        ======================= */


                buffer.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);

                //top-left
                buffer.vertex(width, bottomBarSizeY, 0)
                        .uv(0, height / 32f)
                        .color(64, 64, 64, 255)
                        .endVertex();

                //top-right
                buffer.vertex(0, bottomBarSizeY, 0)
                        .uv(width / 32f, height / 32f)
                        .color(64, 64, 64, 255)
                        .endVertex();

                //bottom-right
                buffer.vertex(0, height, 0)
                        .uv(width / 32f, bottomBarSizeY / 32f)
                        .color(64, 64, 64, 255)
                        .endVertex();

                //bottom-left
                buffer.vertex(width, height, 0)
                        .uv(0, bottomBarSizeY / 32f)
                        .color(64, 64, 64, 255)
                        .endVertex();

                tesselator.end();

                // top grad
                fillGradient(matrices,
                        0, topBarSizeY,
                        width, topBarSizeY + 3,
                        0xA0000000, 0x00000000);

                //bootoom grad
                fillGradient(matrices,
                        0, bottomBarSizeY - 3,
                        width, bottomBarSizeY,
                        0x00000000, 0xA0000000);
            } else {
                this.renderBackground(matrices);
                fillGradient(matrices,
                        0, topBarSizeY,
                        width, bottomBarSizeY,
                        0xA0000000, 0xA0000000);
            }

            drawCenteredString(matrices, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
            super.render(matrices, mouseX, mouseY, delta);
        }

        private static class BrightnessSlider extends AbstractSliderButton {
            private final double min = 100.0;
            private final double max = 1000.0;
            public final double step = 50.0;

            public BrightnessSlider(int x, int y, int width, int height, int defaultValue) {
                super(x, y, width, height, TextComponent.EMPTY, 0.0);
                this.value = (Mth.clamp(defaultValue, min, max) - min) / (max - min);
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("key.brightness-toggle.gamma_value", DataClass.configData.brightnessValue));
            }

            @Override
            protected void applyValue() {
                net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
                if (client == null || client.options == null || client.gameRenderer == null) return;

                double rawValue = min + (value * (max - min));
                int snappedValue = (int) (Math.round(rawValue / step) * step);
                DataClass.configData.brightnessValue = (int) Mth.clamp(snappedValue, min, max);

                this.value = (DataClass.configData.brightnessValue - min) / (max - min);

                client.options.gamma = (double) DataClass.configData.brightnessValue / 100.0;
                client.gameRenderer.lightTexture().tick();

                DataClass.save();
            }
        }
    }
}