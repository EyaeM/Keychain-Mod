package lol.eyae.keymod.Registration;

import lol.eyae.keymod.Keymod;
import lol.eyae.keymod.Registration.KeyMenuTypes;
import lol.eyae.keymod.menu.KeychainScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Keymod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyClient {
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(KeyMenuTypes.KEYCHAIN.get(), KeychainScreen::new);
    }
}