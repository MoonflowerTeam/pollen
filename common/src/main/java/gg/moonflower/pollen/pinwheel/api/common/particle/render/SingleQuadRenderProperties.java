package gg.moonflower.pollen.pinwheel.api.common.particle.render;

import com.mojang.math.Quaternion;
import gg.moonflower.pollen.pinwheel.api.common.particle.Flipbook;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;

/**
 * Specifies render properties for a single quad.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class SingleQuadRenderProperties extends ColoredRenderProperties {

    private Quaternion quaternion = new Quaternion(Quaternion.ONE);
    private float width;
    private float height;
    private float uMin;
    private float vMin;
    private float uMax;
    private float vMax;
    private int lastFrame = -1;

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getUMin() {
        return uMin;
    }

    public float getVMin() {
        return vMin;
    }

    public float getUMax() {
        return uMax;
    }

    public float getVMax() {
        return vMax;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setUV(float uMin, float vMin, float uMax, float vMax) {
        this.uMin = uMin;
        this.vMin = vMin;
        this.uMax = uMax;
        this.vMax = vMax;
        this.lastFrame = -1;
    }

    /**
     * Sets the UV coordinates based on the provided flipbook.
     *
     * @param environment   The environment to resolve the flipbook in
     * @param textureWidth  The width of the texture
     * @param textureHeight The height of the texture
     * @param flipbook      The flipbook to use
     * @param time          The time in seconds since the beginning of the animation
     * @param maxLife       The maximum life of the particle
     */
    public void setUV(MolangEnvironment environment, int textureWidth, int textureHeight, Flipbook flipbook, float time, float maxLife) {
        int maxFrame = (int) flipbook.maxFrame().safeResolve(environment);
        int frame;
        if (flipbook.stretchToLifetime()) {
            frame = Math.min((int) (time / maxLife * (maxFrame + 1)), maxFrame);
        } else {
            frame = (int) (time * flipbook.fps());
            if (flipbook.loop()) {
                frame %= maxFrame;
            } else {
                frame = Math.min(frame, maxFrame);
            }
        }

        if (this.lastFrame == frame) // Only update uvs if the frame has changed
            return;

        float u = flipbook.baseU().safeResolve(environment);
        float v = flipbook.baseV().safeResolve(environment);
        float uSize = flipbook.sizeU();
        float vSize = flipbook.sizeV();
        float uo = flipbook.stepU() * frame;
        float vo = flipbook.stepV() * frame;

        this.uMin = (u + uo) / (float) textureWidth;
        this.vMin = (v + vo) / (float) textureHeight;
        this.uMax = (u + uo + uSize) / (float) textureWidth;
        this.vMax = (v + vo + vSize) / (float) textureHeight;
    }

    @Override
    public Quaternion getRotation() {
        return quaternion;
    }

    @Override
    public void setRotation(Quaternion rotation) {
        this.quaternion = rotation;
    }

    @Override
    public boolean canRender() {
        return this.width * this.height > 0;
    }
}
