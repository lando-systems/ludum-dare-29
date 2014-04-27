package lando.systems.ld29.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld29.core.Assets;

public class DirtBlock extends Block {
    private static TextureRegion img = Assets.blocks.get("dirt");

    public DirtBlock(float x, float y) {
        super(x, y);
        this.blockType = "dirt";
        setSprite(new Sprite(img));
    }
}
