package ovh.corail.recycler.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.packets.ButtonMessage;
import ovh.corail.recycler.recycling.RecyclingManager;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class GuiRecycler extends GuiContainer {
	private BlockPos currentPos;
	private TileEntityRecycler inventory;
	private EntityPlayer playerIn;
	/** TODO to be changed */
	private static ResourceLocation texture = new ResourceLocation(Main.MOD_ID + ":textures/recycler.png");
	private static ResourceLocation texture2 = new ResourceLocation(Main.MOD_ID + ":textures/items/recycler.png");

	public GuiRecycler(EntityPlayer playerIn, World worldIn, int x, int y, int z, TileEntityRecycler inventory) {
		super(new ContainerRecycler(playerIn, worldIn, x, y, z, inventory));
		this.xSize = 176;
		this.ySize = 166;
		this.inventory = inventory;
		this.currentPos = new BlockPos(x, y, z);

	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		this.buttonList.clear();
		/** TODO add translations */
		this.buttonList.add(
				new GuiButton(0, this.guiLeft + 40, this.guiTop + 12, 54, 16, I18n.translateToLocal("button.recycle")));
		this.buttonList.add(new GuiButton(1, this.guiLeft + 95, this.guiTop + 5, 16, 16, "Auto"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glScalef(1F, 1F, 1F);
		mc.renderEngine.bindTexture(texture);
		int posX = ((this.width - this.xSize) / 2);
		int posY = ((this.height - this.ySize) / 2);
		this.drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);
		zLevel = 100.0F;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int guiLeft = (this.width) / 2;
		int guiTop = (this.height) / 2;
		if (inventory.getStackInSlot(0) != null) {
			RecyclingManager rm = RecyclingManager.getInstance();
			int num_recipe = rm.hasRecipe(inventory.getStackInSlot(0));
			if (num_recipe > 0) {
				int inputCount = rm.getRecipe(num_recipe).getItemRecipe().stackSize;
				this.fontRendererObj.drawString("X " + Integer.toString(inputCount), (32), (14), 0xffffff);
			}
			/** TODO change about progress bar */
			if (inventory.isWorking()) {
				mc.renderEngine.bindTexture(texture2);
				drawTexturedModalRect(90, 19, 100, 104, 22, 12);
				int widthWorking = (int) Math.floor((double) inventory.getPercentWorking() * 20.0 / 100);
				drawTexturedModalRect(91, 20, 40, 140, widthWorking, 10);
				this.fontRendererObj.drawString(Integer.toString(inventory.getPercentWorking()) + " %", (93), (22),
						0xffffff);
			}
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 != 28 && par2 != 156) {
			if (par2 == 1) {
				this.mc.thePlayer.closeScreen();
			}
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		/** TODO BUTTON MESSAGE */
		PacketHandler.INSTANCE.sendToServer(new ButtonMessage(button.id, inventory.getPos()));
		switch (button.id) {
		/** TODO check if useless */
		case 0: /** Recycle */
			//inventory.recycle();
			break;
		case 1: /** Auto-recycle */
			//inventory.switchWorking();
			break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
