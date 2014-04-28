package lando.systems.ld29.scamps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld29.Global;
import lando.systems.ld29.World;
import lando.systems.ld29.core.Assets;
import lando.systems.ld29.resources.Resource;
import lando.systems.ld29.scamps.ScampResources.ScampResourceType;
import lando.systems.ld29.scamps.Scamp.*;
import lando.systems.ld29.structures.HouseStructure;
import lando.systems.ld29.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Ian McNamara <ian.mcnamara@doit.wisc.edu>
 * Date: 4/26/14 @ 3:58 PM
 */
public class ScampManager {

//    private static enum ScampPriority {
//        FOOD,
//        WOOD,
//        STONE,
//        IRON,
//        MARBLE,
//        GOLD,
//        GRAPES,
//        FUEL,
//        CIRCUITS,
//        SPACEROCK,
//        STEEL,
//        BUILDHOUSE,
//        BUILDWAREHOUSE,
//        BUILDTEMPLE,
//        BUILDFACTORY,
//        BUILDSPACESHIP,
//        PRAY,
//        SLEEP,
//        GETONSHIP
//        
//    }

    private final static int INITIAL_SCAMP_COUNT = 2;
//    private final static float DEFAULT_SCAMP_PRIORITY_SCORE = 10;
//    private final static float PRIORITY_RECOMPUTE_TIME = 10; // in seconds

    World world;
    public Array<Scamp> scamps;
    public ScampResources scampResources;

    float accum = 0;

//    private Map<ScampPriority, Float> scampPriorityScores = new HashMap<ScampPriority, Float>(
//            ScampPriority.values().length
//    );

    public ScampManager(World world) {
        this.world = world;
        this.placeScamps();
        this.scampResources = new ScampResources();
    }

    /**
     * Places the starting number of scamps in the world.
     * Clears any already existing Scamps, so this should only really be called on startup.
     */
    private void placeScamps() {
        scamps = new Array<>();
        for (int i = 0; i < INITIAL_SCAMP_COUNT; i++) {
            scamps.add(new Scamp( Assets.random.nextInt(world.gameWidth) ));
        }
    }

    public Scamp getScampFromPos(float x, float y) {
        Scamp scamp = null;
        if (Utils.isBetween(y, Global.GROUND_LEVEL, Global.GROUND_LEVEL + Scamp.SCAMP_SIZE)) {
            for(Scamp s : scamps) {
                if (Utils.isBetween(x, s.getPixelPosition(), s.getPixelPosition() + Scamp.SCAMP_SIZE)) {
                    scamp = s;
                    break;
                }
            }
        }
        return scamp;
    }

    public void update(float dt) {
//        accum += dt;
//        if (accum > PRIORITY_RECOMPUTE_TIME) {
//            accum -= PRIORITY_RECOMPUTE_TIME;
//            System.out.println("update() | it's time");
//            determinePriorities();
//        }
    	

        for(Scamp scamp : scamps) {
        	if (scamp.isIdle()) GiveScampJob(scamp);
            scamp.update(dt);
            doGather(scamp);
        }
    }

    public void renderScamps(SpriteBatch batch) {
        for(Scamp scamp : scamps) { scamp.render(batch); }
    }

    private void doGather(Scamp scamp) {
        if (scamp.workingResource == null) return;
        if (scamp.isGatherReady()) {
            int numResourcesGathered = world.rManager.takeResource((int) scamp.workingResource.getX(), 1);
            if (numResourcesGathered > 0) {
                scampResources.addScampResources(scampResources.getType(scamp.workingResource.resourceName()), numResourcesGathered);
                System.out.println("update() | scamp " + scamp.toString() + " gathered " + numResourcesGathered + " resources of type '" + scamp.workingResource.resourceName() + "'");
            } else {
                scamp.setWorkingResource(null);
                scamp.setState(ScampState.IDLE);
                scamp.setTarget(Assets.random.nextInt(World.gameWidth));

            }
            scamp.didGather();
        }
    }
    
    private void GiveScampJob(Scamp scamp){
    	if (scamp.hungerAmount > 5 && scampResources.getScampResourceCount(ScampResourceType.FOOD) > 0){
    		scamp.currentState = ScampState.EATING;
    		return;
    	}
    	
    	if (scamp.hungerAmount > 3 && 
    		scampResources.getScampResourceCount(ScampResourceType.FOOD) < scamps.size &&
    		world.rManager.CountofType("field") > 0){
    		
    			scamp.currentState = ScampState.FOOD;
    			gatherResource(scamp, "field");
    			return;
    	}
    	
    	if (world.structureManager.countStructures("spaceship") > 0){
    		scamp.currentState = ScampState.GETONSHIP;
    		// TODO target spaceship?
    		return;
    	}
    	
    	if (!world.dayCycle.isDay()){
    		scamp.currentState = ScampState.SLEEP;
    		//Get a House that has space available
    		return;
    	}
    	
    	
    	// Build SpaceShip
    	
    	// Build Temple
    	
    	// Build Factory
    	
    	// Build Warehouse
    	
    	// Build House
    	if (tryBuilding(scamp, "house")){
    		return;
    	}

    	
    	
    	
    	
    	
    	
    	//Nothing else Walk Around
    	scamp.currentState = ScampState.STROLLING;
    	scamp.setTarget(Assets.random.nextInt(World.gameWidth));
 	
    }
    
