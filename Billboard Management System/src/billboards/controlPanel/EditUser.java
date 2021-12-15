package billboards.controlPanel;

import billboards.shared.GUITools;
import billboards.shared.Hash;
import billboards.shared.TCPMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class EditUser {
    private static JFrame frame;
    private static int WIDTH = 1000;
    private static int HEIGHT = 250;
    private static String[] Options = {"True", "False"};
    private static String[] UserInformation = new String[2];
    private static String[] UserPermissions = {"false", "false", "false", "false"};
    private static int[] PermissionsIndex = {1, 1, 1, 1};
    private static Boolean IsEdit = false;
    private static String UserID = null;
    private static String EditorID = null;

    public static boolean UserEditOpen = false;

    public static void EditExistingUser(String[] IN_UserInformation, String id, String[] IN_Permissions, String editorID, String token){
        IsEdit = true;
        UserInformation = IN_UserInformation;
        UserID = id;
        EditorID = editorID;
        UserPermissions = IN_Permissions;
        for(int i = 0; i < 4; i++){
            if(IN_Permissions[i].equals("true")){
                PermissionsIndex[i] = 0;
            }
            else{
                PermissionsIndex[i] = 1;
            }
        }
        UserEditorMain(id,token);
    }


    public static void UserEditorMain(String userId,String token){
        if(!IsEdit) {
            Arrays.fill(UserInformation, null);
            Arrays.fill(PermissionsIndex, 1);
            Arrays.fill(UserPermissions, "false");
            UserID = null;
            EditorID = null;
        }
        frame = new JFrame("Create/Edit User");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLayout(new BorderLayout());
        UserEditOpen = true;
        //Window close warning. used to prevent accidental closing and loss of work from https://tips4java.wordpress.com/2009/05/01/closing-an-application/
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();

                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit the application? All unsaved User data will be lost",
                        "Exit Application",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION)
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    UserEditOpen = false;
                    IsEdit = false;

            }
        });

        //Create the input panel for the element selection menu
        JPanel inputPanel = GUITools.CreatePanel(Color.WHITE);
        //Create a Border to increase aesthetic of the window
        JPanel BorderPanel1 = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel BorderPanel2 = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel BorderPanel3 = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel BorderPanel4 = GUITools.CreatePanel(Color.LIGHT_GRAY);

        //Add the panels to the frame
        frame.getContentPane().add(inputPanel, BorderLayout.CENTER);
        frame.getContentPane().add(BorderPanel1, BorderLayout.SOUTH);
        frame.getContentPane().add(BorderPanel2, BorderLayout.WEST);
        frame.getContentPane().add(BorderPanel3, BorderLayout.EAST);
        frame.getContentPane().add(BorderPanel4, BorderLayout.NORTH);

        //Input setup.
        //Input panel layout. Grid layout so that the Elements are rowed
        inputPanel.setLayout(new GridLayout(5, 1));
        JPanel FirstLine = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel SecondLine = GUITools.CreatePanel(Color.WHITE);
        JPanel ThirdLine = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel FourthLine = GUITools.CreatePanel(Color.WHITE);
        JPanel ButtonLine = GUITools.CreatePanel(Color.WHITE);
        //The first line Title
        JLabel UserInfoTitle = GUITools.CreateLabel("User Information");
        FirstLine.add(UserInfoTitle);
        //The second line Components
        JLabel UserNameLabel = GUITools.CreateLabel("Users Name: ");
        JTextField UserName = new JTextField(UserInformation[0], 15);
        JLabel UserIDLabel = GUITools.CreateLabel("User ID:  ");
        JTextField UserIDField = new JTextField(UserID, 15);
        UserIDField.setEditable(false);
        JLabel UserPasswordLabel = GUITools.CreateLabel("User Password:  ");
        JTextField UserPassword = new JTextField(UserInformation[1], 15);
        SecondLine.add(UserNameLabel); SecondLine.add(UserName);
        if(IsEdit){
            SecondLine.add(UserIDLabel); SecondLine.add(UserIDField);
        }
        SecondLine.add(UserPasswordLabel); SecondLine.add(UserPassword);
        //The third line Title
        JLabel PermissionsTitle = GUITools.CreateLabel("User Permissions");
        ThirdLine.add(PermissionsTitle);
        //The fourth Lines components
        JLabel CreateBillboardsLabel = GUITools.CreateLabel(" Create Billboards:");
        JComboBox<String> CreateBillboards = GUITools.CreateCombobox(Options, PermissionsIndex[0]);
        JLabel EditBillboardsLabel = GUITools.CreateLabel(" Edit All Billboards:");
        JComboBox<String> EditBillboards = GUITools.CreateCombobox(Options, PermissionsIndex[1]);
        JLabel ScheduleBillboardsLabel = GUITools.CreateLabel(" Schedule Billboards:");
        JComboBox<String> ScheduleBillboards = GUITools.CreateCombobox(Options, PermissionsIndex[2]);
        JLabel EditUsersLabel = GUITools.CreateLabel(" Edit Users:");
        JComboBox<String> EditUsers = GUITools.CreateCombobox(Options, PermissionsIndex[3]);
        FourthLine.add(CreateBillboardsLabel); FourthLine.add(CreateBillboards); FourthLine.add(EditBillboardsLabel); FourthLine.add(EditBillboards); FourthLine.add(ScheduleBillboardsLabel); FourthLine.add(ScheduleBillboards); FourthLine.add(EditUsersLabel); FourthLine.add(EditUsers);
        //The button line components
        JButton ApplyButton = new JButton("Apply");
        JButton CancelButton = new JButton("Cancel");
        ButtonLine.add(ApplyButton); ButtonLine.add(CancelButton);

        ActionListener ApplyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Check that the person that is editing is not removing their own edit user permissions
                UserPermissions[3] = (String) EditUsers.getSelectedItem();
                if(IsEdit && UserID.equals(EditorID) && UserPermissions[3].equals("False")) {
                    JOptionPane.showMessageDialog(frame, "Warning: You cannot remove your own Edit user Permissions");

                }
                else{
                    Arrays.fill(UserInformation, null);
                    UserInformation[0] = UserName.getText();
                    UserInformation[1] = UserPassword.getText();
                    UserPermissions[0] = (String)CreateBillboards.getSelectedItem();
                    UserPermissions[1] = (String) EditBillboards.getSelectedItem();
                    UserPermissions[2] = (String) ScheduleBillboards.getSelectedItem();
                    UserPermissions[3] = (String) EditUsers.getSelectedItem();

                    if (GUITools.EmptyValue(UserInformation)) {
                        JOptionPane.showMessageDialog(frame, "Error: Ensure that all  text areas Are filled.");
                    } else {
                        //Check whether the editor is changing their own permissions if yes then ensure that these permissions are updated in the control panel
                        if(IsEdit && UserID.equals(EditorID)){
                            //BillboardControlPanelSwing.ChangeUserPermissions = true;
                        }
                        int create = boolToInt(Boolean.parseBoolean(CreateBillboards.getSelectedItem().toString()));
                        int editBillboards = boolToInt(Boolean.parseBoolean(EditBillboards.getSelectedItem().toString()));
                        int schedule = boolToInt(Boolean.parseBoolean(ScheduleBillboards.getSelectedItem().toString()));
                        int editUsers = boolToInt(Boolean.parseBoolean(EditUsers.getSelectedItem().toString()));


                        String message = TCPMessage.build(TCPMessage.type.REQUEST_CREATE_USER,userId,UserName.getText(), Hash.getHash(UserPassword.getText()),token);
                        String response = ServerCommunication.getResponse(message);
                        String message_two = TCPMessage.build(TCPMessage.type.REQUEST_SET_PERMISSIONS,userId,create+"",editBillboards+"",schedule+"",editUsers+"",token);
                        String response_two = ServerCommunication.getResponse(message_two);

                        StartControlPanel.bcp.UpdateTable(1);
                        CloseWindow();
                    }
                }
            }
        };
        //Action listener for the Cancel Button, Will create a conformation box and close the Editor
        ActionListener CancelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit the application? All unsaved User data will be lost",
                        "Exit Application",
                        JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION){
                    CloseWindow();
                }
            }
        };

        //Add action listeners to the two Buttons
        ApplyButton.addActionListener(ApplyListener); CancelButton.addActionListener(CancelListener);

        inputPanel.add(FirstLine); inputPanel.add(SecondLine); inputPanel.add(ThirdLine); inputPanel.add(FourthLine); inputPanel.add(ButtonLine);

        frame.setVisible(true);
    }

    private static int boolToInt(boolean b){
        return b ? 1 : 0;
    }

    public static void CloseWindow(){
        frame.dispose();
        UserEditOpen = false;
        IsEdit = false;
    }

}
