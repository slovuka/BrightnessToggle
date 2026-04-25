package dev.slovuka.mixin;

import net.minecraft.client.Option;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.slovuka.BrightnessToggle.onGammaChanged;

@Mixin(ProgressOption.class)
public class ExampleMixin {
    @Inject(method = "set", at = @At("HEAD"))
    private void onSet(Options options, double value, CallbackInfo ci) {
        if ((Object)this == Option.GAMMA) {
            onGammaChanged(value);
        }
    }
}