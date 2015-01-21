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
import com.jabyftw.gameclient.entity.util.Box2dConstants;
import com.jabyftw.gameclient.entity.util.MapViewer;
import com.jabyftw.gameclient.gamestates.mapeditor.MapEditorPreparationState;
import com.jabyftw.gameclient.maps.util.BlockOpacity;
import com.jabyftw.gameclient.maps.util.BlockStorage;
import com.jabyftw.gameclient.maps.util.Material;
import com.jabyftw.gameclient.maps.util.MyContactListener;
import com.jabyftw.gameclient.screen.MovableCamera;
import com.jabyftw.gameclient.util.BlockAction;
import com.jabyftw.gameclient.util.Drawable;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;

/**
 * Created by Rafael on 15/01/2015.
 */
public class Map implements Drawable, Tickable, Disposable, Json.Serializable {

    public static final MapViewer DEFAULT_MAP_VIEWER = new MapViewer() {

        @Override
        public BlockOpacity getOpacityForBlock(Block block) {
            return BlockOpacity.FULLY_VISIBLE;
        }

        @Override
        public Vector2 getLocation() {
            return new Vector2(0, 0);
        }
    };

    public static final int TILE_WIDTH = 8, TILE_HEIGHT = 8;

    public static final float BASE_TILE_SCALE = 2.0f;

    public static final float TILE_SCALE_WIDTH = BASE_TILE_SCALE * TILE_WIDTH;
    public static final float TILE_SCALE_HEIGHT = BASE_TILE_SCALE * TILE_HEIGHT;

    public static final float BOX2D_TILE_SCALE_WIDTH = TILE_SCALE_WIDTH / Main.PIXELS_PER_METER;
    public static final float BOX2D_TILE_SCALE_HEIGHT = TILE_SCALE_HEIGHT / Main.PIXELS_PER_METER;

    public static ShapeRenderer shapeRenderer;

    private String displayName = "test_map";
    private int width, height;

    private boolean useLightning = false;
    private boolean shouldDispose = true;

    private Block[][] blocks;
    private MapViewer viewer = DEFAULT_MAP_VIEWER;

    private World world;
    private MovableCamera box2dCamera;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;

    public Map() {
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new MyContactListener());
        {
            float v_width_ppm = Main.V_WIDTH / Main.PIXELS_PER_METER;
            float v_height_ppm = Main.V_HEIGHT / Main.PIXELS_PER_METER;

            box2dCamera = new MovableCamera();
            box2dCamera.setToOrtho(false, v_width_ppm, v_height_ppm);
        }
        debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        rayHandler = new RayHandler(world);

        rayHandler.setBlur(true);
        rayHandler.setCulling(true);
        rayHandler.setShadows(true);

        /* NUMBER OF RAYS: 'between 5 and 128 has the best outcome' */
        Filter filter = new Filter();
        filter.categoryBits = Box2dConstants.BIT_LIGHT;
        filter.maskBits = Box2dConstants.BIT_BLOCK; // Stuff affected by lights
        PointLight.setContactFilter(filter);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(final float deltaTime) {
        world.step(Main.STEP, 6, 2);

        /*for(Block[] blockArray : blocks) {
            for(Block block : blockArray) {
                block.update(deltaTime);
            }
        }*/
        doForVisibleBlocks(new BlockAction() {
            @Override
            public void doActionForBlock(Block block) {
                block.update(deltaTime);
            }
        });
    }

