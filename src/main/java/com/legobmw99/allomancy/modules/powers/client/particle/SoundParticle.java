package com.legobmw99.allomancy.modules.powers.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundParticle extends SpriteTexturedParticle {
    public SoundParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, SoundCategory soundCategory) {
        super((ClientWorld) world, x, y, z, motionX, motionY, motionZ);
        // TODO: change sprite - look into  AtlasTexture.LOCATION_PARTICLES_TEXTURE
        setSprite(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(new ItemStack(Items.RABBIT_FOOT)));
        this.motionX = motionX;
        this.motionY = motionY + 0.009D;
        this.motionZ = motionZ;
        this.particleScale *= 1.2F;
        this.canCollide = false;
        setAlphaF(1.0F);
        setMaxAge(20);

        // Default: Blue
        setColor(0F, 1F, 0);


        if (soundCategory == SoundCategory.PLAYERS) {
            //Players: Yellow
            setColor(1F, 1F, 0F);
        }

        if (soundCategory == SoundCategory.NEUTRAL) {
            // Friendly mob: Green
            setColor(0F, 1F, 0.25F);
        }

        if (soundCategory == SoundCategory.HOSTILE) {
            // Hostile mob: Red
            setColor(1F, 0.15F, 0.15F);
        }

    }


    @Override
    public void tick() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }

        this.move(this.motionX, this.motionY, this.motionZ);

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.TERRAIN_SHEET;
    }

}
