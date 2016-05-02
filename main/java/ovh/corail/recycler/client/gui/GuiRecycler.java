package ovh.corail.recycler.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import ovh.corail.recycler.container.ContainerRecycler;
import ovh.corail.recycler.core.Helper;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.core.RecyclingManager;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.packet.ButtonMessage;
import ovh.corail.recycler.packet.ProgressMessage;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class GuiRecycler extends GuiContainer {
	public int i = 0;
	public int j = 0;
	public int k = 0;
	public TileEntityRecycler inventory;
	public EntityPlayer currentPlayer;
	private static ResourceLocation texture = new ResourceLocation(Main.MOD_ID + ":textures/recycler.png");
	private static ResourceLocation texture2 = new ResourceLocation(Main.MOD_ID + ":textures/items/recycler.png");
	
	public GuiRecycler(EntityPlayer player, World world, int x, int y, int z, TileEntityRecycler inventory) {
		super(new ContainerRecycler(player, world, x, y, z, inventory));
		this.inventory = inventory;
		this.currentPlayer = player;
		this.i = x;
		this.j = y;
		this.k = z;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		zLevel = 100.0F;	
	}

	protected void keyTyped(char par1, int par2) {

		if (par2 != 28 && par2 != 156) {
			if (par2 == 1) {
				this.mc.thePlayer.closeScreen();
			}
		}

	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		int guiLeft = (this.width) / 2;
		int guiTop = (this.height) / 2;
		if (inventory.getStackInSlot(0)!=null) {
			RecyclingManager rm = RecyclingManager.getInstance();
			int num_recipe=rm.hasRecipe(inventory.getStackInSlot(0));
			if (num_recipe>0) {
				int inputCount=rm.getRecipe(num_recipe).getItemRecipe().stackSize;
				this.fontRendererObj.drawString("X "+Integer.toString(inputCount), (32), (14), 0xffffff);
			}
			// TODO Current Changes
			if (inventory.isWorking()) {
				mc.renderEngine.bindTexture(texture2);
				drawTexturedModalRect(90, 19, 100, 104, 22, 12);
				int widthWorking=(int) Math.floor((double) inventory.getPercentWorking()*20.0/100);
				drawTexturedModalRect(91, 20, 40, 140, widthWorking, 10);
				this.fontRendererObj.drawString(Integer.toString(inventory.getPercentWorking())+" %", (93), (22), 0xffffff);
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
		// TODO Current Changes
		this.buttonList.add(new GuiButton(0, this.guiLeft + 40, this.guiTop + 12, 54, 16, I18n.translateToLocal("button.recycle")));
		//this.buttonList.add(new GuiButton(1, this.guiLeft + 95, this.guiTop + 5, 16, 16, "Auto"));
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
			if (inventory.recycle(currentPlayer)) {
				currentPlayer.addStat(Main.achievementBuildDisk, 1);
			}
			break;
		case 1: // Auto-recycle
			inventory.switchWorking();
			break;
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

}
