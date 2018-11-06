package ru.intertrust.custommodule.actions.importexport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.healthmarketscience.rmiio.DirectRemoteInputStream;
import ru.intertrust.cm.core.business.api.AttachmentService;
import ru.intertrust.cm.core.business.api.CollectionsService;
import ru.intertrust.cm.core.business.api.CrudService;
import ru.intertrust.cm.core.business.api.dto.DomainObject;
import ru.intertrust.cm.core.business.api.dto.GenericDomainObject;
import ru.intertrust.cm.core.business.api.dto.Id;
import ru.intertrust.cm.core.business.api.dto.impl.RdbmsId;
import ru.intertrust.cm.core.gui.api.server.action.ActionHandler;
import ru.intertrust.cm.core.gui.model.ComponentName;
import ru.intertrust.cm.core.gui.model.GuiException;
import ru.intertrust.cm.core.gui.model.action.SimpleActionContext;
import ru.intertrust.cm.core.gui.model.action.SimpleActionData;
import org.springframework.beans.factory.annotation.*;
import ru.intertrust.custommodule.actions.actionhandlers.StatusSetter;
import ru.intertrust.custommodule.actions.constants.CustomModuleConstants;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;


@ComponentName("inventory.json.loader")
public class InventoryJsonLoaderActionHandler extends ActionHandler<SimpleActionContext, SimpleActionData> {

    private static String outPath;
    private static DomainObject doObject;

    @Inject
    private AttachmentService attachmentService;

    @Inject
    private CrudService crudService;

    @Inject
    private CollectionsService collectionService;

    @Value("${attachment.storage}")
    private String attachmentSaveLocation;

    @Override
    public SimpleActionData executeAction(SimpleActionContext context) {
        Unzipper unzipper = new Unzipper();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();
        Id inventoryDoId = context.getRootObjectId();
        List<DomainObject> attachmentsList = attachmentService
                .findAttachmentDomainObjectsFor(inventoryDoId, CustomModuleConstants.INVENTORY_JSON_ATTACHMENTS_FIELD);

        if (attachmentsList.isEmpty())
            throw new GuiException("Вложение не должно быть пустым!");
        else {
            DomainObject attachmentDO = attachmentsList.get(0);
            String filePath = attachmentSaveLocation + "\\" + attachmentDO.getString(
                    CustomModuleConstants.ATTACHMENT_PATH_FIELD);
            String attachName = attachmentDO.getString(CustomModuleConstants.ATTACHMENT_NAME_FIELD);
            outPath = unzipper.unZip(filePath, attachName);

            doObject = crudService.createDomainObject("inventory");
            String jsonFile = outPath + CustomModuleConstants.JSON_FILE_NAME;

            parseJson(jsonFile, gson, inventoryDoId);

            attachmentService.deleteAttachment(attachmentDO.getId());
            return new SimpleActionData();
        }


    }

