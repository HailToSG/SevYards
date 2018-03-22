package ru.intertrust.custommodule.gui.client;

import ru.intertrust.cm.core.gui.impl.client.BusinessUniverse;
import ru.intertrust.cm.core.gui.model.ComponentName;

/**
 * @author Denis Mitavskiy
 *         Date: 12.03.14
 *         Time: 16:10
 */
@ComponentName("custom.b.u")
public class CustomBusinessUniverse extends BusinessUniverse {
    @Override
    public void onModuleLoad() {
        // ((CustomPlugin) ComponentRegistry.instance.get("custom.plugin")).alert();
        super.onModuleLoad();
    }
}
