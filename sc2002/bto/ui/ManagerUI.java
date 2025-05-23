package sc2002.bto.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sc2002.bto.entity.Application;
import sc2002.bto.entity.Enquiry;
import sc2002.bto.entity.HdbManager;
import sc2002.bto.entity.HdbOfficer;
import sc2002.bto.entity.Project;
import sc2002.bto.entity.Report;
import sc2002.bto.entity.User;
import sc2002.bto.enums.ApplicationStatus;
import sc2002.bto.enums.EnquiryStatus;
import sc2002.bto.enums.FlatType;
import sc2002.bto.enums.OfficerRegistrationStatus;
import sc2002.bto.enums.ReportType;
import sc2002.bto.repository.ApplicationRepository;
import sc2002.bto.repository.EnquiryRepository;
import sc2002.bto.repository.ProjectRepository;
import sc2002.bto.repository.UserRepository;

/**
 * UI class for HDB Manager users in the BTO system.
 * Provides functionality for managers to create and manage projects,
 * approve applications, generate reports, and handle enquiries.
 * 
 */
public class ManagerUI extends BaseUserUI {
    /** The manager user */
    private HdbManager manager;

    /**
     * Constructs a new ManagerUI instance.
     * Initializes a UI controller for manager users.
     */
    public ManagerUI() {
        super();
    }

    /**
     * Initializes the ManagerUI with the current user and repositories,
     * then runs the UI loop.
     * 
     * @param user            The manager user
     * @param userRepo        The repository containing all users
     * @param projectRepo     The repository containing all projects
     * @param applicationRepo The repository containing all applications
     * @param enquiryRepo     The repository containing all enquiries
     * @return true if the application should exit, false to return to login
     */
    @Override
    public boolean run(User user, UserRepository userRepo, ProjectRepository projectRepo,
            ApplicationRepository applicationRepo, EnquiryRepository enquiryRepo) {
        super.initialize(user, userRepo, projectRepo, applicationRepo, enquiryRepo);
        this.manager = (HdbManager) user;
        return super.run(user, userRepo, projectRepo, applicationRepo, enquiryRepo);
    }

    /**
     * Displays additional profile information specific to managers.
     * Shows manager name and count of projects created.
     */
    @Override
    protected void displayAdditionalProfileInfo() {
        System.out.println("Manager Name: " + manager.getManagerName());
        System.out.println("Number of Projects Created: " + manager.getProjectsCreated().size());
    }

    /**
     * Shows the menu options for manager users.
     */
    @Override
    protected void showMenu() {
        System.out.println("\n===== BTO Management System: Manager Menu =====");
        System.out.println("1. View Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Create Project");
        System.out.println("4. Edit Project");
        System.out.println("5. Delete Project");
        System.out.println("6. Toggle Project Visibility");
        System.out.println("7. View All Projects");
        System.out.println("8. View My Projects");
        System.out.println("9. Approve/Reject Officer Registration");
        System.out.println("10. Review Applications");
        System.out.println("11. Handle Withdrawal Requests");
        System.out.println("12. Generate Reports");
        System.out.println("13. View All Enquiries");
        System.out.println("14. View My Projects' Enquiries");
        System.out.println("15. Respond to Enquiry");
        System.out.println("16. Logout");
        System.out.print("Enter your choice: ");
    }

