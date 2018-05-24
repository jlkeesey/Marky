package pub.carkeys.marky;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SignInHandler.SignInCallback {
  private SignInHandler signInHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    signInHandler = new SignInHandler(this);

    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

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
  }

  private void showUserName() {
    showMessage(String.format("who: %s", getUserName()));
  }

  @NonNull
  private String getUserName() {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    //updateUI(currentUser);
    String userName = "<not logged in>";
    if (currentUser != null) {
      userName = currentUser.getDisplayName();
      if (userName == null) {
        userName = "<unknown>";
      }
    }
    return userName;
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
