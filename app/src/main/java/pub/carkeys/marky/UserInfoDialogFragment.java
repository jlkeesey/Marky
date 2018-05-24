package pub.carkeys.marky;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoDialogFragment extends DialogFragment {
  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userName = "<not logged in>";
    if (user != null) {
      userName = user.getDisplayName();
    }
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
    builder.setMessage("who: " + userName);
    return builder.create();
  }
}
