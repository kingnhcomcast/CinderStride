package io.drahlek.cinderstride.items;

import io.drahlek.dirigo.annotation.Item;
import net.minecraft.ChatFormatting;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

@Item(id = "cinder_template", creativeTab = "ingredients")
public class CinderTemplate extends SmithingTemplateItem {
    public static final String NAME = "cinder_template";

    private static final Component APPLIES_TO = Component.translatable("item.cinderstride.cinder_template.applies_to")
            .withStyle(ChatFormatting.BLUE);
    private static final Component INGREDIENTS = Component.translatable("item.cinderstride.cinder_template.ingredients")
            .withStyle(ChatFormatting.BLUE);
    private static final Component UPGRADE_DESCRIPTION = Component.translatable("upgrade.cinderstride.cinder_template")
            .withStyle(ChatFormatting.GRAY);
    private static final Component BASE_SLOT_DESCRIPTION = Component.translatable("item.cinderstride.cinder_template.base_slot_description")
            .withStyle(ChatFormatting.BLUE);
    private static final Component ADDITIONS_SLOT_DESCRIPTION = Component.translatable("item.cinderstride.cinder_template.additions_slot_description")
            .withStyle(ChatFormatting.BLUE);
    private static final List<ResourceLocation> BASE_SLOT_EMPTY_ICONS = List.of(
            ResourceLocation.withDefaultNamespace("container/slot/boots")
    );
    private static final List<ResourceLocation> ADDITION_SLOT_EMPTY_ICONS = List.of(
            ResourceLocation.withDefaultNamespace("container/slot/ingot")
    );

    public CinderTemplate(net.minecraft.world.item.Item.Properties properties) {
        super(
                APPLIES_TO,
                INGREDIENTS,
                UPGRADE_DESCRIPTION,
                BASE_SLOT_DESCRIPTION,
                ADDITIONS_SLOT_DESCRIPTION,
                BASE_SLOT_EMPTY_ICONS,
                ADDITION_SLOT_EMPTY_ICONS,
                new FeatureFlag[0]
        );
    }
}
