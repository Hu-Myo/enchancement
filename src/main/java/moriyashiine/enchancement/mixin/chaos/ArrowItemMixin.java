/*
 * All Rights Reserved (c) MoriyaShiine
 */

package moriyashiine.enchancement.mixin.chaos;

import moriyashiine.enchancement.common.init.ModEnchantments;
import moriyashiine.enchancement.common.init.ModTags;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {
	@Inject(method = "createArrow", at = @At("RETURN"))
	private void enchancement$chaos(World world, ItemStack stack, LivingEntity shooter, CallbackInfoReturnable<PersistentProjectileEntity> cir) {
		if (stack.getItem() == Items.ARROW && cir.getReturnValue() instanceof ArrowEntity arrow) {
			boolean hasChaos = shooter instanceof PlayerEntity ? EnchancementUtil.hasEnchantment(ModEnchantments.CHAOS, shooter.getActiveItem()) : EnchancementUtil.hasEnchantment(ModEnchantments.CHAOS, shooter);
			if (hasChaos) {
				StatusEffect effect = null;
				int attempts = 0;
				if (shooter.isSneaking()) {
					while (effect == null || effect.getCategory() != StatusEffectCategory.BENEFICIAL || Registries.STATUS_EFFECT.entryOf(Registries.STATUS_EFFECT.getKey(effect).orElse(null)).isIn(ModTags.StatusEffects.CHAOS_UNCHOOSABLE)) {
						effect = Registries.STATUS_EFFECT.get(shooter.getRandom().nextInt(Registries.STATUS_EFFECT.size()));
						if (++attempts > 128) {
							return;
						}
					}
				} else {
					while (effect == null || effect.getCategory() != StatusEffectCategory.HARMFUL || Registries.STATUS_EFFECT.entryOf(Registries.STATUS_EFFECT.getKey(effect).orElse(null)).isIn(ModTags.StatusEffects.CHAOS_UNCHOOSABLE)) {
						effect = Registries.STATUS_EFFECT.get(shooter.getRandom().nextInt(Registries.STATUS_EFFECT.size()));
						if (++attempts > 128) {
							return;
						}
					}
				}
				arrow.initFromStack(PotionUtil.setCustomPotionEffects(new ItemStack(Items.TIPPED_ARROW), Collections.singleton(new StatusEffectInstance(effect, effect.isInstant() ? 1 : 200))));
				arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
			}
		}
	}
}
