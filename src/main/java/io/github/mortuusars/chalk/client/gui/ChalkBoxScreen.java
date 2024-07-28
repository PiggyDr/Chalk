package io.github.mortuusars.chalk.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.config.Config;
import io.github.mortuusars.chalk.item.ChalkBoxItem;
import io.github.mortuusars.chalk.menus.ChalkBoxMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class ChalkBoxScreen extends AbstractContainerScreen<ChalkBoxMenu> {
    public static final ResourceLocation TEXTURE = Chalk.resource("textures/gui/container/chalk_box.png");
    private static final int GLOWING_BAR_WIDTH = 72;
    private final int maxGlowingUses;

    public ChalkBoxScreen(ChalkBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        maxGlowingUses = Config.Common.CHALK_BOX_GLOWING_AMOUNT_PER_ITEM.get();

        this.minecraft = Minecraft.getInstance();
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 180;
        this.inventoryLabelY = this.imageHeight - 94;
        super.init();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (getMenu().chalkBoxCoords != null) {
            graphics.renderItem(getMenu().getChalkBoxStack(),getGuiLeft() + getMenu().chalkBoxCoords.getFirst(),getGuiTop() + getMenu().chalkBoxCoords.getSecond());
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 200);
        graphics.fill(getGuiLeft() + getMenu().chalkBoxCoords.getFirst() - 1, getGuiTop() + getMenu().chalkBoxCoords.getSecond() - 1,
                getGuiLeft() + getMenu().chalkBoxCoords.getFirst() + 17, getGuiTop() + getMenu().chalkBoxCoords.getSecond() + 17,
                0x20c8c8c8);
        poseStack.popPose();

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, getGuiLeft(), getGuiTop(), 0,0, imageWidth, imageHeight);

        if (Config.Common.CHALK_BOX_GLOWING_ENABLED.get()){
            // Chalk Slots
            graphics.blit(TEXTURE, getGuiLeft() + 52, getGuiTop() + 17, 0, 180, 72, 36);

            for (int slotIndex = 0; slotIndex < ChalkBoxItem.CHALK_SLOTS; slotIndex++) {
                Slot slot = getMenu().slots.get(slotIndex);
                if (slot.getItem().isEmpty()) {
                    graphics.blit(TEXTURE, getGuiLeft() + slot.x - 1, getGuiTop() + slot.y - 1, 176, 0, 18, 18);
                }
            }

            // Bar + Slot
            graphics.blit(TEXTURE, getGuiLeft() + 52, getGuiTop() + 57, 0, 217, 72, 28);

            Slot slot = getMenu().slots.get(ChalkBoxItem.GLOWINGS_SLOT_INDEX);
            if (slot.getItem().isEmpty()) {
                graphics.blit(TEXTURE, getGuiLeft() + slot.x - 1, getGuiTop() + slot.y - 1, 176, 18, 18, 18);
            }

            // Fill
            int barSize = (int)Math.ceil((Math.min(getMenu().getGlowAmount(), maxGlowingUses) / (float) maxGlowingUses) * GLOWING_BAR_WIDTH);
            int glowingBarFillLevel = Math.min(GLOWING_BAR_WIDTH, barSize);
            graphics.blit(TEXTURE, getGuiLeft() + 52, getGuiTop() + 57, 72, 217, glowingBarFillLevel, 5);

        }
        else {
            // Chalk slots
            graphics.blit(TEXTURE, getGuiLeft() + 52, getGuiTop() + 32, 0, 180, 72, 36);
        }

        graphics.fill(getGuiLeft() + getMenu().chalkBoxCoords.getFirst() - 1, getGuiTop() + getMenu().chalkBoxCoords.getSecond() - 1,
                getGuiLeft() + getMenu().chalkBoxCoords.getFirst() + 17, getGuiTop() + getMenu().chalkBoxCoords.getSecond() + 17,
                0xAAc8c8c8);
    }
}
