package hundun.militarychess.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.gdxgame.gamelib.base.LogicFrameHelper;
import hundun.gdxgame.gamelib.base.util.JavaFeatureForGwt;
import hundun.militarychess.logic.LogicContext.ChessState;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.chess.ChessRule;
import hundun.militarychess.logic.chess.ChessRule.FightResultType;
import hundun.militarychess.logic.data.ChessRuntimeData;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.builder.BuilderMainBoardVM;
import hundun.militarychess.ui.screen.shared.ChessVM;
import hundun.militarychess.ui.screen.shared.DeskAreaVM;
import java.util.ArrayList;
import java.util.List;

public class PlayScreen extends AbstractMilitaryChessScreen {

    // ------ UI layer ------
    private BuilderMainBoardVM mainBoardVM;
    // ------ image previewer layer ------

    // ------ popup layer ------

    public PlayScreen(MilitaryChessGame game) {
        super(game, game.getSharedViewport());

        this.deskCamera = new OrthographicCamera();
        this.deskStage = new Stage(new ScreenViewport(deskCamera), game.getBatch());
        this.logicFrameHelper = new LogicFrameHelper(1);
    }

    @Override
    protected void create() {
        // ------ desk layer ------
        deskAreaVM = new DeskAreaVM(this);
        deskStage.addActor(deskAreaVM);
        deskStage.setScrollFocus(deskAreaVM);

        // ------ UI layer ------
        mainBoardVM = new BuilderMainBoardVM(this);
        uiRootTable.add(mainBoardVM)
            .expandX()
            .growY()
            .right()
        ;
        // ------ image previewer layer ------

        // ------ popup layer ------

    }

    @Override
    public void dispose() {
    }

    @Override
    public void show() {
        super.show();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(popupUiStage);
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(deskStage);
        Gdx.input.setInputProcessor(multiplexer);

        //Gdx.input.setInputProcessor(uiStage);
        //game.getBatch().setProjectionMatrix(uiStage.getViewport().getCamera().combined);

        updateUIForShow();
        Gdx.app.log(this.getClass().getSimpleName(), "show done");
    }

    private void updateUIForShow() {
        deskAreaVM.getCameraDataPackage().forceSet(null, null, CameraDataPackage.DEFAULT_CAMERA_ZOOM_WEIGHT);
        updateUIAfterRoomChanged();
    }

    @Override
    public void updateUIAfterRoomChanged() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();

