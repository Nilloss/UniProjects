package billboards.controlPanel;

public class Permissions {
    private String UserID;
    private boolean create_billboards;
    private boolean edit_all_billboards;
    private boolean schedule_billboards;
    private boolean edit_users;

    /**
     * Class is used to hold the extracted permissions from the server, this is to prevent unnecessary communication with the server
     * @param UserID The user that is logged in or the user that is being edited
     * @param create_billboards boolean value for whether user is able to create billboards
     * @param edit_all_billboards boolean value for whether user is able to edit all of the billboards
     * @param schedule_billboards boolean value for whether user is able to schedule billboards
     * @param edit_users boolean value for whether user is able to edit the user values
     */
    public Permissions(String UserID, boolean create_billboards, boolean edit_all_billboards, boolean schedule_billboards, boolean edit_users){
        this.UserID = UserID;
        this.create_billboards = create_billboards;
        this.edit_all_billboards = edit_all_billboards;
        this.schedule_billboards = schedule_billboards;
        this.edit_users = edit_users;
    }

    //used to check permissions
    public String get_UserID() {
        return UserID;
    }
    public boolean can_Create_billboards() {
        return create_billboards;
    }
    public boolean can_Edit_all_billboards() {
        return edit_all_billboards;
    }
    public boolean can_Schedule_billboards() {
        return schedule_billboards;
    }
    public boolean can_Edit_users() {
        return edit_users;
    }

    //Edits the permissions of a user
    public void edit_user_permissions(boolean create_billboards, boolean edit_all_billboards, boolean schedule_billboards, boolean edit_users){
        this.create_billboards = create_billboards;
        this.edit_all_billboards = edit_all_billboards;
        this.schedule_billboards = schedule_billboards;
        this.edit_users = edit_users;
    }
}
