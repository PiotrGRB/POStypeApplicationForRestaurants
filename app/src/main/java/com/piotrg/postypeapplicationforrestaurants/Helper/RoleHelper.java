package com.piotrg.postypeapplicationforrestaurants.Helper;

public class RoleHelper {
    // enum type inner class, with numerical values corresponding to a role
    public static class ROLE {
        public final static int NOT_SET = -1;
        public final static int ADMIN = 0;
        public final static int USER = 1;
        public final static int VIEWER = 2;
    }
    // string values used to save preferences in SharedPreferences
    public static class SHARED_PREFERENCES {
        public final static String ROLE_NUMBER = "role_number";
    }
    private int currentRole;

    // this is a singleton
    // Create the instance
    private static RoleHelper instance;
    public static RoleHelper getInstance()
    {
        if (instance== null) {
            synchronized(RoleHelper.class) {
                if (instance == null)
                    instance = new RoleHelper();
            }
        }
        // Return the instance
        return instance;
    }
    private RoleHelper()
    {
        currentRole = ROLE.NOT_SET;
        // Constructor hidden because this is a singleton
    }




    // getters and setters
    public int getCurrentRole() {
        return currentRole;
    }
    public void setCurrentRole(int currentRole) {
        this.currentRole = currentRole;
    }


    //inner method to return string name of given role
    public static String NAME(int num) {
        switch(num) {
            case ROLE.NOT_SET:
                return "Role not set.";
            case ROLE.ADMIN:
                return "Administrator.";
            case ROLE.USER:
                return "User.";
            case ROLE.VIEWER:
                return "Viewer.";
        }
        return "";
    }
}