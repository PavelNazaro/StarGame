package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.geekbrains.base.BaseScreen;
import ru.geekbrains.base.Font;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.EnemyPool;
import ru.geekbrains.pool.ExplosionPool;
import ru.geekbrains.sprite.Background;
import ru.geekbrains.sprite.Bullet;
import ru.geekbrains.sprite.ButtonExit;
import ru.geekbrains.sprite.ButtonNewGame;
import ru.geekbrains.sprite.ButtonPause;
import ru.geekbrains.sprite.ButtonResume;
import ru.geekbrains.sprite.ButtonTextExit;
import ru.geekbrains.sprite.EnemyShip;
import ru.geekbrains.sprite.MainShip;
import ru.geekbrains.sprite.MessageGameOver;
import ru.geekbrains.sprite.MessagePause;
import ru.geekbrains.sprite.Star;
import ru.geekbrains.sprite.TrackingStar;
import ru.geekbrains.utils.EnemyGenerator;

public class GameScreen extends BaseScreen {

    private static final float FONT_INFO_PADDING = 0.01f;
    private static final float FONT_INFO_SIZE = 0.02f;
    private static final float FONT_LEVEL_SIZE = 0.1f;
    private static final int TIMER_SHOW_LEVEL = 100;
    private static final float FONT_SCORE_SIZE = 0.05f;

    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";
    private static final String SCORE = "Score: ";

    public enum State {PLAYING, GAME_OVER, PAUSE}

    private Texture bg;
    private TextureAtlas atlas;
    private TextureAtlas atlasMenu;
    private TextureAtlas atlasButtons;

    private Background background;
    private TrackingStar[] stars;

    private MainShip mainShip;

    private BulletPool bulletPool;
    private EnemyPool enemyPool;
    private ExplosionPool explosionPool;

    private Music music;
    private Sound laserSound;
    private Sound bulletSound;
    private Sound explosionSound;

    private EnemyGenerator enemyGenerator;

    private State state;

    private MessageGameOver messageGameOver;
    private MessagePause messagePause;
    private ButtonNewGame buttonNewGame;
    private ButtonExit buttonExit;
    private ButtonTextExit buttonTextExit;
    private ButtonPause buttonPause;
    private ButtonResume buttonResume;

    private int frags;
    private int score;

    private Font fontInfo;
    private Font fontLevel;
    private Font fontScore;
    private StringBuilder sbFrags;
    private StringBuilder sbScore;
    private StringBuilder sbHp;
    private StringBuilder sbLevel;

    private int timerShowLevel = 0;
    private int keepCurrentLevel = 0;

