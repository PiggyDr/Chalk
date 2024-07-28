package io.github.mortuusars.chalk.network;

import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.network.packet.SelectSymbolS2CP;
import io.github.mortuusars.chalk.network.packet.DrawMarkC2SP;
import io.github.mortuusars.chalk.network.packet.OpenCreativeChalkBoxC2SP;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Chalk.ID, bus = EventBusSubscriber.Bus.MOD)
public class Packets {
    @SuppressWarnings("unused")
    @SubscribeEvent
    private static void onRegisterPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(DrawMarkC2SP.TYPE, DrawMarkC2SP.STREAM_CODEC, DrawMarkC2SP::handle);
        registrar.playToServer(OpenCreativeChalkBoxC2SP.TYPE, OpenCreativeChalkBoxC2SP.STREAM_CODEC, OpenCreativeChalkBoxC2SP::handle);

        registrar.playToClient(SelectSymbolS2CP.TYPE, SelectSymbolS2CP.STREAM_CODEC, SelectSymbolS2CP::handle);
    }

    public static void sendToServer(CustomPacketPayload packet, CustomPacketPayload... otherPackets) {
        PacketDistributor.sendToServer(packet, otherPackets);
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload packet, CustomPacketPayload... otherPackets) {
        PacketDistributor.sendToPlayer(player, packet, otherPackets);
    }
}
