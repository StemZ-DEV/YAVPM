package com.stemz.yavpm.features;

import java.util.List;

import com.stemz.yavpm.YAVPM;
import com.stemz.yavpm.config.ConfigManager;
import com.stemz.yavpm.config.YAVPMConfig;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public final class RightClickHarvest {
    private RightClickHarvest() { }
    private static YAVPMConfig config;

    public static void register() {
        UseBlockCallback.EVENT.register(RightClickHarvest::onUseBlock);
        YAVPM.LOGGER.info("Registered Right Click Harvest");
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        config = ConfigManager.get();

        if(!config.rightClickHarvest) return ActionResult.PASS;

        if(player == null || hand == Hand.MAIN_HAND || player.isSpectator()) return ActionResult.PASS;

        if(player.isSneaking() && config.rightClickHarvestSneakBypass) return ActionResult.PASS;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);

        boolean isCrop = state.getBlock() instanceof CropBlock;
        boolean isWart = state.isOf(Blocks.NETHER_WART);

        if(!isCrop && !isWart) return ActionResult.PASS;

        if(!isCropAllowed(state, config)) return ActionResult.PASS;

        if(!isPlantMature(state)) return ActionResult.PASS;

        if(world.isClient()) return ActionResult.SUCCESS;

        ServerWorld server = (ServerWorld) world;

        List<ItemStack> drops = Block.getDroppedStacks(state, server, pos, null, player, player.getMainHandStack());

        Item seedItem = getSeedFor(state);
        boolean replanted = false;

        if(config.rightClickPlant && seedItem != null){
            for(ItemStack drop : drops){
                if(drop.getItem() == seedItem && drop.getCount() > 0){
                    drop.decrement(1);
                    replanted = true;
                    break;
                }
            }
        }

        if(replanted) {
            if(isCrop){
                CropBlock crop = (CropBlock) state.getBlock();
                server.setBlockState(pos, crop.withAge(0), Block.NOTIFY_ALL);
            }else if (isWart){
                server.setBlockState(pos, state.with(NetherWartBlock.AGE, 0), Block.NOTIFY_ALL);
            }

            if(config.rightClickHarvestParticles){
                server.syncWorldEvent(WorldEvents.BONE_MEAL_USED, pos, 0);
                int color = getParticleColorFor(state);

                DustParticleEffect dust = new DustParticleEffect(color, 1.0f);
                server.spawnParticles(dust, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        10, 0.25, 0.12, 0.25, 0.0);
            }

            if(config.rightClickHarvestSound){
                var sound = getBreakSoundFor(state);
                server.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        } else {
            server.breakBlock(pos, replanted, player);
        }

        for(ItemStack drop : drops){
            if(!drop.isEmpty())
                ItemScatterer.spawn(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        }

        player.swingHand(hand, true);

        return ActionResult.SUCCESS;
    }

    private static boolean isPlantMature(BlockState state) {
        if (state.getBlock() instanceof CropBlock crop) {
            try {
                return crop.isMature(state);
            } catch (Throwable t) {
                if (state.contains(CropBlock.AGE)) {
                    int cur = state.get(CropBlock.AGE);
                    return cur >= crop.getMaxAge();
                }
                return false;
            }
        }

        if (state.isOf(Blocks.NETHER_WART)) {
            return state.get(NetherWartBlock.AGE) >= 3;
        }
        return false;
    }

    private static SoundEvent getBreakSoundFor(BlockState state) {
        if (state.isOf(net.minecraft.block.Blocks.NETHER_WART))
            return SoundEvents.BLOCK_NETHER_WART_BREAK;

        return SoundEvents.BLOCK_CROP_BREAK;
    }

    private static boolean isCropAllowed(BlockState state, YAVPMConfig config) {
        if (state.isOf(net.minecraft.block.Blocks.WHEAT))     return config.rightClickHarvestWheat;
        if (state.isOf(net.minecraft.block.Blocks.CARROTS))   return config.rightClickHarvestCarrots;
        if (state.isOf(net.minecraft.block.Blocks.POTATOES))  return config.rightClickHarvestPotatoes;
        if (state.isOf(net.minecraft.block.Blocks.BEETROOTS)) return config.rightClickHarvestBeetroot;
        if (state.isOf(net.minecraft.block.Blocks.NETHER_WART)) return config.rightClickHarvestNetherWart;
        return false;
    }

    private static Item getSeedFor(BlockState state) {
        if (state.isOf(net.minecraft.block.Blocks.WHEAT))     return Items.WHEAT_SEEDS;
        if (state.isOf(net.minecraft.block.Blocks.CARROTS))   return Items.CARROT;
        if (state.isOf(net.minecraft.block.Blocks.POTATOES))  return Items.POTATO;
        if (state.isOf(net.minecraft.block.Blocks.BEETROOTS)) return Items.BEETROOT_SEEDS;
        if (state.isOf(net.minecraft.block.Blocks.NETHER_WART)) return Items.NETHER_WART;
        return null;
    }

    private static int getParticleColorFor(BlockState state) {
        if (state.isOf(net.minecraft.block.Blocks.WHEAT))     return 0xFFC21E;
        if (state.isOf(net.minecraft.block.Blocks.CARROTS))   return 0xF49C1A;
        if (state.isOf(net.minecraft.block.Blocks.POTATOES))  return 0xC8A15A;
        if (state.isOf(net.minecraft.block.Blocks.BEETROOTS)) return 0xE23B3B;
        if (state.isOf(net.minecraft.block.Blocks.NETHER_WART)) return 0xC21E3A;
        return 0xA0E080;
    }
}
