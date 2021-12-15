package billboards.controlPanel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TableButton {
    /**
     * Creates a Table Cell Renderer for a JButton
     */
    static class Renderer extends JButton implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    /**
     * Creates a Table Cell Editor for a JButton
     */
    static class Editor extends DefaultCellEditor {

        private JButton button; // the button for the table
        public int rowNumber;

        /**
         * Instantiates an instance of the class with a JButton. The JButton is assigned the action listener specified
         * and adds another action listener to help handle it being within the table.
         * @param checkBox just a 'new JCheckBox'
         * @param ac an action listener to be added to the button
         */
        public Editor(JCheckBox checkBox, ActionListener ac) {
            super(checkBox);
            button = new JButton();
            button.addActionListener(ac);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        /**
         * Makes the button an item in the table.
         * @param table the table instance
         * @param value the button text or table column value
         * @param isSelected not used but required of the parent class
         * @param row not used but required of the parent class
         * @param column not used but required of the parent class
         * @return returns an instance of the button in question
         */
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value.toString());
            rowNumber = row;
            return button;
        }

//        /**
//         * Set table button text
//         * @param s
//         */
//        public void setButtonText(String s){
//            button.setText(s);
//        }

        /**
         * Just returns the button text.
         * @return the buttons text for the table cell
         */
        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}