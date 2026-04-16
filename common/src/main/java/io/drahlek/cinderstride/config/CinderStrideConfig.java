package io.drahlek.cinderstride.config;

import io.drahlek.cinderstride.Constants;
import io.drahlek.dirigo.config.Config;

public class CinderStrideConfig extends Config<CinderStrideConfigData> {
    private static final CinderStrideConfig INSTANCE = new CinderStrideConfig();

    private CinderStrideConfig() {
        super(Constants.MOD_ID, Constants.MOD_NAME, Constants.MOD_ID + ".json", CinderStrideConfigData.class);
    }

    public static CinderStrideConfig instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("CinderStrideConfig has not been initialized");
        }

        return INSTANCE;
    }

    public static CinderStrideConfigData data() {
        return instance().get();
    }
}
