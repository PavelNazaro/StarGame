package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.base.ScaledButton;
import ru.geekbrains.math.Rect;
import ru.geekbrains.screen.GameScreen;

public class ButtonPause extends ScaledButton {

    private static final float HEIGHT = 0.06f;

    private GameScreen gameScreen;

    public ButtonPause(TextureAtlas atlas, GameScreen gameScreen) {
        super(atlas.findRegion("btPause"));
        this.gameScreen = gameScreen;
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setHeightProportion(HEIGHT);
        setTop(worldBounds.getTop() - 0.01f);
        setRight(worldBounds.getRight() - 0.01f);
    }

    @Override
    public void action() {
        gameScreen.setPauseOn();
    }
}
