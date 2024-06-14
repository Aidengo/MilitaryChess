package hundun.militarychess.ui.screen.shared;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import hundun.militarychess.ui.MilitaryChessGame;
import lombok.Getter;

public class MyWindow extends Table {
    @Getter
    Label titleLabel;
    @Getter
    Table titleTable;
    @Getter
    Table mainTable;
    @Getter
    int titleHeight = 80;

    public MyWindow() {
    }

    public MyWindow(String title, MilitaryChessGame game) {
        init(title, game, null);
    }

    public void init(String title, MilitaryChessGame game) {
        init(title, game, null);
    }

    public void init(String title, MilitaryChessGame game, Button titleButton) {
        titleLabel = newLabel(title, game.getMainSkin());
        titleLabel.setEllipsis(true);

        titleTable = new Table();
        titleTable.setBackground(game.getTextureManager().getMcStyleTableTop());
        titleTable.add(titleLabel).grow();

        if (titleButton!= null) {
            titleTable.add(titleButton);
        }

        super.add(titleTable).height(titleHeight).growX().row();

        mainTable = new Table();
        mainTable.setBackground(game.getTextureManager().getMcStyleTableBottom());
        super.add(mainTable).grow();
    }

    protected Label newLabel (String text, Skin skin) {
        return new Label(text, skin);
    }

    public <T extends Actor> Cell<T> addToMain(T actor) {
        return mainTable.add(actor);
    }

    public Cell<?> rowToMain() {
        return mainTable.row();
    }
}
