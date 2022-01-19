package billboards.shared;

import javax.swing.*;
import java.awt.*;

public class GUITools {

    /**
     * This is a method for creating panels. From Practical 6
     * @param color the background color of the panel
     * @return The JPanel
     */
    public static JPanel CreatePanel(Color color){
        JPanel panel = new JPanel();
        panel.setBackground(color);
        return panel;
    }

    /**
     * This method creates a dropdown box component. From Practical 6
     * @param Values the values that are inside the drop down box
     * @param InitialValue The default value
     * @return The JCombobox box component
     */
    public static JComboBox<String> CreateCombobox(String[] Values, int InitialValue){
        JComboBox<String> combobox = new JComboBox<>(Values);
        combobox.setSelectedIndex(InitialValue);
        return combobox;
    }

    /**
     * This method creates a label component. From Practical 6
     * @param text The Labels texts
     * @return The Jlabel component
     */
    public static JLabel CreateLabel(String text){
        JLabel label = new JLabel();
        label.setText(text);
        return label;
    }

    /**
     * This Method is used to to find what the index number of a value is in a JComboBox
     * @param Array The variables array of the JComboBox
     * @param Input The value that you are trying to find
     * @return An Int of the index value
     */
    public static int FindIndexNumber(String[] Array, String Input){
        //Check each value of the JComboBox array for the input value. If found return the current loop number
        for(int i = 0; i < Array.length; i++){
            if(Input.equals(Array[i])){
                return i;
            }
        }
        return 10;
    }

    /**
     * Checks that all of the values in the string are not empty
     * @param s The String array that is being checked
     * @return Boolean Value
     */
    public static boolean EmptyValue(String[] s){
        boolean StringEmpty = false;
        for(int i = 0; i < s.length-1; i++){
            StringEmpty = s[i].isEmpty();
            if(StringEmpty){
                return StringEmpty;
            }
        }
        return StringEmpty;
    }

}
