package ovh.corail.recycler.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.recycler.core.Main;

public class ItemDiamondNugget extends Item {
	private static final String name = "diamond_nugget";
	
	public ItemDiamondNugget() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Main.tabRecycler);
		setMaxStackSize(64);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		list.add(TextFormatting.WHITE + I18n.translateToLocal("item." + name + ".desc"));
	}
}
