package gg.moonflower.pollen.api.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * All providers for suggestion to the client about commands provided by Pollen.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class PollenSuggestionProviders {

    /**
     * Command suggestion provider for all loaded mod ids.
     */
    public static final SuggestionProvider<CommandSourceStack> MOD_IDS = SuggestionProviders.register(new ResourceLocation(Pollen.MOD_ID, "mod_ids"), (context, builder) -> SharedSuggestionProvider.suggest(Platform.getMods().map(PollinatedModContainer::getId), builder));

    private PollenSuggestionProviders() {
    }

    @ApiStatus.Internal
    public static void init() {
    }
}
