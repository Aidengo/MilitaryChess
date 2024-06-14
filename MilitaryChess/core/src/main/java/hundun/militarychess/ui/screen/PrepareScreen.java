package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.corelib.base.BaseHundunScreen;
import hundun.gdxgame.corelib.base.util.TextureFactory;
import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.data.ArmyRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class PrepareScreen extends BaseHundunScreen<MilitaryChessGame, Void> {
    Image backImage;

    public PrepareScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());

        Label titleLabel = new Label(
            JavaFeatureForGwt.stringFormat("     %s     ", "军旗游戏"),
            game.getMainSkin());
        titleLabel.setFontScale(1.5f);
        this.backImage = new Image(new TextureRegionDrawable(new TextureRegion(TextureFactory.getSimpleBoardBackground())));

    }

    @Override
    protected void create() {
        backImage.setFillParent(true);
        backUiStage.addActor(backImage);
        HorizontalGroup playerModeHorizontalGroup = new HorizontalGroup();
        Map<CheckBox, PlayerMode> playerModeCheckBoxMap = new LinkedHashMap<>();
        playerModeCheckBoxMap.put(new CheckBox(PlayerMode.PVP.getChinese(), game.getMainSkin()), PlayerMode.PVP);
        playerModeCheckBoxMap.put(new CheckBox(PlayerMode.PVC.getChinese(), game.getMainSkin()), PlayerMode.PVC);
        ButtonGroup<CheckBox> playerModeButtonGroup = new ButtonGroup<>();
        playerModeButtonGroup.setMaxCheckCount(1);
        playerModeButtonGroup.setUncheckLast(true);
        playerModeCheckBoxMap.keySet().forEach(it -> {
            playerModeButtonGroup.add(it);
            playerModeHorizontalGroup.addActor(it);
        });
        uiRootTable.add(playerModeHorizontalGroup);
        uiRootTable.row();

        HorizontalGroup pvcPlayerSideHorizontalGroup = new HorizontalGroup();
        pvcPlayerSideHorizontalGroup.addActor(new Label("(仅人机对战时有效)", game.getMainSkin()));
        Map<CheckBox, Boolean> pvcPlayerSideCheckBoxMap = new LinkedHashMap<>();
        pvcPlayerSideCheckBoxMap.put(new CheckBox("先手", game.getMainSkin()), Boolean.TRUE);
        pvcPlayerSideCheckBoxMap.put(new CheckBox("后手", game.getMainSkin()), Boolean.FALSE);
        ButtonGroup<CheckBox> pvcPlayerSideButtonGroup = new ButtonGroup<>();
        pvcPlayerSideButtonGroup.setMaxCheckCount(1);
        pvcPlayerSideButtonGroup.setUncheckLast(true);
        pvcPlayerSideCheckBoxMap.keySet().forEach(it -> {
            pvcPlayerSideButtonGroup.add(it);
            pvcPlayerSideHorizontalGroup.addActor(it);
        });
        uiRootTable.add(pvcPlayerSideHorizontalGroup);
        uiRootTable.row();

        HorizontalGroup chessShowModeHorizontalGroup = new HorizontalGroup();
        Map<CheckBox, ChessShowMode> chessShowModeCheckBoxMap = new LinkedHashMap<>();
        chessShowModeCheckBoxMap.put(new CheckBox(ChessShowMode.MING_QI.getChinese(), game.getMainSkin()), ChessShowMode.MING_QI);
        chessShowModeCheckBoxMap.put(new CheckBox(ChessShowMode.AN_QI.getChinese(), game.getMainSkin()), ChessShowMode.AN_QI);
        ButtonGroup<CheckBox> chessShowModeButtonGroup = new ButtonGroup<>();
        chessShowModeButtonGroup.setMaxCheckCount(1);
        chessShowModeButtonGroup.setUncheckLast(true);
        chessShowModeCheckBoxMap.keySet().forEach(it -> {
            chessShowModeButtonGroup.add(it);
            chessShowModeHorizontalGroup.addActor(it);
        });
        uiRootTable.add(chessShowModeHorizontalGroup);
        uiRootTable.row();

        Table codesHorizontalGroup;
        codesHorizontalGroup = new Table();//diahcbgkkfiecfigjhedjljhg

        TextField redSideTextField = new TextField("cighfeidckbgkhafjgjedljhi", game.getMainSkin());
        //cighcdgkkfedbigaifjfehjlj
        codesHorizontalGroup.add(new Label("红方：", game.getMainSkin()));
        codesHorizontalGroup.add(redSideTextField).width(400);
        uiRootTable.add(codesHorizontalGroup);
        uiRootTable.row();
        codesHorizontalGroup = new Table();//hhgljeiijjgbafeidkgkcfdhc
        TextField blueSideTextField = new TextField("hhgljeiijjgbafeidkgkcfdhc", game.getMainSkin());
        codesHorizontalGroup.add(new Label("蓝方：", game.getMainSkin()));
        codesHorizontalGroup.add(blueSideTextField).width(400);
        uiRootTable.add(codesHorizontalGroup);
        uiRootTable.row();

        TextButton buttonNewGame = new TextButton("开始", game.getMainSkin());
        buttonNewGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                var playerMode = playerModeCheckBoxMap.get(playerModeButtonGroup.getChecked());
                var chessShowMode = chessShowModeCheckBoxMap.get(chessShowModeButtonGroup.getChecked());
                var pvcPlayerAsFirst = pvcPlayerSideCheckBoxMap.get(pvcPlayerSideButtonGroup.getChecked());
                var crossScreenDataPackage = CrossScreenDataPackage.builder()
                    .game(game)
                    .playerMode(playerMode)
                    .chessShowMode(chessShowMode)
                    .currentChessShowSides(new HashSet<>())
                    .pvcPlayerSide(ChessSide.RED_SIDE)
                    .currentSide(pvcPlayerAsFirst ? ChessSide.RED_SIDE : ChessSide.BLUE_SIDE)
                    .currentState(ChessState.WAIT_SELECT_FROM)
                    .armyMap(Map.of(
                        ChessSide.RED_SIDE,
                        ArmyRuntimeData.builder()
                            .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                                redSideTextField.getText(),
                                game.getScreenContext().getLayoutConst(),
                                ChessSide.RED_SIDE))
                            .build(),
                        ChessSide.BLUE_SIDE,
                        ArmyRuntimeData.builder()
                            .chessRuntimeDataList(ChessRuntimeData.fromCodes(
                                blueSideTextField.getText(),
                                game.getScreenContext().getLayoutConst(),
                                ChessSide.BLUE_SIDE))
                            .build()
                    ))
                    .build();
                crossScreenDataPackage.update();
                game.getLogicContext().setCrossScreenDataPackage(crossScreenDataPackage);
                game.getScreenManager().pushScreen(PlayScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            }
        });
        uiRootTable.add(buttonNewGame);
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
}
