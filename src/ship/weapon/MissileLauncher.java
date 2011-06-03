package ship.weapon;

public class MissileLauncher extends ship.weapon.Weapon {
    
    @Override
    public long getShotCoolDown() {
        return 1000;
    }

    @Override
    protected actor.Projectile newProjectile(actor.Actor ship) {
        return new actor.Missile(ship,0.5f,getMultiplier());
    }

    @Override
    public String getWeaponName() {
        return "Missile Launcher";
    }
}
