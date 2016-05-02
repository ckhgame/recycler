package ovh.corail.recycler.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BasicItem extends Item {
	public BasicItem(String unlocalizedName) {
		super();
		setUnlocalizedName(unlocalizedName);
		setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(this, unlocalizedName);
	}
}
