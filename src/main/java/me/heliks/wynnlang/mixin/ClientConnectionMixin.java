package me.heliks.wynnlang.mixin;

import com.mojang.logging.LogUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.heliks.wynnlang.Core;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.RejectedExecutionException;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow
    private Channel channel;
    @Shadow
    private volatile PacketListener packetListener;

    @Shadow
    public abstract void disconnect(Text disconnectReason);

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow
    private int packetsReceivedCounter;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", cancellable = true, at = @At(value = "HEAD"))
    public void channelRead(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (!this.channel.isOpen()) {
            return;
        }
        PacketListener packetListener = this.packetListener;
        if (packetListener == null) {
            throw new IllegalStateException("Received a packet before the packet listener was initialized");
        }
        packet = Core.INSTANCE.getTranslateRepository().applyCurrentPacket(packet);

        if (packetListener.accepts(packet)) {
            try {
                ClientConnectionInvoker.invokeHandlePacket(packet, packetListener);
            } catch (OffThreadException ignored) {
            } catch (RejectedExecutionException rejectedExecutionException) {
                this.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
            } catch (ClassCastException classCastException) {
                LOGGER.error("Received {} that couldn't be processed", (Object) packet.getClass(), (Object) classCastException);
                this.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
            }
            ++this.packetsReceivedCounter;
        }
        ci.cancel();
    }
}
