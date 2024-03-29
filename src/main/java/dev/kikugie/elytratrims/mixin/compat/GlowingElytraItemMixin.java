package dev.kikugie.elytratrims.mixin.compat;

import dev.kikugie.elytratrims.common.config.ConfigTesters;
import dev.kikugie.elytratrims.common.plugin.MixinConfigurable;
import dev.kikugie.elytratrims.common.plugin.RequireMod;
import dev.kikugie.elytratrims.common.plugin.RequireTest;
import dev.kikugie.elytratrims.common.recipe.GlowingItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@RequireTest(ConfigTesters.Glow.class)
@RequireMod("betterend")
@Mixin(targets = {"org.betterx.betterend.item.ArmoredElytra", "org.betterx.betterend.item.CrystaliteElytra"})
@MixinConfigurable
public class GlowingElytraItemMixin implements GlowingItem {
}