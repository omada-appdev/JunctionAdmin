package com.omada.junctionadmin.data.repositorytemp;

import com.omada.junctionadmin.data.DataRepository;
import com.omada.junctionadmin.data.repository.AppDataRepository;
import com.omada.junctionadmin.data.repository.ArticleDataRepository;
import com.omada.junctionadmin.data.repository.EventDataRepository;
import com.omada.junctionadmin.data.sink.remote.RemoteImageDataSink;
import com.omada.junctionadmin.data.repository.InstituteDataRepository;
import com.omada.junctionadmin.data.repository.NotificationDataRepository;
import com.omada.junctionadmin.data.repository.OrganizationDataRepository;
import com.omada.junctionadmin.data.repository.PostDataRepository;
import com.omada.junctionadmin.data.repository.ShowcaseDataRepository;
import com.omada.junctionadmin.data.repository.UserDataRepository;
import com.omada.junctionadmin.data.repository.VenueDataRepository;
import com.omada.junctionadmin.data.source.providers.DataSourceProvider;
import com.omada.junctionadmin.utils.StringUtilities;

import java.util.HashMap;
import java.util.Map;

public class MainDataRepository implements DataRepository {

    // All the data related to state is to be stored here so that it can be read from handlers.
    // private because the get and set operations need to be performed atomically through synchronized.
    // methods which are package-private.
    private static final Map<DataRepositoryAccessIdentifier, DataRepositoryAccessorData> accessTracker = new HashMap<>();

    private DataSourceProvider localDataSourceProvider;
    private DataSourceProvider remoteDataSourceProvider;
    private DataSourceProvider memoryDataSourceProvider;

    private static MainDataRepository mainDataRepository;

    private final AppDataRepository appDataRepository = new AppDataRepository();
    private final UserDataRepository userDataRepository = new UserDataRepository();
    private final OrganizationDataRepository organizationDataRepository = new OrganizationDataRepository();
    private final ArticleDataRepository articleDataRepository = new ArticleDataRepository();
    private final ShowcaseDataRepository showcaseDataRepository = new ShowcaseDataRepository();
    private final PostDataRepository postDataRepository = new PostDataRepository();
    private final VenueDataRepository venueDataRepository = new VenueDataRepository();
    private final InstituteDataRepository instituteDataRepository = new InstituteDataRepository();
    private final RemoteImageDataSink remoteImageDataSink = new RemoteImageDataSink();
    private final NotificationDataRepository notificationDataRepository = new NotificationDataRepository();

    //this is only for events
    private final EventDataRepository eventDataRepository = new EventDataRepository();


    private MainDataRepository() {
        //class constructor init firestore and local db etc here if required
    }

    public static synchronized MainDataRepository getInstance() {
        if (mainDataRepository == null) {
            mainDataRepository = new MainDataRepository();
        }
        return mainDataRepository;
    }

    public AppDataRepository getAppDataRepository() {
        return appDataRepository;
    }

    public UserDataRepository getUserDataRepository() {
        return userDataRepository;
    }

    public EventDataRepository getEventDataRepository() {
        return eventDataRepository;
    }

    public ArticleDataRepository getArticleDataRepository() {
        return articleDataRepository;
    }

    public OrganizationDataRepository getOrganizationDataRepository() {
        return organizationDataRepository;
    }

    public ShowcaseDataRepository getShowcaseDataRepository() {
        return showcaseDataRepository;
    }

    public PostDataRepository getPostDataRepository() {
        return postDataRepository;
    }

    public VenueDataRepository getVenueDataRepository() {
        return venueDataRepository;
    }

    public InstituteDataRepository getInstituteDataRepository() {
        return instituteDataRepository;
    }

    public RemoteImageDataSink getRemoteImageDataSink() {
        return remoteImageDataSink;
    }

    public NotificationDataRepository getNotificationDataRepository() {
        return notificationDataRepository;
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

