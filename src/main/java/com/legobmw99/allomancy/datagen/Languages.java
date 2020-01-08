package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.util.Metal;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class Languages extends LanguageProvider {
    public Languages(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.allomancy", "Allomancy");
        add("metals.iron", "Iron");
        add("metals.steel", "Steel");
        add("metals.tin", "Tin");
        add("metals.pewter", "Pewter");
        add("metals.bronze", "Bronze");
        add("metals.copper", "Copper");
        add("metals.zinc", "Zinc");
        add("metals.brass", "Brass");

        add(MaterialsSetup.TIN_ORE.get(), "Tin Ore");
        add(MaterialsSetup.LEAD_ORE.get(), "Lead Ore");
        add(MaterialsSetup.COPPER_ORE.get(), "Copper Ore");
        add(MaterialsSetup.ZINC_ORE.get(), "Zinc Ore");
        add(ExtrasSetup.IRON_BUTTON.get(), "Iron Button");
        add(ExtrasSetup.IRON_LEVER.get(), "Iron Lever");
        add("block.allomancy.iron_activation.lore",
                "This item seems too heavy to activate by ordinary means");

        add(ConsumeSetup.ALLOMANTIC_GRINDER.get(), "Hand Grinder");
        add(ConsumeSetup.LERASIUM_NUGGET.get(), "Lerasium Nugget");
        add("item.allomancy.lerasium_nugget.lore",
                "This item is endowed with strange powers, perhaps you should ingest it?");
        add(CombatSetup.MISTCLOAK.get(), "Mistcloak");
        add(CombatSetup.COIN_BAG.get(), "Coin Bag");
        add(CombatSetup.OBSIDIAN_DAGGER.get(), "Obsidian Dagger");
        add(CombatSetup.KOLOSS_BLADE.get(), "Koloss Blade");
        add("item.allomancy.koloss_blade.lore",
                "This item is too heavy for the average person to wield.");
        add(ConsumeSetup.VIAL.get(), "Allomantic Vial");

        add(MaterialsSetup.TIN_INGOT.get(), "Tin Ingot");
        add(MaterialsSetup.COPPER_INGOT.get(), "Copper Ingot");
        add(MaterialsSetup.BRONZE_INGOT.get(), "Bronze Ingot");
        add(MaterialsSetup.ZINC_INGOT.get(), "Zinc Ingot");
        add(MaterialsSetup.BRASS_INGOT.get(), "Brass Ingot");
        add(MaterialsSetup.LEAD_INGOT.get(), "Lead Ingot");

        add(MaterialsSetup.FLAKES.get(Metal.IRON.getIndex()).get(), "Iron Flakes");
        add(MaterialsSetup.FLAKES.get(Metal.STEEL.getIndex()).get(), "Steel Flakes");
        add(MaterialsSetup.FLAKES.get(Metal.TIN.getIndex()).get(), "Tin Flakes");
        add(MaterialsSetup.FLAKES.get(Metal.PEWTER.getIndex()).get(), "Pewter Flakes");
        add(MaterialsSetup.FLAKES.get(Metal.ZINC.getIndex()).get(), "Zinc Flakes");
        add(MaterialsSetup.FLAKES.get(Metal.BRASS.getIndex()).get(), "Brass Flakes");
        add(MaterialsSetup.FLAKES.get(Metal.COPPER.getIndex()).get(), "Copper Flakes");
        add(MaterialsSetup.FLAKES.get(Metal.BRONZE.getIndex()).get(), "Bronze Flakes");
        add(MaterialsSetup.FLAKES.get(8).get(), "Lead Flakes");

        add(CombatSetup.GOLD_NUGGET.get(), "Gold Nugget");
        add(CombatSetup.IRON_NUGGET.get(), "Iron Nugget");

        add("advancements.become_mistborn.title", "Become Mistborn!");
        add("advancements.become_mistborn.desc", "You have a power most people envy...");

        add("key.categories.allomancy", "Allomancy");
        add("key.burn", "Burn Metals");

        add("commands.allomancy.getpowers", "%s currently has Allomantic powers %s");
        add("commands.allomancy.addpower", "%s added Allomantic power %s");
        add("commands.allomancy.removepower", "%s removed Allomantic power %s");
        add("commands.allomancy.unrecognized", "Unrecognized Allomancy power '%s'");
    }

    @Override
    public String getName() {
        return "Allomancy Language";
    }
}
