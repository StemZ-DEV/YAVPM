package com.stemz.yavpm;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static RegistryKey<ItemGroup> GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(YAVPM.MOD_ID, "yavpm_group"));
    public static final ItemGroup YAVPM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.JAR_ITEM))
            .displayName(Text.translatable("itemGroup.yavpm"))
            .build();

    public static final Item JAR_ITEM = register("jar_item", Item::new, new Item.Settings());




    public static void init() {
        Registry.register(Registries.ITEM_GROUP, GROUP_KEY, YAVPM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(GROUP_KEY).register(entries -> {
           entries.add(JAR_ITEM);
        });
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(YAVPM.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

}
