package gg.nodus.girlboss.mixin;

import gg.nodus.girlboss.SignatureTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Inject(method = "onMessageHeader", at = @At("TAIL"))
    public void onHeader(MessageHeaderS2CPacket packet, CallbackInfo ci) {
        var sender = MinecraftClient.getInstance().world.getPlayerByUuid(packet.header().sender());
        var senderName = sender.getName().getString();
        var signature = packet.headerSignature().data();
        SignatureTracker.addSignature(sender.getUuid(), signature);
        MinecraftClient.getInstance().player.sendMessage(Text.literal("§a[Girlboss] §7" + senderName + "§7 just sent a private message!"));
    }

    @Inject(method = "onChatMessage", at = @At("TAIL"))
    public void onMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
        for (var entry : packet.message().signedBody().lastSeenMessages().entries()) {
            if (entry.profileId() == packet.message().signedHeader().sender()) {
                continue;
            }
            var sigs = SignatureTracker.getSignatures(entry.profileId());
            for (var sig : sigs) {
                if (Arrays.equals(sig, entry.lastSignature().data())) {
                    var receiver = MinecraftClient.getInstance().world.getPlayerByUuid(packet.message().signedHeader().sender());
                    var sender = MinecraftClient.getInstance().world.getPlayerByUuid(entry.profileId());
                    if (sender != receiver) {
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("§a[Girlboss] §7" + receiver.getName().getString() + "§7 received a private message from " + sender.getName().getString() + "!"));
                    }
                }
            }
        }
    }

}
