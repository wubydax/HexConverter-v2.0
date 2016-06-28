package com.wubydax.hexconverter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wubydax.hexconverter.views.CircleView;
import com.wubydax.hexconverter.views.ColorPickerDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

/*      Created by Roberto Mariani and Anna Berkovitch, 28/06/2016
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.*/


public class SelectorFragment extends Fragment implements ColorPickerDialog.OnColorChangedListener, View.OnClickListener {

    private static final String COLOR_KEY = "color_key";
    private static final String IS_CROP_KEY = "is_crop_key";
    private SharedPreferences mSharedPreferences;
    private int mRequestCode, mColor;
    private View mView;
    private boolean mIsCrop;


    public SelectorFragment() {

    }

    public static SelectorFragment newInstance() {
        return new SelectorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.imageMenu).setVisible(mRequestCode != 0);
        menu.findItem(R.id.cropMenu).setVisible(mRequestCode != 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imageMenu:
                Intent getContentIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getContentIntent.setType("image/*");
                startActivityForResult(getContentIntent, 46);
                break;
            case R.id.cropMenu:
                mIsCrop = !mIsCrop;
                setUpImageView();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 46 && data != null && data.getData() != null) {
            mSharedPreferences.edit().putString(MainActivity.URI_KEY, data.getData().toString()).apply();
            setUpImageView();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setUpImageView() {
        String uriString = mSharedPreferences.getString(MainActivity.URI_KEY, null);
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            try {
                InputStream inputStream = MyApp.getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Matrix matrix = new Matrix();
                matrix.postRotate(Utils.getOrientation(uri));
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ImageView view = ((ImageView) mView.findViewById(R.id.imageColorSelect));
                int width, height, weight;
                if (MyApp.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    width = 0;
                    height = ViewGroup.LayoutParams.MATCH_PARENT;
                    weight = 1;
                } else {
                    width = mIsCrop ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
                    height = 0;
                    weight = 4;
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                        height);
                params.gravity = Gravity.CENTER;
                params.weight = weight;
                ImageView.ScaleType scaleType = mIsCrop ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_CENTER;
                if (view != null) {
                    view.setLayoutParams(params);
                    view.setScaleType(scaleType);
                    view.setImageBitmap(rotatedBitmap);
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
                    final Bitmap drawableBitmap = bitmapDrawable.getBitmap();
                    final Matrix inverse = new Matrix();

                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            ((ImageView) v).getImageMatrix().invert(inverse);
                            float[] touchPoint = new float[]{event.getX(), event.getY()};
                            inverse.mapPoints(touchPoint);
                            int xPosition = (int) touchPoint[0];
                            int yPosition = (int) touchPoint[1];
                            if (xPosition < drawableBitmap.getWidth() && xPosition >= 0 && yPosition >= 0 && yPosition < drawableBitmap.getHeight()) {
                                onColorChanged(drawableBitmap.getPixel(xPosition, yPosition));
                            }

                            return true;

                        }
                    });
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRequestCode = mSharedPreferences.getInt(MainActivity.FRAGMENT_CODE_KEY, 0);
        mColor = mSharedPreferences.getInt(COLOR_KEY, Color.RED);
        mIsCrop = mSharedPreferences.getBoolean(IS_CROP_KEY, false);
        switch (mRequestCode) {
            case 0:
                return inflater.inflate(R.layout.color_selector_layout, container, false);
            case 1:
                return inflater.inflate(R.layout.image_selector_layout, container, false);
            default:
                return super.onCreateView(inflater, container, savedInstanceState);

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        updateValueView();
        if (mRequestCode == 0) {
            setUpDialog();
        } else {
            setUpImageView();
        }

    }

    private void setUpDialog() {
        Button button = (Button) mView.findViewById(R.id.dialogLaunchButton);
        button.setBackgroundTintList(ColorStateList.valueOf(mColor));

        button.setOnClickListener(this);
    }

    private void updateValueView() {
        ((CircleView) mView.findViewById(R.id.colorPreview)).setFillColor(mColor);
        ((TextView) mView.findViewById(R.id.hexText)).setText(String.format(Locale.getDefault(), getString(R.string.hex_color), "#" + Integer.toHexString(mColor)));
        ((TextView) mView.findViewById(R.id.integerText)).setText(String.format(Locale.getDefault(), getString(R.string.color_integer), String.valueOf(mColor)));
        ((TextView) mView.findViewById(R.id.smaliText)).setText(String.format(Locale.getDefault(), getString(R.string.smali_integer), Utils.getSmaliColor(mColor)));

    }


    @Override
    public void onColorChanged(int color) {
        mColor = color;
        updateValueView();
        if (mRequestCode == 0) {
            mView.findViewById(R.id.dialogLaunchButton).setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    @Override
    public void onClick(View v) {
        ColorPickerDialog dialog = new ColorPickerDialog(getActivity(), mColor);
        dialog.setOnColorChangedListener(this);
        dialog.setAlphaSliderVisible(true);
        dialog.setHexValueEnabled(true);
        dialog.show();
    }

    @Override
    public void onDetach() {
        mSharedPreferences.edit().putInt(COLOR_KEY, mColor).apply();
        mSharedPreferences.edit().putBoolean(IS_CROP_KEY, mIsCrop).apply();

        super.onDetach();
    }
}
