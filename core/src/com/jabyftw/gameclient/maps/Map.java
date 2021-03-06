package com.jabyftw.gameclient.maps;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.util.MapViewer;
import com.jabyftw.gameclient.gamestates.mapeditor.MapEditorPreparationState;
import com.jabyftw.gameclient.maps.util.BlockStorage;
import com.jabyftw.gameclient.maps.util.Material;
import com.jabyftw.gameclient.maps.util.MyContactListener;
import com.jabyftw.gameclient.screen.MovableCamera;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.GameDrawable;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;
import com.jabyftw.gameclient.util.tools.BlockAction;

import java.util.Iterator;

/**
 * Created by Rafael on 15/01/2015.
 */
public class Map implements GameDrawable, Tickable, Disposable, Json.Serializable {

    public static final MapViewer DEFAULT_MAP_VIEWER = new MapViewer() {

        @Override
        public float getOpacityForBlock(Block block) {
            return 1;
        }

        @Override
        public Vector2 getLocation() {
            return new Vector2(0, 0);
        }
    };

    public static ShapeRenderer shapeRenderer;

    private String displayName = "no_name";
    private int width, height;

    private boolean useLightning = false;
    private boolean shouldDispose = true;

    private Block[][] blocks;
    private MapViewer viewer = DEFAULT_MAP_VIEWER;

    private final MovableCamera box2dCamera;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private World world;

    public Map() {
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new MyContactListener());
        {
            Vector2 box2dCoordinates = Converter.SCREEN_COORDINATES.toBox2dCoordinates(new Vector2(Constants.Display.V_WIDTH, Constants.Display.V_HEIGHT));

            box2dCamera = new MovableCamera();
            box2dCamera.setToOrtho(false, box2dCoordinates.x, box2dCoordinates.y);
        }
        debugRenderer = new Box2DDebugRenderer(true, true, true, false, true, true);
        rayHandler = new RayHandler(world);

        rayHandler.setBlur(true);
        rayHandler.setCulling(true);
        rayHandler.setShadows(true);

        /* Number of rays: 'between 5 and 128 has the best outcome' */
        Filter filter = new Filter();
        filter.categoryBits = Constants.Box2dConstants.BIT_LIGHT;
        filter.maskBits = Constants.Box2dConstants.BIT_SOLID_BLOCK; // Stuff affected by lights
        PointLight.setContactFilter(filter);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(final float deltaTime) {
        world.step(Constants.Gameplay.STEP, 6, 2);
        rayHandler.update();

        // You should update every block
        for(Block[] blockArray : blocks) {
            for(Block block : blockArray) {
                block.update(deltaTime);
            }
        }
    }

    @Override
    public void drawGame(final SpriteBatch batch) {
        MovableCamera gameCamera = Main.getInstance().getGameCamera();
        {
            box2dCamera.updatePosition(Converter.SCREEN_COORDINATES.toBox2dCoordinates(new Vector2(gameCamera.position.x, gameCamera.position.y)), false);
            box2dCamera.update();
        }
        {
            batch.begin();
            doForVisibleBlocks(new BlockAction() {
                @Override
                public void doActionForBlock(Block block) {
                    block.draw(batch, viewer);
                }
            });
            batch.end();
        }
        {
            batch.setProjectionMatrix(box2dCamera.combined);

            if(Constants.isDebugging) debugRenderer.render(world, box2dCamera.combined);

            // Back to main camera
            batch.setProjectionMatrix(gameCamera.combined);
        }
    }

    @Override
    public void dispose() {
        if(shouldDispose) {
            rayHandler.dispose();
            rayHandler = null;

            debugRenderer.dispose();
            debugRenderer = null;

            world.dispose();
            world = null;

            shapeRenderer.dispose();
            shapeRenderer = null;
        }
        // Just do it once
        shouldDispose = false;
    }

    public void renderLightning(SpriteBatch batch) {
        if(useLightning) {
            batch.setProjectionMatrix(box2dCamera.combined);
            rayHandler.setCombinedMatrix(box2dCamera.combined);
            rayHandler.render();
        }
    }

    public void clearWorld() {
        rayHandler.dispose();
        rayHandler = new RayHandler(world);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = Math.min(Math.max(width, MathUtils.ceilPositive(MapEditorPreparationState.minimum.x)), MathUtils.ceilPositive(MapEditorPreparationState.maximum.x));
    }

