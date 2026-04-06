package io.drahlek.cinderstride.config;

import lombok.Data;

@Data
public class CinderStrideConfigData {
    private boolean enabled = true;
    private int radius = 1;
    private long decayTicks = 40;    //each phase
}
