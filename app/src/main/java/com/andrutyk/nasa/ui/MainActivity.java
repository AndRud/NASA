package com.andrutyk.nasa.ui;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.andrutyk.nasa.R;
import com.andrutyk.nasa.internet.ConnectionDetector;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public final static String APIKey = "kJf18kOfJU8f89NzZ4uTBDb8PPQw13URIR063nRr";
    private Toolbar toolbar;

    private Fragment fragment;

    private boolean isShowSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        if (!connectionDetector.isConnectingToInternet()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.question_internet_conn);
            builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    isShowSettings = true;
                }
            });
            builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showImagePager();
                }
            });
            builder.create().show();
        } else {
            showImagePager();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.image) {
            getMenuInflater().inflate(R.menu.conext_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_set_wallpaper:
                if (fragment != null){
                    ImageView imageView = (ImageView)((ImagePagerFragment)fragment).getCurrentPage().findViewById(R.id.image);
                    setAsWallpaper(imageView);
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_set_wallpaper:
                if (fragment != null){
                    ImageView imageView = (ImageView)((ImagePagerFragment)fragment).getCurrentPage().findViewById(R.id.image);
                    setAsWallpaper(imageView);
                }
                return true;
            case R.id.item_clear_memory_cache:
                ImageLoader.getInstance().clearMemoryCache();
                return true;
            case R.id.item_clear_disc_cache:
                ImageLoader.getInstance().clearDiskCache();
                return true;
            default:
                return false;
        }
    }

    private void showImagePager(){
        if (fragment == null) {
            String tag = ImagePagerFragment.class.getSimpleName();
            fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment == null) {
                fragment = new ImagePagerFragment();
                fragment.setArguments(getIntent().getExtras());
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.contt, fragment, tag).commit();
        }
    }

    private void setAsWallpaper(ImageView imageView){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {
            wallpaperManager.setBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
            Toast.makeText(getApplicationContext(), getString(R.string.change_wallpaper_success), Toast.LENGTH_LONG).show();
        } catch (IOException e){
            Toast.makeText(getApplicationContext(), getString(R.string.change_wallpaper_error), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShowSettings) {
            showImagePager();
            isShowSettings = false;
        }
    }
}
