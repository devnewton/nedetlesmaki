/*
 The MIT License (MIT)

 Copyright (c) 2014 devnewton <devnewton@bci.im>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
package org.geekygoblin.nedetlesmaki.game.components.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import im.bci.jnuit.animation.IAnimationCollection;
import im.bci.jnuit.animation.PlayMode;
import im.bci.jnuit.background.ColoredBackground;
import im.bci.jnuit.lwjgl.assets.IAssets;

/**
 *
 * @author devnewton
 */
@Singleton
public class CutScenes {

    private final IAssets assets;

    @Inject
    public CutScenes(IAssets assets) {

        this.assets = assets;
    }

    public void createCredits(Dialog dialog) {
        IAnimationCollection animations = assets.getAnimations("devnewton.nanim.gz");
        dialog.setBackground(new ColoredBackground(0, 0, 0, 1));
        dialog.addTirade(animations.getAnimationByName("devnewton").start(PlayMode.ONCE), (1280 - 512) / 2, (800 - 128) / 2, 512, 128, "dialog.credits.devnewton");
    }

    public void createIntro(Dialog dialog) {
        IAnimationCollection animations = assets.getAnimations("intro.nanim.gz");
        dialog.addTirade(animations.getAnimationByName("01").start(PlayMode.LOOP), "dialog.intro.01");
        dialog.addTirade(animations.getAnimationByName("02").start(PlayMode.LOOP), "dialog.intro.02");
        dialog.addTirade(animations.getAnimationByName("03").start(PlayMode.LOOP), "dialog.intro.03");
        dialog.addTirade(animations.getAnimationByName("04").start(PlayMode.LOOP), "dialog.intro.04");
        dialog.addTirade(animations.getAnimationByName("05").start(PlayMode.LOOP), "dialog.intro.05");
        dialog.addTirade(animations.getAnimationByName("06").start(PlayMode.LOOP), "dialog.intro.06");
        dialog.addTirade(animations.getAnimationByName("07").start(PlayMode.LOOP), "dialog.intro.07");
        dialog.addTirade(animations.getAnimationByName("08").start(PlayMode.LOOP), "dialog.intro.08");
        dialog.addTirade(animations.getAnimationByName("09").start(PlayMode.LOOP), "dialog.intro.09");
        dialog.addTirade(animations.getAnimationByName("10").start(PlayMode.LOOP), "dialog.intro.10");
        /*Entity intro = game.createEntity();
         intro.addComponent(dialog);
         intro.addComponent(new ZOrder(ZOrders.DIALOG));
         intro.addComponent(new TriggerableWhenRemoved(afterIntroTrigger).add(new Trigger() {

         @Override
         public void process(Game game) {

         assets.forceAnimationUnload("intro.nanim.gz");
         }
         }));
         game.addEntity(intro);
         game.addEntity(game.createEntity().addComponent(new Triggerable(new ShowCutScenesTrigger(intro))));*/
    }

    /* class ShowCutScenesTrigger extends Trigger {

     private final Entity cutscene;

     private ShowCutScenesTrigger(Entity cutscene) {
     this.cutscene = cutscene;
     }

     @Override
     public void process(Game game) {
     mainMenu.getComponent(MainMenu.class).show();
     mainMenu.disable();
     ingameControls.disable();
     cutscene.enable();
     cutscene.getComponent(DialogComponent.class).rewind();
     }

     }*/
}
