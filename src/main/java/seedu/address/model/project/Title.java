package seedu.address.model.project;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

public class Title {

    public final String title;

    public Title(String title) {
        requireNonNull(title);
        this.title = title;
    }
}
