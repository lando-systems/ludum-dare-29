package lando.systems.ld29;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import lando.systems.ld29.blocks.*;
import lando.systems.ld29.core.Assets;
import lando.systems.ld29.scamps.ScampResources.ScampResourceType;
import lando.systems.ld29.screens.GameScreen;
import lando.systems.ld29.util.Config;

public class Hud {
    //private static String[] blockNames = Assets.blocks.keySet().toArray(new String[Assets.blocks.size()]);
	private static String[] blockNames=  { "dirt", "wheat", "stone", "acorn", "grapes", "marble", "iron", "gold" };
    private ScampResourceType[] types = {
        ScampResourceType.FOOD,
        ScampResourceType.WOOD,
        ScampResourceType.STONE,
        ScampResourceType.IRON,
        ScampResourceType.MARBLE,
        ScampResourceType.GOLD,
        ScampResourceType.GRAPES,
        ScampResourceType.FUEL,
        ScampResourceType.CIRCUITS,
        ScampResourceType.METEOR,
        ScampResourceType.STEEL,
        ScampResourceType.PEOPLE
    };
    
    private ArrayList<Plaque> resources = new ArrayList<Plaque>();
    private ArrayList<IToolTip> toolTipItems = new ArrayList<IToolTip>(20);
    
    static final float HUD_BLOCK_WIDTH = 32;
    static final float HUD_WIDTH = blockNames.length * HUD_BLOCK_WIDTH;
    static final float HUDX = (Config.window_width - HUD_WIDTH) / 2;                              
    
    static final NinePatch Panel = Assets.panelBrown;
    
    static final float HUDY = Panel.getPadBottom() + 1;

    public static final float Height = Panel.getTopHeight() + HUD_BLOCK_WIDTH + Assets.panelBrown.getBottomHeight() + 2;
    
    private HudBlock[] blocks;
    private World world;
    private BeliefMeter beliefMeter; 
        
    public Tooltip tooltip;
    
    public Hud(World world) {
        this.world = world;
        tooltip = new Tooltip(world);
        blocks = new HudBlock[blockNames.length];
        float newX = HUDX;
        for(int i = 0; i < blockNames.length; i++){
            blocks[i] = new HudBlock(blockNames[i]);
            blocks[i].setSize(HUD_BLOCK_WIDTH, HUD_BLOCK_WIDTH);
            blocks[i].setPosition(newX, HUDY);
            newX += HUD_BLOCK_WIDTH;
            toolTipItems.add(blocks[i]);
        }
        
        float width = HUDX - ((Panel.getPadLeft() * 2) + Panel.getPadRight()); // 30
        
        float height = 25;
        beliefMeter = new BeliefMeter(width, height);
        beliefMeter.setPosition(10, Global.UNDERGROUND_LEVEL - (height + 1));
        toolTipItems.add(beliefMeter);
        
        addResources(types, HUDX + HUD_WIDTH + Panel.getPadRight(), height, width);
    }

    private void addResources(ScampResourceType[] types, float left, float plaqueHeight, float totalWidth)
    {
    	float vGap = (Global.UNDERGROUND_LEVEL - (plaqueHeight*2)) /2;
    	
    	int columns = (int)Math.ceil(types.length / 2.0);
    	
    	float hGap = Panel.getPadLeft();
    	float plaqueWidth = (totalWidth - ((columns + 1)* hGap)) / columns;
    	
    	float x = left + hGap;
    	float y = Global.UNDERGROUND_LEVEL - (plaqueHeight + 1);
    	
    	for (int i = 0; i < types.length; i++) {
    		if (i == columns) {
    			y -= (plaqueHeight + vGap);
    			x = left + hGap;
    		}    		
    		
    		Plaque p = new Plaque(types[i], plaqueWidth, plaqueHeight);
    		p.setPosition(x,  y);
    		x += plaqueWidth + hGap;
    		
    		resources.add(p);
    		toolTipItems.add(p);
    	}    	
    }
    
    boolean justClicked = true;
    
    public void update(float dt, Player player){
        tooltip.update(dt);
        beliefMeter.setValue(player.belief);
        
        for (Plaque plaque : resources) {
        	plaque.update(world.scampManager, dt);
        }
        
        for (HudBlock hudBlock : blocks) {
        	hudBlock.update(player.belief);
        }
        
    	if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && player.inputDelay <=0 && player.xPos == player.xTarget){
            if (justClicked == false){
                int x = (int)(player.xPos + .5f);
                Block block = getBlockForCoords(x);
                if(null != block) {
                	block.setNewPosition(x, 0);
                	float cost = block.cost;
                    if (player.belief > cost && world.grid.pushUp(block, x)){
                    	player.belief -= cost;
                    	player.inputDelay = .5f; // Seconds until we can act again.
                    	player.animationTime = 0;
                    }
                }
                
            }
            justClicked = true;
        } else {
            justClicked = false;
        }
    }
    
    public IToolTip getToolTipItemFromPos(float x, float y) {
    	IToolTip toolTipItem = null;
    	for (IToolTip toolTip : toolTipItems) {
    		if (toolTip.getToolTipBounds().contains(x, y)) {
    			toolTipItem = toolTip;
    			break;
    		}
    	}
    	return toolTipItem;
    }

    public void render(SpriteBatch batch) {
        // Draw belief meter
        beliefMeter.render(batch);
        
        // Draw block picker
        Assets.panelBrown.draw(
            batch,
            HUDX - Assets.panelBrown.getPadLeft(),
            HUDY - Assets.panelBrown.getPadBottom(),
            (blockNames.length * HUD_BLOCK_WIDTH) + (Assets.panelBrown.getPadRight() * 2),
            (Assets.panelBrown.getPadTop() * 2) + HUD_BLOCK_WIDTH
        );
        
        for(HudBlock block : blocks){
            block.draw(batch);
        }
        
        for (Plaque p : resources) {
            p.render(batch);
        }

        tooltip.render(batch);
    }

    private Block getBlockForCoords(int column){
        Vector3 clickPoint = GameScreen.hudCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        Block ret = null;
        for(int i = 0; i < blocks.length; i++){
            if(blocks[i].getBoundingRectangle().contains(clickPoint.x, clickPoint.y)) {
                if (blockNames[i].equals("dirt")) {
                    ret = new DirtBlock(column, -1);

                } else if (blockNames[i].equals("stone")) {
                    ret = new StoneBlock(column, -1);

                } else if (blockNames[i].equals("iron")) {
                    ret = new IronBlock(column, -1);

                } else if (blockNames[i].equals("acorn")) {
                    ret = new AcornBlock(column, -1);

                } else if (blockNames[i].equals("grapes")) {
                    ret = new GrapesBlock(column, -1);

                } else if (blockNames[i].equals("wheat")) {
                    ret = new WheatBlock(column, -1);
                }
                else if (blockNames[i].equals("gold")) {
                    ret = new GoldBlock(column, -1);
                }
                else if (blockNames[i].equals("marble")) {
                    ret = new MarbleBlock(column, -1);
                }
            }
        }

        return ret;
    }
}
