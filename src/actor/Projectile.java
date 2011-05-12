package actor;

import math.Quaternion;
import math.Vector3;

public abstract class Projectile extends Actor{
    private static final long serialVersionUID = 8097256529802244313L;
    protected final float DEFAULT_SPEED = 1.0f;
    protected final float DEFAULT_SIZE = 0.1f;

    protected static final String MODEL_NAME = "bullet";

    public Projectile(Actor actor){
        super();
        this.velocity = actor.velocity.plus(actor.getDirection().times(DEFAULT_SPEED));
        position = new Vector3(actor.getPosition());
        rotation = new Quaternion(actor.getRotation());
        parentId = actor.id;
        setSize(DEFAULT_SIZE);
        modelName = MODEL_NAME;
    }
}