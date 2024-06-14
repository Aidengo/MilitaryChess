package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.corelib.base.BaseHundunScreen;
import hundun.gdxgame.corelib.base.util.TextureFactory;
import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.ui.MilitaryChessGame;

public class MyMenuScreen extends BaseHundunScreen<MilitaryChessGame, Void> {
    int BUTTON_WIDTH = 100;
    int BUTTON_BIG_HEIGHT = 100;
    int BUTTON_SMALL_HEIGHT = 75;
    Actor title;
    Actor buttonNewGame;
    Image backImage;
    public MyMenuScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());
        Label titleLabel = new Label(
            JavaFeatureForGwt.stringFormat("     %s     ", "军棋游戏"),
            game.getMainSkin());
        titleLabel.setFontScale(1.5f);
        Image backImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground())));

        this.buttonNewGame = new TextButton("开始", game.getMainSkin());
        buttonNewGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getScreenManager().pushScreen(PrepareScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            }
        });
        this.title = titleLabel;
        this.backImage = backImage;

    }

    private void initScene2d() {
        backImage.setFillParent(true);
        backUiStage.addActor(backImage);

        uiRootTable.add(title).row();
        uiRootTable.add(buttonNewGame)
            .height(BUTTON_BIG_HEIGHT)
            .fillY()
            .padTop(10)
            .row();
    }

    @Override
    public void show() {
        super.show();
        //addInputProcessor(uiStage);
        Gdx.input.setInputProcessor(uiStage);
        game.getBatch().setProjectionMatrix(uiStage.getViewport().getCamera().combined);
    }

    @Override
    public void dispose() {
    }

    @Override
    protected void create() {
        initScene2d();
    }
}
