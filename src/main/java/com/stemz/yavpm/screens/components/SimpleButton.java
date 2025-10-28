package com.stemz.yavpm.screens.components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SimpleButton extends ClickableWidget {
    private final Runnable onClick;

    public SimpleButton(int x, int y, int width, int height, Text text, Runnable onClick){
        super(x, y, width, height, text);
        this.onClick = onClick;
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean isHovered = this.isHovered();

        int mainColor = !isHovered ? 0xFF444444 : 0xFF555555;
        int shadowColor = !isHovered ? 0x77000000 : 0x00000000;

        // BG
        ctx.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), mainColor);

        // OuterBorder
        ctx.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFF1E1E1F);

        // InnerBorder
        ctx.drawBorder(this.getX() + 1, this.getY() + 1, this.getWidth() - 2, this.getHeight() - 2, 0x11FFFFFF);

        // Shadow
        ctx.fill(this.getX(), this.getY() + this.getHeight() - 3, this.getX() + this.getWidth(), this.getY() + this.getHeight(), shadowColor);

        // Text
        int textWidth = client.textRenderer.getWidth(this.getMessage());
        int textHeight = client.textRenderer.fontHeight;

        ctx.drawText(client.textRenderer, this.getMessage(), this.getX() + (this.getWidth() - textWidth) / 2, this.getY() + 1 + (this.getHeight() - textHeight) / 2, 0xFFDDDDDD, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.active && this.visible && this.isHovered()) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onClick.run();
            return true;
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }
}