    @Override
    public void show() {
        super.show();
        frags = 0;
        score = 0;
        bg = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(bg));
        atlas = new TextureAtlas(Gdx.files.internal("textures/mainAtlas.tpack"));
        atlasMenu = new TextureAtlas(Gdx.files.internal("textures/menuAtlas.tpack"));
        atlasButtons = new TextureAtlas(Gdx.files.internal("textures/buttonsAtlas.tpack"));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas, explosionSound);
        enemyPool = new EnemyPool(bulletPool, explosionPool, bulletSound, worldBounds);
        mainShip = new MainShip(atlas, bulletPool, explosionPool, laserSound);
        enemyGenerator = new EnemyGenerator(atlas, enemyPool, worldBounds);
        messageGameOver = new MessageGameOver(atlas);
        messagePause = new MessagePause(atlasButtons);
        buttonNewGame = new ButtonNewGame(atlasButtons, this);
        buttonExit = new ButtonExit(atlasMenu);
        buttonTextExit = new ButtonTextExit(atlasButtons);
        buttonPause = new ButtonPause(atlasButtons, this);
        buttonResume = new ButtonResume(atlasButtons, this);
        fontInfo = new Font("font/font.fnt", "font/font.png");
        fontLevel = new Font("font/font.fnt", "font/font.png");
        fontScore = new Font("font/font.fnt", "font/font.png");
        sbFrags = new StringBuilder();
        sbScore = new StringBuilder();
        sbHp = new StringBuilder();
        sbLevel = new StringBuilder();
        stars = new TrackingStar[64];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new TrackingStar(atlas, mainShip.getV());
        }
        music.setLooping(true);
        music.play();
        state = State.PLAYING;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        freeAllDestroyed();
        draw();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star star : stars) {
            star.resize(worldBounds);
        }
        mainShip.resize(worldBounds);
        messageGameOver.resize(worldBounds);
        messagePause.resize(worldBounds);
        buttonNewGame.resize(worldBounds);
        buttonExit.resize(worldBounds);
        buttonTextExit.resize(worldBounds);
        buttonPause.resize(worldBounds);
        buttonResume.resize(worldBounds);
        fontInfo.setSize(FONT_INFO_SIZE);
        fontLevel.setSize(FONT_LEVEL_SIZE);
        fontScore.setSize(FONT_SCORE_SIZE);
    }

    @Override
    public void dispose() {
        atlas.dispose();
        atlasMenu.dispose();
        atlasButtons.dispose();
        bg.dispose();
        bulletPool.dispose();
        explosionPool.dispose();
        music.dispose();
        laserSound.dispose();
        bulletSound.dispose();
        explosionSound.dispose();
        enemyPool.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button, int screenY) {
        if (state == State.PLAYING) {
            mainShip.touchDown(touch, pointer, button, screenY);
            buttonPause.touchDown(touch, pointer, button, screenY);
        } else {
            buttonNewGame.touchDown(touch, pointer, button, screenY);
            buttonTextExit.touchDown(touch, pointer, button, screenY);

            if (state == State.PAUSE) {
                buttonResume.touchDown(touch, pointer, button, screenY);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button, int screenY) {
        if (state == State.PLAYING) {
            mainShip.touchUp(touch, pointer, button, screenY);
            buttonPause.touchUp(touch, pointer, button, screenY);
        } else {
            buttonNewGame.touchUp(touch, pointer, button, screenY);
            buttonTextExit.touchUp(touch, pointer, button, screenY);

            if (state == State.PAUSE) {
                buttonResume.touchUp(touch, pointer, button, screenY);
            }
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (state == State.PLAYING) {
            mainShip.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.PLAYING) {
            mainShip.keyUp(keycode);
        }
        return false;
    }

    public void startNewGame() {
        enemyGenerator.setLevel(1);
        frags = 0;
        score = 0;

        state = State.PLAYING;
        mainShip.startNewGame();

        bulletPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();

        keepCurrentLevel = 0;
    }

    private void update(float delta) {
        if (state != State.PAUSE) {
            for (Star star : stars) {
                star.update(delta);
            }
            explosionPool.updateActiveSprites(delta);
            if (state == State.PLAYING) {
                mainShip.update(delta);
                bulletPool.updateActiveSprites(delta);
                enemyPool.updateActiveSprites(delta);
                enemyGenerator.generate(delta, score);
            }
            int getLevel = enemyGenerator.getLevel();
            if (keepCurrentLevel < getLevel) {
                timerShowLevel = TIMER_SHOW_LEVEL;
                keepCurrentLevel = getLevel;
            }
        }
    }

    private void checkCollisions() {
        if (state != State.PLAYING) {
            return;
        }
        List<EnemyShip> enemyShipList = enemyPool.getActiveObjects();
        for (EnemyShip enemyShip : enemyShipList) {
            float minDist = enemyShip.getHalfWidth() + mainShip.getHalfWidth();
            if (enemyShip.pos.dst(mainShip.pos) < minDist) {
                enemyShip.destroy();
                frags++;
                mainShip.damage(enemyShip.getDamage());
            }
        }
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (Bullet bullet : bulletList) {
            if (bullet.isDestroyed()) {
                continue;
            }
            if (bullet.getOwner() != mainShip) {
                if (mainShip.isBulletCollision(bullet)) {
                    mainShip.damage(bullet.getDamage());
                    bullet.destroy();
                }
            } else {
                for (EnemyShip enemyShip : enemyShipList) {
                    if (enemyShip.isBulletCollision(bullet)) {
                        enemyShip.damage(bullet.getDamage());
                        if (enemyShip.isDestroyed()) {
                            frags++;
                            score += enemyShip.getScore();
                        }
                        bullet.destroy();
                    }
                }
            }
        }
        if (mainShip.isDestroyed()) {
            state = State.GAME_OVER;
        }
    }

    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyedActiveObjects();
        explosionPool.freeAllDestroyedActiveObjects();
        enemyPool.freeAllDestroyedActiveObjects();
    }

    private void draw() {
        Gdx.gl.glClearColor(0.2f, 0.6f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.graphics.setWindowedMode(400, 600);//

        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
        if (state == State.GAME_OVER) {
            messageGameOver.draw(batch);
            buttonNewGame.draw(batch);
            buttonTextExit.draw(batch);
            printScoreAfterDie();
        } else {
            mainShip.draw(batch);
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
            if (state == State.PLAYING) {
                buttonPause.draw(batch);
            } else {
                messagePause.draw(batch);
                buttonResume.draw(batch);
                buttonNewGame.draw(batch);
                buttonTextExit.draw(batch);
            }
        }
        printInfo();
        if (timerShowLevel > 0) {
            printLevelAfterUpperLevel();
            timerShowLevel--;
        }
        batch.end();
    }

    private void printInfo() {
        sbFrags.setLength(0);
        sbHp.setLength(0);
        sbLevel.setLength(0);
        fontInfo.draw(
                batch,
                sbFrags.append(FRAGS)
                        .append(frags)
                        .append("\n")
                        .append(SCORE)
                        .append(score)
                        .append("\n")
                        .append(LEVEL)
                        .append(enemyGenerator.getLevel()),
                worldBounds.getLeft() + FONT_INFO_PADDING,
                worldBounds.getTop() - FONT_INFO_PADDING
        );
        fontInfo.draw(
                batch,
                sbHp.append(HP).append(mainShip.getHP()),
                worldBounds.pos.x,
                worldBounds.getTop() - FONT_INFO_PADDING,
                Align.center
        );
    }

    private void printLevelAfterUpperLevel(){
        sbLevel.setLength(0);
        fontLevel.draw(
                batch,
                sbLevel.append(LEVEL).append(enemyGenerator.getLevel()),
                worldBounds.pos.x,
                worldBounds.pos.y + 0.05f,
                Align.center
        );
    }

    private void printScoreAfterDie(){
        sbScore.setLength(0);
        fontScore.draw(
                batch,
                sbScore.append(SCORE).append(score),
                worldBounds.pos.x,
                worldBounds.pos.y + 0.01f,
                Align.center
        );
    }

    public void setPauseOn() {
        state = State.PAUSE;
        timerShowLevel = 0;
    }

    public void setPauseOff() {
        state = State.PLAYING;
    }
}