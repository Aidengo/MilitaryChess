package hundun.militarychess.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import lombok.Getter;

public class TextureManager {
    MilitaryChessGame game;

    @Getter
    Drawable mcStyleTable;
    @Getter
    Drawable mcStyleTableTop;
    @Getter
    Drawable mcStyleTableBottom;
    @Getter
    Drawable deskBackground;

    public TextureManager(MilitaryChessGame game) {
        this.game = game;
    }

    public void lazyInitOnCreateStage1() {
        NinePatch tempNinePatch;

        tempNinePatch = new NinePatch(
                ignoreFirstLineTexture("McStyleTable.9.png"),
                20, 20, 20, 20
                );
        mcStyleTable = new NinePatchDrawable(tempNinePatch);
        tempNinePatch = new NinePatch(
                ignoreFirstLineTexture("McStyleTable-top.9.png"),
                20, 20, 20, 0
                );
        mcStyleTableTop = new NinePatchDrawable(tempNinePatch);
        tempNinePatch = new NinePatch(
                ignoreFirstLineTexture("McStyleTable-bottom.9.png"),
                20, 20, 0, 20
                );
        mcStyleTableBottom = new NinePatchDrawable(tempNinePatch);

        tempNinePatch = new NinePatch(
                new Texture(Gdx.files.internal("deskBackground.9.png")),
                20, 20, 20, 20
        );
        deskBackground = new NinePatchDrawable(tempNinePatch);
    }

    private TextureRegion ignoreFirstLineTexture(String file) {
        Texture texture = new Texture(Gdx.files.internal(file));
        return new TextureRegion(texture,
                1, 1, texture.getWidth() -1, texture.getHeight() -1
                );
    }
}