        // 构造棋子
        List<ChessRuntimeData> allChessRuntimeDataList = new ArrayList<>();
        crossScreenDataPackage.getArmyMap().values().forEach(it -> allChessRuntimeDataList.addAll(it.getChessRuntimeDataList()));
        deskAreaVM.updateDeskDatas(allChessRuntimeDataList);
        mainBoardVM.getAllButtonPageVM().updateForNewSide();
        mainBoardVM.updateForShow();
    }

    private ChessVM findVM(ChessRuntimeData chessRuntimeData) {
        return deskAreaVM.getNodes().get(chessRuntimeData.getId());
    }

    /**
     * 定时检查，若有AiAction，则执行它
     */
    @Override
    protected void onLogicFrame() {
        super.onLogicFrame();
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        if (crossScreenDataPackage.getAiAction() == null) {
            return;
        }
        switch (crossScreenDataPackage.getCurrentState()) {
            case WAIT_SELECT_FROM:
                game.getFrontend().log(this.getClass().getSimpleName(),
                    "AiAction score = {0}, from = {1}, to = {2}",
                    crossScreenDataPackage.getAiAction().getScore(),
                    crossScreenDataPackage.getAiAction().getFrom().toText(),
                    crossScreenDataPackage.getAiAction().getTo().toText()
                );
                if (crossScreenDataPackage.getAiAction().isCapitulated()) {
                    // 模拟Ai点击认输
                    onCapitulated();
                } else {
                    // 模拟Ai点击棋子
                    onDeskClicked(findVM(crossScreenDataPackage.getAiAction().getFrom()));
                }
                break;
            case WAIT_SELECT_TO:
                // 模拟Ai点击棋子
                onDeskClicked(findVM(crossScreenDataPackage.getAiAction().getTo()));
                break;
            case WAIT_COMMIT:
                // 模拟Ai点击确认
                onCommitButtonClicked();
                break;
            default:
        }
    }
    /**
     * 当认输被点击
     */
    public void onCapitulated() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();

        String message = JavaFeatureForGwt.stringFormat(
            "%s已认输。\n%s胜利。",
            crossScreenDataPackage.getCurrentSide().getChinese(),
            ChessSide.getOpposite(crossScreenDataPackage.getCurrentSide()).getChinese()
        );
        startDialog(message,"对局结束", () -> {
            // 回到菜单页
            game.getScreenManager().pushScreen(MyMenuScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
        });
    }

    /**
     * 当棋子被点击
     */
    @Override
    public void onDeskClicked(ChessVM chessVM) {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        switch (crossScreenDataPackage.getCurrentState()) {
            case WAIT_SELECT_FROM:
                if (chessVM.getDeskData().getChessSide() == crossScreenDataPackage.getCurrentSide()) {
                    mainBoardVM.getAllButtonPageVM().setFrom(chessVM);
                    deskAreaVM.updateMask(chessVM);
                    crossScreenDataPackage.setCurrentState(ChessState.WAIT_SELECT_TO);
                }
                break;
            case WAIT_SELECT_TO:
                if (chessVM.getDeskData().getChessSide() != crossScreenDataPackage.getCurrentSide()) {
                    FightResultType fightResultPreview = ChessRule.fightResultPreview(
                        mainBoardVM.getAllButtonPageVM().getFromChessVM().getDeskData(),
                        chessVM.getDeskData()
                        );
                    mainBoardVM.getAllButtonPageVM().setTo(chessVM, fightResultPreview);
                    crossScreenDataPackage.setCurrentState(ChessState.WAIT_COMMIT);
                }
                break;
            default:
        }
    }
    /**
     * 当确认被点击
     */
    public void onCommitButtonClicked() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        ChessRule.fight(
            mainBoardVM.getAllButtonPageVM().getFromChessVM().getDeskData(),
            mainBoardVM.getAllButtonPageVM().getToChessVM().getDeskData()
        );
        this.afterFight();
    }

    /**
     * 构造消息弹窗
     */
    private void startDialog(String message, String title, Runnable callback) {
        final Dialog dialog = new Dialog(title, game.getMainSkin(), "dialog") {
            public void result(Object obj) {
                boolean action = (boolean) obj;
                if (action) {
                    callback.run();
                }
            }
        };
        dialog.text(message);
        dialog.button("OK", true);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                dialog.show(popupUiStage);
            }
        });
    }

    /**
     * 战斗后的处理
     */
    private void afterFight() {
        game.getFrontend().log(this.getClass().getSimpleName(),
            "afterFight, from = {0}, to = {1}",
            mainBoardVM.getAllButtonPageVM().getFromChessVM().getDeskData().toText(),
            mainBoardVM.getAllButtonPageVM().getToChessVM().getDeskData().toText()
            );
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        // 逻辑类更新
        crossScreenDataPackage.afterFight();
        // 若干UI重置
        mainBoardVM.getAllButtonPageVM().getFromChessVM().updateUI();
        mainBoardVM.getAllButtonPageVM().getToChessVM().updateUI();
        mainBoardVM.getAllButtonPageVM().updateForNewSide();
        deskAreaVM.afterFightOrClear();
        // 若已对局结束，则展示
        if (crossScreenDataPackage.getLoseSide() != null) {
            String message = JavaFeatureForGwt.stringFormat(
                "%s失败，原因：%s。\n%s胜利。",
                crossScreenDataPackage.getLoseSide().getChinese(),
                crossScreenDataPackage.getLoseReason(),
                ChessSide.getOpposite(crossScreenDataPackage.getLoseSide()).getChinese()
            );
            startDialog(message,"对局结束", () -> {
                // 回到菜单页
                game.getScreenManager().pushScreen(MyMenuScreen.class.getSimpleName(), BlendingTransition.class.getSimpleName());
            });
        }
    }

    /**
     * 当清空被点击
     */
    public void onClearButtonClicked() {
        CrossScreenDataPackage crossScreenDataPackage = game.getLogicContext().getCrossScreenDataPackage();
        mainBoardVM.getAllButtonPageVM().setFrom(null);
        mainBoardVM.getAllButtonPageVM().setTo(null, null);
        deskAreaVM.afterFightOrClear();
        crossScreenDataPackage.setCurrentState(ChessState.WAIT_SELECT_FROM);
    }
}
