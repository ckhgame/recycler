package ovh.corail.recycler.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class ItemIronNugget extends Item {
	private static final String name = "iron_nugget";

	public ItemIronNugget() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabs.tabMaterials);
		setMaxStackSize(64);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		/** TODO add info to lang */
		list.add(TextFormatting.WHITE + I18n.translateToLocal("item." + name + ".info"));
	}
}
