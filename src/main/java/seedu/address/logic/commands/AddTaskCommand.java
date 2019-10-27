package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Project;
import seedu.address.model.project.Task;
import static seedu.address.model.util.SortingOrder.CURRENT_SORTING_ORDER_FOR_TASK;
import seedu.address.model.util.SortingOrder;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PROJECTS;


/**
 * Adds a Task field of a project
 */
public class AddTaskCommand extends Command {
    public static final String COMMAND_WORD = "addTask";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds this task to the current project "
            + "s/ [DESCRIPTION] c/ [TIME in the form dd/MM/yyyy hhmm]\n"
            + "Example: " + COMMAND_WORD
            + " s/ Finish GUI"
            + " c/ 05/09/2019 1600";


    public static final String MESSAGE_ADD_TASK_SUCCESS = "Added Task: %1$s to current project.";

    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in this project.";

    public final Task task;

    public AddTaskCommand(Task task) {
        requireNonNull(task);
        this.task = task;
    }


    @Override
    public CommandResult execute(Model model) throws CommandException {

        if (!model.isCheckedOut()) {
            throw new CommandException(model.checkoutConstrain());
        }

        Project projectToEdit = model.getWorkingProject().get();
        List<String> members = projectToEdit.getMembers();
        ArrayList<Task> taskArrayList = new ArrayList<>();
        List<Task> taskToEdit = projectToEdit.getTasks();
        taskArrayList.addAll(taskToEdit);
        taskArrayList.add(task);
        Collections.sort(taskArrayList, CURRENT_SORTING_ORDER_FOR_TASK);
        Project editedProject = new Project(projectToEdit.getTitle(), projectToEdit.getDescription(), taskArrayList, projectToEdit.getFinance());
        editedProject.getMembers().addAll(members);

        if (projectToEdit.hasTask(task)) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }


        model.setProject(projectToEdit, editedProject);
        model.updateFilteredProjectList(PREDICATE_SHOW_ALL_PROJECTS);
        return new CommandResult(String.format(MESSAGE_ADD_TASK_SUCCESS, task));
    }


    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddTaskCommand)) {
            return false;
        }

        // state check
        AddTaskCommand e = (AddTaskCommand) other;
        return task.equals(e.task);
    }
}
