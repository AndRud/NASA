package com.andrutyk.nasa.ui;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andrutyk.nasa.API.Response.Response;
import com.andrutyk.nasa.Content.Imagery;
import com.andrutyk.nasa.Database.Realm.ImageryHelper;
import com.andrutyk.nasa.Loaders.Async.Cursor.ImageryLoader;
import com.andrutyk.nasa.R;
import com.andrutyk.nasa.adapter.ImageAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * Created by admin on 03.02.2016.
 */
public class ImagePagerFragment extends BaseFragment implements LoaderCallbacks<Response>{
    private final static  String DATE_IMAGERY = "DATE_IMAGERY";

    private ImageAdapter imageAdapter = null;
    private ViewPager pager = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    private LayoutInflater inflater;
    private DisplayImageOptions options;

    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoaderManager().initLoader(R.id.imagery_loader, null, this);
        realm = Realm.getInstance(getContext());
        View rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.addOnPageChangeListener(new onOnPageChangeListener());
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new onRefreshListener());
        imageAdapter = new ImageAdapter(getActivity());
        initView();
        pager.setAdapter(imageAdapter);
        return rootView;
    }

    private void initView(){
        addImageryFromNASA(0);
    }

    /*
    * diffDays - difference between current and desired day
    * */
    private void addImageryFromNASA(int diffDays){

        DateTime curDate = new DateTime(DateTime.now());
        DateTime desDate = curDate.minusDays(diffDays);

        Bundle extras = new Bundle();
        String dateStr = DateTimeFormat.forPattern("yyyy-MM-dd").print(desDate);
        extras.putString(DATE_IMAGERY, dateStr);
        getLoaderManager().restartLoader(R.id.imagery_loader, extras, this);
    }

    private void initAdapter(){
    }

    private View createView(final Context context, Imagery imagery){

        inflater = LayoutInflater.from(context);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        View imageLayout = inflater.inflate(R.layout.item_pager_image, null, false);
        assert imageLayout != null;
        imageLayout.setTag(imagery.getDate());
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        registerForContextMenu(imageView);

        final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
        final TextView tvTitle = (TextView) imageLayout.findViewById(R.id.tvTitle);
        String title = imagery.getTitle();
        if (title == null)
            title = "";
        tvTitle.setText(title + " (" + imagery.getDate() + ")");
        final TextView tvDescription = (TextView) imageLayout.findViewById(R.id.tvDescription);
        tvDescription.setText(imagery.getExplanation());
        final TextView tvNoDate = (TextView) imageLayout.findViewById(R.id.tvNoDate);

        String hdURL = imagery.getHdurl();
        if (hdURL == null){
            imageView.setVisibility(View.GONE);
            tvNoDate.setVisibility(View.VISIBLE);
        } else {
            tvNoDate.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(imagery.getHdurl(), imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = getString(R.string.input_output_error);
                            break;
                        case DECODING_ERROR:
                            message = getString(R.string.image_cant_be_decoded);
                            break;
                        case NETWORK_DENIED:
                            message = getString(R.string.downloads_are_denied);
                            break;
                        case OUT_OF_MEMORY:
                            message = getString(R.string.out_of_memory_error);
                            break;
                        case UNKNOWN:
                            message = getString(R.string.unknown_error);
                            break;
                    }
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });
        }

        imageLayout.setTag(imagery.getDate());
        return imageLayout;
    }

    public View getCurrentPage ()
    {
        return imageAdapter.getView (pager.getCurrentItem());
    }

    private View createNoDataView(final Context context){
        inflater = LayoutInflater.from(context);

        View imageLayout = inflater.inflate(R.layout.item_layout_no_data, null, false);
        assert imageLayout != null;

        return imageLayout;
    }

    public void addView(View newPage){
        int pageIndex = imageAdapter.addView(newPage, 0);
        imageAdapter.notifyDataSetChanged();

        if (imageAdapter.getCount() == 1){
            pager.setCurrentItem(pageIndex, true);
            initSecondDay();
        }
    }

    private void initSecondDay(){
        DateTime dateTime = new DateTime(DateTime.now());
        dateTime = dateTime.minusDays(1);
        Bundle extras = new Bundle();
        String dateStr = DateTimeFormat.forPattern("yyyy-MM-dd").print(dateTime);
        extras.putString(DATE_IMAGERY, dateStr);
        getLoaderManager().restartLoader(R.id.imagery_loader, extras, this);
    }

    @Override
    public Loader<Response> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.id.imagery_loader:{
                String dateStr = "";
                if (args != null)
                    dateStr = args.getString(DATE_IMAGERY);
                    return new ImageryLoader(getContext(), realm, dateStr, MainActivity.APIKey);
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Response> loader, Response data) {
        int id = loader.getId();
        if (id == R.id.imagery_loader) {
            Imagery imagery = data.getTypedAnswer();
            try {
                /*String media_type = imagery.getMedia_type().toString();
                if (TextUtils.equals(media_type, Imagery.MEDIA_TYPE_IMAGE) ||
                        TextUtils.equals(media_type, Imagery.MEDIA_TYPE_VIDEO))*/
                    addView(createView(getContext(), imagery));
            } catch (NullPointerException e){
                addView(createNoDataView(getContext()));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Response> loader) {}

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(R.id.imagery_loader);
        super.onDestroy();
    }

    private class onOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0)
                addImageryFromNASA(imageAdapter.getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }

    private class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "Swipe " + (imageAdapter.getCount() - pager.getCurrentItem() -1), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
