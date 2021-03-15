package studio.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class QPadImportDialog extends EscapeDialog {

    private JTextField txtServers;
    private JRadioButton btnOverwrite, btnAppend;
    private JTextField txtRootName;

    private JFileChooser chooser;

    public enum Location {Overwrite, Append}

    public QPadImportDialog(JFrame parent) {
        super(parent, "QPad Server List Import");
        initComponents();
    }

    public String getServersCfgLocation() {
        return txtServers.getText();
    }

    public Location getImportTo() {
        if (btnOverwrite.isSelected()) return Location.Overwrite;
        if (btnAppend.isSelected()) return Location.Append;

        return null;
    }

    public String getRootName() {
        return txtRootName.getText();
    }

    private void initComponents() {
        JLabel lblServers = new JLabel("Select location of Servers.cfg:     ");
        txtServers = new JTextField();
        JButton btnServers = new JButton("...");

        JLabel lblLocation = new JLabel("Location where servers will be imported in the current Server Tree:     ");
        btnOverwrite = new JRadioButton("Overwrite");
        btnAppend = new JRadioButton("Append");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(btnOverwrite);
        buttonGroup.add(btnAppend);

        JLabel lblRootName = new JLabel("Root name of importer server or leave it empty:     ");
        txtRootName = new JTextField();

        JPanel main = new JPanel();
        GroupLayout layout = new GroupLayout(main);
        main.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addGroup(
                            layout.createSequentialGroup()
                                .addComponent(lblServers)
                                .addComponent(txtServers)
                                .addComponent(btnServers)
                    ).addGroup(
                            layout.createSequentialGroup()
                                .addComponent(lblLocation)
                                .addComponent(btnOverwrite)
                                .addComponent(btnAppend)
                    ).addGroup(
                            layout.createSequentialGroup()
                                .addComponent(lblRootName)
                                .addComponent(txtRootName)
                    )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblServers)
                                .addComponent(txtServers)
                                .addComponent(btnServers)
                    ).addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblLocation)
                                .addComponent(btnOverwrite)
                                .addComponent(btnAppend)
                    ).addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblRootName)
                                .addComponent(txtRootName)
                    )
        );

        JButton btnImport = new JButton("Import");
        JButton btnCancel = new JButton("Cancel");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnImport);
        bottom.add(btnCancel);

        JPanel root = new JPanel(new BorderLayout());
        root.add(main, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);

        chooser = new JFileChooser();
        FileFilter serversCfgFF = new ServersCfgFileFilter();
        chooser.setFileHidingEnabled(true);
        chooser.addChoosableFileFilter(serversCfgFF);
        chooser.setFileFilter(serversCfgFF);
        btnImport.addActionListener(e->checkAndAccept());
        btnCancel.addActionListener(e->cancel());
        btnServers.addActionListener(e->selectServersCfg());
    }

    private void selectServersCfg() {
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(this, "Error while selecting Servers.cfg",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        String location = chooser.getSelectedFile().getAbsoluteFile().toString();
        txtServers.setText(location);
    }

    private void checkAndAccept() {
        String location = getServersCfgLocation();
        if (location.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "File to import is not selected",
                    "Not Found Servers.cfg", JOptionPane.ERROR_MESSAGE);
            return;

        }

        if (! new File(location).exists()) {
            JOptionPane.showMessageDialog(this, "File to import is not exist (" + location + ")",
                                "Not Found Servers.cfg", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (getImportTo() == null) {
            JOptionPane.showMessageDialog(this, "Select how current Server Tree should be modified: Overwrite or Append",
                    "Where to Import", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (getRootName().contains("/")) {
            JOptionPane.showMessageDialog(this, "Folder name can't contain /",
                    "Wrong Folder Name", JOptionPane.ERROR_MESSAGE);
            return;
        }

        accept();
    }

    private static class ServersCfgFileFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
            return f.getName().equals("Servers.cfg");
        }

        @Override
        public String getDescription() {
            return "Servers.cfg";
        }
    }

    public static void main(String[] args) {
        QPadImportDialog d = new QPadImportDialog(null);

        d.pack();
        d.setVisible(true);
    }
}
