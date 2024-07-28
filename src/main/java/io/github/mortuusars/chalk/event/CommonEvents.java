package io.github.mortuusars.chalk.event;

import io.github.mortuusars.chalk.Chalk;
import io.github.mortuusars.chalk.advancement.PlayerSleepInfo;
import io.github.mortuusars.chalk.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@SuppressWarnings("unused")
public class CommonEvents {
//    @EventBusSubscriber(modid = Chalk.ID, bus = EventBusSubscriber.Bus.MOD)
//    public static class Mod {
//        @SubscribeEvent
//        public static void commonSetup(FMLCommonSetupEvent event) {
//            event.enqueueWork(() -> {
//            });
//        }
//    }

    @EventBusSubscriber(modid = Chalk.ID, bus = EventBusSubscriber.Bus.GAME)
    public static class Game {

        @SubscribeEvent
        public static void advancementAward(AdvancementEvent.AdvancementEarnEvent event) {
            ResourceLocation id = event.getAdvancement().id();

            for (var entry : Config.Common.SYMBOL_CONFIG.entrySet()) {
                boolean isEnabled = entry.getValue().getFirst().get();
                String location = entry.getValue().getSecond().get();
                if (isEnabled && !location.isEmpty() && location.equals(id.toString()) && event.getEntity() instanceof ServerPlayer player) {
                    player.displayClientMessage(Component.translatable("chat.chalk.symbol_unlocked",
                            Component.translatable(entry.getKey().getTranslationKey()).withStyle(Style.EMPTY.withColor(0x53a5df))), false);
                    player.playNotifySound(Chalk.SoundEvents.MARK_DRAW.get(), SoundSource.PLAYERS, 1f, 1f);
                    return;
                }
            }
        }

        @SubscribeEvent
        public static void onSleepFinished(PlayerWakeUpEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                boolean sleepingLongEnough = serverPlayer.isSleepingLongEnough();
                if (!sleepingLongEnough)
                    return;

                List<String> tags = serverPlayer.getTags().stream().toList();

                List<BlockPos> sleepPositions = new ArrayList<>();

                for (String tag : tags) {
                    if (tag.startsWith("ChalkConsecutiveSleepPositions")) {
                        serverPlayer.removeTag(tag);

                        String dataStr = tag.replace("ChalkConsecutiveSleepPositions", "");
                        PlayerSleepInfo sleepInfo = PlayerSleepInfo.deserialize(dataStr);
                        sleepPositions = new ArrayList<>(sleepInfo.sleepPositions());
                        break;
                    }
                }

                Optional<BlockPos> sleepingPos = serverPlayer.getSleepingPos();
                if (sleepingPos.isPresent()) {
                    if (sleepPositions.size() > 20)
                        sleepPositions.removeFirst();

                    sleepPositions.add(sleepingPos.get());

                    PlayerSleepInfo sleepInfo = new PlayerSleepInfo(sleepPositions);

                    Chalk.CriteriaTriggers.CONSECUTIVE_SLEEPING.value().trigger(serverPlayer, sleepInfo);

                    String serializedDataStr = sleepInfo.serialize();
                    serverPlayer.addTag("ChalkConsecutiveSleepPositions" + serializedDataStr);
                }
            }
        }
    }
}
