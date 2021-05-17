package ProjectClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.gian.stayinformed.MainActivity;
import com.gian.stayinformed.R;

public class DisplayAlertDialog extends AppCompatActivity {
    private Activity myActivity;

    public DisplayAlertDialog(Activity myActivity) {
        this.myActivity = myActivity;
    }

    public void showAlertDialogFavoritos() {
        new AlertDialog.Builder(myActivity.getApplicationContext())
                .setTitle("Internet connection")
                .setMessage("You must have an internet connection to be able to add a country to your favorites list")
                .setPositiveButton(R.string.Continuar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setNegativeButton(R.string.Salir, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myActivity.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void showAlertDialogExit() {
        new AlertDialog.Builder(myActivity.getApplicationContext())
                .setTitle("Internet connection")
                .setMessage("No internet connection detected")
                .setNegativeButton(R.string.Salir, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myActivity.finish();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showAlertDialogOnResponse() {
        if(!checkInternetConnection()){
            new AlertDialog.Builder(myActivity.getApplicationContext())
                    .setTitle("Internet connection")
                    .setMessage("You must have an internet connection to be able to see the statistics of the other countries")
                    .setPositiveButton(R.string.Continuar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.Salir, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            myActivity.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    public boolean checkInternetConnection() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else{
            connected = false;
        }
        return connected;
    }
}
