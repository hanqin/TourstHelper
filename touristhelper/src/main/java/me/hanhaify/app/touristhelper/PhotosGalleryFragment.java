package me.hanhaify.app.touristhelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import it.sephiroth.android.library.widget.HListView;

public class PhotosGalleryFragment extends Fragment {

    private HListView gallery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_gallery, container, false);
        gallery = (HListView) view.findViewById(R.id.horizontal_list_view);
        gallery.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setImageDrawable(parent.getResources().getDrawable(R.drawable.demo_image));
                imageView.setLayoutParams(new HListView.LayoutParams(HListView.LayoutParams.WRAP_CONTENT, HListView.LayoutParams.WRAP_CONTENT));
                return imageView;
            }
        });
        return view;
    }
}
