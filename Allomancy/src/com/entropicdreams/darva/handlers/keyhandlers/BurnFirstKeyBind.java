package com.entropicdreams.darva.handlers.keyhandlers;

import java.util.EnumSet;

import com.entropicdreams.darva.AllomancyData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class BurnFirstKeyBind extends KeyHandler {

	private boolean keyDown = false;
	public BurnFirstKeyBind(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel() {
		return "Burn First";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		EntityClientPlayerMP player;
		player = Minecraft.getMinecraft().thePlayer;
		AllomancyData data;
		if (player == null)
			return;
		if (keyDown == false)
		{
			keyDown = true;
			data = AllomancyData.forPlayer(player);
			switch (data.getSelected())
			{
			case 1:
				//toggle iron.
				data.setbIron(!data.isbIron());
				break;
			case 2:
				//toggle Tin.
				data.setbTin(!data.isbTin());
				break;
			case 3:
				//toggle Copper.
				data.setbCopper(!data.isbCopper());
				break;
			case 4:
				//toggle Zinc.
				data.setbZinc(!data.isbZinc());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (keyDown == true)
		{
			keyDown = false;
		}
				
	}

	@Override
	public EnumSet<TickType> ticks() {
		return  EnumSet.of(TickType.CLIENT);
	}

}