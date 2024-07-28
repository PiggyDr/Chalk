package io.github.mortuusars.chalk.network.packet;

import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.core.MarkSymbol;
import io.github.mortuusars.chalk.network.packet.handler.ClientsideOpenSymbolSelectScreenHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record SelectSymbolS2CP(List<MarkSymbol> unlockedSymbols) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SelectSymbolS2CP> TYPE = new CustomPacketPayload.Type<>(Chalk.resource("select_symbol"));
    public static final StreamCodec<FriendlyByteBuf, SelectSymbolS2CP> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(
                    ArrayList::new,
                    MarkSymbol.STREAM_CODEC,
                    256),
            SelectSymbolS2CP::unlockedSymbols,
            SelectSymbolS2CP::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SelectSymbolS2CP packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientsideOpenSymbolSelectScreenHandler.handle(packet.unlockedSymbols());
        });
    }
}
