package gg.moonflower.pollen.core.mixin.fabric.client;

import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin {

    @Mutable
    @Shadow
    @Final
    private String name;

    @Unique
    private static String captureLocation;
    @Unique
    private static Program.Type captureType;

    @ModifyVariable(method = "<init>", index = 2, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/ShaderInstance;vertexFormat:Lcom/mojang/blaze3d/vertex/VertexFormat;"), argsOnly = true)
    public String clearLocationString(String value) {
        return "";
    }

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceProvider;openAsReader(Lnet/minecraft/resources/ResourceLocation;)Ljava/io/BufferedReader;", shift = At.Shift.BEFORE), index = 4)
    public ResourceLocation modifyLocation(ResourceLocation location) {
        ResourceLocation id = new ResourceLocation(this.name);
        this.name = id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
        return new ResourceLocation(id.getNamespace(), "shaders/core/" + id.getPath() + ".json");
    }

    @Inject(method = "getOrCreate", at = @At("HEAD"))
    private static void captureGetOrCreate(ResourceProvider resourceProvider, Program.Type type, String string, CallbackInfoReturnable<Program> cir) {
        captureLocation = string;
        captureType = type;
    }

    @Inject(method = "getOrCreate", at = @At("TAIL"))
    private static void deleteGetOrCreate(ResourceProvider resourceProvider, Program.Type type, String string, CallbackInfoReturnable<Program> cir) {
        captureLocation = null;
        captureType = null;
    }

    @ModifyVariable(method = "getOrCreate", at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation", ordinal = 0, shift = At.Shift.BEFORE), ordinal = 1)
    private static String modifyStaticLocation(String value) {
        ResourceLocation id = new ResourceLocation(captureLocation);
        return id.getNamespace() + ":shaders/core/" + id.getPath() + captureType.getExtension();
    }
}
