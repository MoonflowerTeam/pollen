package gg.moonflower.pollen.core.mixin.forge;

import com.mojang.brigadier.CommandDispatcher;
import gg.moonflower.pollen.core.command.ConfigCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeInternalHandler.class)
public class ForgeInternalHandlerMixin {

    @Unique
    private RegisterCommandsEvent event;

    @Inject(method = "onCommandsRegister", at = @At("HEAD"), remap = false)
    public void captureEvent(RegisterCommandsEvent event, CallbackInfo ci) {
        this.event = event;
    }

    @Redirect(method = "onCommandsRegister", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/server/command/ConfigCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"), remap = false)
    public void redirectRegisterConfig(CommandDispatcher<CommandSourceStack> dispatcher) {
        ConfigCommand.register(dispatcher, this.event.getCommandSelection() == Commands.CommandSelection.DEDICATED);
    }
}
