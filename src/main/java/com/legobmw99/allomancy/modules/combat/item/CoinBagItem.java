package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.setup.AllomancySetup;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CoinBagItem extends ShootableItem {

    public static final Predicate<ItemStack> NUGGETS = (stack) -> stack.getItem() == Items.IRON_NUGGET || stack.getItem() == Items.GOLD_NUGGET;

    public CoinBagItem() {
        super(AllomancySetup.createStandardItemProperties().maxStackSize(1));
    }

    @Nonnull
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return NUGGETS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.findAmmo(player.getHeldItem(hand));
        if (itemstack.getItem() instanceof ArrowItem) { // the above get function has silly default behavior
            itemstack = new ItemStack(Items.GOLD_NUGGET, 1);
        }

        if (AllomancyCapability.forPlayer(player).isBurning(Metal.STEEL)) {    // make sure there is always an item available
            if (!world.isRemote) {

                Ammo type = (itemstack.getItem() == Items.GOLD_NUGGET) ? Ammo.GOLD : Ammo.IRON;

                ProjectileNuggetEntity nugget_projectile = new ProjectileNuggetEntity(player, world, itemstack, type.damage);
                //          formerly called .shoot()
                nugget_projectile.func_234612_a_(player, player.rotationPitch, player.rotationYawHead, type.arg1, type.arg2, type.arg3);
                world.addEntity(nugget_projectile);


                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }

                return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));

            }


        }
        return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));

    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public int func_230305_d_() { // TODO figure out what this does - new in 1.16, possibly speed?
        return 0;
    }

    private enum Ammo {
        GOLD(4.0F, 2.0F, 4.0F, 1.0F),
        IRON(5.0F, 2.0F, 2.25F, 2.5F);
        float damage, arg1, arg2, arg3;

        Ammo(float damage, float v1, float v2, float v3) {
            this.damage = damage;
            arg1 = v1;
            arg2 = v2;
            arg3 = v3;
        }
    }
}