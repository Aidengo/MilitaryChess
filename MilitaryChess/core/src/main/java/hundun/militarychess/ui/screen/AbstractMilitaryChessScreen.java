package hundun.militarychess.ui.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import hundun.gdxgame.corelib.base.BaseHundunScreen;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.other.CameraDataPackage;
import hundun.militarychess.ui.screen.shared.ChessVM;
import hundun.militarychess.ui.screen.shared.DeskAreaVM;

public abstract class AbstractMilitaryChessScreen extends BaseHundunScreen<MilitaryChessGame, Void> {
    protected OrthographicCamera deskCamera;
    protected Stage deskStage;
    protected DeskAreaVM deskAreaVM;

    public AbstractMilitaryChessScreen(MilitaryChessGame game, Viewport sharedViewport) {
        super(game, sharedViewport);
    }
    public abstract void onDeskClicked(ChessVM vm);
    protected abstract void updateUIAfterRoomChanged();
    @Override
    protected void belowUiStageDraw(float delta) {
        deskStage.act();
        deskStage.getViewport().getCamera().position.set(
                deskAreaVM.getCameraDataPackage().getCurrentCameraX(),
                deskAreaVM.getCameraDataPackage().getCurrentCameraY(),
                0);
        if (deskAreaVM.getCameraDataPackage().getAndClearCameraZoomDirty()) {
            float weight = deskAreaVM.getCameraDataPackage().getCurrentCameraZoomWeight();
            deskCamera.zoom = CameraDataPackage.cameraZoomWeightToZoomValue(weight);
            game.getFrontend().log(this.getClass().getSimpleName(), "deskCamera.zoom = %s", deskCamera.zoom);
        }
        deskStage.getViewport().apply();
        deskStage.draw();
    }
}
