/*
 * Copyright (c) 2018 by James Keesey. All rights reserved.
 */
package pub.carkeys.marky;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public final class SignInHandler {
  public static final int RC_SIGN_IN = 5647;

  private final Activity activity;
  private final List<AuthUI.IdpConfig> providers = new LinkedList<>();

  public SignInHandler(final Activity activity) {
    this(activity,
         Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(),
                       new AuthUI.IdpConfig.TwitterBuilder().build(),
                       new AuthUI.IdpConfig.EmailBuilder().build()));
  }

  @SuppressWarnings("WeakerAccess")
  public SignInHandler(final Activity activity, @NonNull Collection<? extends AuthUI.IdpConfig> providers) {
    this.activity = activity;
    this.providers.addAll(providers);
  }

  public void signIn() {
    activity.startActivityForResult(AuthUI.getInstance()
                                          .createSignInIntentBuilder()
                                          .setAvailableProviders(providers)
                                          .build(), RC_SIGN_IN);
  }

  public void signOut() {
    AuthUI.getInstance().signOut(activity).addOnCompleteListener(new OnCompleteListener<Void>() {
      public void onComplete(@NonNull Task<Void> task) {
        if (activity instanceof SignInCallback) {
          ((SignInCallback) activity).onSignedOut();
        }
      }
    });
  }

  public void handleActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = IdpResponse.fromResultIntent(data);

      if (resultCode == RESULT_OK) {
        if (activity instanceof SignInCallback) {
          ((SignInCallback) activity).onSignedIn();
        }
      } else {
        if (response == null) {
          if (activity instanceof SignInCallback) {
            ((SignInCallback) activity).onSignInCanceled();
          }
        } else {
          if (activity instanceof SignInCallback) {
            int errorCode = ErrorCodes.UNKNOWN_ERROR;
            String message = "Unknown error";
            final FirebaseUiException error = response.getError();
            if (error != null) {
              errorCode = error.getErrorCode();
              message = error.getLocalizedMessage();
            }
            ((SignInCallback) activity).onSignInFailed(errorCode, message);
          }
        }
      }
    }
  }

  public interface SignInCallback {
    void onSignedIn();

    void onSignedOut();

    void onSignInCanceled();

    void onSignInFailed(int errorCode, String message);
  }
}
