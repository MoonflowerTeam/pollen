package gg.moonflower.pollen.api.util;

import net.minecraft.util.Mth;

/**
 * Handles smooth scrolling automatically.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class ScrollHandler {

    public static final float DEFAULT_SCROLL_SPEED = 5;
    public static final float DEFAULT_TRANSITION_SPEED = 0.5f;
    public static final double DEFAULT_MIN_SNAP = 0.1f;

    private int height;
    private int visibleHeight;

    private double scroll;
    private double lastScroll;
    private double nextScroll;
    private float scrollSpeed;
    private float transitionSpeed;
    private double minSnap;

    public ScrollHandler(int height, int visibleHeight) {
        this.height = height;
        this.visibleHeight = visibleHeight;

        this.scroll = 0;
        this.scrollSpeed = DEFAULT_SCROLL_SPEED;
        this.transitionSpeed = DEFAULT_TRANSITION_SPEED;
        this.minSnap = DEFAULT_MIN_SNAP;
    }

    /**
     * Updates the smooth transition of scrolling.
     */
    public void update() {
        this.lastScroll = this.scroll;
        if (this.getMaxScroll() > 0) {
            double delta = this.nextScroll - this.scroll;
            if (Math.abs(delta) < this.minSnap) {
                this.scroll = this.nextScroll;
            } else {
                this.scroll += delta * this.transitionSpeed;
            }

            if (this.scroll < 0) {
                this.scroll = 0;
                this.nextScroll = 0;
            }

            if (this.scroll >= this.getMaxScroll()) {
                this.scroll = this.getMaxScroll();
                this.nextScroll = this.getMaxScroll();
            }
        }
    }

    /**
     * Handles the mouse scrolling event.
     *
     * @param amount The amount the mouse was scrolled
     */
    public boolean mouseScrolled(double maxScroll, double amount) {
        if (this.getMaxScroll() > 0) {
            float scrollAmount = (float) Math.min(Math.abs(amount), maxScroll) * this.getScrollSpeed();
            float finalScroll = (amount < 0 ? -1 : 1) * scrollAmount;
            double scroll = Mth.clamp(this.getScroll() - finalScroll, 0, this.getMaxScroll());
            if (this.getScroll() != scroll) {
                this.scroll(finalScroll);
                return true;
            }
        }
        return false;
    }

    /**
     * Scrolls the specified amount over time.
     *
     * @param scrollAmount The amount to scroll
     */
    public ScrollHandler scroll(double scrollAmount) {
        this.nextScroll -= scrollAmount;
        return this;
    }

    /**
     * @return The height of the scrolling area
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the scrolling area.
     *
     * @param height The total height of the scroll area
     */
    public ScrollHandler setHeight(int height) {
        this.height = height;
        this.setScroll(this.scroll);
        return this;
    }

    /**
     * @return The height visible at one time
     */
    public int getVisibleHeight() {
        return visibleHeight;
    }

    /**
     * Sets the height visible at one time.
     *
     * @param visibleHeight The maximum height that can be displayed at one moment
     */
    public ScrollHandler setVisibleHeight(int visibleHeight) {
        this.visibleHeight = visibleHeight;
        this.setScroll(this.scroll);
        return this;
    }

    /**
     * @return The position of the scroll bar
     */
    public double getScroll() {
        return scroll;
    }

    /**
     * Sets the position of the scroll bar.
     *
     * @param scroll The new scroll value
     */
    public ScrollHandler setScroll(double scroll) {
        this.scroll = Mth.clamp(scroll, 0, this.height - this.visibleHeight);
        this.nextScroll = this.scroll;
        this.lastScroll = this.scroll;
        return this;
    }

    /**
     * @return The maximum value the scroll can be
     */
    public int getMaxScroll() {
        return Math.max(0, this.height - this.visibleHeight);
    }

    /**
     * Calculates the position of the scroll bar based on where it was last tick and now.
     *
     * @param partialTicks The percentage from last tick to this tick
     * @return The position of the scroll bar interpolated over the specified value
     */
    public float getInterpolatedScroll(float partialTicks) {
        return (float) Mth.lerp(partialTicks, this.lastScroll, this.scroll);
    }

    /**
     * @return The speed at which scrolling takes place
     */
    public float getScrollSpeed() {
        return scrollSpeed;
    }

    /**
     * Sets the speed at which scrolling occurs.
     *
     * @param scrollSpeed The new scrolling speed
     */
    public ScrollHandler setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = Math.max(scrollSpeed, 0);
        return this;
    }

    /**
     * @return The scrolling value last tick
     */
    public double getLastScroll() {
        return lastScroll;
    }

    /**
     * @return The scroll value being animated to
     */
    public double getNextScroll() {
        return nextScroll;
    }

    /**
     * Sets the speed at which transitions happen.
     *
     * @param transitionSpeed The new speed of transitions
     */
    public ScrollHandler setTransitionSpeed(float transitionSpeed) {
        this.transitionSpeed = transitionSpeed;
        return this;
    }

    /**
     * Sets the maximum amount scroll velocity needs to be to snap down to zero.
     *
     * @param minSnap The new snapping value
     */
    public ScrollHandler setMinSnap(double minSnap) {
        this.minSnap = minSnap;
        return this;
    }
}