    /**
     * Handles menu choices for manager users.
     * 
     * @param choice The user's menu selection
     * @return true if the user wants to logout, false otherwise
     */
    @Override
    protected boolean handleMenuChoice(String choice) {
        switch (choice) {
            case "1":
                viewProfile();
                return false;
            case "2":
                changePassword();
                return false;
            case "3":
                createProject();
                return false;
            case "4":
                editProject();
                return false;
            case "5":
                deleteProject();
                return false;
            case "6":
                toggleProjectVisibility();
                return false;
            case "7":
                displayAllProjects();
                return false;
            case "8":
                displayManagerProjects();
                return false;
            case "9":
                approveOfficerRegistration();
                return false;
            case "10":
                manager.reviewApplications(applicationRepo, projectRepo);
                return false;
            case "11":
                handleWithdrawalRequests();
                return false;
            case "12":
                generateReports();
                return false;
            case "13":
                manager.printAllEnquiries(enquiryRepo);
                return false;
            case "14":
                manager.printMyProjectsEnquiries(enquiryRepo);
                return false;
            case "15":
                respondToEnquiry();
                return false;
            case "16":
                System.out.println("Logging out...");
                return true;
            default:
                System.out.println("Invalid choice. Please try again.");
                return false;
        }
    }

    /**
 * Displays all projects with filtering capabilities for the HDB Manager.
 * This method:
 * 1. Retrieves all projects from the repository
 * 2. Applies the current filter settings (neighborhood, flat type, sorting)
 * 3. Displays the filtered list with options to further refine filters
 * 
 * Filter settings persist between menu navigations to maintain a consistent
 * user experience. Managers can view all projects regardless of visibility
 * settings, including projects created by other managers.
 */
private void displayAllProjects() {
    List<Project> allProjects = projectRepo.getAll();

    if (allProjects.isEmpty()) {
        System.out.println("No projects available.");
        return;
    }

    while (true) {
        // Apply filters and get the filtered list
        List<Project> filteredProjects = projectFilter.apply(allProjects);

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the current filters.");
            return;
        }

        // Use the base class method to display filtered projects
        displayFilteredProjects("All Projects", filteredProjects);

        System.out.println("\n===== Filter Options =====");
        System.out.println("1. Filter by Neighborhood");
        System.out.println("2. Filter by Flat Type");
        System.out.println("3. Sort by Field");
        System.out.println("4. Toggle Sort Order (Current: " + (projectFilter.isAscending() ? "Ascending" : "Descending") + ")");
        System.out.println("5. Reset Filters");
        System.out.println("6. Back to Main Menu");
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                filterByNeighborhood(allProjects);
                break;
            case "2":
                filterByFlatType();
                break;
            case "3":
                sortByField();
                break;
            case "4":
                toggleSortOrder();
                break;
            case "5":
                projectFilter.reset();
                break;
            case "6":
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
}

    /**
 * Displays projects created by the current manager with filtering capabilities.
 * This method:
 * 1. Retrieves projects created by this manager
 * 2. Applies the current filter settings (neighborhood, flat type, sorting)
 * 3. Displays the filtered list with options to further refine filters
 * 
 * Filter settings persist between menu navigations to maintain a consistent
 * user experience. This allows managers to easily find and manage their own
 * projects, especially when they have created many projects.
 */
private void displayManagerProjects() {
    List<Project> managerProjects = manager.getProjectsCreated();

    if (managerProjects.isEmpty()) {
        System.out.println("You haven't created any projects yet.");
        return;
    }

    while (true) {
        // Apply filters and get the filtered list
        List<Project> filteredProjects = projectFilter.apply(managerProjects);

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the current filters.");
            return;
        }

        // Use the base class method to display filtered projects
        displayFilteredProjects("Your Projects", filteredProjects);

        System.out.println("\n===== Filter Options =====");
        System.out.println("1. Filter by Neighborhood");
        System.out.println("2. Filter by Flat Type");
        System.out.println("3. Sort by Field");
        System.out.println("4. Toggle Sort Order (Current: " + (projectFilter.isAscending() ? "Ascending" : "Descending") + ")");
        System.out.println("5. Reset Filters");
        System.out.println("6. Back to Main Menu");
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                filterByNeighborhood(managerProjects);
                break;
            case "2":
                filterByFlatType();
                break;
            case "3":
                sortByField();
                break;
            case "4":
                toggleSortOrder();
                break;
            case "5":
                projectFilter.reset();
                break;
            case "6":
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
}

