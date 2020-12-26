/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.world;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * TODO: place in JSON and make very overridable
 */
public class Entities {

    private Entities() {
    }

    private static final Attribute IMPASSABLE = Attribute.builder()
            .setName(CommonAttributes.IMPASSABLE.name()).setValue(Boolean.TRUE.toString()).build();
    private static final Attribute CLOSED = Attribute.builder()
            .setName(CommonAttributes.CLOSED.name()).setValue(Boolean.TRUE.toString()).build();
    private static final Attribute HEALTH = Attribute.builder()
            .setName(CommonAttributes.HEALTH.name()).setValue("100").build();
    private static final Attribute LIVING = Attribute.builder()
            .setName(CommonAttributes.LIVING.name()).setValue(Boolean.TRUE.toString()).build();
    private static final Attribute NPC = Attribute.builder()
            .setName(CommonAttributes.NPC.name()).setValue(Boolean.TRUE.toString()).build();

    public static Entity FLOOR = Entity.builder()
            .setName("FLOOR")
            .setDescription("Floor")
            .build();

    public static Entity DIRT = Entity.builder()
            .setName("DIRT")
            .setDescription("Dirt")
            .build();

    public static Entity DOOR = Entity.builder()
            .setName("DOOR")
            .setDescription("Door")
            .setAttributes(getAttributes(
                    CLOSED,
                    IMPASSABLE,
                    create(CommonAttributes.VIEW_BLOCK.name(), "1.0").build()))
            .build();

    public static Entity GRASS = Entity.builder()
            .setName("GRASS")
            .setDescription("Grass")
            .build();

    public static Entity ROCK = Entity.builder()
            .setName("ROCK")
            .setDescription("Rock")
            .setAttributes(getAttributes(
                    create(CommonAttributes.TERRAIN_DIFFICULTY.name(), "0.25").build(),
                    create(CommonAttributes.VIEW_BLOCK.name(), "0.1").build()))
            .build();

    public static Entity TREE = Entity.builder()
            .setName("TREE")
            .setDescription("Tree")
            .setAttributes(getAttributes(
                    IMPASSABLE,
                    create(CommonAttributes.VIEW_BLOCK.name(), "1.0").build()))
            .build();

    public static Entity MONSTER = Entity.builder()
            .setName("MONSTER")
            .setDescription("Monster")
            .setAttributes(getAttributes(
                    LIVING,
                    HEALTH,
                    IMPASSABLE,
                    NPC,
                    create(CommonAttributes.VIEW_BLOCK.name(), "0.25").build()))
            .build();

    public static Entity PLAYER = Entity.builder()
            .setName("PLAYER")
            .setDescription("Player")
            .setAttributes(getAttributes(
                    LIVING,
                    HEALTH,
                    IMPASSABLE,
                    create(CommonAttributes.VIEW_BLOCK.name(), "0.25").build()))
            .build();

    public static Entity PUDDLE = Entity.builder()
            .setName("PUDDLE")
            .setDescription("A puddle")
            .setAttributes(getAttributes(
                    create(CommonAttributes.TERRAIN_DIFFICULTY.name(), "0.25").build()))
            .build();

    public static Entity TALL_GRASS = Entity.builder()
            .setName("TALL_GRASS")
            .setDescription("Tall grass")
            .setAttributes(getAttributes(
                    create(CommonAttributes.VIEW_BLOCK.name(), "0.10").build()))
            .build();

    public static Entity WALL = Entity.builder()
            .setName("WALL")
            .setDescription("Wall")
            .setAttributes(getAttributes(
                    IMPASSABLE,
                    create(CommonAttributes.VIEW_BLOCK.name(), "1.0").build()))
            .build();

    public static Entity WINDOW = Entity.builder()
            .setName("WINDOW")
            .setDescription("Window")
            .setAttributes(getAttributes(
                    CLOSED,
                    IMPASSABLE,
                    create(CommonAttributes.TERRAIN_DIFFICULTY.name(), "3.0").build(),
                    create(CommonAttributes.VIEW_BLOCK.name(), "1.0").build()))
            .build();

    public static Entity createDoor() {
        return DOOR.copy()
                .setName("DOOR")
                .setDescription("Door")
                .build();
    }

    public static Entity.Builder createMonster(String name) {
        return MONSTER.copy()
                .setId(UUID.randomUUID())
                .setName("MONSTER")
                .setDescription("Monster");
    }

    public static Entity createWindow() {
        return WINDOW.copy()
                .setName("WINDOW")
                .setDescription("Window")
                .build();
    }

    public static Attribute.Builder create(String name, String value) {
        return Attribute.builder()
                .setName(name)
                .setValue(value);
    }

    private static Map<String, Attribute> getAttributes(Attribute... attrs) {
        if (attrs == null) {
            return Collections.emptyMap();
        }

        return Stream.of(attrs)
                .collect(Collectors.toMap(Attribute::getName, v -> v));
    }
}
