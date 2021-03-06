package com.chevbook.chevbookapp.CustomDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.chevbook.chevbookapp.Class.Annonce;
import com.chevbook.chevbookapp.Class.Message;
import com.chevbook.chevbookapp.Class.User;
import com.chevbook.chevbookapp.CustomsView.CircularImageView;
import com.chevbook.chevbookapp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

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
import java.text.SimpleDateFormat;

/**
 * Created by Ugho on 25/02/14.
 */
public class CustomDialogMessage {

    private static TextView mTextViewCustomDialogMessageUserName;
    private static TextView mTextViewCustomDialogMessageDate;
    private static TextView mTextViewCustomDialogMessageFromTo;
    private static TextView mTextViewCustomDialogMessageTitleAnnonce;
    private static CircularImageView mImageViewCustomDialogMessageUser;
    private static EditText mEditTextCustomDialogMessageMessage;
    private static TableRow mTableRowDate;
    private static Button mButtonCustomDialogMessageSent;
    private static Button mButtonCustomDialogMessageReply;

    private Activity mActivity;
    private final ImageLoader imageLoader = ImageLoader.getInstance();
    private AlertDialog dialog;

    private Boolean is_a_reply = false;

    private User mUser;
    private Annonce mAnnonce;
    private Message mMessage;

    private Drawable oldBackgroundEditText;

    public CustomDialogMessage(Activity mActivity) {
        this.mActivity = mActivity;
        this.mUser = new User(mActivity.getApplicationContext());
    }

