package ovh.corail.recycler.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class Disk extends BasicItem {
	public Disk() {
		super("diamond_disk");
	    setCreativeTab(CreativeTabs.tabTools);
		setMaxDamage(500);
		setMaxStackSize(1);
	}
}
