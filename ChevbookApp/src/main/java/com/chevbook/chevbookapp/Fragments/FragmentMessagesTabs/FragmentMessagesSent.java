package com.chevbook.chevbookapp.Fragments.FragmentMessagesTabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chevbook.chevbookapp.Adapter.ListViewMessageSentAdapter;
import com.chevbook.chevbookapp.Class.Message;
import com.chevbook.chevbookapp.Class.User;
import com.chevbook.chevbookapp.CustomDialog.CustomDialogMessage;
import com.chevbook.chevbookapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Ugho on 24/02/14.
 */
public class FragmentMessagesSent extends Fragment implements OnRefreshListener {

    @InjectView(R.id.listViewMessageSent)
    ListView mListViewMessageSent;
    @InjectView(R.id.linearLayoutMessagesSentLoading)
    LinearLayout mLinearLayoutMessagesSentLoading;
    @InjectView(R.id.buttonNoResultRafraichirMessagesSent)
    Button mButtonNoResultRafraichirMessagesSent;
    @InjectView(R.id.linearLayoutMessagesSentNoResult)
    LinearLayout mLinearLayoutMessagesSentNoResult;

    private ListViewMessageSentAdapter Adapter;
    private CustomDialogMessage dialogMessage;

    private ActionBarActivity actionBarActivity;
    private PullToRefreshLayout mPullToRefreshLayout;

    private ArrayList<Message> mMessages = new ArrayList<Message>();
    private User mUser;
    private Boolean onResume = false;

    public FragmentMessagesSent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_messages_sent, container, false);
        ButterKnife.inject(this, rootView);
        actionBarActivity = (ActionBarActivity) getActivity();

        Adapter = new ListViewMessageSentAdapter(getActivity().getBaseContext(), mMessages);
        mListViewMessageSent.setAdapter(Adapter);

        mUser = new User(getActivity().getApplicationContext());

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.ptr_layout_messages_sent);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);

        mListViewMessageSent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                dialogMessage = new CustomDialogMessage(getActivity());
                dialogMessage.createDialog();
                dialogMessage.instantiateDialogForLookMessage(mMessages.get(position),false);
                dialogMessage.showDialog();
            }

        });

        mButtonNoResultRafraichirMessagesSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessages.clear();
                mMessages = new ArrayList<Message>();

                listerMessagesSent();
            }
        });

        if(onResume == false) {
            listerMessagesSent();
        }
        else {
            onResume = false;
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        onResume = true;
    }

    @Override
    public void onRefreshStarted(View view) {
        mMessages.clear();
        mMessages = new ArrayList<Message>();

        listerMessagesSent();
    }

    public void listerMessagesSent()
    {
        new AsyncTask<Void, Void, Boolean>() {

            String AfficherJSON = null;
            String ErreurLoginTask = "Erreur ";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                actionBarActivity.setSupportProgressBarIndeterminateVisibility(true);
                mLinearLayoutMessagesSentNoResult.setVisibility(View.GONE);
                mListViewMessageSent.setVisibility(View.GONE);
                mLinearLayoutMessagesSentLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                HttpURLConnection urlConnection = null;
                StringBuilder sb = new StringBuilder();

                try {
                    URL url = new URL(getResources().getString(R.string.URL_SERVEUR) + getResources().getString(R.string.URL_SERVEUR_MESSAGES_ENVOYEES));
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setConnectTimeout(5000);
                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());

                    // Création objet jsonn clé valeur
                    JSONObject jsonParam = new JSONObject();
                    // Exemple Clé valeur utiles à notre application
                    jsonParam.put("email", mUser.getEmail());
                    jsonParam.put("password", mUser.getPasswordSha1());
                    out.write(jsonParam.toString());
                    out.flush();
                    out.close();

                    // récupération du serveur
                    int HttpResult = urlConnection.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        br.close();

                        AfficherJSON = sb.toString();

                        JSONArray jsonArray = new JSONArray(sb.toString());

                        if(jsonArray.length()>0){
                            for(int j = 0; j < jsonArray.length(); j++){

                                JSONObject jsonObject = jsonArray.getJSONObject(j);

                                int id_annonce_destinataire = jsonObject.getInt("Id_Annonce_Destinataire");
                                String email_emetteur = mUser.getEmail();
                                Date date_create_message = ConvertToDate(jsonObject.get("Date").toString());
                                String titre_annonce = jsonObject.getString("Titre_Annonce");
                                String contenu_message = jsonObject.getString("Message");
                                String nomPrenom_emetteur = mUser.getFirstName() + " " + mUser.getLastName().substring(0,1).toUpperCase();
                                String url_image_emetteur = mUser.getUrlProfilPicture();
                                String nomPrenom_destinataire = jsonObject.getString("Prenom_Personne") + " " + jsonObject.getString("Nom_Personne").substring(0,1).toUpperCase();
                                String url_image_destinataire = jsonObject.getString("Avatar_Personne");
                                Boolean est_lu = false;

                                if(jsonObject.getInt("Message_Lu") == 0){
                                    est_lu = false;
                                }
                                else {
                                    est_lu = true;
                                }

                                mMessages.add(new Message(id_annonce_destinataire,
                                        email_emetteur,
                                        date_create_message,
                                        titre_annonce,
                                        contenu_message,
                                        nomPrenom_emetteur,
                                        url_image_emetteur,
                                        nomPrenom_destinataire,
                                        url_image_destinataire,
                                        est_lu));
                            }
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                catch (MalformedURLException e){
                    ErreurLoginTask = ErreurLoginTask + "URL";
                    return false; //Erreur URL
                } catch (SocketTimeoutException e) {
                    ErreurLoginTask = ErreurLoginTask + "Temps trop long";
                    return false; //Temps trop long
                } catch (IOException e) {
                    ErreurLoginTask = ErreurLoginTask + "Connexion internet lente ou inexistante";
                    return false; //Pas de connexion internet
                } catch (JSONException e) {
                    ErreurLoginTask = ErreurLoginTask + "Problème de JSON";
                    return false; //Erreur JSON
                } finally {
                    if (urlConnection != null){
                        urlConnection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(final Boolean result) {

                mListViewMessageSent.setVisibility(View.VISIBLE);
                mLinearLayoutMessagesSentLoading.setVisibility(View.GONE);
                mLinearLayoutMessagesSentNoResult.setVisibility(View.GONE);

                if (result)
                {
                    Adapter.setList(mMessages);
                    Adapter.notifyDataSetChanged();
                }
                else {
                    //Toast.makeText(getActivity(), ErreurLoginTask, Toast.LENGTH_SHORT).show();

                    mListViewMessageSent.setVisibility(View.GONE);
                    mLinearLayoutMessagesSentNoResult.setVisibility(View.VISIBLE);
                }

                /*AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setNegativeButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.setMessage(AfficherJSON);
                adb.show();*/

                actionBarActivity.setSupportProgressBarIndeterminateVisibility(false);
                mPullToRefreshLayout.setRefreshComplete();

            }
        }.execute();
    }

    private Date ConvertToDate(String dateString){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Date();
        }
    }
}
