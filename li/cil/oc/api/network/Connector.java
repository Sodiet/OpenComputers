package li.cil.oc.api.network;

/**
 * Interface for nodes that act as power connectors between their network and
 * some power producer or consumer.
 * <p/>
 * For each connector a buffer is managed. Its size is initialized via the
 * factory function in the network API, but can also be configured later on.
 * Its current fill level can be queried and manipulated as desired.
 * <p/>
 * Each connector can take two roles: it can be a <em>producer</em>, feeding
 * power into the network, or it can be a <em>consumer</em>, requiring power
 * from the network to power something (or it can be both). This depends
 * entirely on how you call {@link #changeBuffer}, i.e. on whether you
 * fill up the connectors buffer or drain it.
 * <p/>
 * To feed power into the network, simply fill up the buffer, to consume power
 * take power from the buffer. The network will balance the power between all
 * buffers connected to it. The algorithm goes as follows: if there was a change
 * to some buffer, computer the average power available in all buffers. Build
 * two sets: those of buffers with above-average level, and those with below-
 * average fill. From all above-average buffers take so much energy that they
 * remain just above average fill (but only take integral values - this is to
 * avoid floating point errors causing trouble). Distribute the collected energy
 * equally among the below-average buffers (as good as possible).
 */
public interface Connector extends Node {
    /**
     * The energy stored in the local buffer.
     */
    double localBuffer();

    /**
     * The size of the local buffer.
     */
    double localBufferSize();

    /**
     * The accumulative energy stored across all buffers in the node's network.
     */
    double globalBuffer();

    /**
     * The accumulative size of all buffers in the node's network.
     */
    double globalBufferSize();

    /**
     * Try to apply the specified delta to the <em>global</em> buffer.
     * <p/>
     * This can be used to apply reactionary power changes. For example, a
     * screen may require a certain amount of energy to refresh its display when
     * a program tries to display text on it. For running costs just apply the
     * same delta each tick.
     * <p/>
     * For negative values, if there is not enough energy stored in the buffer
     * this will return <tt>false</tt>, and the operation depending on that
     * energy should fail - what energy there is will still be consumed, though!
     * <p/>
     * For positive values, if there is a buffer overflow due to the added
     * energy the surplus will be lost and this will return <tt>false</tt>.
     * <p/>
     * If there is enough energy or no overflow this will return <tt>true</tt>.
     * <p/>
     * Keep in mind that this change is applied to the <em>global</em> buffer,
     * i.e. energy from multiple buffers may be consumed / multiple buffers may
     * be filled. The buffer for which this method is called (i.e. this node
     * instance) will be prioritized, though.
     *
     * @param delta the amount of energy to consume or make available.
     * @return whether the energy could be consumed or stored.
     */
    boolean changeBuffer(double delta);
}