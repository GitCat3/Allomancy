package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalSelectScreen;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.legobmw99.allomancy.modules.powers.client.util.ClientUtils;
import com.legobmw99.allomancy.modules.powers.client.util.MetalBlockBlob;
import com.legobmw99.allomancy.modules.powers.network.ChangeEmotionPacket;
import com.legobmw99.allomancy.modules.powers.network.TryPushPullBlock;
import com.legobmw99.allomancy.modules.powers.network.TryPushPullEntity;
import com.legobmw99.allomancy.modules.powers.network.UpdateEnhancedPacket;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.util.PowerUtils;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.Metal;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientEventHandler {


    private final Minecraft mc = Minecraft.getInstance();

    private final Set<Entity> metal_entities = new HashSet<>();
    private final Set<MetalBlockBlob> metal_blobs = new HashSet<>();
    private final Set<PlayerEntity> nearby_allomancers = new HashSet<>();

    private int tickOffset = 0;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase == TickEvent.Phase.END && !this.mc.isPaused() && this.mc.player != null && this.mc.player.isAlive()) {

            PlayerEntity player = this.mc.player;
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);

            if (!cap.isUninvested()) {
                // Duralumin makes you move much quicker and reach much further
                int force_multiplier = cap.isEnhanced() ? 4 : 1;
                int dist_modifier = cap.isEnhanced() ? 2 : 1;

                // Handle our input-based powers
                if (this.mc.options.keyAttack.isDown()) {
                    // Ray trace 20 blocks (or 40 if enhanced)
                    RayTraceResult trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
                    // All iron pulling powers
                    if (cap.isBurning(Metal.IRON)) {
                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && PowerUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                Network.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getId(), PowerUtils.PULL * force_multiplier));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getBlockPos();
                                if (PowerUtils.isBlockStateMetal(this.mc.level.getBlockState(bp)) ||
                                    (player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() && player.isCrouching())) {
                                    Network.sendToServer(new TryPushPullBlock(bp, PowerUtils.PULL * force_multiplier));
                                }
                            }
                        }
                    }
                    // All zinc powers
                    if (cap.isBurning(Metal.ZINC)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                Network.sendToServer(new ChangeEmotionPacket(entity.getId(), true));
                            }
                        }
                    }
                }
                if (this.mc.options.keyUse.isDown()) {
                    // Ray trace 20 blocks (or 40 if enhanced)
                    RayTraceResult trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
                    // All steel pushing powers
                    if (cap.isBurning(Metal.STEEL)) {

                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && PowerUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                Network.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getId(), PowerUtils.PUSH * force_multiplier));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getBlockPos();
                                if (PowerUtils.isBlockStateMetal(this.mc.level.getBlockState(bp)) ||
                                    (player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() && player.isCrouching())) {
                                    Network.sendToServer(new TryPushPullBlock(bp, PowerUtils.PUSH * force_multiplier));
                                }
                            }
                        }
                    }
                    // All brass powers
                    if (cap.isBurning(Metal.BRASS)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                Network.sendToServer(new ChangeEmotionPacket(entity.getId(), false));
                            }
                        }
                    }

                    if (cap.isBurning(Metal.NICROSIL)) {
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            Entity entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof PlayerEntity) {
                                Network.sendToServer(new UpdateEnhancedPacket(true, entity.getId()));
                            }
                        }
                    }
                }

                tickOffset = (tickOffset + 1) % 2;

                if (tickOffset == 0) {
                    // Populate the metal lists
                    this.metal_blobs.clear();
                    this.metal_entities.clear();
                    if (cap.isBurning(Metal.IRON) || cap.isBurning(Metal.STEEL)) {
                        int max = PowersConfig.max_metal_detection.get();
                        BlockPos negative = player.blockPosition().offset(-max, -max, -max);
                        BlockPos positive = player.blockPosition().offset(max, max, max);

                        // Add metal entities to metal list
                        List<Entity> entities = player.level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(negative, positive));
                        entities.stream().filter(PowerUtils::isEntityMetal).filter(e -> !e.equals(player)).forEach(this.metal_entities::add);

                        // Add metal blobs to metal list
                        Stream<BlockPos> blocks = BlockPos.betweenClosedStream(negative, positive);
                        blocks.filter(bp -> PowerUtils.isBlockStateMetal(player.level.getBlockState(bp))).forEach(bp -> {
                            Set<MetalBlockBlob> matches = this.metal_blobs.stream().filter(mbl -> mbl.isMatch(bp)).collect(Collectors.toSet());
                            switch (matches.size()) {
                                case 0: // new blob
                                    this.metal_blobs.add(new MetalBlockBlob(bp));
                                    break;
                                case 1: // add to existing blob
                                    matches.stream().findAny().get().add(bp);
                                    break;
                                default: // this block serves as a bridge between (possibly many) existing blobs
                                    this.metal_blobs.removeAll(matches);
                                    MetalBlockBlob mbb = matches.stream().reduce(null, MetalBlockBlob::merge);
                                    mbb.add(bp);
                                    this.metal_blobs.add(mbb);
                                    break;

                            }

                        });

                    }
                    // Populate our list of nearby allomancy users
                    this.nearby_allomancers.clear();
                    if (cap.isBurning(Metal.BRONZE) && (cap.isEnhanced() || !cap.isBurning(Metal.COPPER))) {
                        // Add metal burners to a list
                        BlockPos negative = player.blockPosition().offset(-30, -30, -30);
                        BlockPos positive = player.blockPosition().offset(30, 30, 30);
                        List<PlayerEntity> nearby_players = player.level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(negative, positive),
                                                                                            entity -> entity != null && entity != player);

                        for (PlayerEntity otherPlayer : nearby_players) {
                            AllomancyCapability capOther = AllomancyCapability.forPlayer(otherPlayer);
                            if (capOther.isBurning(Metal.COPPER) && (!cap.isEnhanced() || capOther.isEnhanced())) {
                                // player is inside a smoker cloud, should not detect unless enhanced
                                this.nearby_allomancers.clear();
                                return;
                            } else {
                                for (Metal mt : Metal.values()) {
                                    if (capOther.isBurning(mt)) {
                                        this.nearby_allomancers.add(otherPlayer);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseInput(final InputEvent.MouseInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    /**
     * Handles either mouse or button presses for the mod's keybinds
     */
    private void acceptInput() {

        boolean extras = false;
        if (PowersClientSetup.enable_more_keybinds) {
            for (KeyBinding key : PowersClientSetup.powers) {
                if (key.isDown()) {
                    extras = true;
                    break;
                }
            }
        }

        if (PowersClientSetup.burn.isDown() || extras) {
            PlayerEntity player = this.mc.player;
            AllomancyCapability cap;
            if (this.mc.screen == null) {
                if (player == null || !this.mc.isWindowActive()) {
                    return;
                }
                cap = AllomancyCapability.forPlayer(player);

                if (extras) { // try one of the extra keybinds
                    for (int i = 0; i < PowersClientSetup.powers.length; i++) {
                        if (PowersClientSetup.powers[i].isDown()) {
                            ClientUtils.toggleBurn(Metal.getMetal(i), cap);
                        }
                    }

                } else { // normal keypress

                    int num_powers = cap.getPowerCount();

                    if (num_powers == 0) {
                        return;
                    } else if (num_powers == 1) {
                        ClientUtils.toggleBurn(cap.getPowers()[0], cap);
                    } else {
                        this.mc.setScreen(new MetalSelectScreen());
                    }
                }
            }
        }

        if (PowersClientSetup.hud.isDown()) {
            PowersConfig.enable_overlay.set(!PowersConfig.enable_overlay.get());
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

        if (!PowersConfig.enable_overlay.get() && !(this.mc.screen instanceof MetalSelectScreen)) {
            return;
        }
        if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
            return;
        }
        if (!this.mc.isWindowActive() || !this.mc.player.isAlive()) {
            return;
        }
        if (this.mc.screen != null && !(this.mc.screen instanceof ChatScreen) && !(this.mc.screen instanceof MetalSelectScreen)) {
            return;
        }

        MetalOverlay.drawMetalOverlay(event.getMatrixStack());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        PlayerEntity player = this.mc.player;
        if (player == null || !player.isAlive() || this.mc.options.getCameraType().isMirrored()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);

        if (cap.isUninvested()) {
            return;
        }


        MatrixStack stack = event.getMatrixStack();
        stack.pushPose();
        Vector3d view = this.mc.cameraEntity.getEyePosition(event.getPartialTicks());
        stack.translate(-view.x, -view.y, -view.z);

        // TODO investigate depreciation
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(stack.last().pose());
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableBlend();

        double dist = 1;
        double yaw = ((mc.player.yRot + 90) * Math.PI) / 180;
        double pitch = ((mc.player.xRot + 90) * Math.PI) / 180;

        Vector3d playervec = view.add(MathHelper.sin((float) pitch) * MathHelper.cos((float) yaw) * dist, MathHelper.cos((float) pitch) * dist - 0.35,
                                      MathHelper.sin((float) pitch) * MathHelper.sin((float) yaw) * dist);

        /*********************************************
         * IRON AND STEEL LINES                      *
         *********************************************/
        if ((cap.isBurning(Metal.IRON) || cap.isBurning(Metal.STEEL))) {

            for (Entity entity : this.metal_entities) {
                ClientUtils.drawMetalLine(playervec, entity.position(), 1.5F, 0F, 0.6F, 1F);
            }

            for (MetalBlockBlob mb : this.metal_blobs) {
                ClientUtils.drawMetalLine(playervec, mb.getCenter(), MathHelper.clamp(0.3F + mb.size() * 0.4F, 0.5F, 7.5F), 0F, 0.6F, 1F);
            }
        }

        /*********************************************
         * BRONZE LINES                              *
         *********************************************/
        if ((cap.isBurning(Metal.BRONZE) && (cap.isEnhanced() || !cap.isBurning(Metal.COPPER)))) {
            for (PlayerEntity playerEntity : this.nearby_allomancers) {
                ClientUtils.drawMetalLine(playervec, playerEntity.position(), 5.0F, 0.7F, 0.15F, 0.15F);
            }
        }

        /*********************************************
         * GOLD AND ELECTRUM LINES                   *
         *********************************************/
        if (cap.isBurning(Metal.GOLD)) {
            RegistryKey<World> deathDim = cap.getDeathDim();
            if (deathDim != null && player.level.dimension() == deathDim) { //world .getDim (look for return type matches)
                ClientUtils.drawMetalLine(playervec, Vector3d.atCenterOf(cap.getDeathLoc()), 3.0F, 0.9F, 0.85F, 0.0F);
            }
        }
        if (cap.isBurning(Metal.ELECTRUM)) {
            RegistryKey<World> spawnDim = cap.getSpawnDim();
            if (spawnDim == null && player.level.dimension() == World.OVERWORLD) { // overworld, no spawn --> use world spawn
                BlockPos spawnLoc = new BlockPos(player.level.getLevelData().getXSpawn(), player.level.getLevelData().getYSpawn(), player.level.getLevelData().getZSpawn());
                ClientUtils.drawMetalLine(playervec, Vector3d.atCenterOf(spawnLoc), 3.0F, 0.7F, 0.8F, 0.2F);

            } else if (spawnDim != null && player.level.dimension() == spawnDim) {
                ClientUtils.drawMetalLine(playervec, Vector3d.atCenterOf(cap.getSpawnLoc()), 3.0F, 0.7F, 0.8F, 0.2F);
            }
        }

        RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        double motionX, motionY, motionZ, magnitude;

        PlayerEntity player = this.mc.player;
        ISound sound = event.getSound();
        if ((player == null) || (sound == null) || !player.isAlive()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        if (cap.isBurning(Metal.TIN)) {

            magnitude = Math.sqrt(player.distanceToSqr(sound.getX(), sound.getY(), sound.getZ()));

            if (((magnitude) > 25) || ((magnitude) < 3)) {
                return;
            }
            Vector3d vec = player.position();
            double posX = vec.x(), posY = vec.y(), posZ = vec.z();
            // Spawn sound particles
            String soundName = sound.getLocation().toString();
            if (soundName.contains("entity") || soundName.contains("step")) {
                motionX = ((posX - (event.getSound().getX() + .5)) * -0.7) / magnitude;
                motionY = ((posY - (event.getSound().getY() + .2)) * -0.7) / magnitude;
                motionZ = ((posZ - (event.getSound().getZ() + .5)) * -0.7) / magnitude;
                this.mc.particleEngine.createParticle(new SoundParticleData(sound.getSource()), posX + (Math.sin(Math.toRadians(player.getYHeadRot())) * -.7d), posY + .2,
                                                      posZ + (Math.cos(Math.toRadians(player.getYHeadRot())) * .7d), motionX, motionY, motionZ);
            }
        }
    }
}
