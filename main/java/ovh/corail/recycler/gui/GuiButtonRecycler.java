package ovh.corail.recycler.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import ovh.corail.recycler.core.Main;

public class GuiButtonRecycler extends GuiButton {
	protected static final ResourceLocation newButtonTextures = new ResourceLocation(Main.MOD_ID + ":textures/gui/recycler.png");

	public GuiButtonRecycler(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, buttonText);
		this.width = widthIn;
		this.height = heightIn;
	}

	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			int buttonHeight = 14;
			int buttonWidth = 74;
			
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(newButtonTextures);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width	&& mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,	GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 183+(i==2?14:0)/*46 + i * buttonHeight*/, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, buttonWidth - this.width / 2, 183+(i==2?14:0)/*46 + i * buttonHeight*/, this.width / 2, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;

			if (packedFGColour != 0) {
				j = packedFGColour;
			} else if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j = 16777120;
			}
			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
		}
	}
}
