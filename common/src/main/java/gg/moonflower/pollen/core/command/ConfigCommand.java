package gg.moonflower.pollen.core.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.command.PollenSuggestionProviders;
import gg.moonflower.pollen.api.command.argument.EnumArgument;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ConfigCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) { // Disable showing serverconfig on a dedicated server because the files won't exist
        dispatcher.register(Commands.literal("config").
            then(Commands.literal("showfile").then(Commands.argument("mod", StringArgumentType.word()).suggests(PollenSuggestionProviders.MOD_IDS).
                then(Commands.argument("type", dedicated ? EnumArgument.enumValues(PollinatedConfigType.COMMON, PollinatedConfigType.CLIENT) : EnumArgument.enumValues(PollinatedConfigType.values())).executes(ctx -> {
                    String modId = StringArgumentType.getString(ctx, "mod");
                    PollinatedConfigType type = EnumArgument.getEnum(PollinatedConfigType.class, ctx, "type");
                    String configFileName = getConfigFileName(modId, type);
                    if (configFileName != null) {
                        File f = new File(configFileName);
                        ctx.getSource().sendSuccess(Component.translatable("commands." + Pollen.MOD_ID + ".config.success",
                            modId,
                            type,
                            Component.literal(f.getName()).withStyle(ChatFormatting.UNDERLINE).
                                withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath())))
                        ), false);
                        return Command.SINGLE_SUCCESS;
                    } else {
                        ctx.getSource().sendFailure(Component.translatable("commands." + Pollen.MOD_ID + ".config.fail", modId, type));
                        return 0;
                    }
                })))
            )
        );
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @ExpectPlatform
    public static String getConfigFileName(String modId, PollinatedConfigType type) {
        return Platform.error();
    }
}
