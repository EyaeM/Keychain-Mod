package lol.eyae.keymod.mixin;

import lol.eyae.keymod.Item.KeyChainItem;
import net.mehvahdjukaar.supplementaries.common.block.tiles.KeyLockableTile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyLockableTile.class, remap = false)
public abstract class KeyLockableTileMixin {

    @Shadow
    public abstract String getPassword();

    @Inject(
            method = "handleAction",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkKeychainForKey(Player player, InteractionHand hand, ItemStack stack, String translName,
                                     CallbackInfoReturnable<Boolean> cir) {
        String requiredPassword = this.getPassword();

        if (requiredPassword == null || requiredPassword.isEmpty()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof KeyChainItem) {
            if (KeyChainItem.hasMatchingKey(mainHand, requiredPassword, player.registryAccess())) {
                cir.setReturnValue(true);
                return;
            }
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof KeyChainItem) {
            if (KeyChainItem.hasMatchingKey(offHand, requiredPassword, player.registryAccess())) {
                cir.setReturnValue(true);
                return;
            }
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.getItem() instanceof KeyChainItem) {
                if (KeyChainItem.hasMatchingKey(invStack, requiredPassword, player.registryAccess())) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        if (net.neoforged.fml.ModList.get().isLoaded("curios")) {
            try {
                if (lol.eyae.keymod.integration.CuriosCompat.hasMatchingKeyInCurios(player, requiredPassword)) {
                    cir.setReturnValue(true);
                    return;
                }
            } catch (Exception e) {
            }
        }
    }
}