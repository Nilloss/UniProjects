package billboards.controlPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
/**
 * This object class is used in the schedule table and is used to highlight and identify the billboard that is currently displayed on viewers
 * @author  Jack Nielsen
 */
class TableHighlighter extends DefaultTableCellRenderer
{

    int index = 0;

    public TableHighlighter(int index){
        this.index = index;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(row == index){
            c.setBackground(new java.awt.Color(67, 255, 54));
        }
        else{
            c.setBackground(Color.WHITE);
        }
        return c;
    }
}