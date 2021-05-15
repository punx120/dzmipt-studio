package studio.ui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClosableTabbedPane extends JTabbedPane {
    public interface CloseTabAction {
        boolean close(int index);
    }

    private final CloseTabAction closeTabAction;

    public ClosableTabbedPane(CloseTabAction closeTabAction) {
        this.closeTabAction = closeTabAction;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int tabIndex = indexAtLocation(e.getX(), e.getY());
                if (tabIndex == -1) return;

                if (e.isPopupTrigger()) {
                    JPopupMenu popup = createTabbedPopupMenu(tabIndex);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    closeTabAction.close(tabIndex);
                }
            }
        });

    }

    private JPopupMenu createTabbedPopupMenu(int index) {
        UserAction closeAction = UserAction.create("Close", "Close current tab",
                0, e-> closeTabAction.close(index) );

        UserAction closeOthersAction = UserAction.create("Close others tab", "Close others tab",
                0, e -> {
                    for (int count = index; count>0; count--) {
                        if (! closeTabAction.close(0)) return;
                    }
                    while (getTabCount() > 1) {
                        if (! closeTabAction.close(1)) return;
                    }
                });

        UserAction closeRightsAction = UserAction.create("Close tabs to the right", "Close tabs to the right",
                0, e -> {
                    while (getTabCount() > index+1) {
                        if (! closeTabAction.close(index+1)) return;
                    }
                });

        closeOthersAction.setEnabled(getTabCount()>1);
        closeRightsAction.setEnabled(getTabCount()>index+1);

        JPopupMenu popup = new JPopupMenu();
        popup.add(closeAction);
        popup.add(closeOthersAction);
        popup.add(closeRightsAction);
        return popup;
    }

}
