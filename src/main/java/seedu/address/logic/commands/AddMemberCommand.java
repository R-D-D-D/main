package seedu.address.logic.commands;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.*;
import seedu.address.model.project.Project;
import seedu.address.model.tag.Tag;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.*;

public class AddMemberCommand extends Command {

    public static final String COMMAND_WORD = "addMember";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the working project "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_PHONE + "PHONE " + "]"
            + "[" + PREFIX_EMAIL + "EMAIL " + "]"
            + "[" + PREFIX_ADDRESS + "ADDRESS " + "]"
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "Co-Team Leader ";

    public static final String MESSAGE_SUCCESS = "New member added to %2$s project and address book: %1$s";
    public static final String MESSAGE_SUCCESS_MISSING_FIELDS = MESSAGE_SUCCESS + " (Please remember to fill in remaining information for member)";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the project";
    public static final String MESSAGE_WRONG_ADD_COMMAND = "This person already exists in the address book, "
            + "please use addFromContacts instead.";
    public static final String MESSAGE_MISSING_FIELDS = "Please remember to fill in the proper fields later.";
    private final NewMemberDescriptor toAdd;

    /**
     * Creates an AddMemberCommand to add the specified {@code Person}
     */
    public AddMemberCommand(NewMemberDescriptor newMemberDescriptor) {
        requireNonNull(newMemberDescriptor);

        this.toAdd = newMemberDescriptor;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Project projectToEdit = model.getWorkingProject().get();
        String projectToEditTitle = projectToEdit.getTitle().toString();

        Person personToAdd = createNewMember(toAdd);
        personToAdd.getProjects().add(projectToEditTitle);
        List<String> memberListToEdit = projectToEdit.getMembers();
        List<String> editedMemberList = new ArrayList<>();

        if (model.hasPerson(personToAdd)) {
            throw new CommandException(MESSAGE_WRONG_ADD_COMMAND);
        }

        if (projectToEdit.hasMember(personToAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        //Adding person to address book and project
        model.addPerson(personToAdd);
        editedMemberList.addAll(memberListToEdit);
        editedMemberList.add(personToAdd.getName().toString());

        Project editedProject = new Project(projectToEdit.getTitle(), projectToEdit.getDescription(),
                editedMemberList, projectToEdit.getTasks(), projectToEdit.getFinance());

        model.setProject(projectToEdit, editedProject);
        model.setWorkingProject(editedProject);

        if (toAdd.isAnyFieldNotEdited()) {
            return new CommandResult(String.format(MESSAGE_MISSING_FIELDS, personToAdd.getName().toString(), projectToEdit), COMMAND_WORD);
        } else {
            return new CommandResult(String.format(MESSAGE_SUCCESS, personToAdd.getName().toString(), projectToEdit), COMMAND_WORD);
        }
    }

    private static Person createNewMember(NewMemberDescriptor newMemberDescriptor) {
        Name name = newMemberDescriptor.getName();
        Phone phone = newMemberDescriptor.getPhone();
        Email email = newMemberDescriptor.getEmail();
        Address address = newMemberDescriptor.getAddress();
        Set<Tag> tags;
        if (newMemberDescriptor.getTags().isEmpty()) {
            tags = new HashSet<Tag>();
        } else {
            tags = newMemberDescriptor.getTags().get();
        }

        return new Person(name, phone, email, address, tags);
    }

    public static class NewMemberDescriptor {
        private Name name;
        private Phone phone = new Phone("00000000");
        private Email email = new Email("no_email@added");
        private Address address = new Address("-none-");
        private Set<Tag> tags;

        public NewMemberDescriptor() {}

        public NewMemberDescriptor(NewMemberDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldNotEdited() {
            return CollectionUtil.isAnyNull(name, phone, email, address);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Name getName() {
            return name;
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Phone getPhone() {
            return phone;
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Email getEmail() {
            return email;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof NewMemberDescriptor)) {
                return false;
            }

            // state check
            NewMemberDescriptor e = (NewMemberDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getTags().equals(e.getTags());
        }
    }


}
