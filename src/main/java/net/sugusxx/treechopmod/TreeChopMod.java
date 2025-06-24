package net.sugusxx.treechopmod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.HashSet;
import java.util.Set;

@Mod(TreeChopMod.Mod_ID)
public class TreeChopMod {
    public static final String Mod_ID = "treechopmod";
    public static final Logger LOGGER  = LogManager.getLogger();

    public TreeChopMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static final Set<Block> VALID_LOGS = Set.of(
            Blocks.OAK_LOG,
            Blocks.BIRCH_LOG,
            Blocks.SPRUCE_LOG,
            Blocks.JUNGLE_LOG,
            Blocks.ACACIA_LOG,
            Blocks.DARK_OAK_LOG,
            Blocks.MANGROVE_LOG,
            Blocks.CHERRY_LOG
    );

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        Block block = state.getBlock();

        if (VALID_LOGS.contains(block)) {
            Set<BlockPos> logs = new HashSet<>();

            if (event.getLevel() instanceof ServerLevel serverLevel) {
                findConnectedLogs(serverLevel, event.getPos(), logs, 30);

                for (BlockPos pos : logs) {
                    serverLevel.destroyBlock(pos, true);
                }
            }
        }
    }

    private void findConnectedLogs(ServerLevel world, BlockPos pos, Set<BlockPos> found, int limit) {
        if (found.size() >= limit || found.contains(pos)) return;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != Blocks.OAK_LOG && state.getBlock() != Blocks.BIRCH_LOG) return;

        found.add(pos);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    findConnectedLogs(world, pos.offset(dx, dy, dz), found, limit);
                }
            }
        }
    }
}