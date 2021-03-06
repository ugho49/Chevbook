package com.chevbook.chevbookapp.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chevbook.chevbookapp.API.API_favoris;
import com.chevbook.chevbookapp.Adapter.ImagePagerAdapter;
import com.chevbook.chevbookapp.Class.Annonce;
import com.chevbook.chevbookapp.Class.User;
import com.chevbook.chevbookapp.CustomDialog.CustomDialogMap;
import com.chevbook.chevbookapp.CustomDialog.CustomDialogMessage;
import com.chevbook.chevbookapp.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailsAnnonceActivity extends ActionBarActivity {

    @InjectView(R.id.view_pager_detail_appartement)
    ViewPager mViewPagerDetailAppartement;
    @InjectView(R.id.indicator_view_pager_detail_appartement)
    CirclePageIndicator mIndicatorViewPagerDetailAppartement;
    @InjectView(R.id.buttonDetailAppartementLookOnMap)
    Button mButtonDetailAppartementLookOnMap;
    @InjectView(R.id.textViewDetailAnnonceTitre)
    TextView mTextViewDetailAnnonceTitre;
    @InjectView(R.id.textViewDetailAppartementDescription)
    TextView mTextViewDetailAppartementDescription;
    @InjectView(R.id.textViewDetailAppartementLoyer)
    TextView mTextViewDetailAppartementLoyer;
    @InjectView(R.id.textViewDetailAppartementQuartier)
    TextView mTextViewDetailAppartementQuartier;
    @InjectView(R.id.textViewDetailAppartementCategorie)
    TextView mTextViewDetailAppartementCategorie;
    @InjectView(R.id.textViewDetailAppartementSurface)
    TextView mTextViewDetailAppartementSurface;
    @InjectView(R.id.textViewDetailAppartementNbPiece)
    TextView mTextViewDetailAppartementNbPiece;
    @InjectView(R.id.textViewDetailAppartementUser)
    TextView mTextViewDetailAppartementUser;
    @InjectView(R.id.textViewDetailAppartementDate)
    TextView mTextViewDetailAppartementDate;
    @InjectView(R.id.textViewDetailAppartementSousCategorie)
    TextView mTextViewDetailAppartementSousCategorie;
    @InjectView(R.id.textViewDetailAppartementType)
    TextView mTextViewDetailAppartementType;
    @InjectView(R.id.textViewDetailAppartementEstMeuble)
    TextView mTextViewDetailAppartementEstMeuble;

    private static ActionBarActivity actionBarActivity;
    private static ImageLoader imageLoader;
    private Annonce mAnnonce;

    private User mUser;

    private boolean is_my_favoris = false;
    private boolean favoris_loading = false;
    private boolean is_my_annonce = false;

    private MenuItem menuAddFavoris;
    private MenuItem menuDeleteFavoris;
    private MenuItem menuSendMessage;

    private CustomDialogMap dialogMap;
    private CustomDialogMessage dialogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_details_annonce);
        ButterKnife.inject(this);
        imageLoader = ImageLoader.getInstance();
        mUser = new User(getApplicationContext());

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String[] mNavigationTitles = getResources().getStringArray(R.array.navigation_array);
        getSupportActionBar().setTitle(mNavigationTitles[1]);
        actionBarActivity = (ActionBarActivity) this;

        mAnnonce = (Annonce)getIntent().getSerializableExtra("annonce");
        is_my_favoris = getIntent().getBooleanExtra("is_favoris", false);


        initData();

        int nbImages = 0;

        for(String S : mAnnonce.getUrl_images_annonces())
        {
            if(!S.equals(""))
            {
                nbImages++;
            }
        }

        if(nbImages > 0) {
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, mAnnonce.getUrl_images_annonces());
            mViewPagerDetailAppartement.setAdapter(adapter);
            mIndicatorViewPagerDetailAppartement.setStrokeColor(Color.WHITE);
            mIndicatorViewPagerDetailAppartement.setViewPager(mViewPagerDetailAppartement);

            if(nbImages == 1)
            {
                mIndicatorViewPagerDetailAppartement.setVisibility(View.GONE);
            }
        }else {
            mViewPagerDetailAppartement.setVisibility(View.GONE);
            mIndicatorViewPagerDetailAppartement.setVisibility(View.GONE);
        }

        dialogMap = new CustomDialogMap(DetailsAnnonceActivity.this, mAnnonce.getAdresse_annonce());
        dialogMap.createDialog();

        dialogMessage = new CustomDialogMessage(DetailsAnnonceActivity.this);
        dialogMessage.createDialog();

        mButtonDetailAppartementLookOnMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //showCustomDialogMap();
                dialogMap.showDialog();
            }
        });
    }

    private void initData(){

        if(mUser.getEmail().equals(mAnnonce.getEmail_user_annonce()))
        {
            is_my_annonce = true;
        }

        mTextViewDetailAnnonceTitre.setText(mAnnonce.getTitre_annonce());
        mTextViewDetailAppartementDescription.setText(mAnnonce.getDescription_annonce());
        mTextViewDetailAppartementLoyer.setText(Double.toString(mAnnonce.getPrix_annonce()) + " €");
        mTextViewDetailAppartementQuartier.setText(mAnnonce.getQuartier_annonce());
        mTextViewDetailAppartementCategorie.setText(mAnnonce.getCategorie_annonce());
        mTextViewDetailAppartementSurface.setText(Integer.toString(mAnnonce.getSurface_annonce()) + "m²");
        mTextViewDetailAppartementNbPiece.setText(Integer.toString(mAnnonce.getNumber_room_annonce()));
        mTextViewDetailAppartementSousCategorie.setText(mAnnonce.getSousCategorie_annonce());
        mTextViewDetailAppartementType.setText(mAnnonce.getType_location_annonce());

        String est_meuble;

        if(mAnnonce.get_isMeuble()){est_meuble = "oui";}
        else{est_meuble = "non";}

        mTextViewDetailAppartementEstMeuble.setText(est_meuble);

        mTextViewDetailAppartementUser.setText(mAnnonce.getPseudo_user_annonce());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String DateAndTime = sdf.format(mAnnonce.getDate_create_annonce());
        mTextViewDetailAppartementDate.setText("Le " + DateAndTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_appartement, menu);

        menuAddFavoris = menu.findItem(R.id.menu_detail_appartements_rate_not_important);
        menuDeleteFavoris = menu.findItem(R.id.menu_detail_appartements_rate_important);
        menuSendMessage = menu.findItem(R.id.menu_detail_appartements_new_message);

        if(is_my_annonce)
        {
            menuAddFavoris.setVisible(false);
            menuDeleteFavoris.setVisible(false);
            menuSendMessage.setVisible(false);
        }
        else {
            if(is_my_favoris)
            {
                setMenuFavorisFullstar(true);
            }
            else {
                menuAddFavoris.setVisible(false);
                menuDeleteFavoris.setVisible(false);
                IsFavorisTask();
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_detail_appartements_new_message:

                dialogMessage.instantiateDialogForSendMessage(this.mAnnonce);
                dialogMessage.showDialog();

                break;

            case R.id.menu_detail_appartements_rate_important:
                //delete to favoris
                if(!favoris_loading){
                    addOrDeleteFavorisTask();
                }
                //setMenuFavorisFullstar(false);
                break;

            case R.id.menu_detail_appartements_rate_not_important:
                //add to favoris
                if(!favoris_loading){
                    addOrDeleteFavorisTask();
                }
                //setMenuFavorisFullstar(true);
                break;

            default:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setMenuFavorisFullstar(Boolean b) {
        //True for show full star
        //false for show empty star
        menuAddFavoris.setVisible(!b);
        menuDeleteFavoris.setVisible(b);
    }

    @Override
    public void onStart() {
        super.onStart();

        EasyTracker.getInstance(this).activityStart(this);  // Start Google Analytics
    }

    @Override
    public void onStop() {
        super.onStop();

        EasyTracker.getInstance(this).activityStop(this);  // Stop Google Analytics
    }

    public void addOrDeleteFavorisTask()
    {
        actionBarActivity.setSupportProgressBarIndeterminateVisibility(true);
        favoris_loading = true;

        int deleteOrAdd = 0;

        if(is_my_favoris)
        {
            deleteOrAdd = 0; //delete favoris
        }
        else {
            deleteOrAdd = 1; //add favoris
        }

        String[] mesparams = {"mettre_enlever_favoris", Integer.toString(mAnnonce.getId_annonce()), Integer.toString(deleteOrAdd)};
        new API_favoris(DetailsAnnonceActivity.this).execute(mesparams);
    }

    public void resultAddOrDeleteFavorisTask(Boolean success)
    {
        actionBarActivity.setSupportProgressBarIndeterminateVisibility(false);
        favoris_loading = false;

        if (success)
        {
            if(is_my_favoris)
            {
                setMenuFavorisFullstar(false);
                is_my_favoris = false;
            }
            else {
                setMenuFavorisFullstar(true);
                is_my_favoris = true;
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Erreur pour l'ajout aux favoris", Toast.LENGTH_SHORT).show();
        }
    }

    public void IsFavorisTask()
    {
        actionBarActivity.setSupportProgressBarIndeterminateVisibility(true);
        favoris_loading = true;

        String[] mesparams = {"est_favoris", Integer.toString(mAnnonce.getId_annonce())};
        new API_favoris(DetailsAnnonceActivity.this).execute(mesparams);
    }

    public void resultIsFavorisTask(Boolean success)
    {
        actionBarActivity.setSupportProgressBarIndeterminateVisibility(false);
        favoris_loading = false;

        if (success)
        {
            //Toast.makeText(getApplication(), "OK", Toast.LENGTH_SHORT).show();
            setMenuFavorisFullstar(true);
            is_my_favoris = true;
        }
        else {
            //Toast.makeText(getApplication(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            setMenuFavorisFullstar(false);
            is_my_favoris = false;
        }
    }

    @Override
    public void finish() {
        if(!is_my_favoris){
            setResult(2);
        }
        super.finish();
    }
}
