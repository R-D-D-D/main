package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Meeting;
import seedu.address.model.project.Project;
import seedu.address.model.project.Task;
import seedu.address.model.util.SortingOrder;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.*;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PROJECTS;
import static seedu.address.commons.core.Messages.MESSAGE_NOT_CHECKED_OUT;


public class AddProjectMeetingCommand extends Command {
    public static final String COMMAND_WORD = "addProjectMeeting";

    public static final String MESSAGE_SUCCESS = "New meeting added!\n"
            + "%1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a meeting to the project. "
            + " Parameters: "
            + PREFIX_TIME + "dd/MM/yyyy hhmm "
            + PREFIX_DESCRIPTION + "DESCRIPTION \n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_TIME + "29/09/2019 1900 "
            + PREFIX_DESCRIPTION + "milestone discussion";

    public static final String MESSAGE_DUPLICATE_MEETING = "Meeting is already set in this project.";

    private final Meeting toAdd;

    /**
    Constructor
     */
    public AddProjectMeetingCommand(Meeting meeting) {
        requireNonNull(meeting);
        toAdd = meeting;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.isCheckedOut()) {
            throw new CommandException(MESSAGE_NOT_CHECKED_OUT);
        }

        Project projectToEdit = model.getWorkingProject().get();
        List<String> members = projectToEdit.getMemberNames();
        List<Task> taskList = projectToEdit.getTasks();
        List<Meeting> meetingList = projectToEdit.getListOfMeeting();
        List<Meeting> newMeetingList = new ArrayList<>();
        newMeetingList.addAll(meetingList);
        if (newMeetingList.contains(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEETING);
        }
        newMeetingList.add(toAdd);
        Collections.sort(newMeetingList, SortingOrder.getCurrentSortingOrderForMeeting());
        Project editedProject = new Project(projectToEdit.getTitle(), projectToEdit.getDescription(), members, taskList, projectToEdit.getFinance(), projectToEdit.getGeneratedTimetable());
        editedProject.setListOfMeeting(newMeetingList);

        model.setProject(projectToEdit, editedProject);
        model.setWorkingProject(editedProject);
        model.updateFilteredProjectList(PREDICATE_SHOW_ALL_PROJECTS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd), COMMAND_WORD);
    }
}
