package moriyashiine.enchancement.mixin.perception.client;

import moriyashiine.enchancement.common.registry.ModEnchantments;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@ModifyVariable(method = "update", at = @At("STORE"), ordinal = 3)
	private float enchancement$perception(float value) {
		if (client.player != null && EnchantmentHelper.getEquipmentLevel(ModEnchantments.PERCEPTION, client.player) > 0) {
			return Math.max(1, value);
		}
		return value;
	}
}