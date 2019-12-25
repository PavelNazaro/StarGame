package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;

public class Logo extends Sprite {

    private static final float V_LEN = 0.01f;
    private Vector2 v;
    private Vector2 endPoint;
    private Vector2 buf;

    public Logo(TextureRegion region) {
        super(region);
        v = new Vector2();
        endPoint = new Vector2();
        buf = new Vector2();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setHeightProportion(0.3f);
        pos.set(worldBounds.getLeft() + halfWidth, worldBounds.getBottom() + halfHeight);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        buf.set(endPoint);
        if (buf.sub(pos).len() > V_LEN){
            pos.add(v);
        } else {
            pos.set(endPoint);
        }
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        endPoint.set(touch);
        v.set(touch.sub(pos)).setLength(V_LEN);
        return false;
    }
}
