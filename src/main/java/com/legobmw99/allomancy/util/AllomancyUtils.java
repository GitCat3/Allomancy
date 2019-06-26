package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.network.NetworkHelper;
import com.legobmw99.allomancy.network.packets.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.network.packets.UpdateBurnPacket;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Contains all static, common methods in one place
 */

public class AllomancyUtils {


    public static final byte PUSH = 1;
    public static final byte PULL = -1;


    /**
     * Draws a line from the player (denoted pX,Y,Z) to the given set of
     * coordinates (oX,Y,Z) in a certain color (r,g,b)
     *
     * @param width the width of the line
     */
    @OnlyIn(Dist.CLIENT)
    public static void drawMetalLine(double pX, double pY, double pZ, double oX, double oY, double oZ, float width,
                                     float r, float g, float b) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslated(-pX, -pY, -pZ);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glLineWidth(width);
        GL11.glColor3f(r, g, b);

        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex3d(pX, pY - 0.5, pZ);
        GL11.glVertex3d(oX, oY, oZ);

        GL11.glEnd();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }


    /**
     * Determines if a block is metal or not
     *
     * @param block to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isBlockMetal(Block block) {
        return AllomancyConfig.whitelist.contains(block.getRegistryName().toString());
    }

    /**
     * Determines if an item is metal or not
     *
     * @param item to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isItemMetal(ItemStack item) {
        return AllomancyConfig.whitelist.contains(item.getItem().getRegistryName().toString());
    }

    /**
     * Determines if an entity is metal or not
     *
     * @param entity to be checked
     * @return Whether or not the entity is metallic
     */
    public static boolean isEntityMetal(Entity entity) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof ItemEntity) {
            return isItemMetal(((ItemEntity) entity).getItem());
        }
        if (entity instanceof ItemFrameEntity) {
            return isItemMetal(((ItemFrameEntity) entity).getDisplayedItem());
        }

        if (entity instanceof FallingBlockEntity){
            return isBlockMetal(((FallingBlockEntity)entity).getBlockState().getBlock());
        }
        //if (entity instanceof EntityIronNugget || entity instanceof EntityGoldNugget) {
        //    return true;
        //}
        if (entity instanceof AbstractMinecartEntity) {
            return true;
        }
        if (entity instanceof MobEntity) {
            MobEntity ent = (MobEntity) entity;
            if (ent instanceof IronGolemEntity) {
                return true;
            }
            if (isItemMetal(ent.getHeldItem(Hand.MAIN_HAND)) || isItemMetal(ent.getHeldItem(Hand.OFF_HAND))) {
                return true;
            }
            for (ItemStack i : ent.getArmorInventoryList()) {
                if (isItemMetal(i)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Move an entity either toward or away from an anchor point
     *
     * @param directionScalar the direction and (possibly) scalar multiple of the magnitude
     * @param toMove          the entity to move
     * @param vec             the point being moved toward or away from
     */
    public static void move(double directionScalar, Entity toMove, BlockPos vec) {

        double motionX, motionY, motionZ, magnitude;
        if (toMove.isPassenger()) {
            toMove = toMove.getRidingEntity();
        }
        // Calculate the length of the vector between the entity and anchor
        magnitude = Math.sqrt(Math.pow((toMove.posX - (double) (vec.getX() + .5)), 2)
                + Math.pow((toMove.posY - (double) (vec.getY() + .5)), 2)
                + Math.pow((toMove.posZ - (double) (vec.getZ() + .5)), 2));
        // Get a unit(-ish) vector in the direction of motion
        motionX = ((toMove.posX - (double) (vec.getX() + .5)) * directionScalar * (1.1) / magnitude);
        motionY = ((toMove.posY - (double) (vec.getY() + .5)) * directionScalar * (1.1) / magnitude);
        motionZ = ((toMove.posZ - (double) (vec.getZ() + .5)) * directionScalar * (1.1) / magnitude);
        // Move along that vector, additively increasing motion until you max
        // out at the above values
        double x = toMove.getMotion().getX(), y = toMove.getMotion().getY(), z = toMove.getMotion().getZ();
        toMove.setMotion(Math.abs(x + motionX) > 0.01
                ? MathHelper.clamp(x + motionX, -Math.abs(motionX), motionX) : 0, Math.abs(y + motionY) > 0.01
                ? MathHelper.clamp(y + motionY, -Math.abs(motionY), motionY) : 0, Math.abs(z + motionZ) > 0.01
                ? MathHelper.clamp(z + motionZ, -Math.abs(motionZ), motionZ) : 0);

        toMove.velocityChanged = true;

        // Only save players from fall damage
        if (toMove instanceof ServerPlayerEntity) {
            toMove.fallDistance = 0;
        }

    }


    /**
     * Used to toggle a metal's burn state and play a sound effect
     *
     * @param metal      the index of the metal to toggle
     * @param capability the capability being handled
     */
    public static void toggleMetalBurn(byte metal, AllomancyCapability capability) {
        NetworkHelper.sendToServer(new UpdateBurnPacket(metal, !capability.getMetalBurning(metal)));

        if (capability.getMetalAmounts(metal) > 0) {
            capability.setMetalBurning(metal, !capability.getMetalBurning(metal));
        }
        // play a sound effect
        if (capability.getMetalBurning(metal)) {
            Allomancy.proxy.getClientPlayer().playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1,
                    5);
        } else {
            Allomancy.proxy.getClientPlayer().playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1,
                    4);
        }
    }

    /**
     * Runs each worldTick, checking the burn times, abilities, and metal
     * amounts. Then syncs to the client to make sure everyone is on the same
     * page
     *
     * @param cap    the AllomancyCapabilities data
     * @param player the player being checked
     */
    public static void updateMetalBurnTime(AllomancyCapability cap, ServerPlayerEntity player) {
        for (int i = 0; i < 8; i++) {
            if (cap.getMetalBurning(i)) {
                if (cap.getAllomancyPower() != i && cap.getAllomancyPower() != 8) {
                    // put out any metals that the player shouldn't be able to burn
                    cap.setMetalBurning(i, false);
                    NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
                } else {
                    cap.setBurnTime(i, cap.getBurnTime(i) - 1);
                    if (cap.getBurnTime(i) == 0) {
                        cap.setBurnTime(i, cap.MAX_BURN_TIME[i]);
                        cap.setMetalAmounts(i, cap.getMetalAmounts(i) - 1);
                        NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
                        if (cap.getMetalAmounts(i) == 0) {
                            cap.setMetalBurning(i, false);
                            NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
                        }
                    }
                }

            }
        }
    }

    @Nullable
    public static RayTraceResult getMouseOverExtended(float dist) {
        Minecraft mc = Minecraft.getInstance();
        float partialTicks = mc.getRenderPartialTicks();
        RayTraceResult objectMouseOver = null;
        Entity pointedEntity;
        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            if (mc.world != null) {
                objectMouseOver = entity.func_213324_a(dist, partialTicks, false);
                Vec3d vec3d = entity.getEyePosition(partialTicks);
                boolean flag = false;
                int i = 3;
                double d1 = dist * dist;

                if (objectMouseOver != null) {
                    d1 = objectMouseOver.getHitVec().squareDistanceTo(vec3d);
                }

                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * dist, vec3d1.y * dist, vec3d1.z * dist);
                float f = 1.0F;
                AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(dist)).grow(1.0D, 1.0D, 1.0D);
                EntityRayTraceResult entityraytraceresult = ProjectileHelper.func_221273_a(entity, vec3d, vec3d2, axisalignedbb, (e) -> {
                    return true;
                }, d1);
                if (entityraytraceresult != null) {
                    Entity entity1 = entityraytraceresult.getEntity();
                    Vec3d vec3d3 = entityraytraceresult.getHitVec();
                    double d2 = vec3d.squareDistanceTo(vec3d3);
                    if (d2 < d1) {
                        objectMouseOver = entityraytraceresult;
                    }
                }

            }
        }
        return objectMouseOver;

    }
}
