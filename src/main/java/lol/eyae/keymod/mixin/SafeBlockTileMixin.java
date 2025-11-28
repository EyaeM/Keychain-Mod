package lol.eyae.keymod.mixin;

import lol.eyae.keymod.Item.KeyChainItem;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SafeBlockTile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SafeBlockTile.class, remap = false)
public abstract class SafeBlockTileMixin {

    @Shadow
    public abstract String getPassword();

    @Inject(
            method = "canPlayerOpen",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkKeychainInCanPlayerOpen(Player player, boolean feedbackMessage, CallbackInfoReturnable<Boolean> cir) {
        if (player == null || player.isCreative()) {
            return;
        }

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
    }
}