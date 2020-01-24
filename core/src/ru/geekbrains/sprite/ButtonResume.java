package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.base.ScaledButton;
import ru.geekbrains.math.Rect;
import ru.geekbrains.screen.GameScreen;

public class ButtonResume extends ScaledButton {

    private static final float HEIGHT = 0.1f;
    private static final float TOP = -0.07f;

    private GameScreen gameScreen;

    public ButtonResume(TextureAtlas atlas, GameScreen gameScreen) {
        super(atlas.findRegion("btResume"));
        this.gameScreen = gameScreen;
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setHeightProportion(HEIGHT);
//        setTop(TOP);
    }

    @Override
    public void action() {
        gameScreen.setPauseOff();
    }
}
