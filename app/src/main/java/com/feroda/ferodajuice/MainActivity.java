package com.feroda.ferodajuice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;

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
                billingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build(),
                        new PurchasesResponseListener() {
                            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List purchases) {
                                // check billingResult
                                // process returned purchase list, e.g. display the plans user owns
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    handlePurchases(purchases);
                                }

                            }
                        }
                );
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
                        billingClient.queryPurchasesAsync(
                                QueryPurchasesParams.newBuilder()
                                        .setProductType(BillingClient.ProductType.SUBS)
                                        .build(),
                                new PurchasesResponseListener() {
                                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List purchases) {
                                        // check billingResult
                                        // process returned purchase list, e.g. display the plans user owns
                                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                            handlePurchases(purchases);
                                        }

                                    }
                                }
                        );

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
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams
                        .newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(str)
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {
                    if (billingResult.getResponseCode() != 0) {
                        Log.e("billingResult"," Error " + billingResult.getDebugMessage());
                    } else if (productDetailsList == null || productDetailsList.size() <= 0) {
                        Log.e("skuDetailsList", "Purchase Item " + str + " not Found");
                    } else {
                        Log.i( "ZERO MF","more than zero  "+productDetailsList.size());
                        for (int i=0;i<productDetailsList.size();i++){
                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(ImmutableList.of(
                                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                                    .setProductDetails(productDetailsList.get(i))
                                                    // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                                    // for a list of offers that are available to the user
                                                    .setOfferToken(productDetailsList.get(i).getSubscriptionOfferDetails().get(i).getOfferToken())
                                                    .build()
                                    ))
                                    .build();

                            // Launch the billing flow
                            billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);

                        }

                    }
                }
        );

    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            handlePurchases(list);
        } else if (billingResult.getResponseCode() == 7) {
           this.billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build(),
                    new PurchasesResponseListener() {
                        public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List purchases) {
                            // check billingResult
                            // process returned purchase list, e.g. display the plans user owns
                            if (purchases != null) {
                                handlePurchases(purchases);
                            }

                        }
                    }
            );

        } else if (billingResult.getResponseCode() == 1) {
            Toast.makeText(getApplicationContext(), "Purchase Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void handlePurchases(List<Purchase> list) {
        for (Purchase next : list) {
            final int indexOf = purchaseItemIDs.indexOf(next.getPurchaseToken());
            if (indexOf > -1) {
                if (next.getPurchaseState() == 1) {
                    if (!verifyValidSignature(next.getOriginalJson(), next.getSignature())) {
                        Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    } else if (!next.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(next.getPurchaseToken())
                                        .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                if (billingResult.getResponseCode() == 0) {
                                    savePurchaseCountValueToPref(purchaseItemIDs.get(indexOf), getPurchaseCountValueFromPref(purchaseItemIDs.get(indexOf)) + 1);
                                    Toast.makeText(getApplicationContext(), "Item " + purchaseItemIDs.get(indexOf) + "Consumed", Toast.LENGTH_SHORT).show();
                                    MainActivity.this.notifyList();
                                }
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