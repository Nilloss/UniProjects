package billboards.controlPanel;

import billboards.BillboardSwing;
import billboards.shared.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;


public class BillboardControlPanelSwing extends BillboardSwing {

    final int width;
    final int height;

    private JTextField usernameField;
    private JTextField passwordField;

    private JTabbedPane tabMenu;
    private int tabAmount;
    private Permissions user;
    private String sessionToken = "[B@64a294a6";
    private String userId;
    public boolean ChangeUserPermissions = false;
    private String hashedPassword = "";



    /**
     * A constructor that initialises the JFrame object with a title, size and
     * sets the visibility to true.
     * @param title the windows title
     * @param width the width of the window
     * @param height the height of the window
     */
    public BillboardControlPanelSwing(String title, int width, int height) {
        super(title);
        frame.setLayout(null);
        this.width = width;
        this.height = height;

        initialise(width, height, true);
    }

    /**
     * Makes the login screen show up
     */
    public void loginScreen() {
        // initialising the username and password fields with labels
        usernameField = new JTextField();
        passwordField = new JTextField();

        // initialising JLabels
        JLabel usernameText = GUITools.CreateLabel("Username: ");
        JLabel passwordText = GUITools.CreateLabel("Password: ");

        // making the login button
        JButton loginButton = new JButton("Login");

        // making the login buttons action listener for what happens when the button is pressed
        ActionListener loginButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ServerCommunication.serverMessage(); TODO: implement server requests here. Send username and password, receive conformation
                if(validateLogin(usernameField.getText(), passwordField.getText())) {
                    // removing frames
                    frame.remove(usernameField);
                    frame.remove(passwordField);
                    frame.remove(usernameText);
                    frame.remove(passwordText);
                    frame.remove(loginButton);

                    // adding everything in the mainScreen to the frame
                    mainScreen();
                }
            }
        };

        // adding the action listener to the button
        loginButton.addActionListener(loginButtonListener);
        // setting the location and size of everything
        loginButton.setBounds( width/2 - width/20,height/2 + 100,150,50);
        usernameField.setBounds(width/2,height/2 - 10,100,25);
        passwordField.setBounds(width/2,(height/2) + 15,100,25);
        usernameText.setBounds(width/2 - 65,height/2 - 10,100,25);
        passwordText.setBounds(width/2 - 65,(height/2) + 15,100,25);

        // adding everything to the frame
        frame.add(usernameField);
        frame.add(passwordField);
        frame.add(usernameText);
        frame.add(passwordText);
        frame.add(loginButton);



        update();
    }

    /**
     * Used to see if the login was valid based on parameters
     * @param validUsername the valid username in question
     * @param validPassword the valid password in question TODO: use a hash
     * @return whether or not the login credentials were valid.
     */
    public boolean validateLogin(String validUsername, String validPassword) {
        hashedPassword = Hash.getHash(validPassword);
        String message = TCPMessage.build(TCPMessage.type.REQUEST_LOGIN,validUsername,hashedPassword);
        String response = ServerCommunication.getResponse(message);
        String[] fields = TCPMessage.receive(response);
        if(Integer.parseInt(fields[0]) == TCPMessage.type.RESPONSE_FAIL){
            MessageBox.infoBox(fields[1],"Error authenticating");
            return false;
        }
        userId = fields[1];
        sessionToken = fields[2];

        String message_two = TCPMessage.build(TCPMessage.type.REQUEST_USER_PERMISSIONS,userId,sessionToken);
        String response_two = ServerCommunication.getResponse(message_two);
        String[] fields_two = TCPMessage.receive(response_two);
        user = new Permissions(userId, stringToBool(fields_two[1]),stringToBool(fields_two[2]),stringToBool(fields_two[3]),stringToBool(fields_two[4]));

        return true;
    }

    /**
     * An implementation of a tab system for the control panel.
     */
    public void mainScreen() {
        // the JTabbedPane allows for all the tabs
        tabMenu = new JTabbedPane();

        // the component for the Main Menu tab
        JLabel mainMenu = new JLabel();

        // the main menu buttons
        JButton schedule = new JButton("Open Schedule");
        JButton userList = new JButton("User List");
        JButton BillboardList = new JButton("Billboard List");
        JButton logout = new JButton("Logout");
        JButton ChangePassword = new JButton("Change Password");

        // adding action listeners
        ActionListener ScheduleListener = new ActionListener() {
            private boolean addedSchedule = false; // so the schedule can only be added to tab once

            @Override
            public void actionPerformed(ActionEvent e) {
                //Schedule stuff here
                if(!addedSchedule) { // TODO: implement server requests here
                    UpdateTable(2);
                    addedSchedule = true;
                }
                else{
                    tabMenu.setSelectedIndex(getTabIndex("Schedule"));
                }

            }
        };

        ActionListener UserListListener = new ActionListener() {
            private boolean addedUserList = false; // so the schedule can only be added to tab once

            @Override
            public void actionPerformed(ActionEvent e) {
                if(user.can_Edit_users()){
                    if(!addedUserList) {
                        addedUserList = true;
                        UpdateTable(1);
                    }else{
                        tabMenu.setSelectedIndex(getTabIndex("User List"));
                    }
                }
                else{
                    MessageBox.infoBox("Only users with an edit user permission can access this","Unauthorised");
                }

            }
        };

        //Action Listener for the Billboard list button
        ActionListener BillboardListListener = new ActionListener() {
            private boolean addedbillboards = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!addedbillboards) {
                    addedbillboards = true;
                    UpdateTable(0);
                }else{
                    tabMenu.setSelectedIndex(getTabIndex("Billboards"));
                }

            }
        };

        //Action listener for the Logout button
        ActionListener LogoutListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmation = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to log out? Any unsaved work will be lost.",
                        "Logout",
                        JOptionPane.YES_NO_OPTION);
                if(confirmation == JOptionPane.YES_OPTION) {
                    String message = TCPMessage.build(TCPMessage.type.REQUEST_LOGOUT,sessionToken);
                    String response = ServerCommunication.getResponse(message);



                    frame.remove(tabMenu);
                    tabAmount = 0;
                    //If the billboard edit or user edit windows are open close them before logging out
                    if (BillboardEditor.EDITWINDOWOPEN){
                        BillboardEditor.CloseWindow();
                        EditUser.CloseWindow();
                    }
                    if(EditUser.UserEditOpen){
                        EditUser.CloseWindow();
                    }
                    loginScreen();
                }
            }
        };

        //Action listener for the change password button
        ActionListener ChangepasswordListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //opens a new window that will require the old password and the new password
                String Password = JOptionPane.showInputDialog("Enter Current password");
                if (Hash.getHash(Password).equals(hashedPassword)){
                    String newPassword = JOptionPane.showInputDialog("Enter New password");
                    String hashedPass = Hash.getHash(newPassword);

                    String message = TCPMessage.build(TCPMessage.type.REQUEST_SET_USER_PASSWORD,userId,hashedPass,sessionToken);
                    String response = ServerCommunication.getResponse(message);
                    String[] fields = TCPMessage.receive(response);
                    if(Integer.parseInt(fields[0]) == 1000){
                        MessageBox.infoBox("Success","Success");
                    }
                    else{
                        MessageBox.infoBox(fields[1],"Error");
                    }
                }
                else{
                    MessageBox.infoBox("Incorrect password","Error");
                }
                // }

            }
        };



        // adding the action listeners to the buttons
        //Check permissions of user if has permission create an action listener to the button. if not gray the button out
        if(user.can_Schedule_billboards()) {
            schedule.addActionListener(ScheduleListener);
        }
        else{
            schedule.setBackground(Color.GRAY);
        }
        if(user.can_Edit_users()) {
            userList.addActionListener(UserListListener);
        }
        else{
            userList.setBackground(Color.GRAY);
        }
        BillboardList.addActionListener(BillboardListListener);
        logout.addActionListener(LogoutListener);
        ChangePassword.addActionListener(ChangepasswordListener);



        // tabMenu bounds
        tabMenu.setBounds(0, 0, width - width/58, height - height/18);

        // button bounds
        schedule.setBounds(width/3 + width/12, height/4 - 50, 200, 50);
        userList.setBounds(width/3 + width/12, height/4 + 50, 200, 50);
        BillboardList.setBounds(width/3 + width/12, height/4 + 150, 200, 50);
        logout.setBounds(width/3 + width/12, height/4 + 250, 200, 50);
        ChangePassword.setBounds(width-250, height-150, 200, 50);
        // adding the buttons to the label
        mainMenu.add(schedule);
        mainMenu.add(userList);
        mainMenu.add(BillboardList);
        mainMenu.add(logout);
        mainMenu.add(ChangePassword);

        // adding the main menu as a tab
        tabMenu.addTab("Main Menu", mainMenu);
        ++tabAmount;

        // adding everything to the frame through tabMenu
        frame.add(tabMenu);


        update();
    }

    private int getTabIndex(String title){
        for(int i = 0; i < tabMenu.getTabCount(); i++){
            if(tabMenu.getTitleAt(i).equals(title)){
                return i;
            }
        }
        return 0;
    }

    private void reloadTab(String tabTitle, Component component){
        if(getTabIndex(tabTitle) != 0){
            tabMenu.remove(getTabIndex(tabTitle));
            tabMenu.add(tabTitle,component);
        }else{
            tabMenu.add(tabTitle,component);
        }
        tabMenu.setSelectedIndex(getTabIndex(tabTitle));
    }

    /**
     * A method for updating the contents of the table lists for all of the tables in the menu
     * @param type An Int value corresponding to what table needs to be updated
     *             0 = The Billboard table
     *             1 = The User table
     *             2 = The schedule table
     * @return Returns the table that is produced from the server connection
     */
    public void UpdateTable(int type) {
        if (type == 0) {
            //Billboard table
            String[] columnNames = {"Billboard Name", "Preview", "Edit", "Delete"};

            String[][] tableData = loadTableData("billboard");
            int numRows = tableData.length;
            String[][] preparedData = new String[numRows][4];
            for(int y = 0; y < numRows; y++){
                for(int x = 0; x < 4; x++){
                    if(x == 0){
                        preparedData[y][x] = tableData[y][1];
                    }else if(x == 1){
                        preparedData[y][x] = "Preview";
                    }else if(x == 2){
                        preparedData[y][x] = "Edit";
                    }else{
                        preparedData[y][x] = "Delete";
                    }
                }
            }

            //Initialise fields
            JTable table = new JTable(preparedData,columnNames);
            JScrollPane tablepane = new JScrollPane(table);
            tablepane.setBounds(0, 0, width, height - 130);
            JLabel BillboardsListTab = new JLabel();
            JButton NewBillboard = new JButton("Create New");
            if(!user.can_Create_billboards()){
                NewBillboard.setEnabled(false);
            }
            JButton RefreshTable = new JButton("Refresh");

            // ACTION LISTENERS
            //Action listener for the table edit buttons
            ActionListener EditBillboardListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = table.getSelectedRow();
                    String billboardCreatorId = tableData[row][3];
                    String billboardId = tableData[row][0];

                    if (user.can_Edit_all_billboards() || (billboardCreatorId == userId && !billboardIsScheduled(billboardId))) {
                        //Check whether there is already an edit window open. If there is prevent opening show error message.
                        if (!BillboardEditor.EDITWINDOWOPEN) {
                            //TODO server connection to get billboard name. Use the row int to find the correct billboard name and then git the correct billboard data
                            /**
                             * Temporary billboard values for testing
                             */
                            String[] testElement1 = {"None", "", "", "", "", "", "", ""};
                            String[] testElement2 = {"Text", "Boo!!!", "40", "50", "5", "40", "WHITE", "200"};
                            String[] testElement3 = {"Image", "URL", "https://www.memesmonkey.com/images/memesmonkey/fe/fe31c45fd08566b4d3e4188efb491f1e.jpeg", "25", "50", "50", "50", ""};
                            //

                            BillboardEditor.EditBillboard("Test Billboard", "RED", testElement1, testElement2, testElement3, billboardId, billboardCreatorId);

                        } else {
                            JOptionPane.showMessageDialog(tabMenu, "Edit Window Already Open.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(tabMenu, "Warning: Unauthorised or billboard is currently scheduled.");
                    }
                }
            };

            //Action listener for the new billboard button
            ActionListener NewBillboardListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    //Check whether there is already an edit window open. If there is prevent opening show error message.
                    if(user.can_Create_billboards()){
                        if (!BillboardEditor.EDITWINDOWOPEN) {
                            BillboardEditor.BillboardEditormain(userId,userId);
                        } else {
                            JOptionPane.showMessageDialog(tabMenu, "Edit Window Already Open.");
                        }
                    }else{
                        MessageBox.infoBox("You do not have permission to create billboards","Warning");
                    }

                }
            };

            //Action Listener for Preview Billboard
            ActionListener PreviewBillboardListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // BillboardDrawer.BillboardDrawerMain(content);

                    int row = table.getSelectedRow();

                    if (!BillboardDrawer.ISDRAWEROPEN) {
                        BillboardDrawer.displayXML(tableData[row][2]);
                    } else {
                        JOptionPane.showMessageDialog(tabMenu, "Preview Window Already Open.");
                    }
                }
            };

            //Action Listener for Delete Billboard
            ActionListener DeleteBillboardListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = table.getSelectedRow();
                    String billboardCreatorId = tableData[row][3];
                    String billboardId = tableData[row][0];
                    if (user.can_Edit_all_billboards() || (billboardCreatorId == userId && !billboardIsScheduled(billboardId))) {
                        int result = JOptionPane.showConfirmDialog(
                                frame,
                                "Are you sure you wish to delete this billboard?",
                                "Delete Billboard",
                                JOptionPane.YES_NO_OPTION);

                        if (result == JOptionPane.YES_OPTION) {
                            String message = TCPMessage.build(TCPMessage.type.REQUEST_DELETE_BILLBOARD, billboardId, sessionToken);
                            ServerCommunication.getResponse(message);
                            UpdateTable(0);
                        }
                    } else {
                        JOptionPane.showMessageDialog(tabMenu, "Warning: Unauthorised or billboard is currently scheduled.");
                    }
                }
            };

            ActionListener RefreshTableListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UpdateTable(0);
                }
            };

            RefreshTable.addActionListener(RefreshTableListener);

            //Check permissions for if user can edit buttons if can create an action listener to the button. If not Grey the button out
            if (user.can_Create_billboards() || user.can_Edit_all_billboards()) {
                NewBillboard.addActionListener(NewBillboardListener);
            } else {
                NewBillboard.setBackground(Color.GRAY);
            }

            //Make the table a scrollpane, and set the bounds for the table
            tablepane = new JScrollPane(table);
            tablepane.setBounds(0, 0, width, height - 130);
            NewBillboard.setBounds(width - 250, height - 125, 200, 50);
            RefreshTable.setBounds(width - 500, height - 125, 200, 50);

            //Add the table and the new billboard button to the tab
            BillboardsListTab.add(RefreshTable);
            BillboardsListTab.add(NewBillboard);
            BillboardsListTab.add(tablepane);

            //Create a Preview button for all of the saved billboards in the table
            table.getColumn("Preview").setCellRenderer(new TableButton.Renderer());
            table.getColumn("Preview").setCellEditor(new TableButton.Editor(new JCheckBox(), PreviewBillboardListener));

            //Create an edit button for all of the saved billboards in the table
            table.getColumn("Edit").setCellRenderer(new TableButton.Renderer());
            table.getColumn("Edit").setCellEditor(new TableButton.Editor(new JCheckBox(), EditBillboardListener));

            //Create a Delete button for all of the saved billboards in the table
            table.getColumn("Delete").setCellRenderer(new TableButton.Renderer());
            table.getColumn("Delete").setCellEditor(new TableButton.Editor(new JCheckBox(), DeleteBillboardListener));


            reloadTab("Billboards",BillboardsListTab);
        }
        else if (type == 1) {
            //User table
            String[] columnNames = {"Name", "Create", "Edit Billboards", "Schedule", "Edit Users", "Edit User", "Delete User"};
            String[][] userData = loadTableData("user");
            int numRows = userData.length;
            String[][] preparedData = new String[numRows][7];
            for(int y = 0; y < numRows; y++){
                for(int x = 0; x < 7; x++){
                    if(x < 5){
                        preparedData[y][x] = userData[y][x+1];
                    }else if(x < 6){
                        preparedData[y][x] = "Edit";
                    }else {
                        preparedData[y][x] = "Delete";
                    }
                }
            }


            JScrollPane tablepane;
            JLabel UserListTab;

            JTable table = new JTable(preparedData,columnNames);
            UserListTab = new JLabel();
            JButton NewUser = new JButton("Create New");
            JButton RefreshTable = new JButton("Refresh");

            //Action listener for the New User button
            ActionListener NewUserListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newId = UUID.randomUUID().toString();

                    if (!EditUser.UserEditOpen) {
                        EditUser.UserEditorMain(newId,sessionToken);
                    } else {
                        JOptionPane.showMessageDialog(tabMenu, "User Edit Window Already open.");
                    }
                }
            };
            //Action listener for the Table button Edit user
            ActionListener EditUserListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!EditUser.UserEditOpen) {
                        int selectedRow = table.getSelectedRow();
                        String userId = userData[selectedRow][0];
                        //TODO Server request for the edited users information
                        String[] Information = {userData[selectedRow][1], "New Password"};
                        String[] permissions = {"true", "true", "true", "true"};
                        EditUser.EditExistingUser(Information, userData[table.getSelectedRow()][0], permissions, userId,sessionToken);
                        //Checks Whether the User that is editing users permissions have been changed.
                        //if they have than update the current users permissions
                        if (ChangeUserPermissions) {
                            //TODO request the current permissions of the user so that if they have been changed in the edit they can be reapplied
                            user.edit_user_permissions(true, true, true, true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(tabMenu, "User Edit Window Already open.");
                    }
                }
            };

            //Action Listener for if delete user is pressed
            ActionListener DeleteUserListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    int selectedRow = table.getSelectedRow();
                    String userId = userData[selectedRow][0];

                    //Check that the User is not trying to delete their own data
                            if(user.get_UserID() != userId) {
                                int result = JOptionPane.showConfirmDialog(
                                        frame,
                                        "Are you sure you want to Delete this user? All user information will be lost.",
                                        "Delete User",
                                        JOptionPane.YES_NO_OPTION);
                                if (result == JOptionPane.YES_OPTION) {
                                    String message = TCPMessage.build(TCPMessage.type.REQUEST_DELETE_USER,userId,sessionToken);
                                    String response = ServerCommunication.getResponse(message);
                                    String[] fields = TCPMessage.receive(response);
                                    if(Integer.parseInt(fields[0]) == 1000){
                                        UpdateTable(1);
                                    }
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(tabMenu, "Warning: Cannot delete your own user information!");
                            }
                }
            };
            //Action Listener for the refresh button
            ActionListener RefreshListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    UserListTab.remove(tablepane);
