package com.moneydance.modules.features.mundane.utils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Supplier;

/**
 * Manages a {@link javax.swing.JFrame}, ensuring that it is created when needed, not recreated if asked to be displayed and already
 * exists and properly hidden and dispose when asked to.
 */
public class FrameSingleton {

    private final Supplier<JFrame> supplier;
    private JFrame frame = null;

    /**
     * @param supplier a function creating the frame.
     */
    public FrameSingleton(Supplier<JFrame> supplier) {
        this.supplier = supplier;
    }

    /**
     * Ensure the frame exists and show it.
     */
    synchronized public void show() {
        if (frame == null) {
            frame = supplier.get();
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    frame = null;
                }
            });
            frame.setVisible(true);
        } else {
            frame.setVisible(true);
            frame.toFront();
            frame.requestFocus();
        }
    }

    /**
     * If the frame is already open, close and dispose it.
     */
    synchronized public void close() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        }
    }
}
