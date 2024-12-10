package io.github.mortuusars.chalk.items;

import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.config.Config;
import io.github.mortuusars.chalk.core.IDrawingTool;
import io.github.mortuusars.chalk.core.Mark;
import io.github.mortuusars.chalk.core.MarkSymbol;
import io.github.mortuusars.chalk.render.ChalkColors;
import io.github.mortuusars.chalk.utils.MarkDrawingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChalkItem extends Item implements IDrawingTool {
    private final DyeColor color;

    public ChalkItem(DyeColor dyeColor, Properties properties) {
        super(properties
                .stacksTo(1)
                .defaultDurability(64)
                .setNoRepair());
        color = dyeColor;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        final InteractionHand hand = context.getHand();
        final ItemStack itemStack = context.getItemInHand();
        final Player player = context.getPlayer();

        if (player == null || !(itemStack.getItem() instanceof ChalkItem))
            return InteractionResult.FAIL;

        // When holding chalks in both hands - skip drawing from offhand
        if (hand == InteractionHand.OFF_HAND && player.getMainHandItem().getItem() instanceof ChalkItem)
            return InteractionResult.FAIL;

        MarkDrawingContext drawingContext = createDrawingContext(player, context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), hand);

        if (!drawingContext.canDraw())
            return InteractionResult.FAIL;

        if (player.isSecondaryUseActive()) {
            drawingContext.openSymbolSelectionScreen();
            return InteractionResult.CONSUME;
        }

        if (drawMark(drawingContext, drawingContext.createRegularMark(ChalkColors.fromDyeColor(color), false)))
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        else if (drawingContext.hasExistingMark())
            return InteractionResult.PASS;

        return InteractionResult.FAIL;
    }

    @Override
    public void onMarkDrawn(Player player, InteractionHand hand, BlockPos markBlockPos, BlockState markBlockState) {
        if (player.isCreative())
            return;

        ItemStack stack = player.getItemInHand(hand);
        stack.hurtAndBreak(1, player, pl -> {
            pl.broadcastBreakEvent(hand);
        });
    }

    @Override
    public Mark getMark(ItemStack itemInHand, MarkDrawingContext drawingContext, MarkSymbol symbol) {
        return drawingContext.createMark(ChalkColors.fromDyeColor(getColor()), symbol, getGlowing(itemInHand));
    }

    @Override
    public int getMarkColorValue(ItemStack stack) {
        return ChalkColors.fromDyeColor(getColor());
    }

    @Override
    public Optional<DyeColor> getMarkColor(ItemStack stack) {
        return Optional.of(getColor());
    }

    @Override
    public boolean getGlowing(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Config.CHALK_DURABILITY.get();
    }
    public DyeColor getColor() {
        return this.color;
    }
    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return false;
    }
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }
}
