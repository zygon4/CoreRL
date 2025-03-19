package com.zygon.rl.world.character;

/**
 *
 * Concept: history through time. facts on a timeline that can be queried as a
 * timeseries. "interaction with family["jones"] within 2 weeks",
 * "sisters["ann", "selma"]"
 *
 * Should this be a central logging facility for any permanent/sticky facts.
 * Such as "character gained a limb".
 *
 *
 * @author zygon
 */
public class History {

    // TBD: is it a bunch of small classes/enums under one roof vs a single
    // taxonimy?
    // e.g. Fact { type, Something }
    // or Fact
    public static enum FactType {
        family, //
        born, // date
        died, // date

    }

    public static class Fact {

    }

}
