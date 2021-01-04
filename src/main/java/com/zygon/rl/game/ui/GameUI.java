package com.zygon.rl.game.ui;

import com.zygon.rl.game.Game;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldTile;
import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.ColorThemes;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.application.AppConfig;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.grid.TileGrid;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class GameUI {

    private static final System.Logger logger = System.getLogger(GameUI.class.getCanonicalName());

    private final Game game;

    public GameUI(Game game) {
        this.game = game;
    }

    public void start() {
        //LibgdxApplications
        TileGrid tileGrid = SwingApplications.startTileGrid(
                AppConfig.newBuilder()
                        .withTitle(game.getConfiguration().getGameName() + "      " + POWERED_BY)
                        .withSize(Size.create(80, 60))
                        //                        .withDebugMode(true)
                        //                        .withDebugConfig(DebugConfig.newBuilder().withRelaxBoundsCheck(true).build())
                        .withDefaultTileset(CP437TilesetResources.rexPaint16x16())
                        .build());

        TitleView titleView = new TitleView(tileGrid, ColorThemes.afterDark(), game);

        titleView.dock();
    }

    private static final String POWERED_BY = "[powered by https://github.com/zygon4/CoreRL]";

    static TileColor convert(Color color) {
        return TileColor.create(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static Location convert(Position position) {
        return Location.create(position.getX(), position.getY());
    }

    static Map<Location, Color> createMiniMap(World world, Location center) {

        // round
        Location rounded = Location.create(25 * (Math.round(center.getX() / 25)),
                25 * (Math.round(center.getY() / 25)));

        // PERF: could be passed in for performance
        Map<Location, Color> colorsByLocation = new HashMap<>();

        for (int y = rounded.getY() + 200, realY = 0; y > rounded.getY() - 200; y -= 25, realY++) {
            for (int x = rounded.getX() - 200, realX = 0; x < rounded.getX() + 200; x += 25, realX++) {

                Location location = Location.create(x, y);
                Entity entity = world.getTerrain(location);
                WorldTile wt = WorldTile.get(entity);

                colorsByLocation.put(Location.create(realX, realY), wt.getColor());
            }
        }

        return colorsByLocation;
    }

    static Entity getNPC(Game game, Location location) {

        Set<Entity> entities = game.getState().getWorld().getAll(location, null);

        return entities.stream()
                .filter(ent -> ent.getAttribute(CommonAttributes.NPC.name()) != null)
                .findFirst().orElse(null);
    }
}
