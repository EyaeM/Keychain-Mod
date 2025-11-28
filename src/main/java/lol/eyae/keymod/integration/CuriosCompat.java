package lol.eyae.keymod.integration;

import lol.eyae.keymod.Item.KeyChainItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

public class CuriosCompat {
    public static void init() {
    }

    public static boolean hasMatchingKeyInCurios(Player player, String password) {
        List<SlotResult> found = CuriosApi.getCuriosHelper().findCurios(player,
                i -> i.getItem() instanceof KeyChainItem);

        if (found.isEmpty()) return false;

        for (var slot : found) {
            ItemStack stack = slot.stack();
            if (KeyChainItem.hasMatchingKey(stack, password, player.registryAccess())) {
                return true;
            }
        }
        return false;
    }
}