package com.omada.junctionadmin.data.repository;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.handler.AppDataHandler;
import com.omada.junctionadmin.data.handler.ArticleDataHandler;
import com.omada.junctionadmin.data.handler.EventDataHandler;
import com.omada.junctionadmin.data.handler.ImageUploadHandler;
import com.omada.junctionadmin.data.handler.InstituteDataHandler;
import com.omada.junctionadmin.data.handler.OrganizationDataHandler;
import com.omada.junctionadmin.data.handler.PostDataHandler;
import com.omada.junctionadmin.data.handler.ShowcaseDataHandler;
import com.omada.junctionadmin.data.handler.UserDataHandler;
import com.omada.junctionadmin.data.handler.VenueDataHandler;
import com.omada.junctionadmin.utils.StringUtilities;

import java.util.HashMap;
import java.util.Map;

public class MainDataRepository implements DataRepository {

    // All the data related to state is to be stored here so that it can be read from handlers.
    // private because the get and set operations need to be performed atomically through synchronized.
    // methods which are package-private.

    private static final Map<DataRepositoryAccessIdentifier, DataRepositoryAccessorData> accessTracker = new HashMap<>();

    private static MainDataRepository mainDataRepository;

    private final AppDataHandler appDataHandler = new AppDataHandler();
    private final UserDataHandler userDataHandler = new UserDataHandler();
    private final OrganizationDataHandler organizationDataHandler = new OrganizationDataHandler();
    private final ArticleDataHandler articleDataHandler = new ArticleDataHandler();
    private final ShowcaseDataHandler showcaseDataHandler = new ShowcaseDataHandler();
    private final PostDataHandler postDataHandler = new PostDataHandler();
    private final VenueDataHandler venueDataHandler = new VenueDataHandler();
    private final InstituteDataHandler instituteDataHandler = new InstituteDataHandler();
    private final ImageUploadHandler imageUploadHandler = new ImageUploadHandler();

    //this is only for events
    private final EventDataHandler eventDataHandler = new EventDataHandler();


    private MainDataRepository() {
        //class constructor init firestore and local db etc here if required
    }

    public static synchronized MainDataRepository getInstance() {
        if (mainDataRepository == null) {
            mainDataRepository = new MainDataRepository();
        }
        return mainDataRepository;
    }

    public AppDataHandler getAppDataHandler() {
        return appDataHandler;
    }

    public UserDataHandler getUserDataHandler() {
        return userDataHandler;
    }

    public EventDataHandler getEventDataHandler() {
        return eventDataHandler;
    }

    public ArticleDataHandler getArticleDataHandler() {
        return articleDataHandler;
    }

    public OrganizationDataHandler getOrganizationDataHandler() {
        return organizationDataHandler;
    }

    public ShowcaseDataHandler getShowcaseDataHandler() {
        return showcaseDataHandler;
    }

    public PostDataHandler getPostDataHandler() {
        return postDataHandler;
    }

    public VenueDataHandler getVenueDataHandler() {
        return venueDataHandler;
    }

    public InstituteDataHandler getInstituteDataHandler() {
        return instituteDataHandler;
    }

    public ImageUploadHandler getImageUploadHandler() {
        return imageUploadHandler;
    }


    public Object getAccessorDataForHandlerWithKey(DataRepositoryAccessIdentifier accessIdentifier, DataRepositoryHandlerIdentifier handlerIdentifier, String key) {
        DataRepositoryAccessorData data = accessTracker.get(accessIdentifier);
        if(data == null) {
            throw new RuntimeException("Attempt to get accessor data of a non-existent or de-registered access identifier");
        }
        return data.getHandlerData(handlerIdentifier, key);
    }

    /* Only for external accessors that are separate from the model */
    public synchronized static DataRepositoryAccessIdentifier registerForDataRepositoryAccess() {
        DataRepositoryAccessIdentifier identifier = new DataRepositoryAccessIdentifier(
                StringUtilities.randomAlphabetGenerator(6)
        );
        accessTracker.put(identifier, new DataRepositoryAccessorData());
        return identifier;
    }

    public synchronized static void removeDataRepositoryAccessRegistration(DataRepositoryAccessIdentifier accessIdentifier) {
        accessTracker.remove(accessIdentifier);
    }

    /*
     Package private because only base data handler should be able to access it and to register handlers
     */
    public synchronized static DataRepositoryHandlerIdentifier registerDataHandler() {
        return new DataRepositoryHandlerIdentifier(
                StringUtilities.randomAlphabetGenerator(6)
        );
    }


    // contains all the data that an accessor needs for each handler. The handlers that need this data
    // can get it from accessTracker
    private static final class DataRepositoryAccessorData {

        private final Map<DataRepositoryHandlerIdentifier, Map<String, Object>>  accessorData = new HashMap<>();

        public synchronized void putHandlerData(
                DataRepositoryHandlerIdentifier handlerIdentifier, String key, Object value) {

            Map<String, Object> handlerData = accessorData.get(handlerIdentifier);

            if(handlerData == null) {
                handlerData = new HashMap<>();
                accessorData.put(handlerIdentifier, handlerData);
            }

            handlerData.put(key, value);
        }

        public synchronized Object getHandlerData(DataRepositoryHandlerIdentifier handlerIdentifier, String key) {

            Map<String, Object> handlerData = accessorData.get(handlerIdentifier);

            if(handlerData == null) {
                handlerData = new HashMap<>();
                accessorData.put(handlerIdentifier, handlerData);
                return null;
            }

            return handlerData.get(key);
        }
    }

}

