Part 1
---
1. What is Newton's second law of motion? Do not just write F = ma; explain the intuition behind it.
    Newton's second law of motion is the acceleration of an object in relation to its mass and its rate of change in velocity.

2. What is momentum? Impulse?
    Momentum is the mass and speed of an object, or a force acting on an object for a given time.
    Impulse is the change in momentum through time.

3. What is a perfectly elastic collision? Give a real-life example of a (nearly) perfectly elastic collision.
    A perfectly elastic collision is one that has no kinetic energy lost when 2 objects collide. (i.e. Newton balls colliding)

4. What is a perfectly inelastic collision? Give a real-life example of a (nearly) perfectly inelastic collision.
    A perfectly inelastic collision is when the greatest amount of kinetic energy is lost in a collision. (i.e. throwing clay into another clay piece and at collision they stick)

5. What is the coefficient of restitution? What range of values can it take?
    The COR is the ratio of the final and init velocity difference between 2 colliding objects. (range = 0 to 1)

6. What is angular velocity? Angular acceleration?
    Angular velocity is the rate of change in angle in a rotating object.
    Angular acceleration is the change in angular velocity in a rotating object.

7. What is the moment of inertia? Angular momentum?
    Moment of Inertia is the resistance to angular acceleration in an object.
    Angular momentum is the moment of inertia and its angular velocity (i.e. how difficult it is to stop a fast rotating body vs a slow one).

8. What is torque?
    Torque is the equivalance of force but for rotating objects around an axis.

9. What is the relationship between torque, moment of inertia, and angular acceleration?
    Torque is the moment of inertia times the angular acceleration of an object. It is much like force but the mass is based on the shape of the rotating object.

Part 2
---
1. In Box2d, what is the difference between a Shape and a Body?
    A Body is like the physics based properties of an object while a Shape is the physical representation of it. A Shape interacts and collides while a Body tells us the momentum, speed, etc of that object.    

2. Between a Shape and a Body, which do you go to to change a physical property? To apply a force?
    To change a physical property, you go to Shape. To apply force you go to Body. 

3. When would you want a Body to contain multiple Shapes?
    When that Body has a complex shape that can't be represented by one Shape or when the Body has rigid limbs/attachments. 

4. In Box2d, what is a World? What are some of its important properties?
    A world is the entity in which the bodies live and it manages the environment physics (i.e. gravity, etc...). 

5. What is the advantage of sleeping an object with a Body? When would you want to do this?
    The advantage of sleeping an object is to exclude the object from expensive calulations but still allow it to respond to simple collisions. You may want to do this when objects are offscreen to lessen lag.

6. In Box2d, what is the difference between a static body and a dynamic body? How do you specify which type a body is?
    A static body does not react to collisions or world forces, but a dynamic body does. By default, every Body is static. To specify, you use the setType(BodyDef.BodyType type) method.

7. In Box2d, what is a Bullet and when do you want an object to be one?
    A Bullet is a Body, but will be treated for continuous collision detection such that collision points are accurate. You want an object to be a bullet when it travels too fast and is at risk of skipping collision detection.

8. In LibGDX, what can you do with a ContactListener inside of a World? How does this help with sensors?
    A ContactListener inside a World will call all methods in the ContactLisenter when a collision occurs between any of the bodies in the world. This helps, since sensors generate Contact callbacks to the ContactListener.

9. In LibGDX, what are the steps that you must take to add a Shape to a Body?
    1. Create a Body and set its initial properties
    2. Create a Shape and set its init properties
    3. Use the Body function CreateFixture to add the Shape to the Body

Part 4
---
1. Why can you not just set body type to BodyType.StaticBody, like we do the other platforms?
    You cannot use a StaticBody because the Spinner needs to be able to collide with other objects (i.e. the bullets, the player, etc)