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
package im.bci.nanim;

import im.bci.nanim.NanimParser.Frame;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author devnewton
 */
public class Nanimation implements IAnimation {

    public class Play implements IPlay {

        public int currentFrameIndex;
        public long currentTime;
        public PlayMode mode = PlayMode.LOOP;
        private State state = State.STOPPED;

        @Override
        public String getName() {
            return Nanimation.this.name;
        }

        @Override
        public NanimationFrame getCurrentFrame() {
            return frames.get(currentFrameIndex);
        }

        @Override
        public void start(PlayMode mode) {
            this.state = State.STARTED;
            this.mode = mode;
            this.currentTime = 0;
            this.currentFrameIndex = 0;
        }

        @Override
        public void stop() {
            state = State.STOPPED;
            currentTime = 0;
            currentFrameIndex = 0;
        }

        @Override
        public boolean isStopped() {
            return state == State.STOPPED;
        }

        @Override
        public void update(long elapsedTime) {

            if (state == State.STOPPED) {
                return;
            }

            this.currentTime += elapsedTime;
            if (currentTime >= totalDuration) {

                switch (mode) {
                    case ONCE:
                        currentFrameIndex = frames.size() - 1;
                        state = State.STOPPED;
                        return;
                    case LOOP:
                        currentTime %= totalDuration;
                        currentFrameIndex = 0;
                        break;
                }
            }

            while (currentTime > frames.get(currentFrameIndex).endTime) {
                ++this.currentFrameIndex;
            }
        }
    }

    enum State {

        STARTED, STOPPED
    }
    private ArrayList<NanimationFrame> frames = new ArrayList<>();
    private long totalDuration;// milliseconds
    private final String name;

    public Nanimation(im.bci.nanim.NanimParser.Animation nanimation,
            Map<String, NanimationImage> images) {
        name = nanimation.getName();
        frames.ensureCapacity(nanimation.getFramesCount());
        for (Frame nframe : nanimation.getFramesList()) {
            NanimationFrame frame = addFrame(
                    images.get(nframe.getImageName()), nframe.getDuration());
            frame.u1 = nframe.getU1();
            frame.v1 = nframe.getV1();
            frame.u2 = nframe.getU2();
            frame.v2 = nframe.getV2();
        }
    }

    private NanimationFrame addFrame(NanimationImage image, long duration) {
        final NanimationFrame frame = new NanimationFrame(image, duration);
        frames.add(frame);
        totalDuration += duration;
        frame.endTime = totalDuration;
        return frame;
    }

    @Override
    public Play start(PlayMode mode) {
        if (!frames.isEmpty()) {
            Play play = new Play();
            play.start(mode);
            return play;
        } else {
            return null;
        }
    }

    /**
     * Call play.stop
     *
     * @param play
     */
    @Override
    public void stop(IPlay play) {
        if (null != play) {
            play.stop();
        }
    }

    @Override
    public String getName() {
        return name;
    }
}