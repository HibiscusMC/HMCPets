package com.hibiscusmc.hmcpets.api.model.pathfinding;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.Map;


//This is for villagers
//Spent 30m debugging why this didn't work on wolves
public class PetFollowBehavior extends Behavior<LivingEntity> {

	private final Player owner;
	private final double speed;
	private final double minDistance;

	public PetFollowBehavior(org.bukkit.entity.Player ownerBukkit, double speed, double minDistance) {
		super(Map.of());
		this.owner = ((CraftPlayer) ownerBukkit).getHandle();
		this.speed = speed;
		this.minDistance = minDistance;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity mob) {
		if (owner == null) return false;
		double distSq = mob.distanceToSqr(owner);
		return distSq > minDistance * minDistance;
	}

	@Override
	protected void start(ServerLevel level, LivingEntity mob, long gameTime) {

	}

	@Override
	protected void stop(ServerLevel level, LivingEntity mob, long gameTime) {
		if(!(mob instanceof Mob mobInstance)) return;

		mobInstance.getNavigation().stop();
	}

	@Override
	protected void tick(ServerLevel level, LivingEntity mob, long gameTime) {
		if (owner == null) return;
		if(!(mob instanceof Mob mobInstance)) return;

		Vec3 ownerPos = owner.position();
		Vec3 mobPos = mob.position();

		//Calculate the direction vector from the owner position removing the mob position
		//Works because physics ig
		Vec3 dir = ownerPos.subtract(mobPos).normalize();

		Vec3 target = ownerPos.subtract(dir.scale(1.5));

		PathNavigation nav = mobInstance.getNavigation();
		nav.moveTo(target.x, target.y, target.z, speed);
	}
}