    /**
     * Handles the process of creating a new project.
     */
    private void createProject() {
        System.out.println("\n===== Create New Project =====");

        // Check if manager is already handling a project in this period
        if (manager.isHandlingActiveProject()) {
            System.out.println("You are already handling a project in the current application period.");
            System.out.println("Cannot create a new project until the current one closes.");
            return;
        }

        System.out.print("Enter Project Name: ");
        String projectName = scanner.nextLine();

        System.out.print("Enter Neighborhood: ");
        String neighborhood = scanner.nextLine();

        // Flat types
        System.out.println("Include 2-Room Flats? (Y/N): ");
        boolean include2Room = scanner.nextLine().equalsIgnoreCase("Y");

        System.out.println("Include 3-Room Flats? (Y/N): ");
        boolean include3Room = scanner.nextLine().equalsIgnoreCase("Y");

        if (!include2Room && !include3Room) {
            System.out.println("Project must include at least one flat type.");
            return;
        }

        List<FlatType> flatTypesList = new ArrayList<>();
        if (include2Room)
            flatTypesList.add(FlatType.TWO_ROOM);
        if (include3Room)
            flatTypesList.add(FlatType.THREE_ROOM);

        FlatType[] flatTypes = flatTypesList.toArray(new FlatType[0]);

        // Number of units
        int twoRoomUnits = 0;
        if (include2Room) {
            System.out.print("Enter number of 2-Room units: ");
            try {
                twoRoomUnits = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
        }

        int threeRoomUnits = 0;
        if (include3Room) {
            System.out.print("Enter number of 3-Room units: ");
            try {
                threeRoomUnits = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
        }

        // Application dates
        System.out.print("Enter application opening date (yyyy-MM-dd): ");
        String openDate = scanner.nextLine();

        System.out.print("Enter application closing date (yyyy-MM-dd): ");
        String closeDate = scanner.nextLine();

        // Call manager's method
        manager.createProject(projectName, neighborhood, flatTypes, twoRoomUnits, threeRoomUnits, openDate, closeDate,
                projectRepo);

    }

    /**
     * Allows the manager to edit a project with filtering support.
     * This method:
     * 1. Retrieves projects created by this manager
     * 2. Applies the current filter settings (neighborhood, flat type, sorting)
     * 3. Displays the filtered list with options to further refine filters
     * 4. Prompts the manager to select a project from the filtered list
     * 5. Displays current project details and allows editing of various fields
     * 6. Updates the project with the new information
     * 
     * Filter settings persist between menu navigations to maintain a consistent
     * user experience. This makes it easier for managers to find specific projects
     * for editing, especially when they manage many projects.
     */
    private void editProject() {
        List<Project> managerProjects = manager.getProjectsCreated();

        if (managerProjects.isEmpty()) {
            System.out.println("You haven't created any projects yet.");
            return;
        }

        // Display projects with filters
        displayFilteredProjects("Your Projects", managerProjects);

        // Apply filters and get the filtered list
        List<Project> filteredProjects = projectFilter.apply(managerProjects);

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the current filters.");
            return;
        }

        System.out.print("\nSelect a project to edit: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (projectChoice < 0 || projectChoice >= filteredProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }

        Project selectedProject = filteredProjects.get(projectChoice);

        // Display current project details
        System.out.println("\n===== Current Project Details =====");
        System.out.println("Project Name: " + selectedProject.getProjectName());
        System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
        System.out.println("Application Period: " + selectedProject.getApplicationOpenDate() + " to "
                + selectedProject.getApplicationCloseDate());
        System.out.println("2-Room Units: " + selectedProject.getTwoRoomUnitsAvailable());
        System.out.println("3-Room Units: " + selectedProject.getThreeRoomUnitsAvailable());

        // Edit project details
        System.out.println("\n===== Edit Project Details =====");
        System.out.println("Leave fields blank to keep current values");

        System.out.print("New Project Name (current: " + selectedProject.getProjectName() + "): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            selectedProject.setProjectName(newName);
        }

        System.out.print("New Neighborhood (current: " + selectedProject.getNeighborhood() + "): ");
        String newNeighborhood = scanner.nextLine();
        if (!newNeighborhood.isEmpty()) {
            selectedProject.setNeighborhood(newNeighborhood);
        }

        System.out.print("New Application Opening Date (current: " + selectedProject.getApplicationOpenDate() + "): ");
        String newOpenDate = scanner.nextLine();
        if (!newOpenDate.isEmpty()) {
            selectedProject.setApplicationOpenDate(newOpenDate);
        }

        System.out.print("New Application Closing Date (current: " + selectedProject.getApplicationCloseDate() + "): ");
        String newCloseDate = scanner.nextLine();
        if (!newCloseDate.isEmpty()) {
            selectedProject.setApplicationCloseDate(newCloseDate);
        }

        System.out.print("New 2-Room Units (current: " + selectedProject.getTwoRoomUnitsAvailable() + "): ");
        String newTwoRoomUnits = scanner.nextLine();
        if (!newTwoRoomUnits.isEmpty()) {
            try {
                selectedProject.setTwoRoomUnitsAvailable(Integer.parseInt(newTwoRoomUnits));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for 2-Room Units. Value not updated.");
            }
        }

        System.out.print("New 3-Room Units (current: " + selectedProject.getThreeRoomUnitsAvailable() + "): ");
        String newThreeRoomUnits = scanner.nextLine();
        if (!newThreeRoomUnits.isEmpty()) {
            try {
                selectedProject.setThreeRoomUnitsAvailable(Integer.parseInt(newThreeRoomUnits));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for 3-Room Units. Value not updated.");
            }
        }

        // Call manager's method
        manager.editProject(selectedProject);

        // Update in repository
        projectRepo.update(selectedProject);

        System.out.println("Project updated successfully.");
    }

    /**
     * Allows the manager to delete a project with filtering support.
     * This method:
     * 1. Retrieves projects created by this manager
     * 2. Applies the current filter settings (neighborhood, flat type, sorting)
     * 3. Displays the filtered list with options to further refine filters
     * 4. Prompts the manager to select a project from the filtered list
     * 5. Requests confirmation before deletion
     * 6. Deletes the selected project if confirmed
     * 
     * Filter settings persist between menu navigations to maintain a consistent
     * user experience. This makes it easier for managers to locate specific
     * projects
     * for deletion, especially when they need to remove outdated or unnecessary
     * projects.
     */
    private void deleteProject() {
        List<Project> managerProjects = manager.getProjectsCreated();

        if (managerProjects.isEmpty()) {
            System.out.println("You haven't created any projects yet.");
            return;
        }

        // Display projects with filters
        displayFilteredProjects("Your Projects", managerProjects);

        // Apply filters and get the filtered list
        List<Project> filteredProjects = projectFilter.apply(managerProjects);

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the current filters.");
            return;
        }

        System.out.print("\nSelect a project to delete: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (projectChoice < 0 || projectChoice >= filteredProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }

        Project selectedProject = filteredProjects.get(projectChoice);

        System.out.print("Are you sure you want to delete this project? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            // Call manager's method
            manager.deleteProject(selectedProject);

            // Remove from repository
            projectRepo.delete(selectedProject.getProjectID());

            System.out.println("Project deleted successfully.");
        }
    }

    /**
     * Allows the manager to toggle the visibility of a project with filtering
     * support.
     * This method:
     * 1. Retrieves projects created by this manager
     * 2. Applies the current filter settings (neighborhood, flat type, sorting)
     * 3. Displays the filtered list with options to further refine filters
     * 4. Prompts the manager to select a project from the filtered list
     * 5. Toggles the visibility of the selected project
     * 
     * Filter settings persist between menu navigations to maintain a consistent
     * user experience. This feature allows managers to control which projects are
     * visible to applicants, for example, hiding projects that are not ready for
     * applications or have reached capacity.
     */
    private void toggleProjectVisibility() {
        List<Project> managerProjects = manager.getProjectsCreated();

        if (managerProjects.isEmpty()) {
            System.out.println("You haven't created any projects yet.");
            return;
        }

        // Display projects with filters
        displayFilteredProjects("Your Projects", managerProjects);

        // Apply filters and get the filtered list
        List<Project> filteredProjects = projectFilter.apply(managerProjects);

        if (filteredProjects.isEmpty()) {
            System.out.println("No projects match the current filters.");
            return;
        }

        System.out.print("\nSelect a project to toggle visibility: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (projectChoice < 0 || projectChoice >= filteredProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }

        Project selectedProject = filteredProjects.get(projectChoice);

        // Call manager's method
        manager.toggleProjectVisibility(selectedProject);

        // Update in repository
        projectRepo.update(selectedProject);
    }

    /**
     * Handles the process of approving or rejecting officer registrations.
     */
    private void approveOfficerRegistration() {
        // Get all HDB Officers
        List<User> allUsers = userRepo.getAll();
        List<HdbOfficer> allOfficers = allUsers.stream()
                .filter(u -> u instanceof HdbOfficer)
                .map(u -> (HdbOfficer) u)
                .collect(Collectors.toList());

        List<Project> managerProjects = manager.getProjectsCreated();

        List<HdbOfficer> pendingOfficers = allOfficers.stream()
                .filter(o -> o.getPendingProject() != null)
                .filter(o -> o.getRegistrationStatus() == OfficerRegistrationStatus.PENDING)
                .filter(o -> managerProjects.stream()
                        .anyMatch(p -> p.getProjectID().equals(o.getPendingProject().getProjectID())))
                .collect(Collectors.toList());

        if (pendingOfficers.isEmpty()) {
            System.out.println("No officer applications.");
            return;
        }

        System.out.println("\n===== Pending Officer Registrations =====");
        for (int i = 0; i < pendingOfficers.size(); i++) {
            HdbOfficer o = pendingOfficers.get(i);
            System.out.println((i + 1) + ". Officer Name: " + o.getName());
            System.out.println("   NRIC: " + o.getId());
            System.out.println("   Project: " + o.getPendingProject().getProjectName());
        }

        System.out.print("Select an officer registration to process: ");
        int officerChoice;
        try {
            officerChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (officerChoice < 0 || officerChoice >= pendingOfficers.size()) {
            System.out.println("Invalid officer selection.");
            return;
        }

        HdbOfficer selectedOfficer = pendingOfficers.get(officerChoice);

        System.out.print("Approve this registration? (Y/N): ");
        String approve = scanner.nextLine();

        if (approve.equalsIgnoreCase("Y")) {
            manager.approveOfficerRegistration(selectedOfficer);
        } else {
            selectedOfficer.setRegistrationStatus(OfficerRegistrationStatus.REJECTED);
            selectedOfficer.setPendingProject(null);
            System.out.println("Officer registration rejected.");
        }

        userRepo.update(selectedOfficer);
    }

    /**
     * Handles the process of managing withdrawal requests.
     */
    private void handleWithdrawalRequests() {
        // Get applications with withdrawal requests for projects managed by this
        // manager
        List<Application> withdrawalRequests = applicationRepo.getAll().stream()
                .filter(a -> a.isWithdrawalRequested() &&
                        a.getStatus() != ApplicationStatus.UNSUCCESSFUL &&
                        a.getProject().getManagerInCharge().equals(manager.getManagerName()))
                .collect(Collectors.toList());

        if (withdrawalRequests.isEmpty()) {
            System.out.println("No withdrawal requests found.");
            return;
        }

        System.out.println("\n===== Withdrawal Requests =====");
        for (int i = 0; i < withdrawalRequests.size(); i++) {
            Application a = withdrawalRequests.get(i);
            System.out.println((i + 1) + ". Application ID: " + a.getApplicationId());
            System.out.println("   Applicant: " + a.getApplicant().getName());
            System.out.println("   Project: " + a.getProject().getProjectName());
            System.out.println("   Flat Type: " + a.getSelectedFlatType());
            System.out.println("   Current Status: " + a.getStatus());
        }

        System.out.print("Select a withdrawal request to process: ");
        int requestChoice;
        try {
            requestChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (requestChoice < 0 || requestChoice >= withdrawalRequests.size()) {
            System.out.println("Invalid request selection.");
            return;
        }

        Application selectedApplication = withdrawalRequests.get(requestChoice);

        System.out.print("Approve this withdrawal request? (Y/N): ");
        String approve = scanner.nextLine();

        // Call manager's method
        manager.handleWithdrawalRequest(selectedApplication, approve.equalsIgnoreCase("Y"));

        // Update application in repository
        applicationRepo.update(selectedApplication);

        System.out.println("Withdrawal request processed successfully.");
    }

    /**
     * Handles the process of generating reports.
     */
    private void generateReports() {
        System.out.println("\n===== Generate Reports =====");
        System.out.println("1. All Bookings");
        System.out.println("2. Bookings by Flat Type");
        System.out.println("3. Bookings by Marital Status");
        System.out.print("Select report type: ");

        String reportChoice = scanner.nextLine();

        ReportType reportType = null;
        switch (reportChoice) {
            case "1":
                reportType = ReportType.ALL_BOOKINGS;
                break;
            case "2":
                reportType = ReportType.BY_FLAT_TYPE;
                break;
            case "3":
                reportType = ReportType.BY_MARITAL_STATUS;
                break;
            default:
                System.out.println("Invalid report type selection.");
                return;
        }

        // Call manager's method
        Report report = manager.generateReport(reportType, applicationRepo);

        if (report != null) {
            report.printReport();
        }
    }

    /**
     * Handles the process of responding to enquiries.
     */
    private void respondToEnquiry() {
        // Get pending enquiries for projects managed by this manager
        List<Enquiry> pendingEnquiries = enquiryRepo.getAll().stream()
                .filter(e -> e.getStatus() == EnquiryStatus.PENDING &&
                        manager.getProjectsCreated().stream()
                                .anyMatch(p -> p.getProjectID().equals(e.getProject().getProjectID())))
                .collect(Collectors.toList());

        if (pendingEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for your projects.");
            return;
        }

        System.out.println("\n===== Pending Enquiries =====");
        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry e = pendingEnquiries.get(i);
            System.out.println((i + 1) + ". Enquiry ID: " + e.getEnquiryId());
            System.out.println("   From: " + e.getApplicant().getName());
            System.out.println("   Project: " + e.getProject().getProjectName());
            System.out.println("   Message: " + e.getMessage());
        }

        System.out.print("Select an enquiry to respond to: ");
        int enquiryChoice;
        try {
            enquiryChoice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (enquiryChoice < 0 || enquiryChoice >= pendingEnquiries.size()) {
            System.out.println("Invalid enquiry selection.");
            return;
        }

        Enquiry selectedEnquiry = pendingEnquiries.get(enquiryChoice);

        System.out.println("Enter your response:");
        String response = scanner.nextLine();

        if (response.trim().isEmpty()) {
            System.out.println("Response cannot be empty.");
            return;
        }

        // Call manager's method
        manager.respondToEnquiry(selectedEnquiry, response);

        // Update enquiry in repository
        enquiryRepo.update(selectedEnquiry);

        System.out.println("Response submitted successfully.");
    }

    /**
     * Toggles the sort order between ascending and descending.
     * This affects how projects are sorted in the various list views.
     */
    protected void toggleSortOrder() {
        projectFilter.setAscending(!projectFilter.isAscending());
    }
}
