package ovh.corail.recycler.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ovh.corail.recycler.common.CommonProxy;
import ovh.corail.recycler.common.Main;
import ovh.corail.recycler.common.MainUtil;

public class ClientProxy extends CommonProxy {

	public void  renderBlocks() {
		//for (int i = 0; i < Main.blocks.size(); i++) {
		//	renderItem(Item.getItemFromBlock(Main.blocks.get(i)));
		//}
		renderItem(Item.getItemFromBlock(MainUtil.recycler));
	}
	public void renderItem(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Main.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
	public void  renderItems() {
		//for (int i = 0; i < Main.items.size(); i++) {
		//	renderItem(Main.items.get(i));
		//}
		renderItem(MainUtil.iron_nugget);
		renderItem(MainUtil.diamond_nugget);
		renderItem(MainUtil.diamond_disk);
	}
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		renderItems();
		renderBlocks();
	}
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
}