    public void createDialog() {
        View custom_view_message = mActivity.getLayoutInflater().inflate(R.layout.custom_dialog_message, null);

        mTextViewCustomDialogMessageTitleAnnonce = (TextView)custom_view_message.findViewById(R.id.textViewCustomDialogMessageTitreAnnonce);
        mEditTextCustomDialogMessageMessage = (EditText)custom_view_message.findViewById(R.id.editTextCustomDialogMessageMessage);
        mTextViewCustomDialogMessageUserName = (TextView)custom_view_message.findViewById(R.id.textViewCustomDialogMessageUserName);
        mTextViewCustomDialogMessageDate = (TextView)custom_view_message.findViewById(R.id.textViewCustomDialogMessageDate);
        mTableRowDate = (TableRow)custom_view_message.findViewById(R.id.tableRowDate);
        mTextViewCustomDialogMessageFromTo = (TextView) custom_view_message.findViewById(R.id.textViewCustomDialogMessageFromTo);
        mImageViewCustomDialogMessageUser = (CircularImageView)custom_view_message.findViewById(R.id.imageViewCustomDialogMessageUser);
        mButtonCustomDialogMessageSent = (Button)custom_view_message.findViewById(R.id.buttonCustomDialogMessageSent);
        mButtonCustomDialogMessageReply = (Button)custom_view_message.findViewById(R.id.buttonCustomDialogMessageReply);

        oldBackgroundEditText = mEditTextCustomDialogMessageMessage.getBackground();

        dialog = new AlertDialog.Builder(mActivity)
                .setView(custom_view_message)
                .setTitle("Message")
                .setCancelable(false)
                .setNegativeButton(mActivity.getResources().getString(R.string.btn_return), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "Annulation", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                })
                .create();
    }

    public void showDialog() {
        mButtonCustomDialogMessageReply.setOnClickListener(clickListener);
        mButtonCustomDialogMessageSent.setOnClickListener(clickListener);

        dialog.show();
    }

    public void instantiateDialogForSendMessage(final Annonce a) {
        mAnnonce = a;

        mMessage = new Message();
        mMessage.setTitre_annonce(mAnnonce.getTitre_annonce());
        mMessage.setUrl_image_destinataire(mAnnonce.getAvatar_user_annonce());
        mMessage.setNomPrenom_destinataire(mAnnonce.getPseudo_user_annonce());
        mMessage.setId_annonce_destinataire(mAnnonce.getId_annonce());
        mMessage.setContenu_message("");

        imageLoader.displayImage(mMessage.getUrl_image_destinataire(), mImageViewCustomDialogMessageUser);
        mTextViewCustomDialogMessageTitleAnnonce.setText(mMessage.getTitre_annonce());
        mTextViewCustomDialogMessageUserName.setText(mMessage.getNomPrenom_destinataire());
        mEditTextCustomDialogMessageMessage.setText(mMessage.getContenu_message());

        mTextViewCustomDialogMessageFromTo.setText("A :");

        mEditTextCustomDialogMessageMessage.setEnabled(true);
        mEditTextCustomDialogMessageMessage.setBackgroundDrawable(oldBackgroundEditText);

        mTableRowDate.setVisibility(View.GONE);
        mButtonCustomDialogMessageSent.setVisibility(View.VISIBLE);
        mButtonCustomDialogMessageReply.setVisibility(View.GONE);
    }

    public void instantiateDialogForReplyAMessage(final Message m) {
        mMessage = new Message();
        mMessage.InstantiateByMessage(m);
        mMessage.setContenu_message("");

        imageLoader.displayImage(mMessage.getUrl_image_emetteur(), mImageViewCustomDialogMessageUser);
        mTextViewCustomDialogMessageTitleAnnonce.setText(mMessage.getTitre_annonce());
        mTextViewCustomDialogMessageUserName.setText(mMessage.getNomPrenom_emetteur());
        mEditTextCustomDialogMessageMessage.setText(mMessage.getContenu_message());

        mTextViewCustomDialogMessageFromTo.setText("A :");

        mEditTextCustomDialogMessageMessage.setEnabled(true);
        mEditTextCustomDialogMessageMessage.setBackgroundDrawable(oldBackgroundEditText);

        mTableRowDate.setVisibility(View.GONE);
        mButtonCustomDialogMessageSent.setVisibility(View.VISIBLE);
        mButtonCustomDialogMessageReply.setVisibility(View.GONE);
    }

    public void instantiateDialogForLookMessage(final Message m, boolean message_recu) {
        mMessage = new Message();
        mMessage.InstantiateByMessage(m);

        mTextViewCustomDialogMessageTitleAnnonce.setText(mMessage.getTitre_annonce());
        mEditTextCustomDialogMessageMessage.setText(mMessage.getContenu_message());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String DateMessage = sdf.format(m.getDate_create_message());

        mTextViewCustomDialogMessageDate.setText(DateMessage);
        mTableRowDate.setVisibility(View.VISIBLE);
        mButtonCustomDialogMessageSent.setVisibility(View.GONE);

        mEditTextCustomDialogMessageMessage.setEnabled(false);
        mEditTextCustomDialogMessageMessage.setBackgroundColor(mActivity.getResources().getColor(R.color.white_transparent));

        if(message_recu){
            imageLoader.displayImage(mMessage.getUrl_image_emetteur(), mImageViewCustomDialogMessageUser);
            mTextViewCustomDialogMessageFromTo.setText("De :");
            mTextViewCustomDialogMessageUserName.setText(mMessage.getNomPrenom_emetteur());
            mButtonCustomDialogMessageReply.setVisibility(View.VISIBLE);
        }
        else {
            imageLoader.displayImage(mMessage.getUrl_image_destinataire(), mImageViewCustomDialogMessageUser);
            mTextViewCustomDialogMessageFromTo.setText("A :");
            mTextViewCustomDialogMessageUserName.setText(mMessage.getNomPrenom_destinataire());
            mButtonCustomDialogMessageReply.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.buttonCustomDialogMessageSent:
                    //Toast.makeText(mActivity.getApplicationContext(), "Envoi en cours...", Toast.LENGTH_SHORT).show();

                    envoiMessage();

                    break;

                case R.id.buttonCustomDialogMessageReply:
                    //Toast.makeText(mActivity.getApplicationContext(), "Réponse...", Toast.LENGTH_SHORT).show();

                    is_a_reply = true;
                    instantiateDialogForReplyAMessage(mMessage);

                    break;

            }
        }
    };


    private void envoiMessage(){
        new AsyncTask<Void, Void, Boolean>() {

            String AfficherJSON = null;
            String ErreurLoginTask = "Erreur ";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mMessage.setContenu_message(mEditTextCustomDialogMessageMessage.getText().toString());
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                /*if(is_a_reply){

                } else {

                }*/

                HttpURLConnection urlConnection = null;
                StringBuilder sb = new StringBuilder();

                try {
                    URL url = new URL(mActivity.getResources().getString(R.string.URL_SERVEUR) + mActivity.getResources().getString(R.string.URL_SERVEUR_ECRIRE_MESSAGE));
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
                    jsonParam.put("Message", mMessage.getContenu_message());
                    jsonParam.put("Id_Annonce", mMessage.getId_annonce_destinataire());
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

                        if(sb.toString().equals("true")){
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
            protected void onPostExecute(Boolean success) {

                //Toast.makeText(mActivity.getApplicationContext(), AfficherJSON, Toast.LENGTH_SHORT).show();

                if (success) {
                    Toast.makeText(mActivity.getApplicationContext(), "Message envoyé", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
                else {
                    Toast.makeText(mActivity.getApplicationContext(), "Erreur d'envoi du message", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(mActivity.getApplicationContext(), ErreurLoginTask, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
