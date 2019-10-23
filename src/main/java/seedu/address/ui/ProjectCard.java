package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.project.Project;
import seedu.address.model.project.Task;

import java.util.Comparator;

/**
 * An UI component that displays information of a {@code Project}.
 */
public class ProjectCard extends UiPart<Region> {

    private static final String FXML = "ProjectListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Project project;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private Label memberTitle;
    @FXML
    private FlowPane members;
    @FXML
    private Label taskTitle;
    @FXML
    private FlowPane tasks;
    @FXML
    private FlowPane meetings;

    public ProjectCard(Project project, int displayedIndex) {
        super(FXML);
        this.project = project;
        int count = 0;
        id.setText(displayedIndex + ". ");
        title.setText(project.getTitle().title);
        description.setText(project.getDescription().description);
        memberTitle.setText("Members:");
        project.getMembers().forEach(member -> members.getChildren().add(new Label(member)));

        for (Task task : project.getTasks()) {
            tasks.getChildren().add(new Label("    " + ++count + ". " + task.toString()));
        }
        taskTitle.setText("Tasks: ");
        tasks.setOrientation(Orientation.VERTICAL);
        tasks.setPrefWrapLength(100);
        project.getListOfMeeting().stream()
                .sorted(Comparator.comparing(m -> m.getTime().getDate()))
                .forEach(meeting -> meetings.getChildren().add(new Label(meeting.getDescription().description + " " + meeting.getTime().time)));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ProjectCard)) {
            return false;
        }

        // state check
        ProjectCard card = (ProjectCard) other;
        return id.getText().equals(card.id.getText())
                && project.equals(card.project);
    }
}
