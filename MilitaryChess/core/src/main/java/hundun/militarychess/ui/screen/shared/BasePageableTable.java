package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.HashMap;
import java.util.Map;
import hundun.militarychess.ui.MilitaryChessGame;
import hundun.militarychess.ui.screen.AbstractMilitaryChessScreen;

public abstract class BasePageableTable extends MyWindow {
    protected final AbstractMilitaryChessScreen screen;
    private final Container<Table> currentTableContainer;
    private final Map<String, Table> pageRootTableMap = new HashMap<>();
    private final HorizontalGroup horizontalGroup = new HorizontalGroup();
    public BasePageableTable(AbstractMilitaryChessScreen screen) {
        this.screen = screen;
        this.currentTableContainer = new Container<>();
    }

    @Override
    public void init(String title, MilitaryChessGame game, Button titleButton) {
        super.init(title, game, titleButton);

        currentTableContainer.fill();
        currentTableContainer.pad(25);

        this.addToMain(horizontalGroup);

        this.rowToMain();

        this.addToMain(currentTableContainer)
                .width(screen.getGame().getScreenContext().getLayoutConst().ANY_EXTRA_TOTAL_WIDTH)
                .growY();
    }

    protected void addPage(
            String pageKey,
            String buttonText,
            Table pageRootTable
    ) {

        if (buttonText != null) {
            TextButton button = new TextButton(buttonText, screen.getGame().getMainSkin());
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    updateByState(pageKey);
                }
            });
            horizontalGroup.addActor(button);
        }
        pageRootTableMap.put(pageKey, pageRootTable);
    }
    protected void updateByState(String key) {
        currentTableContainer.setActor(pageRootTableMap.get(key));
    }

}