    public void setHeight(int height) {
        this.height = Math.min(Math.max(height, MathUtils.ceilPositive(MapEditorPreparationState.minimum.y)), MathUtils.ceilPositive(MapEditorPreparationState.maximum.y));
    }

    String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public MapViewer getViewer() {
        return viewer;
    }

    public void setViewer(MapViewer viewer) {
        this.viewer = viewer;
    }

    public Vector2 getMaximumBox2dBounds() {
        return Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(width, height));
    }

    public Vector2 getMinimumBox2dBounds() {
        return Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(0, 0));
    }

    public World getWorld() {
        return world;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

    private Vector2 validateLocation(Vector2 worldCoordinates) {
        float x = worldCoordinates.x;
        float y = worldCoordinates.y;
        {
            // Fix X
            if(x < 0) x = 0;
            else if(x >= width) x = width - 1;

            // Fix Y
            if(y < 0) y = 0;
            else if(y >= height) y = height - 1;
        }
        return worldCoordinates.set(x, y);
    }

    public boolean isLocationValid(Vector2 worldCoordinates) {
        return worldCoordinates.x >= 0 && worldCoordinates.x < width && worldCoordinates.y >= 0 && worldCoordinates.y < height;
    }

    public Block getBlockFrom(Vector2 worldCoordinates) {
        return blocks[MathUtils.floorPositive(worldCoordinates.x)][MathUtils.floorPositive(worldCoordinates.y)];
    }

    public void createBaseBlock() {
        blocks = new Block[width][height];

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        ChainShape chainShape = new ChainShape();
        {
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.fixedRotation = true;

            Vector2 box2dLowerCorner = Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(0, 0)),
                    box2dUpperCorner = Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(1, 1));

            chainShape.createLoop(new Vector2[]{
                    new Vector2(box2dLowerCorner.x, box2dLowerCorner.y), // 0, 0
                    new Vector2(box2dUpperCorner.x, box2dLowerCorner.y), // 1, 0
                    new Vector2(box2dUpperCorner.x, box2dUpperCorner.y), // 1, 1
                    new Vector2(box2dLowerCorner.x, box2dUpperCorner.y)  // 0, 1
            });

            fixtureDef.density = 1;
            fixtureDef.friction = 0.35f;
            fixtureDef.restitution = 0.07f;
            fixtureDef.shape = chainShape;
        }
        for(int x = 0; x < blocks.length; x++) {
            for(int y = 0; y < blocks[x].length; y++) {
                blocks[x][y] = Block.createBlock(bodyDef, fixtureDef, this, x, y);
            }
        }
        chainShape.dispose();

        // World coordinates
        Vector2 min = new Vector2(0, 0), max = new Vector2(width, height);
        // Screen
        Main.getInstance().getGameCamera().setCameraBounds(Converter.WORLD_COORDINATES.toScreenCoordinates(min.cpy()), Converter.WORLD_COORDINATES.toScreenCoordinates(max.cpy()));
    }

    void doForVisibleBlocks(BlockAction blockAction) {
        Vector3 unprojectLower = box2dCamera.unproject(new Vector3(0, Gdx.graphics.getHeight(), 0)),
                unprojectUpper = box2dCamera.unproject(new Vector3(Gdx.graphics.getWidth(), 0, 0));

        // Unprojection from BOX2D camera
        Vector2 lowerCorner = Converter.BOX2D_COORDINATES.toWorldCoordinates(new Vector2(unprojectLower.x, unprojectLower.y)),
                upperCorner = Converter.BOX2D_COORDINATES.toWorldCoordinates(new Vector2(unprojectUpper.x, unprojectUpper.y));

        if(!isLocationValid(lowerCorner)) lowerCorner = validateLocation(lowerCorner);
        if(!isLocationValid(upperCorner)) upperCorner = validateLocation(upperCorner);

        for(int x = MathUtils.floorPositive(lowerCorner.x); x <= MathUtils.floorPositive(upperCorner.x); x++) {
            for(int y = MathUtils.floorPositive(lowerCorner.y); y <= MathUtils.floorPositive(upperCorner.y); y++) {
                try {
                    blockAction.doActionForBlock(blocks[x][y]);
                } catch(ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage() + "\nx: " + x + " y: " + y + " upper: " + upperCorner.toString() + " lower: " + lowerCorner.toString());
                }
            }
        }
    }

    public Array<Block> getBlocksNear(Vector2 worldCoordinates, float worldDistance) {

        Vector2 upperCorner = worldCoordinates.cpy().add(worldDistance, worldDistance);
        Vector2 lowerCorner = worldCoordinates.cpy().sub(worldDistance, worldDistance);

        if(!isLocationValid(lowerCorner)) lowerCorner = validateLocation(lowerCorner);
        if(!isLocationValid(upperCorner)) upperCorner = validateLocation(upperCorner);

        worldDistance = Util.square(worldDistance);
        Array<Block> nearBlocks = new Array<Block>(false, MathUtils.ceilPositive(worldDistance * Util.square(2) * 1.2f));

        for(int x = (int) lowerCorner.x; x < (int) upperCorner.x; x++) {
            for(int y = (int) lowerCorner.y; y < (int) upperCorner.y; y++) {
                Block block = blocks[x][y];
                if(Converter.BOX2D_COORDINATES.toWorldCoordinates(block.getBox2dBody().getPosition()).dst2(worldCoordinates) <= worldDistance)
                    nearBlocks.add(block);
            }
        }

        return nearBlocks;
    }

    public Array<Block> filterBlocksByMaterial(Array<Block> blocks, Material... materials) {
        Array<Material> desiredMaterials = new Array<Material>();
        desiredMaterials.addAll(materials);

        Iterator<Block> iterator = blocks.iterator();
        int size = blocks.size;
        while(iterator.hasNext() && size > 0) {
            Block block = iterator.next();
            if(!desiredMaterials.contains(block.getMaterial(), true))
                iterator.remove();
        }

        return blocks;
    }

    public Vector2 getSpawnLocation() {
        return new Vector2(getWidth() / 2f, getHeight() / 2f);
    }

    public MovableCamera getBox2dCamera() {
        return box2dCamera;
    }

    public void setUseLightning(boolean useLightning) {
        this.useLightning = useLightning;
        System.out.println("Map.setUseLightning { useLightning: " + useLightning + " }");
    }

    public boolean shouldDispose() {
        return shouldDispose;
    }

    public void setShouldDispose(boolean shouldDispose) {
        this.shouldDispose = shouldDispose;
    }

    public static Map readMap(FileHandle fileHandle) {
        return new Json().fromJson(Map.class, Base64Coder.decodeString(fileHandle.readString()));
    }

    public void saveMap() {
        Resources.getFileHandle(FilesEnum.LOCAL_MAP_DIRECTORY).child("/" + displayName + ".map")
                .writeString(Base64Coder.encodeString(new Json().toJson(this, Map.class)), false);
    }

    @Override
    public void write(Json json) {
        json.writeValue("displayName", getDisplayName(), String.class);
        json.writeValue("width", getWidth(), Integer.class);
        json.writeValue("height", getHeight(), Integer.class);

        json.writeObjectStart("blocks");
        int size = 0;
        {
            for(int x = 0; x < blocks.length; x++) {
                for(int y = 0; y < blocks[x].length; y++) {

                    Block block = blocks[x][y];
                    if(block.getMaterial() != Material.AIR) {
                        json.writeValue("blockStorage", new BlockStorage(x, y, block.getMaterial()), BlockStorage.class);
                        size++;
                    }

                }
            }
        }
        json.writeObjectEnd();

        System.out.println("Wrote map -> " + size + " blocks");
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        displayName = jsonData.getString("displayName");
        height = jsonData.getInt("height");
        width = jsonData.getInt("width");
        createBaseBlock();

        JsonValue listOfBlocks = jsonData.get("blocks");

        int size = 0;
        JsonValue next;
        while((next = listOfBlocks.get(size)) != null) {
            BlockStorage blockStorage = json.readValue(BlockStorage.class, next);
            blocks[blockStorage.x][blockStorage.y].setMaterial(blockStorage.material);
            size++;
        }

        System.out.println("Map.read = { " + size + " blocks }");
        Main.gc();
    }
}
