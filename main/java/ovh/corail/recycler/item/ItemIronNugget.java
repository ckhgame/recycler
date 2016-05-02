package ovh.corail.recycler.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import ovh.corail.recycler.core.Main;

public class ItemIronNugget extends Item {
	private static final String name = "iron_nugget";
	
	public ItemIronNugget() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Main.tabRecycler);
		setMaxStackSize(64);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		super.addInformation(stack, player, list, advanced);
		list.add(TextFormatting.WHITE + I18n.translateToLocal("item." + name + ".desc"));
	}
}
