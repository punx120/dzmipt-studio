package studio.ui.chart;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ChartConfigPanel extends Box implements ActionListener {

    private Chart chart;
    private List<String> names;
    private List<Integer> xIndex;
    private List<Integer> yIndex;

    private JComboBox<ChartType> comboCharType;
    private JComboBox<String> comboX;
    private JCheckBox chkAll;
    private JCheckBox[] chkY;
    private LegendIcon[] icons;
    private JPanel pnlLagend;

    private JColorChooser colorChooser;
    private LegendIcon colorChoosePreviewIcon;

    private final static Border EMPTY_BORDER = BorderFactory.createEmptyBorder(2,0,2,0);
    private final static Border SELECTED_BORDER = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);


    public ChartConfigPanel(Chart chart, List<String> names, List<Integer> xIndex, List<Integer> yIndex) {
        super(BoxLayout.Y_AXIS);
        this.chart = chart;
        this.names = names;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        int count = yIndex.size();
        Paint[] colors = chart.getColors(count);
        Shape[] shapes = chart.getShapes(count);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        Box boxStyle = Box.createHorizontalBox();
        boxStyle.add(new JLabel("Type: "));

        comboCharType = new JComboBox<>(ChartType.values());
        comboCharType.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboCharType.getPreferredSize().height));
        comboCharType.addActionListener(this::charTypeSelected);
        ChartType chartType = (ChartType) comboCharType.getSelectedItem();

        boxStyle.add(comboCharType);
        boxStyle.add(Box.createHorizontalGlue());
        add(boxStyle);

        Box boxDomain = Box.createHorizontalBox();
        boxDomain.add(new JLabel("Domain axis: "));

        String[] xItems = xIndex.stream().map(names::get).toArray(String[]::new);
        comboX = new JComboBox<>(xItems);
        comboX.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboX.getPreferredSize().height));
        comboX.addActionListener(this);
        boxDomain.add(comboX);
        boxDomain.add(Box.createHorizontalGlue());
        add(boxDomain);

        Box boxSeries = Box.createHorizontalBox();
        boxSeries.add(new JLabel("Series:"));
        boxSeries.add(Box.createHorizontalGlue());

        chkAll = new JCheckBox("All", true);
        chkAll.addActionListener(this::allSeriesClicked);
        boxSeries.add(chkAll);

        add(boxSeries);

        pnlLagend = new JPanel();
        GroupLayout layout = new GroupLayout(pnlLagend);
        pnlLagend.setLayout(layout);

        GroupLayout.ParallelGroup chkGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.ParallelGroup iconGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup rowsGroup = layout.createSequentialGroup();

        chkY = new JCheckBox[count];
        icons = new LegendIcon[count];
        JComponent[] iconPanels = new JComponent[count];
        for (int i=0; i<count; i++) {
            chkY[i] = new JCheckBox(names.get(yIndex.get(i)), true);
            chkY[i].addActionListener(this);

            icons[i] = new LegendIcon(colors[i], shapes[i]);
            icons[i].setChartType(chartType);
            iconPanels[i] = new JLabel(icons[i]);
            iconPanels[i].setBorder(EMPTY_BORDER);
            final int theIndex = i;
            iconPanels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    legendPressed(theIndex, e);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (chkY[theIndex].isEnabled()) {
                        iconPanels[theIndex].setBorder(SELECTED_BORDER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    iconPanels[theIndex].setBorder(EMPTY_BORDER);
                }
            });

            chkGroup.addComponent(chkY[i]);
            iconGroup.addComponent(iconPanels[i]);

            rowsGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(chkY[i])
                                        .addComponent(iconPanels[i]) );
        }

        Box glue = Box.createVerticalBox();

        chkGroup.addComponent(glue);
        rowsGroup.addComponent(glue);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                                        .addGroup(chkGroup)
                                        .addGroup(iconGroup)
                                  );

        layout.setVerticalGroup(rowsGroup);

        add(new JScrollPane(pnlLagend));
        validateState();

        colorChooser = new JColorChooser();

        colorChoosePreviewIcon = new LegendIcon(null, null);
        colorChooser.getSelectionModel().addChangeListener(e-> colorChoosePreviewIcon.setColor(colorChooser.getColor()));

        colorChooser.setPreviewPanel(new JLabel(colorChoosePreviewIcon, SwingConstants.CENTER));
    }

    public int getDomainIndex() {
        return xIndex.get(comboX.getSelectedIndex());
    }

    public boolean isSeriesEnables(int index) {
        return chkY[index].isSelected() && chkY[index].isEnabled();
    }

    public Paint getColor(int index) {
        return icons[index].getColor();
    }

    public Shape getShape(int index) {
        return icons[index].getShape();
    }

    public ChartType getChartType(int index) {
        return icons[index].getChartType();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pnlLagend.repaint();
        validateState();
        chart.createPlot();
    }

    private void validateState() {
        for (JCheckBox checkBox: chkY) {
            checkBox.setEnabled(true);
        }
        int x = xIndex.get(comboX.getSelectedIndex());
        int index = yIndex.indexOf(x);
        if (index > -1) {
            chkY[index].setEnabled(false);
        }
    }

    private void allSeriesClicked(ActionEvent e) {
        for (JCheckBox checkBox: chkY) {
            checkBox.setSelected(chkAll.isSelected());
        }
        actionPerformed(e);
    }

    private void charTypeSelected(ActionEvent e) {
        ChartType chartType = (ChartType) comboCharType.getSelectedItem();
        for (int i=0; i<icons.length; i++) {
            icons[i].setChartType(chartType);
        }
        actionPerformed(e);
    }

    private void legendPressed(int index, MouseEvent e) {
        Paint theColor = icons[index].getColor();
        Shape theShape = icons[index].getShape();
        ChartType theChartType = icons[index].getChartType();

        JPopupMenu popup = new JPopupMenu();

        JMenuItem menu = new JMenuItem("Change color", new SquareIcon(theColor, 15));
        menu.addActionListener(actionEvent -> showChangeColor(index, actionEvent));
        popup.add(menu);

        JMenu subMenu = new JMenu("Change type");
        for (ChartType chartType : ChartType.values()) {
            if (theChartType == chartType) continue;

            LegendIcon icon = new LegendIcon(theColor, theShape);
            icon.setChartType(chartType);

            JMenuItem item = new JMenuItem(chartType.toString(), icon);
            item.addActionListener(actionEvent -> changeChartType(index, chartType, actionEvent));
            subMenu.add(item);
        }
        popup.add(subMenu);

        if (icons[index].getChartType().hasShape()) {
            subMenu = new JMenu("Change shape");
            for (Shape shape: chart.getAllShapes()) {
                if (theShape == shape) continue;

                LegendIcon icon = new LegendIcon(theColor, shape);
                icon.setChartType(theChartType);

                JMenuItem item = new JMenuItem(icon);
                item.addActionListener(actionEvent -> changeShape(index, shape, actionEvent));
                subMenu.add(item);
            }
            popup.add(subMenu);

            menu = new JMenuItem("Set this shape to all");
            menu.addActionListener(actionEvent -> setShapeToAll(index, actionEvent));
            popup.add(menu);
        }

        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showChangeColor(int index, ActionEvent e) {
        Paint paint = icons[index].getColor();
        Color color = Color.BLACK;
        if (paint instanceof Color) {
            color = (Color)paint;
        }
        colorChooser.setColor(color);
        colorChoosePreviewIcon.setColor(color);
        colorChoosePreviewIcon.setShape(icons[index].getShape());
        colorChoosePreviewIcon.setChartType(icons[index].getChartType());
        JDialog dialog = JColorChooser.createDialog(pnlLagend, "Choose color", true, colorChooser, actionEvent -> changeColor(index, actionEvent), null);
        dialog.setVisible(true);
    }

    private void changeColor(int index, ActionEvent e) {
        icons[index].setColor(colorChooser.getColor());
        actionPerformed(e);
    }

    private void changeChartType(int index, ChartType chartType, ActionEvent e) {
        icons[index].setChartType(chartType);
        actionPerformed(e);
    }

    private void changeShape(int index, Shape shape, ActionEvent e) {
        icons[index].setShape(shape);
        actionPerformed(e);
    }

    private void setShapeToAll(int index, ActionEvent e) {
        Shape shape = icons[index].getShape();
        for (LegendIcon icon: icons) {
            icon.setShape(shape);
        }
        actionPerformed(e);
    }

}
