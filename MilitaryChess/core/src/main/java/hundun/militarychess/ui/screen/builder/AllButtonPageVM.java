package hundun.militarychess.ui.screen.builder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import hundun.militarychess.logic.LogicContext.ChessShowMode;
import hundun.militarychess.logic.LogicContext.CrossScreenDataPackage;
import hundun.militarychess.logic.LogicContext.PlayerMode;
import hundun.militarychess.logic.chess.ChessRule.FightResultType;
import hundun.militarychess.logic.data.ChessRuntimeData.ChessSide;
import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.ChessVM;
import lombok.Getter;

public class AllButtonPageVM extends Table {

    PlayScreen screen;
    @Getter
    ChessVM fromChessVM;
    @Getter
    ChessVM toChessVM;
    FightResultType fightResultPreview;
    ChessSide currentSide;

    Label currentSideLabel;
    Label fromLabel;
    Label toLabel;
    Label fightResultPreviewLabel;
    TextButton commitButton;
    TextButton clearButton;
    TextButton capitulateButton;
    public AllButtonPageVM(PlayScreen screen) {
        this.screen = screen;

        int pad = 20;

        this.currentSideLabel = new Label("", screen.getGame().getMainSkin());
        this.add(currentSideLabel).padBottom(pad).row();

        this.fromLabel = new Label("", screen.getGame().getMainSkin());
        this.add(fromLabel).padBottom(pad).row();

        this.toLabel = new Label("", screen.getGame().getMainSkin());
        this.add(toLabel).padBottom(pad).row();

        this.fightResultPreviewLabel = new Label("", screen.getGame().getMainSkin());
        this.add(fightResultPreviewLabel).padBottom(pad).row();

        this.commitButton = new TextButton("确认", screen.getGame().getMainSkin());
        this.commitButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.onCommitButtonClicked();
            }
        });
        this.add(commitButton).padBottom(pad).row();

        this.clearButton = new TextButton("清空", screen.getGame().getMainSkin());
        this.clearButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.onClearButtonClicked();
            }
        });
        this.add(clearButton).padBottom(pad).row();
        this.capitulateButton = new TextButton("认输", screen.getGame().getMainSkin());
        this.capitulateButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.onCapitulated();
            }
        });
        this.add(capitulateButton).padTop(pad * 3).row();
    }

    public void setFrom(ChessVM chessVM) {
        this.fromChessVM = chessVM;
        updateUI();
    }

    public void setTo(ChessVM chessVM, FightResultType fightResultPreview) {
        this.toChessVM = chessVM;
        this.fightResultPreview = fightResultPreview;
        this.commitButton.setDisabled(fightResultPreview == null || fightResultPreview == FightResultType.CAN_NOT);
        updateUI();
    }


    public void updateUI() {
        CrossScreenDataPackage crossScreenDataPackage = screen.getGame().getLogicContext().getCrossScreenDataPackage();
        if (fromChessVM != null) {
            if (crossScreenDataPackage.getCurrentChessShowSides().contains(fromChessVM.getDeskData().getChessSide())) {
                this.fromLabel.setText("发起者: "
                    + fromChessVM.getDeskData().toText()
                );
            } else {
                this.fromLabel.setText("发起者: 已隐藏");
            }
        } else {
            this.fromLabel.setText("发起者: 待选择");
        }
        if (toChessVM != null) {
            if (crossScreenDataPackage.getCurrentChessShowSides().contains(toChessVM.getDeskData().getChessSide())) {
                this.toLabel.setText("目标: "
                    + toChessVM.getDeskData().toText()
                );
            } else {
                this.toLabel.setText("目标: 已隐藏");
            }
        } else {
            this.toLabel.setText("目标: 待选择");
        }
        if (fightResultPreview != null) {
            if (crossScreenDataPackage.getChessShowMode() == ChessShowMode.MING_QI
                || fightResultPreview == FightResultType.JUST_MOVE
                || fightResultPreview == FightResultType.CAN_NOT
            ) {
                this.fightResultPreviewLabel.setText("预测: "
                    + fightResultPreview.getChinese()
                );
            } else {
                this.fightResultPreviewLabel.setText("预测: 已隐藏");
            }
        } else {
            this.fightResultPreviewLabel.setText("");
        }
        currentSideLabel.setText("当前操作方: " + currentSide.getChinese());
    }

    /**
     * 当前操作方变动时调用
     */
    public void updateForNewSide() {
        CrossScreenDataPackage crossScreenDataPackage = screen.getGame().getLogicContext().getCrossScreenDataPackage();
        ChessSide currentSide = crossScreenDataPackage.getCurrentSide();
        boolean isAiSide = crossScreenDataPackage.getPlayerMode() == PlayerMode.PVC && currentSide != crossScreenDataPackage.getPvcPlayerSide();
        this.fromChessVM = null;
        this.toChessVM = null;
        this.fightResultPreview = null;
        this.commitButton.setDisabled(true);
        // 不能帮ai按按钮
        this.clearButton.setDisabled(isAiSide);
        this.capitulateButton.setDisabled(isAiSide);

        this.currentSide = currentSide;
        updateUI();

    }
}
