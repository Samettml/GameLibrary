package org.comu;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * https://tips4java.wordpress.com/2008/11/06/wrap-layout/
 */
public class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right + getHgap() * 2);
            int x = 0, y = getVgap(), rowHeight = 0;

            Dimension dim = new Dimension(0, 0);

            for (Component m : target.getComponents()) {
                if (!m.isVisible()) continue;

                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                if (x + d.width > maxWidth) {
                    x = 0;
                    y += rowHeight + getVgap();
                    rowHeight = 0;
                }

                x += d.width + getHgap();
                rowHeight = Math.max(rowHeight, d.height);
            }

            y += rowHeight;
            dim.width = maxWidth;
            dim.height = y;

            return dim;
        }
    }
}
