package io.github.mortuusars.chalk.network.packet;

import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.item.ChalkBoxItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenCreativeChalkBoxC2SP(int chalkBoxSlotIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenCreativeChalkBoxC2SP> TYPE = new CustomPacketPayload.Type<>(Chalk.resource("open_creative_chalk_box"));
    public static final StreamCodec<FriendlyByteBuf, OpenCreativeChalkBoxC2SP> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            OpenCreativeChalkBoxC2SP::chalkBoxSlotIndex,
            OpenCreativeChalkBoxC2SP::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenCreativeChalkBoxC2SP packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (!player.isCreative()) {
                Chalk.LOGGER.error("Cannot open Chalk Box. Player is not in creative mode.");
                return;
            }

            if (!(player instanceof ServerPlayer serverPlayer)) {
                Chalk.LOGGER.error("Cannot open Chalk Box. Player is not ServerPlayer.");
                return;
            }

            int slotId = packet.chalkBoxSlotIndex();

            ItemStack itemStack = player.getInventory().getItem(slotId);
            if (itemStack.getItem() instanceof ChalkBoxItem chalkBoxItem) {
                chalkBoxItem.openGUI(serverPlayer, itemStack);
            }
            else {
                Chalk.LOGGER.error("Cannot open Chalk Box. Item in slot '{}' is not a ChalkBoxItem but '{}'", slotId, itemStack);
            }
        });
    }
}
