package ru.intertrust.custommodule.gui.client.plugin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import ru.intertrust.cm.core.config.gui.navigation.AttributeConfig;
import ru.intertrust.cm.core.config.gui.navigation.CustomPluginConfig;
import ru.intertrust.cm.core.gui.impl.client.Plugin;
import ru.intertrust.cm.core.gui.impl.client.PluginView;

/**
 * @author Denis Mitavskiy
 *         Date: 23.08.13
 *         Time: 15:28
 */
public class CustomPluginView extends PluginView {
    private final AttributeConfig attributeConfig;

    public CustomPluginView(Plugin plugin) {
        super(plugin);
        attributeConfig = ((CustomPluginConfig) plugin.getConfig()).getAttributeConfigList().get(0);
    }

    @Override
    public IsWidget getViewWidget() {
        return new Button(attributeConfig.getName() + " ==> " + attributeConfig.getValue());
    }
}
