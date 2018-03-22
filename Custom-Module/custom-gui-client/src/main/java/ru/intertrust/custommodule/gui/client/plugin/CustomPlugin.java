package ru.intertrust.custommodule.gui.client.plugin;

import com.google.gwt.user.client.Window;
import ru.intertrust.cm.core.gui.api.client.Component;
import ru.intertrust.cm.core.gui.impl.client.Plugin;
import ru.intertrust.cm.core.gui.impl.client.PluginView;
import ru.intertrust.cm.core.gui.model.ComponentName;

/**
 * @author Denis Mitavskiy
 *         Date: 13.08.13
 *         Time: 18:43
 */
@ComponentName("custom.plugin")
public class CustomPlugin extends Plugin {

    @Override
    public PluginView createView() {
        return new CustomPluginView(this);
    }

    @Override
    public Component createNew() {
        return new CustomPlugin();
    }

    public void alert() {
        Window.alert("ALERT FROM CUSTOM");
    }
}
