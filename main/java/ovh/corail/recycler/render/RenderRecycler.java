package ovh.corail.recycler.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class RenderRecycler extends TileEntitySpecialRenderer<TileEntityRecycler> {
	private ModelRecycler modelRecycler = new ModelRecycler();
	@Override
	public void renderTileEntityAt(TileEntityRecycler te, double x, double y, double z, float partialTicks,
			int destroyStage) {
		
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
				GlStateManager.pushMatrix();
				this.bindTexture(new ResourceLocation("recycler:textures/blocks/recycler_front.png"));
				this.modelRecycler.render();
				GlStateManager.popMatrix();
				GlStateManager.popMatrix();
		
	}

}
