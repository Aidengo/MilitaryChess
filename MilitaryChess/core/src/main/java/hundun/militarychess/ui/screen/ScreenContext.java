package hundun.militarychess.ui.screen;

import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.Getter;

@Getter
public class ScreenContext {
    PlayScreen playScreen;
    PrepareScreen prepareScreen;
    MyMenuScreen menuScreen;

    LayoutConst layoutConst;
    public ScreenContext(MilitaryChessGame game) {
    }
    public void lazyInit(MilitaryChessGame game) {
        this.layoutConst = new LayoutConst();
        this.menuScreen = new MyMenuScreen(game);
        this.playScreen = new PlayScreen(game);
        this.prepareScreen = new PrepareScreen(game);

        game.getScreenManager().addScreen(menuScreen.getClass().getSimpleName(), menuScreen);
        game.getScreenManager().addScreen(playScreen.getClass().getSimpleName(), playScreen);
        game.getScreenManager().addScreen(prepareScreen.getClass().getSimpleName(), prepareScreen);

        BlendingTransition blendingTransition = new BlendingTransition(game.getBatch(), 1F);
        game.getScreenManager().addScreenTransition(BlendingTransition.class.getSimpleName(), blendingTransition);
    }
}
