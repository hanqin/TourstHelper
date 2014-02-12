
package com.wuman.oauth.samples.flickr.api;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.Key;
import com.wuman.oauth.samples.flickr.api.model.ContactsPhotos;

import java.io.IOException;

public class Flickr extends AbstractGoogleJsonClient {

    public static final String DEFAULT_ROOT_URL = "https://secure.flickr.com/";

    public static final String DEFAULT_SERVICE_PATH = "services/rest/";

    public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    public Flickr(com.google.api.client.http.HttpTransport transport,
                  com.google.api.client.json.JsonFactory jsonFactory,
                  HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }

    Flickr(Builder builder) {
        super(builder);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize(httpClientRequest);
    }

    public Photos photos() {
        return new Photos();
    }

    public class Photos {

        public Search search() throws IOException {
            Search request = new Search();
            initialize(request);
            return request;
        }

        public class Search extends FlickrRequest<ContactsPhotos> {

            @Key("lat")
            private Double lat;

            @Key("lon")
            private Double lon;

            @Key("accuracy")
            private Integer accuracy;

            @Key("extras")
            private String extras;

            @Key("per_page")
            private Integer perPage;

            @Key("page")
            private Integer page;

            public Search setLat(double lat) {
                this.lat = lat;
                return this;
            }

            public Search setLon(double lon) {
                this.lon = lon;
                return this;
            }

            public Search setAccuracy(int accuracy) {
                this.accuracy = accuracy;
                return this;
            }

            public Search setExtras(String extras) {
                this.extras = extras;
                return this;
            }

            public Search setPerPage(int perPage) {
                this.perPage = perPage;
                return this;
            }

            public Search setPage(int page) {
                this.page = page;
                return this;
            }

            public Double getLat() {
                return lat;
            }

            public Double getLon() {
                return lon;
            }

            public Integer getAccuracy() {
                return accuracy;
            }

            public String getExtras() {
                return extras;
            }

            public Integer getPerPage() {
                return perPage;
            }

            public Integer getPage() {
                return page;
            }

            public Search() {
                super(Flickr.this,
                        HttpMethods.GET,
                        "",
                        null,
                        com.wuman.oauth.samples.flickr.api.model.ContactsPhotos.class);
                setMethod("flickr.photos.search");
            }
        }
    }

    public static final class Builder extends
            AbstractGoogleJsonClient.Builder {

        public Builder(com.google.api.client.http.HttpTransport transport,
                       com.google.api.client.json.JsonFactory jsonFactory,
                       HttpRequestInitializer httpRequestInitializer) {
            super(transport,
                    jsonFactory,
                    DEFAULT_ROOT_URL,
                    DEFAULT_SERVICE_PATH,
                    httpRequestInitializer,
                    false);
        }

        @Override
        public Flickr build() {
            return new Flickr(this);
        }

        @Override
        public Builder setRootUrl(String rootUrl) {
            return (Builder) super.setRootUrl(rootUrl);
        }

        @Override
        public Builder setServicePath(String servicePath) {
            return (Builder) super.setServicePath(servicePath);
        }

        @Override
        public Builder setGoogleClientRequestInitializer(
                GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder) super
                    .setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }

        @Override
        public Builder setHttpRequestInitializer(HttpRequestInitializer httpRequestInitializer) {
            return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
        }

        @Override
        public Builder setApplicationName(String applicationName) {
            return (Builder) super.setApplicationName(applicationName);
        }

        @Override
        public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
            return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
        }

        @Override
        public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
            return (Builder) super
                    .setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
        }

        @Override
        public Builder setSuppressAllChecks(boolean suppressAllChecks) {
            return (Builder) super.setSuppressAllChecks(suppressAllChecks);
        }

        public Builder setFlickrRequestInitializer(
                FlickrRequestInitializer instagramRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(instagramRequestInitializer);
        }

    }
}