    private void parseJson(String jsonFile, Gson gson, Id inventoryDoId) {
        try {
            JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
            Id inventoryId = new RdbmsId();
            DomainObject inventoryDo;
            List<Territory> territories = jsonObject.getTerritories();
            for (Territory terr : territories) {
                String invIdString = terr.getInventory().getId();

                if (!invIdString.equals("0") && invIdString.length() > 15)
                    inventoryId.setFromStringRepresentation(terr.getInventory().getId());

                List<LinkedTreeMap<String, String>> photoList = terr.getInventory().getTerritoryPhotos();
                inventoryDo = crudService.find(inventoryId);
                parseInventoryWithLinkedObjects(inventoryDo, terr);

                if (!(photoList == null) && !(photoList.isEmpty()))
                    parseAttachments(photoList, CustomModuleConstants.INVENTORY_PHOTO_ATTACHMENTS_FIELD,
                            inventoryDoId, CustomModuleConstants.INVENTORY_FIELD);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parseInventoryWithLinkedObjects(DomainObject inventoryDo, Territory territory) {
        boolean isDoSaved;
        boolean isNew;
        boolean hasPhotos;
        String doType;
        String doIdString;
        String defaultIdString = "5089000000000000";
        Id doId = new RdbmsId();
        List<LinkedTreeMap<String, ?>> linkedObjectList = territory.getInventory().getAllLinkedObjects();

        if (!linkedObjectList.isEmpty())
            for (LinkedTreeMap<String, ?> linkedObject : linkedObjectList) {
                StatusSetter statusSetter = new StatusSetter();
                isDoSaved = false;
                isNew = true;
                String photoField = null;
                doType = (String) linkedObject.remove("do_type");

                doIdString = (String) linkedObject.remove("id");
                if (!doIdString.equals("0") && doIdString.length() > 15) {
                    doId.setFromStringRepresentation(doIdString);
                } else
                    doId.setFromStringRepresentation(defaultIdString);

                if (!crudService.exists(doId)) {
                    doObject = crudService.createDomainObject(doType);
                    doObject.setReference("inventory_id", inventoryDo.getId());
                } else {
                    doObject = crudService.find(doId);
                    isDoSaved = true;
                    isNew = false;
                }
                Timestamp jsonTimeStamp = Timestamp.valueOf((String) linkedObject.remove("updated_date"));
                if (!isNew) {
                    Timestamp modifiedDate = new Timestamp(doObject.getModifiedDate().getTime());
                    if (modifiedDate.after(jsonTimeStamp)) {
                        continue;
                    }
                }

                if (linkedObject.get("status") != null) {
                    if (linkedObject.remove("status").equals("Deleted")) {
                        doObject.setReference("status", statusSetter.getStatusIdByName(
                                CustomModuleConstants.STATUS_DELETED, collectionService));
                        continue;
                    }
                }
                for (String s : linkedObject.keySet()) {
                    if (s.contains("_photo")) {
                        photoField = s;
                    }
                }

                if (!linkedObject.isEmpty())
                    for (Map.Entry<String, ?> entry : linkedObject.entrySet()) {
                        if (!(entry.getKey().contains("_photo"))) {
                            if (entry.getKey().equals("can_capacity")) {
                                doObject.setDecimal(entry.getKey(), new BigDecimal((Double) entry.getValue())
                                        .setScale(1, BigDecimal.ROUND_HALF_UP));
                                continue;
                            }

                            switch ((entry.getValue()).getClass().getName()) {
                                case "java.lang.Double":
                                    doObject.setLong(entry.getKey(), ((Double) entry.getValue()).longValue());
                                    break;
                                case "java.lang.Boolean":
                                    doObject.setBoolean(entry.getKey(), (Boolean) entry.getValue());
                                    break;
                                default:
                                    doObject.setString(entry.getKey(), (String) entry.getValue());
                                    break;
                            }
                            continue;
                        }
                        if (!isDoSaved) {
                            doObject = crudService.save(doObject);
                                parseAttachments((List<LinkedTreeMap<String, String>>) linkedObject.get(photoField),
                                        photoField, doObject.getId(), doType);
                        }
                        else {
                                parseAttachments((List<LinkedTreeMap<String, String>>) linkedObject.get(photoField),
                                        photoField, doObject.getId(), doType);
                        }
                    }
            }
    }

    private void parseAttachments(List<LinkedTreeMap<String, String>> attachmentList,
                                  String attachType, Id doReference, String doType) {
        DomainObject attachDo;
        Id attachId = new RdbmsId();
        String attachName;
        String doIdString;
        String defaultIdString = "5089000000000000";
        if (!attachmentList.isEmpty())
            for (LinkedTreeMap<String, String> attachmentTreeMap : attachmentList) {
                doIdString = attachmentTreeMap.remove("id");
                if (doIdString.equals("0") || doIdString.length() <= 15)
                    attachId.setFromStringRepresentation(defaultIdString);
                else
                    attachId.setFromStringRepresentation(doIdString);

                if (!(crudService.exists(attachId)))
                    attachDo = attachmentService.createAttachmentDomainObjectFor(doReference, attachType);
                else
                    attachDo = crudService.find(attachId);

                Timestamp jsonTimestamp = Timestamp.valueOf(attachmentTreeMap.remove("updated_date"));
                Timestamp modifiedDate = new Timestamp(attachDo.getModifiedDate().getTime());

                if (!attachDo.isNew() && jsonTimestamp.after(modifiedDate)) {
                    continue;
                }

                attachName = attachmentTreeMap.get("name");
                attachDo.setString(CustomModuleConstants.ATTACHMENT_NAME_FIELD, attachName);
                attachDo.setString(CustomModuleConstants.ATTACHMENT_MIME_TYPE_FIELD, "image/jpeg");
                attachDo.setReference(doType, doReference);
                saveAttachDo(attachDo, attachName);
            }
    }

    private void saveAttachDo(DomainObject attachDo, String attachName) {
        try {
            DirectRemoteInputStream directRemoteInputStream = new DirectRemoteInputStream(
                    new FileInputStream(outPath + attachName), false);
            attachmentService.saveAttachment(directRemoteInputStream, attachDo);
        } catch (FileNotFoundException e) {
            e.getStackTrace();
        }

    }
}

