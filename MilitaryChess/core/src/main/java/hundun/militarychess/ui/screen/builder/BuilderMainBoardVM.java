package hundun.militarychess.ui.screen.builder;

import hundun.militarychess.ui.screen.PlayScreen;
import hundun.militarychess.ui.screen.shared.BasePageableTable;
import lombok.Getter;

public class BuilderMainBoardVM extends BasePageableTable {

    PlayScreen builderScreen;

    @Getter
    AllButtonPageVM allButtonPageVM;


    private enum BuilderMainBoardState {
        PAGE1,
        PAGE2
    }

    public BuilderMainBoardVM(PlayScreen screen) {
        super(screen);
        init("", screen.getGame());

        this.builderScreen = screen;

        this.allButtonPageVM = new AllButtonPageVM(screen);

        addPage(BuilderMainBoardState.PAGE1.name(),
                null,
                allButtonPageVM
        );

    }

    public void updateForShow() {

        updateByState(BuilderMainBoardState.PAGE1.name());
    }

}
