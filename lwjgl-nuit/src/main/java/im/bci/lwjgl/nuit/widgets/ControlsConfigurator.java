/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.lwjgl.nuit.widgets;

import im.bci.lwjgl.nuit.NuitToolkit;
import im.bci.lwjgl.nuit.controls.Action;
import im.bci.lwjgl.nuit.controls.Control;
import im.bci.lwjgl.nuit.controls.ControlActivatedDetector;
import im.bci.lwjgl.nuit.controls.GamepadAxisControl;
import im.bci.lwjgl.nuit.controls.GamepadButtonControl;
import im.bci.lwjgl.nuit.controls.KeyControl;
import im.bci.lwjgl.nuit.controls.MouseButtonControl;
import im.bci.lwjgl.nuit.utils.TrueTypeFont;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ControlsConfigurator extends Table {

    private List<ControlActivatedDetector> possibleControls;
    private List<Action> actions;
    private NuitToolkit toolkit;
    private List<Action> resets;
    private List<Action> defaults;

    public ControlsConfigurator(NuitToolkit toolkit, List<Action> actions, List<Action> defaults) {
        super(toolkit);
        this.toolkit = toolkit;
        this.actions = actions;
        this.defaults = defaults;
        initResets();
        initPossibleControls();
        initUI(toolkit);
    }

	private void initResets() {
		this.resets = new ArrayList<>();
        for (Action action : actions) {
            resets.add(new Action(action));
        }
	}

    private void initUI(NuitToolkit toolkit) {
        this.defaults().expand();
        this.cell(new Label(toolkit, "Action"));
        this.cell(new Label(toolkit, "Control"));
        this.cell(new Label(toolkit, "Alternative"));
        this.row();
        for (final Action action : actions) {
            this.cell(new Label(toolkit, action.getName()));
            ControlConfigurator mainConfigurator = new ControlConfigurator() {
                @Override
                public Control getControl() {
                    return action.getMainControl();
                }

                @Override
                public void setControl(Control control) {
                    action.setMainControl(control);
                }
            };
            this.cell(mainConfigurator).fill();
            ControlConfigurator alternativeConfigurator = new ControlConfigurator() {
                @Override
                public Control getControl() {
                    return action.getAlternativeControl();
                }

                @Override
                public void setControl(Control control) {
                    action.setAlternativeControl(control);
                }
            };
            this.cell(alternativeConfigurator).fill();
            this.row();
        }

        this.cell(new Button(toolkit, "Back") {
            @Override
            public void onOK() {
                onBack();
            }
        });
        this.cell(new Button(toolkit, "Reset") {
            @Override
            public void onOK() {
                onReset();
            }
        });
        this.cell(new Button(toolkit, "Defaults") {
            @Override
            public void onOK() {
                onDefaults();
            }
        });
    }

    public abstract class ControlConfigurator extends Widget {

        private boolean suckFocus;

        public abstract Control getControl();

        public abstract void setControl(Control control);

        @Override
        public boolean isFocusWhore() {
            return true;
        }

        @Override
        public void suckFocus() {
            for (ControlActivatedDetector control : possibleControls) {
                control.reset();
            }
            suckFocus = true;
        }
        
        @Override
        public void onMouseClick(float mouseX, float mouseY) {
        	suckFocus();
        }

        @Override
        public boolean isSuckingFocus() {
            return suckFocus;
        }

        @Override
        public void update() {
            if (isSuckingFocus()) {
                for (ControlActivatedDetector control : possibleControls) {
                    control.poll();
                    if (control.isActivated()) {
                        suckFocus = false;
                        setControl(control.getControl());
                        toolkit.resetInputPoll();
                    }
                }
            }
        }

        @Override
        public void draw() {
            String text = null;
            if (suckFocus) {
                text = "Press a key...";
            } else if (null != getControl()) {
                text = getControl().getName();
            }
            if (null != text) {
                GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glPushMatrix();
                TrueTypeFont font = toolkit.getFont();
                GL11.glTranslatef(getX() + getWidth() / 2.0f - font.getWidth(text) / 4.0f, getY() + getHeight() / 2.0f + font.getHeight(text) / 2.0f, 0.0f);
                GL11.glScalef(1, -1, 1);
                font.drawString(text);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }

    protected void onDefaults() {
        if (null != defaults) {
            for (int i = 0; i < defaults.size(); ++i) {
                actions.get(i).setMainControl(defaults.get(i).getMainControl());
                actions.get(i).setAlternativeControl(defaults.get(i).getAlternativeControl());
            }
        }
    }

    protected void onReset() {
        for (int i = 0; i < resets.size(); ++i) {
            actions.get(i).setMainControl(resets.get(i).getMainControl());
            actions.get(i).setAlternativeControl(resets.get(i).getAlternativeControl());
        }
    }

    public void onBack() {
    }
    
    @Override
    public void onShow() {
    	initResets();
    }

    private void initPossibleControls() {
        possibleControls = new ArrayList<>();
        for (int c = 0; c < Controllers.getControllerCount(); ++c) {
            Controller pad = Controllers.getController(c);
            for (int a = 0; a < pad.getAxisCount(); ++a) {
                possibleControls.add(new ControlActivatedDetector(new GamepadAxisControl(pad, a)));
            }
            for (int b = 0; b < pad.getButtonCount(); ++b) {
                possibleControls.add(new ControlActivatedDetector(new GamepadButtonControl(pad, b)));
            }
        }
        for (Field field : Keyboard.class.getFields()) {
            String name = field.getName();
            if (name.startsWith("KEY_")) {
                try {
                    int key = field.getInt(null);
                    possibleControls.add(new ControlActivatedDetector(new KeyControl(key)));
                } catch (Exception e) {
                    Logger.getLogger(ControlsConfigurator.class.getName()).log(Level.SEVERE, "error retrieving key", e);
                }
            }
        }
        for (int m = 0; m < Mouse.getButtonCount(); ++m) {
            possibleControls.add(new ControlActivatedDetector(new MouseButtonControl(m)));
        }
    }

}