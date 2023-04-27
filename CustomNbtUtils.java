package @package@;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Optional;

/**
 * 1.19.3,1.19.4
 */
public class CustomNbtUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static BlockState readBlockState(CompoundTag compoundTag) {
        if (!compoundTag.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(compoundTag.getString("Name")));
            BlockState blockState = block.defaultBlockState();
            if (compoundTag.contains("Properties", 10)) {
                CompoundTag compoundTag2 = compoundTag.getCompound("Properties");
                StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
                Iterator var = compoundTag2.getAllKeys().iterator();
                while (var.hasNext()) {
                    String string = (String) var.next();
                    Property<?> property = stateDefinition.getProperty(string);
                    if (property != null) {
                        blockState = setValueHelper(blockState, property, string, compoundTag2, compoundTag);
                    }
                }
            }
            return blockState;
        }
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S stateHolder, Property<T> property, String string, CompoundTag compoundTag, CompoundTag compoundTag2) {
        Optional<T> optional = property.getValue(compoundTag.getString(string));
        if (optional.isPresent()) {
            return stateHolder.setValue(property, optional.get());
        } else {
            LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{string, compoundTag.getString(string), compoundTag2.toString()});
            return stateHolder;
        }
    }
}