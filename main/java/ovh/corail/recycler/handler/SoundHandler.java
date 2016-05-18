package ovh.corail.recycler.handler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.core.Main;

public class SoundHandler {
	public static SoundEvent recycler;

	public static void registerSounds() {
		recycler = registerSound("recycler");
	}

	private static SoundEvent registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(Main.MOD_ID, soundName);
		return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
	}
}
