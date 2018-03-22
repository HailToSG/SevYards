package ru.intertrust.custommodule.gui.server.plugin;

import ru.intertrust.cm.core.business.api.dto.Dto;
import ru.intertrust.cm.core.gui.api.server.plugin.PluginHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.plugin.PluginData;
import ru.intertrust.custommodule.gui.model.client.CustomPluginData;

/**
 * @author Denis Mitavskiy
 *         Date: 23.08.13
 *         Time: 13:22
 */
@ComponentName("custom.plugin")
public class CustomPluginHandler extends PluginHandler {
    @Override
    public PluginData initialize(Dto param) {
        System.out.println("CustomPluginHandler initialized");
        return new CustomPluginData();
    }

    public PluginData doSomethingGood(Dto dto) {
        System.out.println("CustomPluginHandler executed doSomethingGood()");
        return new CustomPluginData();
    }

}
