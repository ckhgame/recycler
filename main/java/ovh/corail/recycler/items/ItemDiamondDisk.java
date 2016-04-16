package ovh.corail.recycler.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class ItemDiamondDisk extends Item {
	private static final String name = "diamond_disk";
	public ItemDiamondDisk() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabs.tabTools);
		setMaxDamage(5000);
		setMaxStackSize(1);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		/** TODO add info to lang */ 
		list.add(TextFormatting.WHITE + I18n.translateToLocal("item." + name + ".info"));
	}
}
