package com.vlusi.klintelligent.ImageLoadBrowser;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.activities.VideoPlayActivity;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoViewFragment extends Fragment {



    private String url;
    private ImageView iv_play;


    public PhotoViewFragment(String url){
        this.url=url;
    }

    private PhotoView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_photo_view, container, false);


        imageView = (PhotoView) view.findViewById(R.id.photoIm);
        iv_play = (ImageView) view.findViewById(R.id.iv_play);
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                intent.putExtra("video_path", url);
                startActivity(intent);
            }
        });

        imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                getActivity().finish();
            }
        });


       if (url.endsWith("jpg")){
           iv_play.setVisibility(View.GONE);
           Glide.with(getContext()).load(url).into(imageView);
       }else if (url.endsWith(".mp4")){
           Glide.with( getContext() ).load(url).into(imageView );
           iv_play.setVisibility(View.VISIBLE);
       }
        return view;
    }




}
