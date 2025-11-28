package lol.eyae.keymod.Registration;

import lol.eyae.keymod.Keymod;
import lol.eyae.keymod.menu.KeychainMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class KeyMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Keymod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<KeychainMenu>> KEYCHAIN =
            MENU_TYPES.register("keychain", () ->
                    IMenuTypeExtension.create((windowId, inv, data) -> {
                        var player = inv.player;
                        var mainHand = player.getMainHandItem();
                        var offHand = player.getOffhandItem();

                        var keychainStack = mainHand.getItem() instanceof lol.eyae.keymod.Item.KeyChainItem
                                ? mainHand : offHand;

                        return new KeychainMenu(windowId, inv, keychainStack);
                    })
            );
}