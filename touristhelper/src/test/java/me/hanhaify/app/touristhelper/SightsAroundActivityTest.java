package me.hanhaify.app.touristhelper;

import android.app.Activity;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SightsAroundActivityTest {

    @Test
    public void should_() {
        Activity activity = new Activity();
        TextView textView = new TextView(activity);

        textView.setText("Hello");

        assertThat(textView).hasText("Hello");
    }
}
