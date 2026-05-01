package io.drahlek.cinderstride.config;

import io.drahlek.dirigo.annotation.ConfigSetting;
import lombok.Data;

@Data
public class CinderStrideConfigData {
    @ConfigSetting(value = "radius", min = 1, max = 4, defaultValue = "1")
    private int radius = 1;
    @ConfigSetting(value = "decayTicks", min = 20, max = 120, defaultValue = "40")
    private long decayTicks = 40;    //each phase
    @ConfigSetting(value = "durabilityLossChance", min = 0f, max = 1f, defaultValue = "0.25f")
    private float durabilityLossChance = 0.25f; //percent chance each cooled block causes 1 point of durability

    public int getRadius() {
        return radius;
    }

    public long getDecayTicks() {
        return decayTicks;
    }

    public float getDurabilityLossChance() {
        return durabilityLossChance;
    }
}
