package ru.intertrust.custommodule.gui.client.action;

import com.google.gwt.user.client.Window;
import ru.intertrust.cm.core.gui.api.client.Component;
import ru.intertrust.cm.core.gui.impl.client.action.Action;
import ru.intertrust.cm.core.gui.model.ComponentName;

@ComponentName("test.action")
public class TestAction extends Action {
    @Override
    protected void execute() {
        Window.alert("Test!!");
    }

    @Override
    public Component createNew() {
        return new TestAction();
    }
}
