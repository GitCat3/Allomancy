package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AllomancyCapabilityPacket {

    private final CompoundNBT nbt;
    private final int entityID;

    /**
     * Packet for sending Allomancy player data to a client
     *
     * @param data     the AllomancyCapability data for the player
     * @param entityID the player's ID
     */
    public AllomancyCapabilityPacket(AllomancyCapability data, int entityID) {
        this(data != null ? data.serializeNBT() : new AllomancyCapability().serializeNBT(), entityID);
    }

    private AllomancyCapabilityPacket(CompoundNBT data, int entityID) {
        this.nbt = data;
        this.entityID = entityID;
    }

    public static AllomancyCapabilityPacket decode(PacketBuffer buf) {
        return new AllomancyCapabilityPacket(buf.readNbt(), buf.readInt());
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(this.nbt);
        buf.writeInt(this.entityID);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = (PlayerEntity) Minecraft.getInstance().level.getEntity(this.entityID);
            if (player != null) {
                AllomancyCapability playerCap = AllomancyCapability.forPlayer(player);
                playerCap.deserializeNBT(this.nbt);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
