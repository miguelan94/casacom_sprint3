package com.streamnow.ubs.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.streamnow.ubs.R;
import com.streamnow.ubs.utils.Lindau;

/** !
 * Created by Miguel Estévez on 15/2/16.
 */
public class BaseActivity extends AppCompatActivity
{
    @Override
    //@TargetApi(21)
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if(Lindau.getInstance().getCurrentSessionUser() != null)
            {
                window.setStatusBarColor(Lindau.getInstance().getCurrentSessionUser().userInfo.partner.colorTop);
            }
            else
            {
                window.setStatusBarColor(getResources().getColor(R.color.appColor));
            }
        }
        else
        {
        }
    }
}
