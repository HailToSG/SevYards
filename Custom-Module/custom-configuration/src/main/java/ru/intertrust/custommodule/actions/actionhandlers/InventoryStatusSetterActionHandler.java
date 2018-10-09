package ru.intertrust.custommodule.actions.actionhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.*;
import ru.intertrust.cm.core.dao.access.AccessControlService;
import ru.intertrust.cm.core.dao.access.AccessToken;
import ru.intertrust.cm.core.dao.api.DomainObjectDao;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.action.ActionContext;
import ru.intertrust.cm.core.gui.model.action.ActionData;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import java.util.ArrayList;
import java.util.List;

@ComponentName("inventory.action")
public class InventoryStatusSetterActionHandler extends ActionHandler<ActionContext, ActionData> {

    @Autowired
    private CollectionsService collectionsService;

    @Autowired
    private CrudService crudService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private DomainObjectDao domainObjectService;

    @Override
    public ActionData executeAction(ActionContext context) {
        StatusSetter statusSetter = new StatusSetter();
        Id rootObjectId = context.getRootObjectId();
        SimpleActionData actionData = new SimpleActionData();
        if (rootObjectId != null) {
            DomainObject rootDomainObject = crudService.find(rootObjectId);
            String actualStatusName = statusSetter.getQueryStatusById(rootDomainObject.getStatus(), collectionsService);

            if (actualStatusName.equals(CustomModuleConstants.STATUS_DRAFT)) {
                statusSetter.changeStatusForDo(rootDomainObject.getId(), CustomModuleConstants.STATUS_FINISHED,
                        collectionsService, accessControlService, domainObjectService);
            }
            if (actualStatusName.equals(CustomModuleConstants.STATUS_FINISHED)) {
                statusSetter.changeStatusForDo(rootDomainObject.getId(), CustomModuleConstants.STATUS_DRAFT,
                        collectionsService, accessControlService, domainObjectService);
            }
        }

        return actionData;
    }
}
