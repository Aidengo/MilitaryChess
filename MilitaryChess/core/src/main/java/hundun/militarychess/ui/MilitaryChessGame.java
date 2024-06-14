package hundun.militarychess.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.stripe.FreeTypeSkin;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.corelib.base.BaseHundunGame;
import hundun.gdxgame.gamelib.base.save.ISaveTool;
import hundun.militarychess.logic.LogicContext;
import hundun.militarychess.ui.screen.MyMenuScreen;
import hundun.militarychess.ui.screen.ScreenContext;
import lombok.Getter;

public class MilitaryChessGame extends BaseHundunGame<Void> {
    public static final String GAME_WORD_SKIN_KEY = "default";

    @Getter
    private final ScreenContext screenContext;
    @Getter
    private final LogicContext logicContext;

    @Getter
    private final Viewport sharedViewport;
    @Getter
    private final TextureManager textureManager;

    public MilitaryChessGame(ISaveTool<Void> saveTool) {
        super(640, 480);
        //this.skinFilePath = "skins/orange/skin/uiskin.json";
        debugMode = true;

        this.sharedViewport = new ScreenViewport();
        // this project use external files, not saveHandler
        this.saveHandler = null;
        this.mainSkinFilePath = null;
        this.screenContext = new ScreenContext(this);
        this.logicContext = new LogicContext(this);
        this.textureManager = new TextureManager(this);
    }

    @Override
    protected void createStage1() {
        super.createStage1();
        this.mainSkin = new FreeTypeSkin(Gdx.files.internal("skins/freetype/skin.json"));
        this.logicContext.lazyInitOnCreateStage1();
        this.textureManager.lazyInitOnCreateStage1();
    }

    @Override
    protected void createStage2() {
        screenContext.lazyInit(this);

    }

    @Override
    protected void createStage3() {
        screenManager.pushScreen(MyMenuScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
    }
}
