package com.dustmybroom62.android.flashlight;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_CAMERA = 1017;
    private static final double SETTINGS_MORSE_CODE_BASE_MILLISECONDS = 200;
    Camera camera = null;
    static View coordView;
    static boolean hasCameraRights = false;
    private boolean appStartup = false;
    static StrobeRunner strobeRunner;
    static Settings settings;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = Settings.getInstance(getBaseContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        MorseCode.appAssets = getAssets();
        appStartup = true;
        coordView = (View) findViewById(R.id.main_content);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    static void showMessage(String message) {
        Snackbar.make(coordView, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void requestCameraPermission() {
        final Activity activity = this;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(coordView, "Camera access is required for Flashlight.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(coordView, "Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasCameraRights = true;
            Snackbar.make(coordView, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
//            swPower.setEnabled(true);
//            tv1.setText(swPower.isChecked() ? "On" : "Off");
        } else {
            hasCameraRights = false;
//            tv1.setText("Disabled");
//            swPower.setEnabled(false);
//            swSos.setEnabled(false);
            Snackbar.make(coordView, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            hasCameraRights = true;
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onStop() {
        settings.serialize();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        strobeRunner.requestStop = true;
        super.onDestroy();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        StrobeLightView strobeLightView;
        TextView tv1;
        Switch swPower;
        Switch swSos;
        Switch swMorse;
        EditText etMorseMessage;
        EditText etMorseDuration;
        CheckBox chkMorseRepeat;
        CheckBox chkSoundOn;
        TextView tvHello;
        private boolean flashOn = false;
        private boolean sosOn = false;
        private boolean morseOn = false;
        private boolean appStartup = true;
        Thread srThread;
        private static final int TAB_LIGHT = 1;
        private static final int TAB_STROBE = 2;
        private static final int TAB_MORSE = 3;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNumber) {
                case TAB_LIGHT:
                    return getViewLight(inflater, container);
                case TAB_STROBE:
                    return getViewStrobe(inflater, container);
                case TAB_MORSE:
                    return getViewMorse(inflater, container);
            }
            return new View(getContext());
        }

        @NonNull
        public View getViewStrobe(LayoutInflater inflater, ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_strobe, container, false);
            strobeLightView = new StrobeLightView(rootView.getContext());
            strobeLightView.hasCameraRights = hasCameraRights;
            strobeLightView.Init(rootView);
//            tvHello = (TextView) rootView.findViewById(R.id.tvHello);
//            tvHello.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            //strobeRunner = StrobeRunner.getInstance();
            return rootView;
        }

        @NonNull
        public View getViewMorse(LayoutInflater inflater, ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_morse, container, false);
            etMorseMessage = (EditText) rootView.findViewById(R.id.etMorseMessage);
            etMorseMessage.setText(settings.getMorseMessage());
            etMorseDuration = (EditText) rootView.findViewById(R.id.etMorseDuration);
            int morseDuration = (int) settings.getMorseDuration();
            etMorseDuration.setText(String.valueOf(morseDuration));
            etMorseDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
            chkMorseRepeat = (CheckBox) rootView.findViewById(R.id.chkMorseRepeat);
            chkMorseRepeat.setChecked(settings.getMorseRepeat());
            chkSoundOn = (CheckBox) rootView.findViewById(R.id.chkSoundOn);
            chkSoundOn.setChecked(settings.getSoundOn());

            swMorse = (Switch) rootView.findViewById(R.id.switchMorse);
            swMorse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!hasCameraRights) {
                        buttonView.setChecked(false);
                        showMessage("Not Allowed: Flashlight requires Camera privelege.");
                        return;
                    }
                    if (isChecked) {
                        morseOn = true;
                        if (null != swSos) {
                            swSos.setChecked(false);
                        }
                        if (null != swPower) {
                            swPower.setChecked(false);
                        }
                        if (null != tv1) {
                            tv1.setText("Morse Code Mode");
                        }
                        String msg = etMorseMessage.getText().toString();
                        settings.setMorseMessage(msg);
                        boolean repeat = chkMorseRepeat.isChecked();
                        boolean soundOn = chkSoundOn.isChecked();
                        String szDuration = etMorseDuration.getText().toString();
                        double duration = Double.valueOf(szDuration);
                        settings.setMorseDuration(duration);
                        settings.setMorseRepeat(repeat);
                        settings.setSoundOn(soundOn);
                        setMorseOn(msg, duration, repeat);
                    } else {
                        morseOn = false;
                        if (!flashOn && !sosOn) {
                            if (null != tv1) {
                                tv1.setText("Off");
                            }
                            flashlightOff();
                        }
                    }
                }
            });
            strobeRunner = StrobeRunner.getInstance();
            return rootView;
        }

        @NonNull
        public View getViewLight(LayoutInflater inflater, ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            tv1 = (TextView) rootView.findViewById(R.id.textView1);
            swPower = (Switch) rootView.findViewById(R.id.switch1);

            swPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean on) {
                    if (!hasCameraRights) {
                        buttonView.setChecked(false);
                        tv1.setText("Disabled");
                        showMessage("Not Allowed: Flashlight requires Camera privelege.");
                        return;
                    }
                    if (on) {
                        flashOn = true;
                        if (null != swMorse) {
                            swMorse.setChecked(false);
                        }
                        swSos.setChecked(false);
                        tv1.setText("On");
                        flashlightOn();
                    } else {
                        flashOn = false;
                        if (!sosOn && !morseOn) {
                            tv1.setText("Off");
                            flashlightOff();
                        }
                    }
                }
            });

            swSos = (Switch) rootView.findViewById(R.id.switchSos);
            swSos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!hasCameraRights) {
                        buttonView.setChecked(false);
                        showMessage("Not Allowed: Flashlight requires Camera privelege.");
                        return;
                    }
                    if (isChecked) {
                        sosOn = true;
                        if (null != swMorse) {
                            swMorse.setChecked(false);
                        }
                        swPower.setChecked(false);
                        tv1.setText("SOS Mode");
                        setMorseOn("sos ", settings.getMorseDuration(), true);
                    } else {
                        sosOn = false;
                        if (!flashOn && !morseOn) {
                            tv1.setText("Off");
                            flashlightOff();
                        }
                    }
                }
            });
            strobeRunner = StrobeRunner.getInstance();
            if (appStartup) {
                appStartup = false;
                swPower.setChecked(true);
            }
            return rootView;
        }

        protected void flashlightOn() {
            try {
                strobeRunner.pattern = new Delay[]{new Delay(1, 0, 250)};
                strobeRunner.patternAlt = new Delay[]{};
                strobeRunner.patternRepeat = true;
                strobeRunner.playSound = false;
                if (!strobeRunner.isRunning) {
                    srThread = new Thread(strobeRunner);
                    srThread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(coordView, e.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        }

        protected void flashlightOff() {
            try {
                strobeRunner.requestStop = true;
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(coordView, e.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        }

        protected void setMorseOn(String msg, double morseCodeBaseMilliseconds, Boolean repeat) {
            try {
                strobeRunner.patternRepeat = repeat;
                MorseCode.setBaseUnitMilliseconds(morseCodeBaseMilliseconds);
                strobeRunner.pattern = MorseCode.BuildMorseCodeDelayArray(msg);
                strobeRunner.playSound = settings.getSoundOn();
                if (!strobeRunner.isRunning) {
                    srThread = new Thread(strobeRunner);
                    srThread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(coordView, e.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
