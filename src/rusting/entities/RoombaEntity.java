package rusting.entities;

import arc.func.Cons;
import arc.math.geom.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

//hehehe I'm in danger
public class RoombaEntity implements ElevationMovec, Entityc {

    @Override
    public SolidPred solidity() {
        return null;
    }

    @Override
    public boolean canPass(int tileX, int tileY) {
        return false;
    }

    @Override
    public boolean canPassOn() {
        return false;
    }

    @Override
    public boolean moving() {
        return false;
    }

    @Override
    public void move(float cx, float cy) {

    }

    @Override
    public Vec2 vel() {
        return null;
    }


    @Override
    public float drag() {
        return 0;
    }

    @Override
    public void drag(float drag) {

    }

    @Override
    public boolean checkTarget(boolean targetAir, boolean targetGround) {
        return false;
    }

    @Override
    public boolean isGrounded() {
        return false;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public boolean canDrown() {
        return false;
    }

    @Override
    public void landed() {

    }

    @Override
    public void wobble() {

    }

    @Override
    public void moveAt(Vec2 vector, float acceleration) {

    }

    @Override
    public float floorSpeedMultiplier() {
        return 0;
    }

    @Override
    public float elevation() {
        return 0;
    }

    @Override
    public void elevation(float elevation) {

    }

    @Override
    public boolean hovering() {
        return false;
    }

    @Override
    public void hovering(boolean hovering) {

    }

    @Override
    public float drownTime() {
        return 0;
    }

    @Override
    public void drownTime(float drownTime) {

    }

    @Override
    public float splashTimer() {
        return 0;
    }

    @Override
    public void splashTimer(float splashTimer) {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public float healthf() {
        return 0;
    }

    @Override
    public boolean isAdded() {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void remove() {

    }

    @Override
    public void add() {

    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public <T extends Entityc> T self() {
        return null;
    }

    @Override
    public <T> T as() {
        return null;
    }

    @Override
    public <T> T with(Cons<T> cons) {
        return null;
    }

    @Override
    public int classId() {
        return 0;
    }

    @Override
    public boolean serialize() {
        return false;
    }

    @Override
    public void read(Reads read) {

    }

    @Override
    public void write(Writes write) {

    }

    @Override
    public void afterRead() {

    }

    @Override
    public int id() {
        return 0;
    }

    @Override
    public void id(int id) {

    }

    @Override
    public float hitSize() {
        return 0;
    }

    @Override
    public void getCollisions(Cons<QuadTree> consumer) {

    }

    @Override
    public void updateLastPosition() {

    }

    @Override
    public void collision(Hitboxc other, float x, float y) {

    }

    @Override
    public float deltaLen() {
        return 0;
    }

    @Override
    public float deltaAngle() {
        return 0;
    }

    @Override
    public boolean collides(Hitboxc other) {
        return false;
    }

    @Override
    public void hitbox(Rect rect) {

    }

    @Override
    public void hitboxTile(Rect rect) {

    }

    @Override
    public float lastX() {
        return 0;
    }

    @Override
    public void lastX(float lastX) {

    }

    @Override
    public float lastY() {
        return 0;
    }

    @Override
    public void lastY(float lastY) {

    }

    @Override
    public float deltaX() {
        return 0;
    }

    @Override
    public void deltaX(float deltaX) {

    }

    @Override
    public float deltaY() {
        return 0;
    }

    @Override
    public void deltaY(float deltaY) {

    }

    @Override
    public void hitSize(float hitSize) {

    }

    @Override
    public void killed() {

    }

    @Override
    public void kill() {

    }

    @Override
    public void heal() {

    }

    @Override
    public boolean damaged() {
        return false;
    }

    @Override
    public void damagePierce(float amount, boolean withEffect) {

    }

    @Override
    public void damagePierce(float amount) {

    }

    @Override
    public void damage(float amount) {

    }

    @Override
    public void damage(float amount, boolean withEffect) {

    }

    @Override
    public void damageContinuous(float amount) {

    }

    @Override
    public void damageContinuousPierce(float amount) {

    }

    @Override
    public void clampHealth() {

    }

    @Override
    public void heal(float amount) {

    }

    @Override
    public void healFract(float amount) {

    }

    @Override
    public float health() {
        return 0;
    }

    @Override
    public void health(float health) {

    }

    @Override
    public float hitTime() {
        return 0;
    }

    @Override
    public void hitTime(float hitTime) {

    }

    @Override
    public float maxHealth() {
        return 0;
    }

    @Override
    public void maxHealth(float maxHealth) {

    }

    @Override
    public boolean dead() {
        return false;
    }

    @Override
    public void dead(boolean dead) {

    }

    @Override
    public void set(float x, float y) {

    }

    @Override
    public void set(Position pos) {

    }

    @Override
    public void trns(float x, float y) {

    }

    @Override
    public void trns(Position pos) {

    }

    @Override
    public int tileX() {
        return 0;
    }

    @Override
    public int tileY() {
        return 0;
    }

    @Override
    public Floor floorOn() {
        return null;
    }

    @Override
    public Block blockOn() {
        return null;
    }

    @Override
    public boolean onSolid() {
        return false;
    }

    @Override
    public Tile tileOn() {
        return null;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public float x() {
        return 0;
    }

    @Override
    public void x(float x) {

    }

    @Override
    public float y() {
        return 0;
    }

    @Override
    public void y(float y) {

    }
}
