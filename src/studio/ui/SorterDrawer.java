package studio.ui;

import javax.swing.*;
import java.awt.*;

public class SorterDrawer {

    private static Icon ASC_ICON = Util.ASC_ICON;
    private static Icon DESC_ICON = Util.DESC_ICON;

    public static void paint(boolean asc, boolean desc, Component component, Graphics g) {
        if (asc || desc) {
            Dimension size = component.getSize();
            int x = (size.width - ASC_ICON.getIconWidth()) / 2;
            Icon icon = asc ? ASC_ICON : DESC_ICON;
            icon.paintIcon(component, g, x, 2);
        }

    }
}
