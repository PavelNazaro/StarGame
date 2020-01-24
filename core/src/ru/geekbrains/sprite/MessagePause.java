package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;

public class MessagePause extends Sprite {

    private static final float HEIGHT = 0.2f;
    private static final float TOP = 0.27f;

    public MessagePause(TextureAtlas atlas) {
        super(atlas.findRegion("btPause"));
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setHeightProportion(HEIGHT);
        setTop(TOP);
    }
}
