package com.streamnow.lindaumobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.streamnow.lindaumobile.datamodel.DMCategory;
import com.streamnow.lindaumobile.datamodel.DMDocument;
import com.streamnow.lindaumobile.datamodel.DMElement;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.DocMenuAdapter;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;


/**
 * !
 * Created by Miguel Estévez on 31/1/16.
 */
public class DocmanMenuActivity extends BaseActivity {
    private boolean isRootMenu;
    private ArrayList<IMenuPrintable> adapterArray;
    private View dividerTop;
    private ProgressDialog progressDialog;

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(Lindau.getInstance().getCurrentSessionUser().userInfo.language);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        setContentView(R.layout.activity_docman_menu);
        RelativeLayout mainBackground = (RelativeLayout) findViewById(R.id.main_background);
        mainBackground.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);
        ImageView leftArrow = (ImageView) findViewById(R.id.left_arrow_doc);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dividerTop = findViewById(R.id.divider);
        this.isRootMenu = getIntent().getBooleanExtra("root_menu", false);

        if (this.isRootMenu) {
            progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);

            RequestParams requestParams = new RequestParams("access_token", sessionUser.accessToken);
            LDConnection.post("getDocsInfo", requestParams, new ResponseHandlerJson());
        } else {
            String categoyId = getIntent().getStringExtra("category_id");

            switch (categoyId) {
                case "-1":
                    this.adapterArray = Lindau.getInstance().getUserTree();
                    break;
                case "-2":
                    this.adapterArray = Lindau.getInstance().getRepoTree();
                    break;
                default:
                    this.adapterArray = Lindau.getInstance().getTreeWithCategoryId(categoyId);
                    break;
            }

            final ListView listView = (ListView) findViewById(R.id.docman_menu_list_view);
            listView.setDivider(new ColorDrawable(sessionUser.userInfo.partner.lineColorSmartphone));
            listView.setDividerHeight(1);
            listView.setAdapter(new DocMenuAdapter(DocmanMenuActivity.this, this.adapterArray));
            dividerTop.setBackgroundColor(sessionUser.userInfo.partner.lineColorSmartphone);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    menuItemClicked(position);
                }
            });
        }
    }

    private void menuItemClicked(int position) {
        DMElement element = (DMElement) this.adapterArray.get(position);

        if (element.elementType == DMElement.DMElementType.DMElementTypeCategory) {
            DMCategory category = (DMCategory) element;
            Intent intent = new Intent(this, DocmanMenuActivity.class);

            if (this.isRootMenu) {
                if (category.getRowTitleText().equals(getString(R.string.personal_docs))) {
                    intent.putExtra("category_id", "-1");
                } else if (category.getRowTitleText().equals(getString(R.string.general_docs))) {
                    intent.putExtra("category_id", "-2");
                }
            } else {
                intent.putExtra("category_id", category.id);
            }

            startActivity(intent);
        } else if (element.elementType == DMElement.DMElementType.DMElementTypeDocument) {
            System.out.println("webview document");
            // TODO Open webview with document shown inside (call WebViewActiviy with some url)
            DMDocument document = (DMDocument) element;
            Intent intent = new Intent(this, WebViewActivity.class);

            String urlAsString = LDConnection.getAbsoluteUrl("getDocInfo") + "?access_token=" + sessionUser.accessToken + "&doc_id=" + document.id;
            String encodedURL = null;
            try {
                encodedURL = URLEncoder.encode(urlAsString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String docUrl = "http://docs.google.com/gview?embedded=true&url=" + encodedURL;
            intent.putExtra("web_url", docUrl);
            startActivity(intent);
        }
    }

    private void showAlertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private class ResponseHandlerJson extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                System.out.println("response:" + response.toString());
                if (response.getString("status").equals("ok")) {
                    ArrayList<IMenuPrintable> userTreeArray = new ArrayList<>();
                    userTreeArray.addAll(DMCategory.categoriesWithArray(response.getJSONArray("usertree")));

                    ArrayList<IMenuPrintable> repoTreeArray = new ArrayList<>();
                    repoTreeArray.addAll(DMCategory.categoriesWithArray(response.getJSONArray("repotree")));

                    if (userTreeArray.size() > 0 || repoTreeArray.size() > 0) {
                        Lindau.getInstance().setUserTree(userTreeArray);
                        Lindau.getInstance().setRepoTree(repoTreeArray);

                        adapterArray = new ArrayList<>();
                        if (repoTreeArray.size() > 0)
                            adapterArray.add(new DMCategory(getString(R.string.general_docs)));
                        if (userTreeArray.size() > 0)
                            adapterArray.add(new DMCategory(getString(R.string.personal_docs)));

                        final ListView listView = (ListView) findViewById(R.id.docman_menu_list_view);
                        dividerTop.setBackgroundColor(sessionUser.userInfo.partner.lineColorSmartphone);
                        listView.setDivider(new ColorDrawable(sessionUser.userInfo.partner.lineColorSmartphone));
                        listView.setDividerHeight(1);
                        listView.setAdapter(new DocMenuAdapter(DocmanMenuActivity.this, adapterArray));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                menuItemClicked(position);
                            }
                        });
                    } else {
                        dividerTop.setVisibility(View.GONE);
                        new AlertDialog.Builder(DocmanMenuActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage(getString(R.string.no_docs_available))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                } else {
                    //showAlertDialog(getString(R.string.network_error));
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

                    } else {
                        System.out.println("network error");
                        showAlertDialog(getResources().getString(R.string.network_error));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //showAlertDialog(getString(R.string.network_error));
            }
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
            System.out.println("getContact onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            System.out.println("getContact onFailure json" + errorResponse.toString());
            progressDialog.dismiss();
        }
    }
}
