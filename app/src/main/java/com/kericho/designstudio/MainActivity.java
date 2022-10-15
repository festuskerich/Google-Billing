package com.kericho.designstudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {


    public static final String PREF_FILE = "MyPref";
    public static ArrayList<String> purchaseItemIDs = new ArrayList<>();

    public BillingClient billingClient;
    CardView cardView;
    CardView cardView1;
    CardView cardView2;
    CardView cardView3;

    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        cardView = findViewById(R.id.one);
        cardView1 = findViewById(R.id.two);
        cardView2 = findViewById(R.id.three);
        cardView3 = findViewById(R.id.four);

        //one
        //exel
        //prem
        //two
        cardView.setOnClickListener(v -> {
            if (billingClient.isReady()) {
                initiatePurchase("one");
                return;
            }
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {
                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == 0) {
                        initiatePurchase("one");
                        billingClient.queryPurchasesAsync(
                                QueryPurchasesParams.newBuilder()
                                        .setProductType(BillingClient.ProductType.INAPP)
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

        cardView1.setOnClickListener(v -> {
            if (billingClient.isReady()) {
                initiatePurchase("exel");
                return;
            }
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {
                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == 0) {
                        initiatePurchase("exel");
                        billingClient.queryPurchasesAsync(
                                QueryPurchasesParams.newBuilder()
                                        .setProductType(BillingClient.ProductType.INAPP)
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

        cardView2.setOnClickListener(v -> {
            if (billingClient.isReady()) {
                initiatePurchase("prem");
                return;
            }
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {
                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == 0) {
                        initiatePurchase("prem");
                        billingClient.queryPurchasesAsync(
                                QueryPurchasesParams.newBuilder()
                                        .setProductType(BillingClient.ProductType.INAPP)
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

        cardView3.setOnClickListener(v -> {
            if (billingClient.isReady()) {
                initiatePurchase("two");
                return;
            }
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingServiceDisconnected() {
                }

                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == 0) {
                        initiatePurchase("two");
                        billingClient.queryPurchasesAsync(
                                QueryPurchasesParams.newBuilder()
                                        .setProductType(BillingClient.ProductType.INAPP)
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

        billingClient.queryProductDetailsAsync(
                QueryProductDetailsParams
                        .newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams
                                                .Product
                                                .newBuilder()
                                                .setProductId(str)
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()))
                        .build(),
                (billingResult, productDetailsList) -> {
                    if (billingResult.getResponseCode() != 0) {
                        Log.e("billingResult", " Error " + billingResult.getDebugMessage());
                    } else if (productDetailsList.size() <= 0) {
                        Log.e("skuDetailsList", "Purchase Item " + str + " not Found");
                    } else {
                        productDetailsList.get(0);
                        Log.i("ZERO MF", "more than zero  " + productDetailsList.get(0).toString());
                        billingClient.launchBillingFlow(MainActivity.this, BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(ImmutableList
                                        .of(
                                                BillingFlowParams
                                                        .ProductDetailsParams
                                                        .newBuilder()
                                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                                        .setProductDetails(productDetailsList.get(0))
                                                        .build()
                                        ))
                                .build());

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
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                    (billingResult1, purchases) -> {
                        // check billingResult
                        // process returned purchase list, e.g. display the plans user owns
                        handlePurchases(purchases);

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
            if (next.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!next.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(next.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                        if (billingResult.getResponseCode() == 0) {
                            startActivity(new Intent(MainActivity.this, ResurceActivity.class));
                            finish();
                            savePurchaseCountValueToPref(purchaseItemIDs.get(0), getPurchaseCountValueFromPref(purchaseItemIDs.get(0)) + 1);
                            Toast.makeText(getApplicationContext(), "Item " + purchaseItemIDs.get(0) + "Consumed", Toast.LENGTH_SHORT).show();
                           // MainActivity.this.notifyList();
                        }
                    });
                }
            } else if (next.getPurchaseState() == 2) {
                Toast.makeText(getApplicationContext(), purchaseItemIDs.get(0) + " Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            } else if (next.getPurchaseState() == 0) {
                Toast.makeText(getApplicationContext(), purchaseItemIDs.get(0) + " Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }

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