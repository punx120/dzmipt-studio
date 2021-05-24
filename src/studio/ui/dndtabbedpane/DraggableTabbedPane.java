package studio.ui.dndtabbedpane;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.util.Objects;

public class DraggableTabbedPane extends JTabbedPane {

    private final String dragID;

    public DraggableTabbedPane(String dragID) {
        super();
        this.dragID = dragID;
    }

    public DraggableTabbedPane(String dragID, int tabPlacement) {
        super(tabPlacement);
        this.dragID = dragID;
    }

    public DraggableTabbedPane(String dragID, int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        this.dragID = dragID;
    }

    private void initDnD() {
        DragGestureRecognizer dgr = DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                this,
                //@TODO: validate the constant
                DnDConstants.ACTION_COPY_OR_MOVE,
                new DragGestureHandler(this));


        DropTarget dt = new DropTarget(
                this,
                //@TODO: v1alidate the constant
                DnDConstants.ACTION_COPY_OR_MOVE,
                new DropTargetHandler(this),
                true);

    }

    String getDragID() {
        return dragID;
    }

    private static final Color targetColor = new Color(36, 4, 94);
    private static final Color sourceColor = new Color(255, 255, 255, 154);

    private Rectangle targetRect = null;
    private Rectangle sourceRect = null;

    @Override
    public void setUI(TabbedPaneUI ui) {
        super.setUI(ui);
        initDnD();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (sourceRect != null) {
            g.setColor(sourceColor);
            g.fillRect(sourceRect.x, sourceRect.y, sourceRect.width, sourceRect.height);
        }

        if (targetRect != null) {
            g.setColor(targetColor);
            g.fillRect(targetRect.x, targetRect.y, targetRect.width, targetRect.height);
        }
    }

    public void setSourceRect(Rectangle rect) {
        if (Objects.equals(sourceRect, rect)) return;
        sourceRect = rect;
        repaint();
    }

    public void setTargetRect(Rectangle rect) {
        if (Objects.equals(targetRect, rect)) return;
        targetRect = rect;
        repaint();
    }
}
