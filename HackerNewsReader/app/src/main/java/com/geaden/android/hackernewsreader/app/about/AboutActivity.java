package com.geaden.android.hackernewsreader.app.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.geaden.android.hackernewsreader.app.BuildConfig;
import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.util.AboutUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * About app screen.
 *
 * @author Gennady Denisov
 */
public class AboutActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.about_main)
    TextView mAboutMain;

    @Bind(R.id.about_licenses)
    TextView mAboutLicences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_act);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setTitle(R.string.about_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAboutMain.setText(Html.fromHtml(getString(R.string.about_main, BuildConfig.VERSION_NAME)));
        mAboutLicences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUtils.showOpenSourceLicenses(AboutActivity.this);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
