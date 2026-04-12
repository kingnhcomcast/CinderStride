package io.drahlek.cinderstride.config;

import lombok.Data;

@Data
public class CinderStrideConfigData {
    private int radius = 1;
    private long decayTicks = 40;    //each phase
    private float durabilityLossChance = 0.25f; //percent chance each cooled block causes 1 point of durability
}
