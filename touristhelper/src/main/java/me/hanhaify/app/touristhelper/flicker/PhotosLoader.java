package me.hanhaify.app.touristhelper.flicker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.Lists;
import com.wuman.oauth.samples.AsyncResourceLoader;
import com.wuman.oauth.samples.OAuth;
import com.wuman.oauth.samples.flickr.FlickrConstants;
import com.wuman.oauth.samples.flickr.api.Flickr;
import com.wuman.oauth.samples.flickr.api.FlickrRequestInitializer;
import com.wuman.oauth.samples.flickr.api.model.ContactsPhotos;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.hanhaify.app.touristhelper.Constants;
import me.hanhaify.app.touristhelper.R;

public class PhotosLoader extends AsyncResourceLoader<ContactsPhotos> {

    static final Logger LOGGER = Logger.getLogger("PhotosLoader");
    private final OAuth oauth;
    private final Bundle bundle;

    public PhotosLoader(FragmentActivity activity, Bundle bundle) {
        super(activity);
        this.bundle = bundle;
        oauth = OAuth.newInstance(activity.getApplicationContext(),
                activity.getSupportFragmentManager(),
                new ClientParametersAuthentication(FlickrConstants.CONSUMER_KEY,
                        FlickrConstants.CONSUMER_SECRET),
                FlickrConstants.AUTHORIZATION_VERIFIER_SERVER_URL,
                FlickrConstants.TOKEN_SERVER_URL,
                FlickrConstants.REDIRECT_URL,
                Lists.<String>newArrayList(),
                FlickrConstants.TEMPORARY_TOKEN_REQUEST_URL);
    }

    @Override
    public ContactsPhotos loadResourceInBackground() throws Exception {
        Credential credential = oauth.authorize10a(
                getContext().getString(R.string.token_flickr)).getResult();

        Flickr flickr =
                new Flickr.Builder(OAuth.HTTP_TRANSPORT, OAuth.JSON_FACTORY, credential)
                        .setApplicationName(getContext().getString(R.string.app_name))
                        .setFlickrRequestInitializer(new FlickrRequestInitializer())
                        .build();

        Flickr.Photos.Search search = flickr.photos().search();
        search = search.setExtras("geo")
                .setAccuracy(Constants.DEFAULT_ZOOM_LEVEL)
                .setLat(bundle.getDouble(Constants.BUNDLE_KEY_LAT))
                .setLon(bundle.getDouble(Constants.BUNDLE_KEY_LON));
        LOGGER.log(Level.INFO, search.toString());
        ContactsPhotos photos = search.execute();
        return photos;
    }

    @Override
    public void updateErrorStateIfApplicable(AsyncResourceLoader.Result<ContactsPhotos> result) {
        ContactsPhotos data = result.data;
        result.success = "ok".equals(data.getStat());
        result.errorMessage = result.success ? null :
                (data.getErrorCode() + ": " + data.getErrorMessage());
    }
}
