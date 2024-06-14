package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import hundun.militarychess.logic.chess.GameboardPosRule;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.screen.AbstractMilitaryChessScreen;

public class DeskClickListener extends ClickListener {
    MilitaryChessGame game;
    AbstractMilitaryChessScreen screen;
    private final ChessVM vm;

    public DeskClickListener(AbstractMilitaryChessScreen screen, ChessVM vm) {
        this.game = screen.getGame();
        this.screen = screen;
        this.vm = vm;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        screen.onDeskClicked(vm);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(vm.getDeskData().toText()
            + " has been clicked."
        );
        var relation = GameboardPosRule.gameboardPosMap.get(vm.getDeskData().getPos());
        stringBuilder.append("NeighbourMap{");
        relation.getNeighbourMap().forEach((k, v) -> stringBuilder.append(k).append("=").append(v != null ? v.toText() : "null"));
        stringBuilder.append("}");
        game.getFrontend().log(this.getClass().getSimpleName(), stringBuilder.toString());
    }
}
