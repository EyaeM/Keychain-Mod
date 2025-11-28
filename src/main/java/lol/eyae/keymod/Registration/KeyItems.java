package lol.eyae.keymod.Registration;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.level.block.Block;
import lol.eyae.keymod.Keymod;
import lol.eyae.keymod.Item.*;

public class KeyItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(Keymod.MODID);
    public static final DeferredItem<Item> KEYCHAIN = REGISTRY.register("keychain", () -> new KeyChainItem(new Item.Properties()));

    private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
        return block(block, new Item.Properties());
    }

    private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }
}