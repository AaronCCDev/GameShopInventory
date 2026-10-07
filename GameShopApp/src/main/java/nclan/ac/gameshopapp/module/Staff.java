package nclan.ac.gameshopapp.module;

public class Staff {

    private final String staffId;
    private final String name;
    private final String role;

    public Staff(
            String staffId,
            String name,
            String role
    ) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return staffId
                + " - "
                + name
                + " - "
                + role;
    }
}