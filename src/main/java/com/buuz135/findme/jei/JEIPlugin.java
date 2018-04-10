package com.buuz135.findme.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

    public static IJeiRuntime runtime;

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }
}
