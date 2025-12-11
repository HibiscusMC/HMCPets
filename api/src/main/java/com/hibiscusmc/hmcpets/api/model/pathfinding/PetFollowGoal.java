package com.hibiscusmc.hmcpets.api.model.pathfinding;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.EnumSet;

public class PetFollowGoal extends Goal {

	private final Mob pet;
	private final Player owner;
	private final double speed;
	private final double minDistance;

	public PetFollowGoal(Mob pet, org.bukkit.entity.Player ownerBukkit,
	                     double speed,
	                     double minDistance) {
		this.pet = pet;
		this.owner = ((CraftPlayer) ownerBukkit).getHandle();
		this.speed = speed;
		this.minDistance = minDistance;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (owner == null) return false;
		double distSq = pet.distanceToSqr(owner);
		return distSq > minDistance * minDistance;
		//A max distance here *could* be used, but does it matter considering the pet will be TPed if out of range?
	}

	@Override
	public boolean canContinueToUse() {
		return canUse();
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {
		pet.getNavigation().stop();
	}

	@Override
	public void tick() {
		if (owner == null) return;

		Vec3 ownerPos = owner.position();
		Vec3 petPos = pet.position();

		//Calculate diff vector between owner & pet, physics goes brr
		Vec3 dir = ownerPos.subtract(petPos).normalize();

		Vec3 target = ownerPos.subtract(dir.scale(1.5));

		pet.getNavigation().moveTo(target.x, target.y, target.z, speed);
		pet.getLookControl().setLookAt(owner, 10.0F, pet.getMaxHeadXRot());
	}
}