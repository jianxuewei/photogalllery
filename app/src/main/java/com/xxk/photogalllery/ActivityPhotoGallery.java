package com.xxk.photogalllery;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityPhotoGallery extends Activity_SingleFragment {


    @Override
    protected Fragment createFragment() {
        return new Fragment_PhotoGallery();
    }
}
