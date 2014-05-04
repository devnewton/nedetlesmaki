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
package org.geekygoblin.nedetlesmaki.playn.core;

import org.geekygoblin.nedetlesmaki.core.IAssets;
import im.bci.jnuit.animation.IAnimationCollection;
import im.bci.jnuit.playn.animation.PlaynAnimationLoader;
import java.util.HashMap;
import playn.core.PlayN;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynAssets implements IAssets {
    
    private final HashMap<String, IAnimationCollection> animations = new HashMap<String, IAnimationCollection>();

    @Override
    public IAnimationCollection getAnimations(String name) {
        IAnimationCollection collection = animations.get(name);
        if(null == collection) {
            collection = PlaynAnimationLoader.load(PlayN.assets(), name);
            animations.put(name, collection);
        }
        return collection;
    }

    @Override
    public void clearUseless() {
    }

    @Override
    public void setIcon(String icon) {
    }

}
