package io.drahlek.cinderstride.blocks;

import com.mojang.serialization.MapCodec;
import io.drahlek.cinderstride.mixin.CampfireBlockEntityAccessor;
import io.drahlek.dirigo.annotation.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import org.joml.Vector3f;

import static java.lang.Math.floorMod;

@Block(id = HearthFireBlock.NAME, registerItem = false, validBlockEntityTypes = {"minecraft:campfire"})
public class HearthFireBlock extends CampfireBlock {
    public static final String NAME = "hearthfire";
    public static final MapCodec<HearthFireBlock> CODEC = simpleCodec(HearthFireBlock::new);
    private static final Vector3f HEARTH_SMOKE_COLOR = rgb(0xDD6B49);
    private static final Vector3f HEARTH_SMOKE_HOT_COLOR = rgb(0xFF9E66);
    private static final Vector3f HEARTH_WHOOSH_COLOR = rgb(0xFFD08A);
    private static final Vector3f HEARTH_EMBER_START_COLOR = rgb(0xFFB55E);
    private static final Vector3f HEARTH_EMBER_END_COLOR = rgb(0xB73C22);

    public HearthFireBlock(BlockBehaviour.Properties properties) {
        super(
                true,
                1,
                properties
                        .mapColor(MapColor.PODZOL)
                        .instrument(NoteBlockInstrument.BASS)
                        .strength(2.0F)
                        .sound(SoundType.WOOD)
                        .lightLevel(state -> state.getValue(LIT) ? 15 : 0)
                        .noOcclusion()
                        .ignitedByLava()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<CampfireBlock> codec() {
        return (MapCodec<CampfireBlock>) (MapCodec<?>) CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }

        if (random.nextInt(10) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    SoundEvents.CAMPFIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    0.45F + random.nextFloat() * 0.35F,
                    0.45F + random.nextFloat() * 0.2F,
                    false
            );
        }

        if (random.nextInt(5) == 0) {
            spawnGroundSparks(level, pos, random);
        }

        spawnAmbientEmbers(level, pos, random, random.nextInt(4) == 0);

        if (random.nextInt(14) == 0) {
            spawnBigPuff(level, pos, random);
            level.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.65D,
                    pos.getZ() + 0.5D,
                    SoundEvents.BLAZE_SHOOT,
                    SoundSource.BLOCKS,
                    0.09F + random.nextFloat() * 0.05F,
                    0.45F + random.nextFloat() * 0.15F,
                    false
            );
        }
    }

    private static void spawnGroundSparks(Level level, BlockPos pos, RandomSource random) {
        int sparkCount = 1 + random.nextInt(2);
        for (int i = 0; i < sparkCount; i++) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;
            double y = pos.getY() + 0.12D + random.nextDouble() * 0.08D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;
            double xVelocity = (random.nextDouble() - 0.5D) * 0.03D;
            double yVelocity = 5.0E-4D + random.nextDouble() * 0.002D;
            double zVelocity = (random.nextDouble() - 0.5D) * 0.03D;

            level.addParticle(ParticleTypes.LAVA, x, y, z, xVelocity, yVelocity, zVelocity);
            if (random.nextFloat() < 0.7F) {
                level.addParticle(ParticleTypes.SMALL_FLAME, x, y + 0.01D, z, xVelocity * 0.35D, yVelocity * 6.0D, zVelocity * 0.35D);
            }
        }
    }

    private static Vector3f rgb(int color) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        return new Vector3f(red, green, blue);
    }

    public static boolean isHearthfire(BlockState state) {
        return state.getBlock() instanceof HearthFireBlock;
    }

    public static BlockState copyCampfireState(BlockState source, BlockState target) {
        return target
                .setValue(CampfireBlock.LIT, source.getValue(CampfireBlock.LIT))
                .setValue(CampfireBlock.SIGNAL_FIRE, source.getValue(CampfireBlock.SIGNAL_FIRE))
                .setValue(CampfireBlock.WATERLOGGED, source.getValue(CampfireBlock.WATERLOGGED))
                .setValue(CampfireBlock.FACING, source.getValue(CampfireBlock.FACING));
    }

    public static void transferCampfireData(CampfireBlockEntity source, CampfireBlockEntity target) {
        if (source == null || target == null) {
            return;
        }

        NonNullList<ItemStack> sourceItems = source.getItems();
        NonNullList<ItemStack> targetItems = target.getItems();
        for (int i = 0; i < sourceItems.size(); i++) {
            targetItems.set(i, sourceItems.get(i).copy());
        }

        int[] sourceProgress = ((CampfireBlockEntityAccessor) source).cinderstride$getCookingProgress();
        int[] targetProgress = ((CampfireBlockEntityAccessor) target).cinderstride$getCookingProgress();
        System.arraycopy(sourceProgress, 0, targetProgress, 0, Math.min(sourceProgress.length, targetProgress.length));

        int[] sourceTimes = ((CampfireBlockEntityAccessor) source).cinderstride$getCookingTime();
        int[] targetTimes = ((CampfireBlockEntityAccessor) target).cinderstride$getCookingTime();
        System.arraycopy(sourceTimes, 0, targetTimes, 0, Math.min(sourceTimes.length, targetTimes.length));

        target.setChanged();
        if (target.getLevel() != null) {
            BlockPos pos = target.getBlockPos();
            BlockState state = target.getBlockState();
            target.getLevel().sendBlockUpdated(pos, state, state, 3);
        }
    }

    public static void cookTick(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            CampfireBlockEntity blockEntity,
            RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> cachedCheck
    ) {
        boolean hasCookingItem = false;
        NonNullList<ItemStack> items = blockEntity.getItems();
        int[] progress = ((CampfireBlockEntityAccessor) blockEntity).cinderstride$getCookingProgress();
        int[] cookingTime = ((CampfireBlockEntityAccessor) blockEntity).cinderstride$getCookingTime();

        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) {
                continue;
            }

            hasCookingItem = true;
            progress[slot] += 2;
            if (progress[slot] < cookingTime[slot]) {
                continue;
            }

            SingleRecipeInput input = new SingleRecipeInput(stack);
            ItemStack result = cachedCheck.getRecipeFor(input, level)
                    .map(holder -> holder.value().assemble(input, level.registryAccess()))
                    .orElse(stack);
            if (!result.isItemEnabled(level.enabledFeatures())) {
                continue;
            }

            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
            items.set(slot, ItemStack.EMPTY);
            level.sendBlockUpdated(pos, state, state, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
        }

        if (hasCookingItem) {
            blockEntity.setChanged();
        }
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity) {
        RandomSource random = level.getRandom();
        boolean signalFire = state.getValue(CampfireBlock.SIGNAL_FIRE);

        if (random.nextFloat() < 0.18F) {
            int smokeCount = 2 + random.nextInt(2);
            for (int i = 0; i < smokeCount; i++) {
                spawnWarmSmoke(level, pos, random, signalFire, false);
            }

            if (random.nextFloat() < 0.35F) {
                spawnWarmSmoke(level, pos, random, signalFire, true);
            }
        }

        int facingIndex = state.getValue(CampfireBlock.FACING).get2DDataValue();
        NonNullList<ItemStack> items = blockEntity.getItems();

        for (int slot = 0; slot < items.size(); slot++) {
            if (items.get(slot).isEmpty() || random.nextFloat() >= 0.3F) {
                continue;
            }

            Direction direction = Direction.from2DDataValue(floorMod(slot + facingIndex, 4));
            double x = pos.getX() + 0.5D
                    - direction.getStepX() * 0.3125F
                    + direction.getClockWise().getStepX() * 0.3125F;
            double y = pos.getY() + 0.3125D;
            double z = pos.getZ() + 0.5D
                    - direction.getStepZ() * 0.3125F
                    + direction.getClockWise().getStepZ() * 0.3125F;

            int puffCount = 2 + random.nextInt(2);
            for (int puff = 0; puff < puffCount; puff++) {
                double xOffset = random.nextDouble() / 4.0D * (random.nextBoolean() ? 1.0D : -1.0D);
                double yOffset = random.nextDouble() * 0.3D;
                double zOffset = random.nextDouble() / 4.0D * (random.nextBoolean() ? 1.0D : -1.0D);

                level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0D, 8.0E-4D, 0.0D);
                if (random.nextFloat() < 0.65F) {
                    level.addParticle(
                            new DustParticleOptions(HEARTH_SMOKE_HOT_COLOR, 0.95F + random.nextFloat() * 0.35F),
                            x + xOffset,
                            y + yOffset + 0.05D,
                            z + zOffset,
                            0.0D,
                            0.015D + random.nextDouble() * 0.015D,
                            0.0D
                    );
                }
            }
        }
    }

    public static void spawnAmbientEmbers(Level level, BlockPos pos, RandomSource random, boolean extra) {
        int count = extra ? 2 + random.nextInt(2) : 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.45D;
            double y = pos.getY() + 0.35D + random.nextDouble() * 0.25D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.45D;
            double xVelocity = (random.nextDouble() - 0.5D) * 0.01D;
            double yVelocity = 0.02D + random.nextDouble() * 0.03D + (extra ? 0.01D : 0.0D);
            double zVelocity = (random.nextDouble() - 0.5D) * 0.01D;

            level.addParticle(
                    new DustColorTransitionOptions(HEARTH_EMBER_START_COLOR, HEARTH_EMBER_END_COLOR, 0.8F + random.nextFloat() * 0.35F),
                    x,
                    y,
                    z,
                    xVelocity,
                    yVelocity,
                    zVelocity
            );
            level.addParticle(ParticleTypes.FLAME, x, y, z, xVelocity * 0.35D, yVelocity * 0.65D, zVelocity * 0.35D);
            if (random.nextFloat() < 0.65F) {
                level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, xVelocity, yVelocity * 0.8D, zVelocity);
            }
        }
    }

    public static void spawnBigPuff(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 3 + random.nextInt(2); i++) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.42D;
            double y = pos.getY() + 0.45D + random.nextDouble() * 0.35D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.42D;
            double xVelocity = (random.nextDouble() - 0.5D) * 0.012D;
            double yVelocity = 0.1D + random.nextDouble() * 0.04D;
            double zVelocity = (random.nextDouble() - 0.5D) * 0.012D;
            double smokeXVelocity = xVelocity * 0.2D;
            double smokeZVelocity = zVelocity * 0.2D;

            level.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x, y, z, smokeXVelocity, yVelocity, smokeZVelocity);
            level.addParticle(
                    new DustColorTransitionOptions(HEARTH_WHOOSH_COLOR, HEARTH_SMOKE_COLOR, 2.0F + random.nextFloat() * 0.55F),
                    x,
                    y + 0.04D,
                    z,
                    xVelocity,
                    0.04D + random.nextDouble() * 0.02D,
                    zVelocity
            );
            level.addParticle(
                    new DustParticleOptions(HEARTH_WHOOSH_COLOR, 1.75F + random.nextFloat() * 0.45F),
                    x,
                    y,
                    z,
                    xVelocity * 0.9D,
                    0.04D,
                    zVelocity * 0.9D
            );
            level.addParticle(ParticleTypes.FLAME, x, y + 0.08D, z, xVelocity * 0.45D, 0.035D, zVelocity * 0.45D);
            if (random.nextFloat() < 0.85F) {
                level.addParticle(ParticleTypes.SMALL_FLAME, x, y + 0.05D, z, xVelocity * 0.7D, 0.03D, zVelocity * 0.7D);
            }
        }

        int burstCount = 5 + random.nextInt(3);
        for (int i = 0; i < burstCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double radius = 0.08D + random.nextDouble() * 0.2D;
            double x = pos.getX() + 0.5D + Math.cos(angle) * radius;
            double y = pos.getY() + 0.62D + random.nextDouble() * 0.18D;
            double z = pos.getZ() + 0.5D + Math.sin(angle) * radius;
            double xVelocity = Math.cos(angle) * (0.012D + random.nextDouble() * 0.01D);
            double yVelocity = 0.05D + random.nextDouble() * 0.025D;
            double zVelocity = Math.sin(angle) * (0.012D + random.nextDouble() * 0.01D);

            level.addParticle(
                    new DustColorTransitionOptions(HEARTH_WHOOSH_COLOR, HEARTH_SMOKE_HOT_COLOR, 1.9F + random.nextFloat() * 0.5F),
                    x,
                    y,
                    z,
                    xVelocity,
                    yVelocity,
                    zVelocity
            );
            level.addParticle(ParticleTypes.FLAME, x, y + 0.03D, z, xVelocity * 0.45D, yVelocity * 0.55D, zVelocity * 0.45D);
            if (random.nextFloat() < 0.8F) {
                level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, xVelocity * 0.55D, yVelocity * 0.65D, zVelocity * 0.55D);
            }
        }

        spawnAmbientEmbers(level, pos, random, true);
        spawnAmbientEmbers(level, pos, random, true);
    }

    private static void spawnWarmSmoke(Level level, BlockPos pos, RandomSource random, boolean signalFire, boolean largePuff) {
        double x = pos.getX() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1.0D : -1.0D);
        double y = pos.getY() + random.nextDouble() + random.nextDouble() + (largePuff ? 0.25D : 0.0D);
        double z = pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1.0D : -1.0D);
        double xVelocity = (random.nextDouble() - 0.5D) * 0.006D;
        double yVelocity = (signalFire ? 0.11D : 0.085D) + (largePuff ? 0.02D : 0.0D);
        double zVelocity = (random.nextDouble() - 0.5D) * 0.006D;
        double smokeXVelocity = xVelocity * 0.18D;
        double smokeZVelocity = zVelocity * 0.18D;

        level.addAlwaysVisibleParticle(
                signalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE,
                true,
                x,
                y,
                z,
                smokeXVelocity,
                yVelocity,
                smokeZVelocity
        );
        level.addParticle(
                ParticleTypes.SMOKE,
                x,
                y - 0.15D,
                z,
                smokeXVelocity,
                0.012D + random.nextDouble() * 0.008D,
                smokeZVelocity
        );
        level.addParticle(
                new DustColorTransitionOptions(
                        largePuff ? HEARTH_SMOKE_HOT_COLOR : HEARTH_SMOKE_COLOR,
                        HEARTH_SMOKE_COLOR,
                        largePuff ? 1.45F + random.nextFloat() * 0.35F : 1.0F + random.nextFloat() * 0.2F
                ),
                x,
                y - 0.1D,
                z,
                xVelocity * 0.9D,
                0.02D + random.nextDouble() * 0.01D,
                zVelocity * 0.9D
        );
        if (largePuff) {
            level.addParticle(
                    new DustParticleOptions(HEARTH_SMOKE_HOT_COLOR, 1.15F + random.nextFloat() * 0.3F),
                    x,
                    y - 0.04D,
                    z,
                    xVelocity * 0.7D,
                    0.026D,
                    zVelocity * 0.7D
            );
            level.addParticle(ParticleTypes.FLAME, x, y, z, xVelocity * 0.35D, 0.02D, zVelocity * 0.35D);
        }
    }
}
