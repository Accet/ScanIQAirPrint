///*
// * (C) Copyright 2016 HP Development Company, L.P.
// * All Rights Reserved Worldwide
// * 08/18/2016 SMKAB
// *
// * ScanSettingsHelper.java
// */
//
//package helperClasses;
//
//import android.graphics.Rect;
//
//import com.hp.mobile.scan.sdk.model.Resolution;
//import com.hp.mobile.scan.sdk.model.ResolutionCapability;
//import com.hp.mobile.scan.sdk.model.ScanTicket;
//import com.hp.mobile.scan.sdk.model.ScanValues;
//import com.hp.mobile.scan.sdk.model.ScannerCapabilities;
//import com.hp.mobile.scan.sdk.model.Size;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ScanSettingsHelper {
//
//    private static final Map<Integer, String> sDocumentFormatMap = new LinkedHashMap<>();
//
//    static {
//        sDocumentFormatMap.put(ScanValues.DOCUMENT_FORMAT_RAW, "Raw");
//        sDocumentFormatMap.put(ScanValues.DOCUMENT_FORMAT_JPEG, "JPEG");
//        sDocumentFormatMap.put(ScanValues.DOCUMENT_FORMAT_PDF, "PDF");
//    }
//
//    private static final Map<Integer, String> sColorModeMap = new LinkedHashMap<>();
//
//    static {
//        sColorModeMap.put(ScanValues.COLOR_MODE_BLACK_AND_WHITE, "Black and white");
//        sColorModeMap.put(ScanValues.COLOR_MODE_GRAYSCALE_8, "Gray (8)");
//        sColorModeMap.put(ScanValues.COLOR_MODE_GRAYSCALE_16, "Gray (16)");
//        sColorModeMap.put(ScanValues.COLOR_MODE_RGB_24, "Color (rgb24)");
//        sColorModeMap.put(ScanValues.COLOR_MODE_RGB_48, "Color (rgb48)");
//    }
//
//    private static final Map<Integer, String> sIntentMap = new LinkedHashMap<>();
//
//    static {
//        sIntentMap.put(ScanValues.INTENT_DOCUMENT, "Document");
//        sIntentMap.put(ScanValues.INTENT_TEXT_AND_GRAPHIC, "TextAndGraphic");
//        sIntentMap.put(ScanValues.INTENT_PHOTO, "Photo");
//        sIntentMap.put(ScanValues.INTENT_PREVIEW, "Preview");
//        sIntentMap.put(ScanValues.INTENT_OBJECT, "Object");
//    }
//
//    private static final Map<Integer, String> sContentTypeMap = new LinkedHashMap<>();
//
//    static {
//        sContentTypeMap.put(ScanValues.CONTENT_TYPE_AUTO, "Auto");
//        sContentTypeMap.put(ScanValues.CONTENT_TYPE_TEXT, "Text");
//        sContentTypeMap.put(ScanValues.CONTENT_TYPE_PHOTO, "Photo");
//        sContentTypeMap.put(ScanValues.CONTENT_TYPE_TEXT_AND_PHOTO, "Text and Photo");
//        sContentTypeMap.put(ScanValues.CONTENT_TYPE_LINE_ART, "Line art");
//        sContentTypeMap.put(ScanValues.CONTENT_TYPE_MAGAZINE, "Magazine");
//        sContentTypeMap.put(ScanValues.CONTENT_TYPE_HALFTONE, "Halftone");
//    }
//
//    private static final Map<String, Rect> sDefaultScanAreas = new LinkedHashMap<>();
//
//    static {
//        // values 1/300 of inch
//        sDefaultScanAreas.put("US Legal", new Rect(0, 0, (int) (8.5 * 300), 14 * 300));
//        sDefaultScanAreas.put("US Letter", new Rect(0, 0, (int) (8.5 * 300), 11 * 300));
//        sDefaultScanAreas.put("A4", new Rect(0, 0, (int) (8.27 * 300), (int) (11.69 * 300)));
//        sDefaultScanAreas.put("4x6", new Rect(0, 0, 4 * 300, 6 * 300));
//    }
//
//    @SuppressWarnings("unchecked")
//    public static ScanSettings createSettingsFromCapabilities(
//            ScannerCapabilities aScannerCapabilities) {
//        List<Setting> thePlatenSettings = null;
//        List<Setting> theCameraSettings = null;
//        List<Setting> theAdfSimplexSettings = null;
//        List<Setting> theAdfDuplexSettings = null;
//        List<Setting> theGeneralSettings = new ArrayList<>();
//        BooleanSetting theDuplexSetting = null;
//
//        List<String> theInputSourceNames = new ArrayList<>();
//        List<Integer> theInputSourceValues = new ArrayList<>();
//        if (aScannerCapabilities != null) {
//            Map<String, Object> thePlaten = (Map<String, Object>) aScannerCapabilities.get(
//                    ScannerCapabilities.SCANNER_CAPABILITY_IS_PLATEN);
//            Map<String, Object> theAdfSimplex = (Map<String, Object>) aScannerCapabilities.get(
//                    ScannerCapabilities.SCANNER_CAPABILITY_IS_ADF_SIMPLEX);
//            Map<String, Object> theAdfDuplex = (Map<String, Object>) aScannerCapabilities.get(
//                    ScannerCapabilities.SCANNER_CAPABILITY_IS_ADF_DUPLEX);
//            Map<String, Object> theCamera = (Map<String, Object>) aScannerCapabilities.get(
//                    ScannerCapabilities.SCANNER_CAPABILITY_IS_CAMERA);
//
//            if (thePlaten != null) {
//                theInputSourceNames.add("Platen");
//                theInputSourceValues.add(ScanValues.INPUT_SOURCE_PLATEN);
//                thePlatenSettings = createInputSourceSettings(thePlaten);
//            }
//            if (theAdfSimplex != null && theAdfDuplex != null) {
//                theDuplexSetting = new BooleanSetting(ScanTicket.SCAN_SETTING_DUPLEX, "Duplex");
//            }
//            if (theAdfSimplex != null || theAdfDuplex != null) {
//                theInputSourceNames.add("Feeder");
//                theInputSourceValues.add(ScanValues.INPUT_SOURCE_ADF);
//            }
//            if (theAdfSimplex != null) {
//                theAdfSimplexSettings = createInputSourceSettings(theAdfSimplex);
//                if (theDuplexSetting != null) {
//                    theAdfSimplexSettings.add(theDuplexSetting);
//                }
//            }
//            if (theAdfDuplex != null) {
//                theAdfDuplexSettings = createInputSourceSettings(theAdfDuplex);
//                if (theDuplexSetting != null) {
//                    theAdfDuplexSettings.add(theDuplexSetting);
//                }
//            }
//            if (theCamera != null) {
//                theInputSourceNames.add("Camera");
//                theInputSourceValues.add(ScanValues.INPUT_SOURCE_CAMERA);
//                theCameraSettings = createInputSourceSettings(theCamera);
//            }
//            if (theInputSourceNames.size() > 1) {
//                theInputSourceNames.add(0, "Auto");
//                theInputSourceValues.add(0, ScanValues.INPUT_SOURCE_AUTO);
//            }
//        }
//        ChoiceSetting<Integer> theInputSourceSetting = new ChoiceSetting<>(
//                ScanTicket.SCAN_TICKET_INPUT_SOURCE_KEY, "Input Source",
//                theInputSourceNames, theInputSourceValues);
//        if (theInputSourceValues.size() > 1) {
//            theInputSourceSetting.setValue(ScanValues.INPUT_SOURCE_AUTO);
//        } else {
//            theInputSourceSetting.setValueIndex(0);
//        }
//
//        return new ScanSettings(theInputSourceSetting,
//                thePlatenSettings, theCameraSettings, theAdfSimplexSettings, theAdfDuplexSettings,
//                theDuplexSetting, theGeneralSettings);
//    }
//
//    @SuppressWarnings("unchecked")
//    private static List<Setting> createInputSourceSettings(Map<String, Object>
//            aInputSourceCapabilities) {
//        List<Setting> theSettings = new ArrayList<>();
//
//        Collection<Integer> theIntents = (Collection<Integer>) aInputSourceCapabilities.get(
//                ScannerCapabilities.SOURCE_CAPABILITY_INTENTS);
//        if (Utils.isNotEmpty(theIntents)) {
//            theSettings.add(createChoiceSetting(ScanTicket.SCAN_SETTING_INTENT, "Intent",
//                    theIntents, sIntentMap, null, true));
//        }
//
//        Collection<Integer> theDocumentFormats = (Collection<Integer>)
//                aInputSourceCapabilities.get(ScannerCapabilities.SOURCE_CAPABILITY_FORMATS);
//        if (Utils.isNotEmpty(theDocumentFormats)) {
//            theSettings.add(createChoiceSetting(ScanTicket.SCAN_SETTING_FORMAT, "Document Format",
//                    theDocumentFormats, sDocumentFormatMap, null, true));
//        }
//
//        Collection<Integer> theColorModes = (Collection<Integer>) aInputSourceCapabilities.get(
//                ScannerCapabilities.SOURCE_CAPABILITY_COLOR_MODES);
//        if (Utils.isNotEmpty(theColorModes)) {
//            theSettings.add(createChoiceSetting(ScanTicket.SCAN_SETTING_COLOR_MODE, "Color",
//                    theColorModes, sColorModeMap, null, true));
//        }
//
//        Collection<Integer> theContentTypes = (Collection<Integer>) aInputSourceCapabilities.get(
//                ScannerCapabilities.SOURCE_CAPABILITY_CONTENT_TYPES);
//        if (Utils.isNotEmpty(theContentTypes)) {
//            theSettings.add(createChoiceSetting(ScanTicket.SCAN_SETTING_CONTENT_TYPE,
//                    "Content type", theContentTypes, sContentTypeMap, null, true));
//        }
//
//        ResolutionCapability theResolutionCapability = (ResolutionCapability)
//                aInputSourceCapabilities.get(ScannerCapabilities.SOURCE_CAPABILITY_RESOLUTIONS);
//        Collection<Resolution> theDiscreteResolutions = theResolutionCapability != null
//                ? theResolutionCapability.getDiscreteResolutions()
//                : null;
//        if (Utils.isNotEmpty(theDiscreteResolutions)) {
//            List<Resolution> theSortedResolutions = new ArrayList<>(theDiscreteResolutions);
//            Collections.sort(theSortedResolutions, new Comparator<Resolution>() {
//                @Override
//                public int compare(Resolution lhs, Resolution rhs) {
//                    int theResult = Integer.compare(lhs.getHorizontal(), rhs.getHorizontal());
//                    return theResult != 0
//                            ? theResult
//                            : Integer.compare(lhs.getVertical(), rhs.getVertical());
//                }
//            });
//
//            List<String> theResolutionsNames = new ArrayList<>();
//            for (Resolution theResolution : theSortedResolutions) {
//                if (theResolution.getHorizontal() == theResolution.getVertical()) {
//                    theResolutionsNames.add(String.valueOf(theResolution.getHorizontal()));
//                } else {
//                    theResolutionsNames.add(theResolution.getHorizontal() + "x"
//                            + theResolution.getVertical());
//                }
//            }
//            theResolutionsNames.add(0, "Unspecified");
//            theSortedResolutions.add(0, null);
//            ChoiceSetting<Resolution> theResolutionSetting = new ChoiceSetting<>(
//                    ScanTicket.SCAN_SETTING_RESOLUTION, "Resolution", theResolutionsNames,
//                    theSortedResolutions);
//            theResolutionSetting.setValue(null);
//            theSettings.add(theResolutionSetting);
//        }
//
//        Size theMinArea = (Size) aInputSourceCapabilities.get(
//                ScannerCapabilities.SOURCE_CAPABILITY_MIN_SCAN_AREA);
//        Size theMaxArea = (Size) aInputSourceCapabilities.get(
//                ScannerCapabilities.SOURCE_CAPABILITY_MAX_SCAN_AREA);
//        if (theMinArea != null && theMaxArea != null) {
//            Rect theSupportedRange = new Rect(0, 0, theMaxArea.getWidth(), theMaxArea.getHeight());
//            // allows to set only one scan area
//            ScanAreaSetting theScanAreaSetting = new ScanAreaSetting(
//                    ScanTicket.SCAN_SETTING_SCAN_AREAS, "Scan area",
//                    theSupportedRange, theMinArea.getWidth(), theMinArea.getHeight());
//            theSettings.add(theScanAreaSetting);
//            theSettings.add(new BooleanSetting(ScanTicket.SCAN_SETTING_MUST_HONOR_SCAN_AREAS,
//                    "Must honor scan area"));
//        }
//        return theSettings;
//    }
//
//    private static <T> ChoiceSetting<T> createChoiceSetting(String aKey, String aName,
//            Collection<T> aValues, Map<T, String> aValueNameMap, T aDefaultValue,
//            boolean aAddUnspecifiedValue) {
//        List<String> theNames = new ArrayList<>();
//        List<T> theValues = new ArrayList<>();
//        if (aValueNameMap == null) {
//            for (T theValue : aValues) {
//                theValues.add(theValue);
//                theNames.add(String.valueOf(theValue));
//            }
//        } else {
//            // to keep map order, iterates over its entries
//            for (Map.Entry<T, String> theEntry : aValueNameMap.entrySet()) {
//                if (aValues.contains(theEntry.getKey())) {
//                    theValues.add(theEntry.getKey());
//                    theNames.add(theEntry.getValue());
//                }
//            }
//        }
//        if (aAddUnspecifiedValue) {
//            theNames.add(0, "Unspecified");
//            theValues.add(0, null);
//        }
//        ChoiceSetting<T> theChoiceSetting = new ChoiceSetting<>(aKey, aName, theNames, theValues);
//        theChoiceSetting.setValue(aDefaultValue);
//        return theChoiceSetting;
//    }
//
//    public static void updateSettings(ScanTicket aScanTicket,
//            ChoiceSetting<Integer> aInputSourceChoiceSetting,
//            List<Setting> aInputSourceSettings,
//            List<Setting> aGeneralSettings) {
//        if (aScanTicket == null) {
//            return;
//        }
//        if (aInputSourceChoiceSetting == null) {
//            return;
//        }
//        int theInputSource = aScanTicket.getInputSource();
//        aInputSourceChoiceSetting.setValue(theInputSource);
//
//        if (aInputSourceSettings == null) {
//            return;
//        }
//
//        updateSettings(aScanTicket, aInputSourceSettings);
//        updateSettings(aScanTicket, aGeneralSettings);
//    }
//
//    public static void updateSettings(ScanTicket aScanTicket, List<Setting> aSettings) {
//        if (aScanTicket == null || aSettings == null) {
//            return;
//        }
//        for (Setting theSetting : aSettings) {
//            Object theValue = aScanTicket.getSetting(theSetting.getKey());
//            try {
//                if (ScanTicket.SCAN_SETTING_SCAN_AREAS.equals(theSetting.getKey())) {
//                    Collection theCollection = (Collection) theValue;
//                    if (Utils.isNotEmpty(theCollection)) {
//                        theValue = theCollection.iterator().next();
//                    } else {
//                        theValue = null;
//                    }
//                }
//                //noinspection unchecked
//                theSetting.setValue(theValue);
//            } catch (ClassCastException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static ScanTicket createScanTicket(List<Setting> aSettings) {
//        @SuppressWarnings("unchecked")
//        Setting<Integer> theInputSourceSetting = findSettingByKey(aSettings, ScanTicket
//                .SCAN_TICKET_INPUT_SOURCE_KEY);
//        return createScanTicket(theInputSourceSetting, aSettings, null);
//    }
//
//    public static ScanTicket createScanTicket(Setting<Integer> aInputSourceChoiceSetting,
//                                              List<Setting> aInputSourceSettings, List<Setting> aGeneralSettings) {
//        if (aInputSourceChoiceSetting == null) {
//            return null;
//        }
//        Integer theInputSourceValue = aInputSourceChoiceSetting.getValue();
//        if (theInputSourceValue == null) {
//            theInputSourceValue = ScanValues.INPUT_SOURCE_AUTO;
//        }
//
//        Map<String, Object> theScanTicketSettings = new HashMap<>();
//        putValuesToScanTicket(theScanTicketSettings, aInputSourceSettings);
//
//        putValuesToScanTicket(theScanTicketSettings, aGeneralSettings);
//
//        return new ScanTicket("Custom", theInputSourceValue, theScanTicketSettings);
//    }
//
//    private static void putValuesToScanTicket(Map<String, Object> aScanTicketSettings,
//            List<Setting> aSettings) {
//        if (aScanTicketSettings == null || aSettings == null) {
//            return;
//        }
//        // assume all settings have keys and values from ScanTicket
//        for (Setting theSetting : aSettings) {
//            Object theValue = theSetting.getValue();
//            if (ScanTicket.SCAN_SETTING_SCAN_AREAS.equals(theSetting.getKey())) {
//                theValue = theValue != null ? Collections.singletonList(theValue) : null;
//            }
//            aScanTicketSettings.put(theSetting.getKey(), theValue);
//        }
//    }
//
//    public static void updateSettings(List<Setting> aDestinationSettings, List<Setting>
//            aSourceSetting) {
//        if (Utils.isEmpty(aDestinationSettings) || Utils.isEmpty(aSourceSetting)) {
//            return;
//        }
//        Map<String, Setting> mSourceSettingsMap = new HashMap<>();
//        for (Setting theSetting : aSourceSetting) {
//            mSourceSettingsMap.put(theSetting.getKey(), theSetting);
//        }
//        for (Setting theDestinationSetting : aDestinationSettings) {
//            Setting theSourceSetting = mSourceSettingsMap.get(theDestinationSetting.getKey());
//            if (theSourceSetting != null) {
//                try {
//                    //noinspection unchecked
//                    theDestinationSetting.setValue(theSourceSetting.getValue());
//                } catch (ClassCastException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public static Map<String, Rect> createPredefinedAreas(ScanAreaSetting aScanAreaSetting) {
//        Rect theSupportedRange = new Rect(aScanAreaSetting.getMinX(),
//                aScanAreaSetting.getMinY(), aScanAreaSetting.getMaxX(),
//                aScanAreaSetting.getMaxY());
//        int theMinWidth = aScanAreaSetting.getMinWidth();
//        int theMinHeight = aScanAreaSetting.getMinHeight();
//
//        Map<String, Rect> theResult = new LinkedHashMap<>();
//        theResult.put("Unspecified", null);
//        for (Map.Entry<String, Rect> theScanAreaEntry : sDefaultScanAreas.entrySet()) {
//            Rect theScanArea = theScanAreaEntry.getValue();
//            if (theSupportedRange.contains(theScanArea)
//                    && theScanArea.width() >= theMinWidth
//                    && theScanArea.height() >= theMinHeight) {
//                theResult.put(theScanAreaEntry.getKey(), theScanArea);
//            }
//        }
//        return theResult;
//    }
//
//    @SuppressWarnings("unchecked")
//    public static void addSettingChangedListener(Collection<Setting> aSettings, String aKey,
//            Setting.SettingChangedListener aListener) {
//        Setting theSetting = findSettingByKey(aSettings, aKey);
//        if (theSetting != null) {
//            theSetting.addSettingChangedListener(aListener);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public static void removeSettingChangedListener(Collection<Setting> aSettings, String aKey,
//            Setting.SettingChangedListener aListener) {
//        Setting theSetting = findSettingByKey(aSettings, aKey);
//        if (theSetting != null) {
//            theSetting.removeSettingChangedListener(aListener);
//        }
//    }
//
//    public static Setting findSettingByKey(Collection<Setting> aSettings, String aKey) {
//        if (aSettings == null || aKey == null) {
//            return null;
//        }
//        for (Setting theSetting : aSettings) {
//            if (aKey.equals(theSetting.getKey())) {
//                return theSetting;
//            }
//        }
//        return null;
//    }
//
//    public static void removeSettingByKey(Collection<Setting> aSettings, String aKey) {
//        if (aSettings == null || aKey == null) {
//            return;
//        }
//        Iterator<Setting> theIterator = aSettings.iterator();
//        while (theIterator.hasNext()) {
//            Setting theSetting = theIterator.next();
//            if (aKey.equals(theSetting.getKey())) {
//                theIterator.remove();
//                break;
//            }
//        }
//    }
//}
