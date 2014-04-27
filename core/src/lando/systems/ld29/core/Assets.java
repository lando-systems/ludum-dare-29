package lando.systems.ld29.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Assets {
    public static Random random;
    public static SpriteBatch batch;
    public static SpriteBatch hudBatch;
    public static ShapeRenderer shapes;

    public static Texture libgdx;
    public static Map<String,TextureRegion> blocks;
    public static Map<String,TextureRegion> resources;

    public static Texture scamps_spritesheet;
    public static Array<TextureRegion> scamps;
    public static int num_scamps;

    public static NinePatch panelBrown;
    public static NinePatch thoughtBubble;
    
    public static BitmapFont gameFont;
    public static BitmapFont TooltipHeaderFont;
    public static BitmapFont TooltipTextFont;
    public static BitmapFont HUDFont;
    
    //public static Sound sound;
    //public static Music music;

    public static void load() {
        random = new Random();
        batch  = new SpriteBatch();
        hudBatch = new SpriteBatch();
        shapes = new ShapeRenderer();

        libgdx = new Texture("badlogic.jpg");
        libgdx.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        Texture blocksSpritesheet = new Texture("art/blocks-spritesheet.png");
        blocks = new HashMap<String,TextureRegion>();
        blocks.put("dirt",   new TextureRegion(blocksSpritesheet, 0, 0, 16, 16));
        blocks.put("acorn",  new TextureRegion(blocksSpritesheet, 16, 0, 16, 16));
        blocks.put("gold",   new TextureRegion(blocksSpritesheet, 32, 0, 16, 16));
        blocks.put("grapes", new TextureRegion(blocksSpritesheet, 48, 0, 16, 16));
        blocks.put("iron",   new TextureRegion(blocksSpritesheet, 64, 0, 16, 16));
        blocks.put("stone",  new TextureRegion(blocksSpritesheet, 80, 0, 16, 16));
        blocks.put("lava",   new TextureRegion(blocksSpritesheet, 96, 0, 16, 16));
        blocks.put("water",  new TextureRegion(blocksSpritesheet, 112, 0, 16, 16));

        scamps_spritesheet = new Texture("art/scamps-spritesheet.png");
        scamps = new Array<TextureRegion>();
        TextureRegion[][] scamp_regions = TextureRegion.split(scamps_spritesheet, 16, 16);
        for(int i = 0; i < scamp_regions.length; ++i) {
            scamps.addAll(scamp_regions[i], 0, scamp_regions[i].length);
        }
        num_scamps = 81; // 9 rows of unusable scamp images at the moment (9 images per row)

        Texture resourceSpritesheet = new Texture("art/resources-spritesheet.png");
        resources = new HashMap<String,TextureRegion>();
        resources.put("field",   new TextureRegion(resourceSpritesheet, 0, 0, 32, 32));
        resources.put("forrest",  new TextureRegion(resourceSpritesheet, 32, 0, 32, 32));
        resources.put("mountain",   new TextureRegion(resourceSpritesheet, 64, 0, 32, 32));
        resources.put("quarry", new TextureRegion(resourceSpritesheet, 96, 0, 32, 32));
        resources.put("vinyard",   new TextureRegion(resourceSpritesheet, 128, 0, 32, 32));

//        sound = Gdx.audio.newSound("audio/sound.wav");
//        music = Gdx.audio.newMusic("audio/music.mp3");

        panelBrown = new NinePatch(
            new Texture("art/panel_brown.png"),
            10, 10, 10 , 10
        );
        

        panelBrown.setColor(new Color(153, 102, 51, 1));
        
        thoughtBubble = new NinePatch(
                new Texture("art/thought.png"),
                4, 5, 4 , 9
            );
        
        gameFont = new BitmapFont(Gdx.files.internal("fonts/ariel.fnt"),Gdx.files.internal("fonts/ariel.png"),false);
        TooltipHeaderFont = new BitmapFont(Gdx.files.internal("fonts/ariel.fnt"),Gdx.files.internal("fonts/ariel.png"),false);

        TooltipTextFont = new BitmapFont(Gdx.files.internal("fonts/ariel.fnt"),Gdx.files.internal("fonts/ariel.png"),false);
        TooltipTextFont.setScale(.7f);
        
        HUDFont = new BitmapFont(Gdx.files.internal("fonts/ariel.fnt"),Gdx.files.internal("fonts/ariel.png"),false);
        HUDFont.setScale(.5f);
        
    }

    public static void dispose() {
        libgdx.dispose();
        // todo : dispose the rest of the things
//        sound.dispose();
//        music.dispose();
        batch.dispose();
        hudBatch.dispose();
    }

}
