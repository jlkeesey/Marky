/*
 * Copyright (c) 2018 by James Keesey. All rights reserved.
 */
package pub.carkeys.marky;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SignInHandler.SignInCallback {
  private SignInHandler signInHandler;
  private TextView displayName;
  private LocationServiceController locationServiceController = new LocationServiceController(this);
  private CoordinatorLayout coordinatorLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    signInHandler = new SignInHandler(this);

    setContentView(R.layout.activity_main);
    coordinatorLayout = findViewById(R.id.coordinatorLayout);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    displayName = findViewById(R.id.display_name);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
          signInHandler.signIn();
        } else {
          signInHandler.signOut();
        }
      }
    });
    locationServiceController.startService(coordinatorLayout);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    locationServiceController.stopService();
  }

  @Override
  protected void onResume() {
    super.onResume();
    displayName.setText(getUserName());
    SimpleDraweeView draweeView = findViewById(R.id.mugshot_f);
    draweeView.setImageURI(getUserImage());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == SignInHandler.RC_SIGN_IN) {
      signInHandler.handleActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    showUserName();
    locationServiceController.bindService();
  }

  @Override
  protected void onStop() {
    super.onStop();
    locationServiceController.unbindService();
  }

  private void showUserName() {
    showMessage(String.format("who: %s", getUserName()));
  }

  @NonNull
  private String getUserName() {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userName = "<not logged in>";
    if (currentUser != null) {
      userName = currentUser.getDisplayName();
      if (userName == null) {
        userName = "<unknown>";
      }
    }
    return userName;
  }

  @NonNull
  private Uri getUserImage() {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    Uri uri = Uri.EMPTY;
    if (currentUser != null) {
      uri = currentUser.getPhotoUrl();
      if (uri == null) {
        uri = Uri.EMPTY;
      }
    }
    return uri;
  }

  private void showMessage(final String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onSignedIn() {
    showMessage(String.format("Signed in as %s.", getUserName()));
  }

  @Override
  public void onSignedOut() {
    showMessage("Signed out.");
  }

  @Override
  public void onSignInCanceled() {
    showMessage("Sign in cancelled.");
  }

  @Override
  public void onSignInFailed(final int errorCode, final String message) {
    showMessage(String.format(Locale.US, "Sign in failure (%d): %s.", errorCode, message));
  }
}
