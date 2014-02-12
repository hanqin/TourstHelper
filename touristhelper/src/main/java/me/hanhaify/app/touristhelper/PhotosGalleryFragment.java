package me.hanhaify.app.touristhelper;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wuman.oauth.samples.AsyncResourceLoader;
import com.wuman.oauth.samples.flickr.FlickrConstants;
import com.wuman.oauth.samples.flickr.api.model.ContactsPhotos;
import com.wuman.oauth.samples.flickr.api.model.Photo;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;
import me.hanhaify.app.touristhelper.flicker.PhotosLoader;

public class PhotosGalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<AsyncResourceLoader.Result<ContactsPhotos>> {

    public static final int DEFAULT_LOADER_ID = 0;
    private HListView gallery;
    private PhotosGalleryAdapter adapter;
    private OnPhotosChangedListener onPhotosLoadedListener = new DoNothingOnPhotosChanged();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_gallery, container, false);
        gallery = (HListView) view.findViewById(R.id.horizontal_list_view);
        adapter = new PhotosGalleryAdapter();
        gallery.setDividerWidth(15);
        gallery.setAdapter(adapter);
        return view;
    }

    @Override
    public Loader<AsyncResourceLoader.Result<ContactsPhotos>> onCreateLoader(int id, Bundle bundle) {
        return new PhotosLoader(getActivity(), bundle);
    }

    @Override
    public void onLoadFinished(Loader<AsyncResourceLoader.Result<ContactsPhotos>> resultLoader, AsyncResourceLoader.Result<ContactsPhotos> contactsPhotosResult) {
        if (!contactsPhotosResult.success) {
            String warningMessage = getActivity().getString(R.string.loading_error) + contactsPhotosResult.errorMessage;
            Log.w(getTag(), warningMessage);
            Toast.makeText(getActivity(), warningMessage, Toast.LENGTH_LONG).show();
            return;
        }
        adapter.setData(contactsPhotosResult.data);
        onPhotosLoadedListener.onPhotosLoaded(contactsPhotosResult.data);
    }

    @Override
    public void onLoaderReset(Loader<AsyncResourceLoader.Result<ContactsPhotos>> resultLoader) {
        adapter.clear();
        onPhotosLoadedListener.onPhotosCleared();
    }

    public void startLoading(double lat, double lon) {
        Bundle bundle = new Bundle();

        bundle.putDouble("lat", lat);
        bundle.putDouble("lon", lon);

        Loader<Object> loader = getLoaderManager().getLoader(DEFAULT_LOADER_ID);
        if (loader == null) {
            getLoaderManager().initLoader(DEFAULT_LOADER_ID, bundle, this);
        } else {
            getLoaderManager().restartLoader(DEFAULT_LOADER_ID, bundle, this);
        }
    }

    public static interface OnPhotosChangedListener {
        public void onPhotosLoaded(ContactsPhotos contactsPhotos);

        public void onPhotosCleared();
    }

    public static interface OnPhotoSelectedListener {
        void selectedPhoto(Photo photo);
    }

    public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
        adapter.setOnPhotoSelectedListener(onPhotoSelectedListener);
    }

    public void setOnPhotosLoadedListener(OnPhotosChangedListener onPhotosLoadedListener) {
        this.onPhotosLoadedListener = onPhotosLoadedListener;
    }

    private static class PhotosGalleryAdapter extends BaseAdapter {
        private List<Photo> photos = new ArrayList<Photo>();

        private OnPhotoSelectedListener onPhotoSelectedListener = new EmptyOnPhotoSelectedListener();

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Photo getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(parent.getContext());
                int value = dpToPx(parent.getContext(), 100);
                imageView.setLayoutParams(new HListView.LayoutParams(value, value));
            } else {
                imageView = ((ImageView) convertView);
            }

            final Photo photo = getItem(position);
            String imageUrl = FlickrConstants.generateLargeSquarePhotoUrl(photo.getFarm(),
                    photo.getServer(),
                    photo.getId(), photo.getSecret());
            Picasso.with(parent.getContext()).load(imageUrl)
                    .placeholder(R.drawable.loading)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPhotoSelectedListener.selectedPhoto(photo);
                }
            });
            return imageView;
        }

        public void setData(ContactsPhotos data) {
            if (data == null) return;
            photos.addAll(data.getPhotos().getPhotoList());
            notifyDataSetChanged();
        }
        public void clear() {
            photos.clear();
            notifyDataSetChanged();
        }

        public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
            this.onPhotoSelectedListener = onPhotoSelectedListener;
        }

        private static class EmptyOnPhotoSelectedListener implements OnPhotoSelectedListener {
            @Override
            public void selectedPhoto(Photo photo) {
            }
        }
    }

    private static int dpToPx(Context context, int dpValue) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
        return (int) px;
    }
    private static class DoNothingOnPhotosChanged implements OnPhotosChangedListener {

        @Override
        public void onPhotosLoaded(ContactsPhotos contactsPhotos) {
        }
        @Override
        public void onPhotosCleared() {
        }

    }
}
