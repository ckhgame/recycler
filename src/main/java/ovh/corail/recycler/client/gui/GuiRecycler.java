package ovh.corail.recycler.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import ovh.corail.recycler.common.ContainerRecycler;
import ovh.corail.recycler.common.Main;
import ovh.corail.recycler.common.MainUtil;
import ovh.corail.recycler.common.RecyclingManager;
import ovh.corail.recycler.common.handler.PacketHandler;
import ovh.corail.recycler.common.packets.ButtonMessage;
import ovh.corail.recycler.common.tileentity.RecyclerTile;

public class GuiRecycler extends GuiContainer {
	public int i = 0;
	public int j = 0;
	public int k = 0;
	public RecyclerTile inventory;
	private static ResourceLocation texture = new ResourceLocation(Main.MODID + ":textures/recycler.png");

	public GuiRecycler(EntityPlayer player, World world, int x, int y, int z, RecyclerTile inventory) {
		super(new ContainerRecycler(player, world, x, y, z, inventory));
		this.inventory = inventory;
		this.i = x;
		this.j = y;
		this.k = z;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int posX = (this.width) / 2;
		int posY = (this.height) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		zLevel = 100.0F;
	}

	public void updateScreen() {
		super.updateScreen();
	}

	protected void keyTyped(char par1, int par2) {

		if (par2 != 28 && par2 != 156) {
			if (par2 == 1) {
				this.mc.thePlayer.closeScreen();
			}
		}

	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		int posX = (this.width) / 2;
		int posY = (this.height) / 2;
		if (inventory.getStackInSlot(0)!=null) {
			RecyclingManager rm = RecyclingManager.getInstance();
			int num_recipe=rm.hasRecipe(inventory.getStackInSlot(0));
			if (num_recipe>0) {
				int inputCount=rm.getRecipe(num_recipe).getItemRecipe().stackSize;
				this.fontRendererObj.drawString("X "+Integer.toString(inputCount), (32), (17), 0xffffff);
			}		
		}
	}

	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - 176) / 2;
		this.guiTop = (this.height - 166) / 2;
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		int posX = (this.width) / 2;
		int posY = (this.height) / 2;
		this.buttonList.add(new GuiButton(0, this.guiLeft + 50, this.guiTop + 12, 54, 16, MainUtil.getTranslation("button.recycle")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		PacketHandler.INSTANCE.sendToServer(new ButtonMessage(button.id, inventory.getPos().getX(),
				inventory.getPos().getY(), inventory.getPos().getZ()));
		switch (button.id) {
		case 0: // Recycle
			inventory.recycle();
			break;
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

}
