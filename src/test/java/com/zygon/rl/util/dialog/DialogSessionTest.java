/*
 * Copyright Liminal Data Systems 2024
 */
package com.zygon.rl.util.dialog;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author djc
 */
public class DialogSessionTest {

    @Test
    public void testIsTerminal() {
        Dialog dialog = Dialog.create("greetings");
        Assert.assertTrue(dialog.isTerminal());
    }

    @Test
    public void testIsNotTerminal() {
        DialogChoice choice = DialogChoice.create("hi there");
        Dialog dialog = Dialog.create("greetings", Optional.empty(), List.of(choice));
        Assert.assertFalse(dialog.isTerminal());
    }

    @Test
    public void testSessionEnds() {

        Dialog dialogA = Dialog.create("greetings");
//        Dialog dialogB = Dialog.create("greetings");

        DialogChoice choiceX = DialogChoice.create("Choice X");
//        DialogChoice choiceY = DialogChoice.create("Choice Y");

        dialogA = dialogA.addChoices(List.of(choiceX));

        DialogSession session = DialogSession.play(dialogA);

        Assert.assertEquals("greetings", session.getGreeting());

        Map<Integer, String> dialogChoices = session.getDialogChoices();
        Assert.assertEquals(1, dialogChoices.size());
        Assert.assertEquals(1, dialogChoices.entrySet().iterator().next().getKey().intValue());
        Assert.assertEquals("Choice X", dialogChoices.entrySet().iterator().next().getValue());

        DialogSession picked = session.pick(null, 1);
        Assert.assertNull(picked);
    }

    @Test
    public void testSessionContinues() {

        Dialog dialogA = Dialog.create("greetings");

        DialogChoice choiceX = DialogChoice.create("Choice X", Optional.empty(), Optional.of(dialogA));

        dialogA = dialogA.addChoices(List.of(choiceX));

        DialogSession session = DialogSession.play(dialogA);

        Assert.assertEquals("greetings", session.getGreeting());

        Map<Integer, String> dialogChoices = session.getDialogChoices();
        Assert.assertEquals(1, dialogChoices.size());
        Assert.assertEquals(1, dialogChoices.entrySet().iterator().next().getKey().intValue());
        Assert.assertEquals("Choice X", dialogChoices.entrySet().iterator().next().getValue());

        DialogSession picked = session.pick(null, 1);
        Assert.assertEquals("greetings", picked.getGreeting());
    }

    @Test
    public void testSessionContinues2() {

        Dialog dialogA = Dialog.create("greetingsA");
        Dialog dialogB = Dialog.create("greetingsB");

        DialogChoice choiceX = DialogChoice.create("Choice X", Optional.empty(), Optional.of(dialogA));
        DialogChoice choiceY = DialogChoice.create("Choice Y", Optional.empty(), Optional.of(dialogB));

        dialogA = dialogA.addChoices(List.of(choiceX, choiceY));

        DialogSession session = DialogSession.play(dialogA);

        Assert.assertEquals("greetingsA", session.getGreeting());

        Map<Integer, String> dialogChoices = session.getDialogChoices();
        Assert.assertEquals(2, dialogChoices.size());
        Assert.assertEquals(1, dialogChoices.entrySet().iterator().next().getKey().intValue());

        Assert.assertEquals("Choice X", dialogChoices.get(1));
        Assert.assertEquals("Choice Y", dialogChoices.get(2));

        // Goes back to A
        DialogSession pickedA = session.pick(null, 1);
        Assert.assertEquals("greetingsA", pickedA.getGreeting());

        DialogSession pickedB = session.pick(null, 2);
        Assert.assertEquals("greetingsB", pickedB.getGreeting());
    }
}