    @Override
    public void draw(final SpriteBatch batch) {
        MovableCamera gameCamera = Main.getInstance().getGameCamera();
        {
            box2dCamera.updatePosition(new Vector2(gameCamera.position.x / Main.PIXELS_PER_METER, gameCamera.position.y / Main.PIXELS_PER_METER), false);
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
            if(useLightning) {
                rayHandler.setCombinedMatrix(box2dCamera.combined);
                rayHandler.updateAndRender();
            }
            if(Main.isDebugging) debugRenderer.render(world, box2dCamera.combined);

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
        this.width = Math.min(Math.max(width, MapEditorPreparationState.MINIMUM_WIDTH), MapEditorPreparationState.MAXIMUM_WIDTH);
    }

    public void setHeight(int height) {
        this.height = Math.min(Math.max(height, MapEditorPreparationState.MINIMUM_HEIGHT), MapEditorPreparationState.MAXIMUM_HEIGHT);
    }

    public String getDisplayName() {
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

    public World getWorld() {
        return world;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

    public Vector2 box2dCoordinatesToWorldCoordinates(Vector2 vector2) {
        return vector2.scl(1 / Map.BOX2D_TILE_SCALE_WIDTH, 1 / Map.BOX2D_TILE_SCALE_HEIGHT);
    }

    public Vector2 screenCoordinatesToWorldCoordinates(Vector2 vector2) {
        return vector2.scl(1f / Map.TILE_SCALE_WIDTH, 1f / Map.TILE_SCALE_HEIGHT);
    }

    public boolean isLocationValid(Vector2 vector2, boolean worldCoordinates) {
        // Box2dCoordinates / Map.TILE_SCALE -> worldCoordinates
        if(!worldCoordinates)
            vector2 = screenCoordinatesToWorldCoordinates(vector2);
        return vector2.x >= 0 && vector2.x < width && vector2.y >= 0 && vector2.y < height;
    }

    public Block getBlockFrom(Vector2 vector2) {
        return blocks[(int) vector2.x][(int) vector2.y];
    }

    public void createBaseBlock() {
        blocks = new Block[width][height];

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        ChainShape chainShape = new ChainShape();
        {
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.fixedRotation = true;

            float scaleWidth = Map.BOX2D_TILE_SCALE_WIDTH;
            float scaleHeight = Map.BOX2D_TILE_SCALE_HEIGHT;

            chainShape.createLoop(new Vector2[]{
                    new Vector2(0, 0),
                    new Vector2(scaleWidth, 0),
                    new Vector2(scaleWidth, scaleHeight),
                    new Vector2(0, scaleHeight)
            });

            fixtureDef.density = 1;
            fixtureDef.friction = 0.35f;
            fixtureDef.restitution = 0.07f;
            fixtureDef.shape = chainShape;
        }
        for(int x = 0; x < blocks.length; x++) {
            for(int y = 0; y < blocks[x].length; y++) {
                blocks[x][y] = Block.createBlock(bodyDef, fixtureDef, this, x, y, Material.AIR);
            }
        }
        chainShape.dispose();

        Vector2 min = new Vector2(0, 0),
                max = new Vector2(width * Map.TILE_SCALE_WIDTH, height * Map.TILE_SCALE_HEIGHT);

        createWorldBounds(min, max);
        Main.getInstance().getGameCamera().setCameraBounds(min, max);
    }

    private void createWorldBounds(Vector2 min, Vector2 max) {
        min = min.cpy().scl(1 / Main.PIXELS_PER_METER);
        max = max.cpy().scl(1 / Main.PIXELS_PER_METER);
        System.out.println("WorldBounds -> { min: " + min.toString() + " max: " + max.toString() + " }");

        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(new Vector2[]{
                new Vector2(min.x, min.y),
                new Vector2(max.x, min.y),
                new Vector2(max.x, max.y),
                new Vector2(min.x, max.y)
        });

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Box2dConstants.BIT_WORLD_BOUNDS;
        fixtureDef.filter.maskBits = Box2dConstants.BIT_PLAYER | Box2dConstants.BIT_ENEMY | Box2dConstants.BIT_BULLET;
        fixtureDef.shape = chainShape;
        body.createFixture(fixtureDef);

        chainShape.dispose();
    }

    public void doForVisibleBlocks(BlockAction blockAction) {
        Vector3 unprojectLower = box2dCamera.unproject(new Vector3(0, Gdx.graphics.getHeight(), 0)),
                unprojectUpper = box2dCamera.unproject(new Vector3(Gdx.graphics.getWidth(), 0, 0));

        Vector2 lower = new Vector2(unprojectLower.x, unprojectLower.y).scl(1 / Map.BOX2D_TILE_SCALE_WIDTH),
                upper = new Vector2(unprojectUpper.x, unprojectUpper.y).scl(1 / Map.BOX2D_TILE_SCALE_HEIGHT);

        if(!isLocationValid(lower, true)) lower = new Vector2(0, 0);
        if(!isLocationValid(upper, true)) upper = new Vector2(getWidth() - 1, getHeight() - 1);
        // TODO fix only the x or y, not both

        for(int x = MathUtils.floorPositive(lower.x); x <= MathUtils.floorPositive(upper.x); x++) {
            for(int y = MathUtils.floorPositive(lower.y); y <= MathUtils.floorPositive(upper.y); y++) {
                blockAction.doActionForBlock(blocks[x][y]);
            }
        }
    }

    public Array<Block> getBlocksNear(Vector2 position, float distance) {
        Array<Block> nearBlocks = new Array<Block>();

        Vector2 upperCorner = position.cpy().add(distance, distance);
        Vector2 lowerCorner = position.cpy().sub(distance, distance);

        if(!isLocationValid(upperCorner, true)) upperCorner = new Vector2(getWidth() - 1, getHeight() - 1);
        if(!isLocationValid(lowerCorner, true)) lowerCorner = new Vector2(0, 0);
        // TODO fix only the x or y, not both

        distance = Util.square(distance);

        for(int x = (int) lowerCorner.x; x < (int) upperCorner.x; x++) {
            for(int y = (int) lowerCorner.y; y < (int) upperCorner.y; y++) {
                Block block = blocks[x][y];
                if(block.getBox2dBody().getPosition().dst2(position) <= distance)
                    nearBlocks.add(block);
            }
        }

        return nearBlocks;
    }

    public Array<Block> filterBlocksByMaterial(Array<Block> blocks, Material... materials) {
        Array<Material> desiredMaterials = new Array<Material>();
        desiredMaterials.addAll(materials);

        for(Block block : blocks) {
            if(!desiredMaterials.contains(block.getMaterial(), true))
                blocks.removeValue(block, true);
        }

        return blocks;
    }

    public Vector2 getSpawnLocation() {
        return new Vector2(getWidth() / 2f, getHeight() / 2f);
    }

    public MovableCamera getBox2dCamera() {
        return box2dCamera;
    }

    public boolean useLightning() {
        return useLightning;
    }

    public void setUseLightning(boolean useLightning) {
        this.useLightning = useLightning;
        System.out.println("Using lightning: " + useLightning);
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

        System.out.println("Read " + size + " blocks");
        Main.gc();
    }
}
