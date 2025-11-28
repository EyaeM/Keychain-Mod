package lol.eyae.keymod.menu;

import lol.eyae.keymod.Item.KeyChainItem;
import lol.eyae.keymod.Registration.KeyMenuTypes;
import net.mehvahdjukaar.supplementaries.common.items.KeyItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KeychainMenu extends AbstractContainerMenu {
    private final ItemStack keychainStack;
    private final Container keychainContainer;
    private final HolderLookup.Provider registries;

    public KeychainMenu(int id, Inventory playerInv, ItemStack keychain) {
        super(KeyMenuTypes.KEYCHAIN.get(), id);
        this.keychainStack = keychain;
        this.registries = playerInv.player.registryAccess();
        this.keychainContainer = new SimpleContainer(9);

        for (int i = 0; i < 9; i++) {
            this.addSlot(new KeychainSlot(keychainContainer, i, 8 + i * 18, 20));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 109));
        }

        this.loadKeysFromNBT();
    }

    private void loadKeysFromNBT() {
        for (int i = 0; i < 9; i++) {
            ItemStack key = KeyChainItem.getKeyInSlot(keychainStack, i, registries);
            keychainContainer.setItem(i, key);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < 9) {
                if (!this.moveItemStackTo(slotStack, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotStack.getItem() instanceof KeyItem) {
                    if (!this.moveItemStackTo(slotStack, 0, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem() == keychainStack ||
                player.getOffhandItem() == keychainStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (int i = 0; i < 9; i++) {
            KeyChainItem.setKeyInSlot(keychainStack, i, keychainContainer.getItem(i), registries);
        }
    }

    private static class KeychainSlot extends Slot {
        public KeychainSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof KeyItem;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}