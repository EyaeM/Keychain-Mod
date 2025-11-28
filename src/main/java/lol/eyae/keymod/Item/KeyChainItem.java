package lol.eyae.keymod.Item;

import lol.eyae.keymod.menu.KeychainMenu;
import net.mehvahdjukaar.supplementaries.common.items.KeyItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class KeyChainItem extends Item {
    public static final int KEYCHAIN_SIZE = 9;

    public KeyChainItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                player.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.keychain");
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                        return new KeychainMenu(id, inv, stack);
                    }
                });
            }
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    public static ListTag getKeys(ItemStack keychain) {
        CustomData customData = keychain.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();

        if (!tag.contains("Keys", Tag.TAG_LIST)) {
            tag.put("Keys", new ListTag());
        }
        ListTag keys = tag.getList("Keys", Tag.TAG_COMPOUND);
        return keys;
    }

    public static ItemStack getKeyInSlot(ItemStack keychain, int slot, HolderLookup.Provider registries) {
        if (slot < 0 || slot >= KEYCHAIN_SIZE) return ItemStack.EMPTY;

        ListTag keys = getKeys(keychain);
        if (slot >= keys.size()) return ItemStack.EMPTY;

        CompoundTag keyTag = keys.getCompound(slot);
        if (keyTag.isEmpty()) return ItemStack.EMPTY;

        var result = ItemStack.CODEC.decode(registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), keyTag);

        ItemStack decoded = result.resultOrPartial(err -> {
        }).map(pair -> pair.getFirst()).orElse(ItemStack.EMPTY);

        return decoded;
    }

    public static void setKeyInSlot(ItemStack keychain, int slot, ItemStack key, HolderLookup.Provider registries) {
        if (slot < 0 || slot >= KEYCHAIN_SIZE) return;

        ListTag keys = getKeys(keychain);

        while (keys.size() <= slot) {
            keys.add(new CompoundTag());
        }

        CompoundTag keyTag = new CompoundTag();
        if (!key.isEmpty()) {
            ItemStack.CODEC.encodeStart(registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), key)
                    .resultOrPartial()
                    .ifPresent(tag -> {
                        if (tag instanceof net.minecraft.nbt.CompoundTag ct) {
                            keyTag.merge(ct);
                        }
                    });
        }
        keys.set(slot, keyTag);

        CustomData customData = keychain.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();
        tag.put("Keys", keys);
        keychain.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean hasMatchingKey(ItemStack keychain, String password, HolderLookup.Provider registries) {
        ListTag keys = getKeys(keychain);

        for (int i = 0; i < Math.min(keys.size(), KEYCHAIN_SIZE); i++) {
            CompoundTag keyTag = keys.getCompound(i);
            if (keyTag.isEmpty()) continue;

            ItemStack keyStack = ItemStack.parseOptional(registries, keyTag);
            if (keyStack.getItem() instanceof KeyItem) {
                String keyPassword = keyStack.getHoverName().getString();
                if (keyPassword.equals(password)) {
                    return true;
                }
            }
        }

        return false;
    }
}