package io.drahlek.cinderstride.datacomponents;

import io.drahlek.dirigo.annotation.DataComponent;
import net.minecraft.core.component.DataComponentType;

public class CinderStrideDataComponents {
    @DataComponent(id = "upgraded", type = Boolean.class)
    public static DataComponentType<Boolean> UPGRADED;

    private CinderStrideDataComponents() {}

}
