package ga.josepolanco.mitesina;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hololo.tutorial.library.PermissionStep;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class PantallaDeslizante extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment(new Step.Builder().setTitle("This is header")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.bg_login_01) // int top drawable
                .setSummary("This is summary")
                .build());

        addFragment(new Step.Builder().setTitle("This is header")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.bg_login_01) // int top drawable
                .setSummary("This is summary")
                .build());

        addFragment(new Step.Builder().setTitle("This is header")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.bg_login_01) // int top drawable
                .setSummary("This is summary")
                .build());

        setPrevText("Anterior"); // Previous button text
        setNextText("Siguiente"); // Next button text
        setFinishText("Finalizar"); // Finish button text
        setCancelText("Cancelar"); // Cancel button text

        finishTutorial();

        // Permission Step
        /*addFragment(new PermissionStep.Builder().setTitle(getString(R.string.permission_title))
                .setContent(getString(R.string.permission_detail))
                .setBackgroundColor(Color.parseColor("#FF0957"))
                .setDrawable(R.drawable.mobile_example_4)
                .setSummary(getString(R.string.continue_and_learn))
                .setPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .build());*/
    }

    @Override
    public void finishTutorial() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void currentFragmentPosition(int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
