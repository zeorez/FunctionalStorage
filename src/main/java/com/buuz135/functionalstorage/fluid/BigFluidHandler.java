package com.buuz135.functionalstorage.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public abstract class BigFluidHandler implements IFluidHandler, INBTSerializable<CompoundTag> {

    private CustomFluidTank[] tanks;

    public BigFluidHandler(int size, int capacity) {
        this.tanks = new CustomFluidTank[size];
        for (int i = 0; i < this.tanks.length; i++) {
            this.tanks[i] = new CustomFluidTank(capacity);
        }
    }

    public CustomFluidTank[] getTankList() {
        return this.tanks;
    }

    @Override
    public int getTanks() {
        return this.tanks.length;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return this.tanks[tank].getFluidInTank(0);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tanks[tank].getTankCapacity(0);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.tanks[tank].isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        for (CustomFluidTank tank : tanks) {
            if (tank.fill(resource, FluidAction.SIMULATE) != 0) {
                int ret = tank.fill(resource, action);
                if (action == FluidAction.EXECUTE) onChange();
                return ret;
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        for (CustomFluidTank tank : tanks) {
            if (!tank.drain(resource, FluidAction.SIMULATE).isEmpty()) {
                FluidStack ret = tank.drain(resource, action);
                if (action == FluidAction.EXECUTE) onChange();
                return ret;
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (CustomFluidTank tank : tanks) {
            if (!tank.drain(maxDrain, FluidAction.SIMULATE).isEmpty()) {
                FluidStack ret = tank.drain(maxDrain, action);
                if (action == FluidAction.EXECUTE) onChange();
                return ret;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        for (int i = 0; i < this.tanks.length; i++) {
            compoundTag.put(i + "", this.tanks[i].writeToNBT(new CompoundTag()));
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (int i = 0; i < this.tanks.length; i++) {
            this.tanks[i].readFromNBT(nbt.getCompound(i + ""));
        }
    }

    public abstract void onChange();

    public static class CustomFluidTank extends FluidTank {


        public CustomFluidTank(int capacity) {
            super(capacity);

        }

        public CustomFluidTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }


    }
}