//                    //table = UpdateTable(1);
//                    tablepane = new JScrollPane(table);
//                    tablepane.setBounds(0, 0, width, height - 130);
//                    UserListTab.add(tablepane);
//                    //Create a Edit user button for all of the saved billboards in the table
//                    table.getColumn("Edit User").setCellRenderer(new TableButton.Renderer());
//                    table.getColumn("Edit User").setCellEditor(new TableButton.Editor(new JCheckBox(), EditUserListener));
//
//                    //Create a Delete User button for all of the saved billboards in the table
//                    table.getColumn("Delete User").setCellRenderer(new TableButton.Renderer());
//                    table.getColumn("Delete User").setCellEditor(new TableButton.Editor(new JCheckBox(), DeleteUserListener));
                }
            };

            RefreshTable.addActionListener(RefreshListener);
            NewUser.addActionListener(NewUserListener);

            //Make the table a scrollpane, and set the bounds for the table
            tablepane = new JScrollPane(table);
            tablepane.setBounds(0, 0, width, height - 130);
            NewUser.setBounds(width - 250, height - 125, 200, 50);
            RefreshTable.setBounds(width - 500, height - 125, 200, 50);

            //Add the table and the new billboard button to the tab
            UserListTab.add(NewUser);
            UserListTab.add(RefreshTable);
            UserListTab.add(tablepane);

            //Create a Edit user button for all of the saved billboards in the table
            table.getColumn("Edit User").setCellRenderer(new TableButton.Renderer());
            table.getColumn("Edit User").setCellEditor(new TableButton.Editor(new JCheckBox(), EditUserListener));

            //Create a Delete User button for all of the saved billboards in the table
            table.getColumn("Delete User").setCellRenderer(new TableButton.Renderer());
            table.getColumn("Delete User").setCellEditor(new TableButton.Editor(new JCheckBox(), DeleteUserListener));

            //Add the list tab to the main window
            reloadTab("User List", UserListTab);
        }
        else{
            //Schedule table
            JLabel scheduleComponent;
            JTable table;
            String[] columnNames = {"Name", "Time Scheduled", "duration", "options"};
            String[][] loadedData;
            String[][] preparedData;
            JButton btnRefresh;
            JButton btnCreate;
            CreateSchedule createSchedule = new CreateSchedule(loadTableData("schedule"),sessionToken);

            //Refresh data
            loadedData = loadTableData("schedule");
            int y = loadedData.length;
            preparedData = new String[y][4];
            for(int i = 0; i < y; i ++){
                for(int k = 0; k < 4; k++){
                    preparedData[i][k] = k == 3 ? "Remove" : loadedData[i][k+1];
                }
            }

            //Initialise sub components
            scheduleComponent = new JLabel();
            btnRefresh = new JButton("Refresh");
            btnCreate = new JButton("Create New");
            if(!user.can_Schedule_billboards()){
                btnCreate.setEnabled(false);
            }


            table = new JTable(preparedData, columnNames);

            JScrollPane schedulePane = new JScrollPane(table);

            //Set bounds & add sub components
            btnRefresh.setBounds(width-500, height-125, 200, 50);
            btnCreate.setBounds(width-250, height-125, 200, 50);
            schedulePane.setBounds(0,0,width,height - 130);
            scheduleComponent.add(schedulePane);
            scheduleComponent.add(btnRefresh);
            scheduleComponent.add(btnCreate);

            btnCreate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            if(createSchedule.isActive()){
                                createSchedule.requestFocus();
                            }
                            else{
                                createSchedule.setVisible(true);
                            }
                        }
                    });
                }
            });
            ActionListener removeScheduleListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Adding remove schedule listeners");
                    int selectedRow = table.getSelectedRow();

                    //use selected row to remove the schedule
                    String message = TCPMessage.build(TCPMessage.type.REQUEST_REMOVE_SCHEDULE,loadedData[selectedRow][0],sessionToken);
                    String response = ServerCommunication.getResponse(message);

                    //fire refresh
                    UpdateTable(2);
                }
            };

            int currScheduledIndex = Schedule.getCurrentlyScheduledIndex(loadedData);

            table.setDefaultRenderer(Object.class,new TableHighlighter(currScheduledIndex));
            table.getColumn("options").setCellRenderer(new TableButton.Renderer());
            table.getColumn("options").setCellEditor(new TableButton.Editor(new JCheckBox(),removeScheduleListener));

            reloadTab("Schedule",scheduleComponent);
        }
    }

    /**
     * Retrieves the table data from the server for use within the interface
     * @param table
     * @return
     */
    private String[][] loadTableData(String table){
        try {
            int type;
            if(table.equals("schedule")){
                type = TCPMessage.type.REQUEST_VIEW_SCHEDULE;
            }else if(table.equals("user")){
                type = TCPMessage.type.REQUEST_LIST_USERS;
            }else if(table.equals("billboard")){
                type = TCPMessage.type.REQUEST_LIST_BILLBOARDS;
            }else {
                System.out.println("incorrect table: use 'schedule' or 'user' or 'billboard'");
                return null;
            }
            String message = TCPMessage.build(type,sessionToken);
            String response = ServerCommunication.getResponse(message);
            return TCPMessage.receiveList(response);
        } catch (Exception e) {
            System.out.println("Unable to load table data");
            return null;
        }
    }

    private boolean stringToBool(String s){
        int i = Integer.parseInt(s);
        return i == 1;
    }

    private boolean billboardIsScheduled(String billboardId){
        String[][] scheduleData = loadTableData("schedule");
        for(String[] s : scheduleData){
            if(s[4].equals(billboardId)){
                return true;
            }
        }
        return false;
    }

}
