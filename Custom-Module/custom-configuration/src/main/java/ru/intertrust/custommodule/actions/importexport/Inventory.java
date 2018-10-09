package ru.intertrust.custommodule.actions.importexport;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

public class Inventory {

    private String id;
    private int kind, summaryArea, facadeState, territoryState;
    private List<LinkedTreeMap<String, String>> territory_photos;
    private List<LinkedTreeMap<String, ?>> allLinkedObjects;

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getSummaryArea() {
        return summaryArea;
    }

    public void setSummaryArea(int summaryArea) {
        this.summaryArea = summaryArea;
    }

    public int getFacadeState() {
        return facadeState;
    }

    public void setFacadeState(int facadeState) {
        this.facadeState = facadeState;
    }

    public int getTerritoryState() {
        return territoryState;
    }

    public void setTerritoryState(int territoryState) {
        this.territoryState = territoryState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

        public List<LinkedTreeMap<String, String>> getTerritoryPhotos() {
            return territory_photos;
        }

        public void setTerritoryPhotos(List<LinkedTreeMap<String, String>> territory_photos) {
            this.territory_photos = territory_photos;
        }

        public List<LinkedTreeMap<String, ?>> getAllLinkedObjects() {
            return allLinkedObjects;
        }

        public void setAllLinkedObjects(List<LinkedTreeMap<String, ?>> allLinkedObjects) {
            this.allLinkedObjects = allLinkedObjects;
        }
}
