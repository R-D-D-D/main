package seedu.address.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.*;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.util.SampleDataUtil;

import java.util.logging.Logger;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    private State currentState = State.PROJECT_LIST;
    private boolean onAddressBook = false;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private BudgetListPanel budgetListPanel;
    private ProjectListPanel projectListPanel;
    private ProjectOverview projectOverview;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private VBox projectList;

    @FXML
    private VBox budgetList;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane budgetListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane projectListPanelPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        projectListPanel = new ProjectListPanel(logic.getFilteredProjectList());
        projectListPanelPlaceholder.getChildren().add(projectListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public ProjectListPanel getProjectListPanel() {
        return projectListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, IllegalValueException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            String commandWord = commandResult.getCommandWord();
            State nextState = stateOf(commandWord);

            changeUiDisplay(nextState);

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | IllegalValueException e) {
            logger.info("Invalid command: " + commandText);
            logger.info(e.getMessage());
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    private enum State {
        ADDRESS_BOOK,
        PROJECT_LIST,
        PROJECT_OVERVIEW,
        PROJECT_FINANCE
    }

    private void changeUiDisplay(State nextState) {
        switch (nextState) {
        case ADDRESS_BOOK:
            personListPanel = new PersonListPanel(logic.getFilteredPersonList(), logic);
            projectListPanelPlaceholder.getChildren().setAll(personListPanel.getRoot());
            currentState = nextState;
            break;

        case PROJECT_LIST:
            projectListPanel = new ProjectListPanel(logic.getFilteredProjectList());
            projectListPanelPlaceholder.getChildren().setAll(projectListPanel.getRoot());
            currentState = nextState;
            break;

        case PROJECT_OVERVIEW:
            projectOverview = new ProjectOverview(logic.getFilteredProjectList(), logic.getWorkingProject().get());
            projectListPanelPlaceholder.getChildren().setAll(projectOverview.getRoot());
            currentState = nextState;
            break;

        case PROJECT_FINANCE:
            budgetListPanel = new BudgetListPanel(logic.getWorkingProject().get().getFinance().getBudgetObservableList());
            projectListPanelPlaceholder.getChildren().setAll(budgetListPanel.getRoot());
            currentState = nextState;
            break;

        default:
            assert false : "Unrecognised state";
        }
    }

    private State stateOf(String commandWord) {
        State state = State.PROJECT_LIST;
        switch (commandWord) {
        case AddProjectCommand.COMMAND_WORD:

        case RemoveMemberCommand.COMMAND_WORD:
            state = State.PROJECT_LIST;
            break;

        case AddBudgetCommand.COMMAND_WORD:
            state = State.PROJECT_OVERVIEW;

        case AddFromContactsCommand.COMMAND_WORD:

        case AddMemberCommand.COMMAND_WORD:

        case AddProjectMeetingCommand.COMMAND_WORD:

        case AddSpendingCommand.COMMAND_WORD:

        case AddTaskCommand.COMMAND_WORD:

        case GenerateSlotCommand.COMMAND_WORD:

        case CheckoutCommand.COMMAND_WORD:

        case DeleteTaskCommand.COMMAND_WORD:
            state = State.PROJECT_OVERVIEW;
            break;

        case ExitCommand.COMMAND_WORD:
            break;

        case ListBudgetCommand.COMMAND_WORD:
            state = State.PROJECT_FINANCE;
            break;

        case AddCommand.COMMAND_WORD:

        case AddProfilePictureCommand.COMMAND_WORD:

        case ClearCommand.COMMAND_WORD:

        case DeleteCommand.COMMAND_WORD:

        case EditCommand.COMMAND_WORD:

        case FindCommand.COMMAND_WORD:

        case HelpCommand.COMMAND_WORD:

        case ListCommand.COMMAND_WORD:

        case AddTimetableCommand.COMMAND_WORD:
            state = State.ADDRESS_BOOK;
            break;

        default:
            assert false : "Unrecognised Command";
        }

        return state;
    }
}
