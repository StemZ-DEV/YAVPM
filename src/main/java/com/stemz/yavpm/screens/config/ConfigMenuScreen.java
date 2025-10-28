package com.stemz.yavpm.screens.config;

import com.stemz.yavpm.screens.components.SimpleButton;
import com.stemz.yavpm.screens.components.SimpleToggle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfigMenuScreen extends Screen {
    private final Screen parent;

    public ConfigMenuScreen(String title, Screen parent) {
        super(Text.of(title));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        SimpleButton btn = new SimpleButton(10, 10, 90, 22, Text.literal("Button"), () -> {

        });

        SimpleToggle toggle = new SimpleToggle(10, 35, 90, 22, Text.literal("Toggle"), () -> {

        }, true);

        addDrawableChild(btn);
        addDrawableChild(toggle);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
