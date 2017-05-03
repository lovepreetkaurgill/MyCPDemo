package com.auribises.mycpdemo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    @InjectView(R.id.editTextName)
    EditText eTxtName;

    @InjectView(R.id.editTextPhone)
    EditText eTxtPhone;

    @InjectView(R.id.editTextEmail)
    EditText eTxtEmail;

    @InjectView(R.id.radioButtonMale)
    RadioButton rbMale;

    @InjectView(R.id.radioButtonFemale)
    RadioButton rbFemale;

    @InjectView(R.id.spinnerCity)
    Spinner spCity;
    ArrayAdapter<String> adapter;

    @InjectView(R.id.buttonSubmit)
    Button btnSubmit;

    Student student,rcvStudent;

    ContentResolver resolver;

    boolean updateMode;

    RequestQueue requestQueue;

    ProgressDialog progressDialog;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        preferences = getSharedPreferences(Util.PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        student = new Student();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
        adapter.add("--Select City--");
        adapter.add("Ludhiana");
        adapter.add("Chandigarh");
        adapter.add("Delhi");
        adapter.add("Bengaluru");
        adapter.add("California");

        spCity.setAdapter(adapter);

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    student.setCity(adapter.getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);

        resolver = getContentResolver();

        requestQueue = Volley.newRequestQueue(this);

        Intent rcv = getIntent();
        updateMode = rcv.hasExtra("keyStudent");


        if(updateMode){
            rcvStudent = (Student)rcv.getSerializableExtra("keyStudent");
            eTxtName.setText(rcvStudent.getName());
            eTxtPhone.setText(rcvStudent.getPhone());
            eTxtEmail.setText(rcvStudent.getEmail());


            if(rcvStudent.getGender().equals("Male")){
                rbMale.setChecked(true);
            }else{
                rbFemale.setChecked(true);
            }

            int p = 0;
            for(int i=0;i<adapter.getCount();i++){
                if(adapter.getItem(i).equals(rcvStudent.getCity())){
                    p = i;
                    break;
                }
            }

            spCity.setSelection(p);

            btnSubmit.setText("Update");
        }
    }

    boolean isNetworkConected(){

        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();


        return (networkInfo!=null && networkInfo.isConnected());

    }

    public void clickHandler(View view){
        if(view.getId() == R.id.buttonSubmit){
            //String name = eTxtName.getText().toString().trim();
            //student.setName(name);
            student.setName(eTxtName.getText().toString().trim());
            student.setPhone(eTxtPhone.getText().toString().trim());
            student.setEmail(eTxtEmail.getText().toString().trim());


            //insertIntoDB();

            if(validateFields()) {
                if (isNetworkConected())
                    insertIntoCloud();
                else
                    Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "Please correct Input", Toast.LENGTH_LONG).show();
            }
        }
    }

    void insertIntoCloud(){

        final String token = FirebaseInstanceId.getInstance().getToken();
        Log.i("TOKEN",token);

        String url="";

        if(!updateMode){
            url = Util.INSERT_STUDENT_PHP;
        }else{
            url = Util.UPDATE_STUDENT_PHP;
        }

        progressDialog.show();

        // Volley String Request
        /*StringRequest request = new StringRequest(Request.Method.GET, Util.INSERT_STUDENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Response: "+response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Some Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    if(success == 1){
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();

                        if(!updateMode){

                            editor.putString(Util.KEY_NAME,student.getName());
                            editor.putString(Util.KEY_PHONE,student.getPhone());
                            editor.putString(Util.KEY_EMAIL,student.getEmail());

                            editor.commit();

                            Intent home = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(home);
                            finish();
                        }

                        if(updateMode)
                            finish();

                    }else{
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Some Exception",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Some Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                if(updateMode)
                    map.put("id",String.valueOf(rcvStudent.getId()));

                map.put("name",student.getName());
                map.put("phone",student.getPhone());
                map.put("email",student.getEmail());
                map.put("gender",student.getGender());
                map.put("city",student.getCity());
                map.put("token",token);
                return map;
            }
        };

        requestQueue.add(request); // execute the request, send it ti server

        clearFields();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();

        if(b) {
            if (id == R.id.radioButtonMale) {
                student.setGender("Male");
            } else {
                student.setGender("Female");
            }
        }
    }

    void insertIntoDB(){

        ContentValues values = new ContentValues();

        values.put(Util.COL_NAME,student.getName());
        values.put(Util.COL_PHONE,student.getPhone());
        values.put(Util.COL_EMAIL,student.getEmail());
        values.put(Util.COL_GENDER,student.getGender());
        values.put(Util.COL_CITY,student.getCity());

        if(!updateMode){
            Uri dummy = resolver.insert(Util.STUDENT_URI,values);
            Toast.makeText(this,student.getName()+ " Registered Successfully "+dummy.getLastPathSegment(),Toast.LENGTH_LONG).show();

            Log.i("Insert",student.toString());

            clearFields();
        }else{
            String where = Util.COL_ID + " = "+rcvStudent.getId();
            int i = resolver.update(Util.STUDENT_URI,values,where,null);
            if(i>0){
                Toast.makeText(this,"Updation Successful",Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

    void clearFields(){
        eTxtName.setText("");
        eTxtEmail.setText("");
        eTxtPhone.setText("");
        spCity.setSelection(0);
        rbMale.setChecked(false);
        rbFemale.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,101,0,"All Students");


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == 101){
            Intent i = new Intent(MainActivity.this,AllStudentsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    boolean validateFields(){
        boolean flag = true;

        if(student.getName().isEmpty()){
            flag = false;
            eTxtName.setError("Please Enter Name");
        }

        if(student.getPhone().isEmpty()){
            flag = false;
            eTxtPhone.setError("Please Enter Phone");
        }else{
            if(student.getPhone().length()<10){
                flag = false;
                eTxtPhone.setError("Please Enter 10 digits Phone Number");
            }
        }

        if(student.getEmail().isEmpty()){
            flag = false;
            eTxtEmail.setError("Please Enter Email");
        }else{
            if(!(student.getEmail().contains("@") && student.getEmail().contains("."))){
                flag = false;
                eTxtEmail.setError("Please Enter correct Email");
            }
        }

        return flag;

    }
}
