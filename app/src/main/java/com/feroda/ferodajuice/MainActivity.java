package com.feroda.ferodajuice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {


    public static final String PREF_FILE = "MyPref";
    private static final ArrayList<String> purchaseItemDisplay = new ArrayList<>();
    public static ArrayList<String> purchaseItemIDs = new ArrayList<>();


    ArrayAdapter<String> arrayAdapter;
    public BillingClient billingClient;
    ListView listView;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        purchaseItemIDs.add("lemon");
        purchaseItemIDs.add("mellon");
        purchaseItemIDs.add("nanasi");
        purchaseItemIDs.add("chungwa");


        listView = findViewById(R.id.listview);
        billingClient = BillingClient
                .newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();


        billingClient.startConnection(new BillingClientStateListener() {
            public void onBillingServiceDisconnected() {
                Toast.makeText(getApplicationContext(), "Billing service disconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                List<Purchase> purchasesList = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    handlePurchases(purchasesList);
                }
            }
        });
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, R.layout.item, purchaseItemDisplay);
        this.arrayAdapter = arrayAdapter2;
        this.listView.setAdapter(arrayAdapter2);
        notifyList();
        listView.setOnItemClickListener((adapterView, view, i, j) -> {
            if (billingClient.isReady()) {
                initiatePurchase(purchaseItemIDs.get(i));
                return;
            }
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {
                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == 0) {
                        initiatePurchase(purchaseItemIDs.get(i));
                        List<Purchase> purchasesList = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            handlePurchases(purchasesList);
                        }
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    public void notifyList() {
        purchaseItemDisplay.clear();
        for (String next : purchaseItemIDs) {
            purchaseItemDisplay.add(getPurchaseCountValueFromPref(next)+ " Cups of "+next + " Consumed " );
        }
        this.arrayAdapter.notifyDataSetChanged();
    }

    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }

    private SharedPreferences.Editor getPreferenceEditObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0).edit();
    }

    public int getPurchaseCountValueFromPref(String str) {
        return getPreferenceObject().getInt(str, 0);
    }

    public void savePurchaseCountValueToPref(String str, int i) {
        getPreferenceEditObject().putInt(str, i).commit();
    }

    public void initiatePurchase(final String str) {
        List<String> skuList = new ArrayList<>();
        skuList.add(str);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() != 0) {
                        Log.e("billingResult"," Error " + billingResult.getDebugMessage());
                    } else if (skuDetailsList == null || skuDetailsList.size() <= 0) {
                        Log.e("skuDetailsList", "Purchase Item " + str + " not Found");
                    } else {
                        Log.i( "ZERO MF","more than zero  "+skuDetailsList.size());
                        billingClient.launchBillingFlow(MainActivity.this,
                                BillingFlowParams
                                        .newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build());
                    }
                });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        if (billingResult.getResponseCode() == 0 && list != null) {
            handlePurchases(list);
        } else if (billingResult.getResponseCode() == 7) {
            List<Purchase> purchasesList = this.billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
            if (purchasesList != null) {
                handlePurchases(purchasesList);
            }
        } else if (billingResult.getResponseCode() == 1) {
            Toast.makeText(getApplicationContext(), "Purchase Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void handlePurchases(List<Purchase> list) {
        for (Purchase next : list) {
            final int indexOf = purchaseItemIDs.indexOf(next.getSku());
            if (indexOf > -1) {
                if (next.getPurchaseState() == 1) {
                    if (!verifyValidSignature(next.getOriginalJson(), next.getSignature())) {
                        Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    } else if (!next.isAcknowledged()) {
                        this.billingClient.consumeAsync(ConsumeParams.newBuilder()
                                .setPurchaseToken(next.getPurchaseToken())
                                .build(), (billingResult, str) -> {
                            if (billingResult.getResponseCode() == 0) {
                                savePurchaseCountValueToPref(purchaseItemIDs.get(indexOf), getPurchaseCountValueFromPref(purchaseItemIDs.get(indexOf)) + 1);
                                Toast.makeText(getApplicationContext(), "Item " + purchaseItemIDs.get(indexOf) + "Consumed", Toast.LENGTH_SHORT).show();
                                MainActivity.this.notifyList();
                            }
                        });
                    }
                } else if (next.getPurchaseState() == 2) {
                    Toast.makeText(getApplicationContext(), purchaseItemIDs.get(indexOf) + " Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
                } else if (next.getPurchaseState() == 0) {
                    Toast.makeText(getApplicationContext(), purchaseItemIDs.get(indexOf) + " Purchase Status Unknown", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean verifyValidSignature(String str, String str2) {
        try {
            return Sec.verifyPurchase(getString(R.string.lic), str, str2);
        } catch (IOException unused) {
            return false;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        BillingClient billingClient2 = this.billingClient;
        if (billingClient2 != null) {
            billingClient2.endConnection();
        }
    }
}