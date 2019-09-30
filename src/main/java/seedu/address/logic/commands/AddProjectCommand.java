package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Project;

import static java.util.Objects.requireNonNull;

public class AddProjectCommand extends Command {
    public static final String COMMAND_WORD = "add_project";

    public static final String MESSAGE_SUCCESS = "New project added: %1$s";

    private final Project toAdd;

    public AddProjectCommand(Project project) {
        requireNonNull(project);
        toAdd = project;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        model.addProject(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }
}