    private boolean tryBuilding(Scamp scamp, String name){
    	Map<String, Integer> costs = new HashMap<String, Integer>();
    	switch(name){
    	case "house":
    		costs = HouseStructure.buildCost;
    		break;
    	}
    	for(String key : costs.keySet()){
    		if (scampResources.getScampResourceCount(key) < costs.get(key) &&
    			world.rManager.CountofType(world.rManager.getResourceFromProduct(key))){
    				gatherResource(scamp, key);
    		}
    	}
    	
    	return false;
    }
    
    private void gatherResource(Scamp scamp, String resourceName){
		Resource resource = world.rManager.getResource(resourceName);
		scamp.setTarget(resource.getX());
		scamp.setWorkingResource(resource);
    }

//    public void determinePriorities() {
//        System.out.println("determinePriorities | called");
//
//        updatePriorities();
//        updateScampTask(getIdleScamp(), getTopScampPriority());
//    }

//    private void updatePriorities() {
//        for (ScampPriority priority: ScampPriority.values()) {
//            switch (priority) {
//                case FOOD:
//                    int foodCount = scampResources.getScampResourceCount(ScampResources.ScampResourceType.FOOD);
//                    float foodPriority = 50;
//
//                    double temp = (foodCount / (scamps.size * 2));
//                    if (temp < 1) {
//                        temp = (1 - temp) * 50;
//                    } else {
//                        temp = Math.pow(temp, 2);
//                    }
//
//                    foodPriority += (int)temp;
//                    scampPriorityScores.put(priority, foodPriority);
//                    System.out.println("determinePriorities | foodPriority='" + foodPriority + "'");
//
//                    break;
//
//                case SHELTER:
//                    float shelterPriority = 40;
//                    scampPriorityScores.put(priority, shelterPriority);
//                    System.out.println("determinePriorities | shelterPriority='" + shelterPriority + "'");
//                    break;
//
//                case WINE:
//                    float winePriority = 30;
//                    scampPriorityScores.put(priority, winePriority);
//                    System.out.println("determinePriorities | winePriority='" + winePriority + "'");
//                    break;
//
//                case TEMPLE:
//                    float templePriority = 5;
//                    scampPriorityScores.put(priority, templePriority);
//                    System.out.println("determinePriorities | templePriority='" + templePriority + "'");
//                default:
//            }
//        }
//    }

//    private void updateScampTask(Scamp idleScamp, ScampPriority topPriority) {
//        if (idleScamp == null) {
//            System.out.println("determinePriorities | no idle scamps available for current top priority '" + topPriority.toString() + "'");
//        } else {
//            switch (topPriority) {
//                case FOOD:
//                    // find a field to harvest from
//                    Resource resource = world.rManager.getResource("field");
//                    if (resource != null) {
//                        idleScamp.setTarget(resource.getX());
//                        idleScamp.setState(ScampState.HARVESTING);
//                        idleScamp.setWorkingResource(resource);
//                        System.out.println("determinePriorities | Scamp " + idleScamp.toString() + " now harvesting field at x=" + idleScamp.getBlockTargetPosition());
//                    } else {
//                        // todo: handle case where there are no fields
//                        System.out.println("determinePriorities | top priority is food, but can't find field resource");
//                    }
//                    break;
//                case SHELTER:
//                    break;
//                case WINE:
//                    break;
//                case TEMPLE:
//                    break;
//            }
//        }
//    }

//    private Scamp getIdleScamp() {
//        for(Scamp scamp : scamps) {
//            if (scamp.isIdle()) {
//                return scamp;
//            }
//        }
//        return null;
//    }

//    private ScampPriority getTopScampPriority() {
//        float maxPriority = -999999;
//        ScampPriority topPriority = null;
//
//        for(ScampPriority priority : scampPriorityScores.keySet()) {
//            float thisPriority = scampPriorityScores.get(priority);
//            if (thisPriority > maxPriority) {
//                maxPriority = thisPriority;
//                topPriority = priority;
//            }
//        }
//        return topPriority;
//    }

}
