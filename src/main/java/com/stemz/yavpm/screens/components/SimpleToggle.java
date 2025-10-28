package com.stemz.yavpm.screens.components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SimpleToggle extends ClickableWidget {
    private final Runnable onClick;
    private boolean toggled = false;

    public SimpleToggle(int x, int y, int width, int height, Text text, Runnable onClick, boolean isToggled){
        super(x, y, width, height, text);
        this.onClick = onClick;
        this.toggled = isToggled;
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

        ctx.drawText(client.textRenderer, this.getMessage(), this.getX() + 5, this.getY() + 1 + (this.getHeight() - textHeight) / 2, 0xFFDDDDDD, true);

        // Toggle Indicator
        int indicatorWidth = 25;
        int indicatorHeight = 10;
        int handleOffset = 2;
        int indicatorX = this.getX() + this.getWidth() - indicatorWidth;
        int indicatorY = this.getY() + (this.getHeight() - indicatorHeight) / 2;

        ctx.fill(indicatorX, indicatorY, indicatorX + indicatorWidth - 5, indicatorY + indicatorHeight, this.toggled ? 0xFF3b8526 : 0xFF4F5052);
        ctx.drawBorder(indicatorX, indicatorY, indicatorWidth - 5, indicatorHeight, 0xFF1E1E1F);
        ctx.drawBorder(indicatorX + 1, indicatorY + 1, indicatorWidth - 7, indicatorHeight - 2, 0x11FFFFFF);

        // Indicator "handle thing"
        int handleSize = 11;
        int handleX = this.toggled ? indicatorX + handleSize - (handleOffset - 1) : indicatorX;
        ctx.fill(handleX, indicatorY - (handleOffset - 1), handleX + handleSize, indicatorY + handleSize - (handleOffset - 1), 0xFFDDDDDD);
        ctx.drawBorder(handleX, indicatorY - (handleOffset - 1), handleSize, handleSize, 0xFF1E1E1F);
        ctx.fill(handleX, indicatorY + handleSize - (1 + handleOffset), handleX + handleSize, indicatorY + handleSize - handleOffset, 0x77000000);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.active && this.visible && this.isHovered()) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.toggled = !this.toggled;
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
