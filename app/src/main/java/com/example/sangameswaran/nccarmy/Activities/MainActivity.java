package com.example.sangameswaran.nccarmy.Activities;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sangameswaran.nccarmy.Entities.AssignTaskEntity;
import com.example.sangameswaran.nccarmy.Entities.AttendanceReportEntity;
import com.example.sangameswaran.nccarmy.Entities.ParadeDeclarationEntity;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.AssignTaskFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.ChatFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.CreateNewAdminFragment;
import com.example.sangameswaran.nccarmy.Entities.AdminEntity;
import com.example.sangameswaran.nccarmy.Entities.CadetEntity;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.GrantRevokePermissionFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.MarkAttendanceFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.ParadeTaskReportFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.UnAuthFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.ViewAllTasksFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.ViewCadetsDetailFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.ViewMyTaskFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.ViewParadeOverallReportFragment;
import com.example.sangameswaran.nccarmy.R;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.RegisterCadetFragment;
import com.example.sangameswaran.nccarmy.FragmentsAndAdapters.ViewPermissionChangesFragment;
import com.example.sangameswaran.nccarmy.service.ParadeTimeNotificationService;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Calendar.MINUTE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,OnMapReadyCallback,GoogleMap.OnMapClickListener{

    public CadetEntity register=new CadetEntity();
    private AdminEntity adminEntity=new AdminEntity();
    RelativeLayout rlDashboard,DashboardLoader;
    BarChart overallAttendanceChart;
    PieChart induividualAttendance;
    TextView tvRegisteredImei;
    TextView dbtvParadeName,dbtvAttendance,dbtvTotalHC;
    Map<String,Double> coordinateMap=new HashMap<>();
    TextView tvDCTextView,tvdbDateTextView,tvdbTimeTextView,dbtvSpecialInstructions;
    EditText dcEdit, SpecialInstructionsEdit;
    TextView dateEdit,timeEdit;
    RelativeLayout viewRl,EditRl;
    GoogleMap map;
    Button signoutBtn;
    ArcProgress pendingArcProgress,completedArcProgress;
    View headerView;
    private Button toggler;
    List<AssignTaskEntity> assignedTasks;
    TextView navHeaderUserName,navHeaderAccessLevel;
    private boolean alteringParade=false;
    double lat=0.0;
    double lon=0.0;
    TextView assignNewTaskDb,viewAllTaskDb;
    boolean markedLocation=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assignedTasks=new ArrayList<>();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        changeLoginFlag("1");
        toggle.syncState();
        tvDCTextView=(TextView)findViewById(R.id.tvdbDc);
        tvdbDateTextView=(TextView)findViewById(R.id.tvdbDate);
        tvdbTimeTextView=(TextView)findViewById(R.id.tvdbTime);
        dbtvSpecialInstructions=(TextView)findViewById(R.id.dbtvSpecialInstructions);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView=navigationView.getHeaderView(0);
        toggler=(Button)findViewById(R.id.btnAlterParade);
        viewRl=(RelativeLayout)findViewById(R.id.viewRl);
        EditRl=(RelativeLayout)findViewById(R.id.editRl);
        dcEdit =(EditText)findViewById(R.id.dcEdit);
        dateEdit=(TextView)findViewById(R.id.dateEdit);
        timeEdit=(TextView)findViewById(R.id.timeEdit);
        pendingArcProgress=(ArcProgress)findViewById(R.id.pendingArcProgress);
        completedArcProgress=(ArcProgress)findViewById(R.id.completedArcProgress);
        SpecialInstructionsEdit =(EditText)findViewById(R.id.specialInstructionsEdit);
        assignNewTaskDb=(TextView)findViewById(R.id.assignNewTaskDb);
        viewAllTaskDb=(TextView)findViewById(R.id.viewAllTaskDb);
        toggler.setText("Alter");
        navigationView.setNavigationItemSelectedListener(this);
        Gson gs=new Gson();
        try {
            SharedPreferences sp=getSharedPreferences("userDetails",MODE_PRIVATE);
            String jsonString=sp.getString("user","throwRuntimeException");
            adminEntity=gs.fromJson(jsonString,AdminEntity.class);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Cast Exception",Toast.LENGTH_LONG).show();
            finishAffinity();
        }
        if(adminEntity.getIsSuperAdmin().equals("1")){

        }else {
            UnAuthFragment fragment=new UnAuthFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
        }
        navHeaderUserName=(TextView)headerView.findViewById(R.id.nav_header_user_name);
        navHeaderAccessLevel=(TextView)headerView.findViewById(R.id.nav_header_access_level);
        signoutBtn=(Button)headerView.findViewById(R.id.signoutBtn);
        tvRegisteredImei=(TextView)headerView.findViewById(R.id.tvRegisteredImei);
        navHeaderUserName.setText(adminEntity.getUser_name());
        if(adminEntity.getIsSuperAdmin().equals("1"))
            navHeaderAccessLevel.setText("Super Admin");
        else if (adminEntity.getIsAdmin().equals("1"))
            navHeaderAccessLevel.setText("Admin");
        else
            navHeaderAccessLevel.setText("Cadet");
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                finishAffinity();
            }
        });
        TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tvRegisteredImei.setText("Device IMEI : "+tm.getDeviceId());
        rlDashboard= (RelativeLayout) findViewById(R.id.rlDashboard);
        DashboardLoader=(RelativeLayout)findViewById(R.id.DashboardLoader);
        induividualAttendance=(PieChart)findViewById(R.id.induividualParadeOverView);
        dbtvAttendance=(TextView)findViewById(R.id.dbtvAttendance);
        dbtvParadeName=(TextView)findViewById(R.id.dbtvParadeName);
        dbtvTotalHC=(TextView)findViewById(R.id.dbtvTotalCount);
        overallAttendanceChart=(BarChart)findViewById(R.id.overallAttendanceChart);
        viewAllTaskDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewAllTasksFragment fragment=new ViewAllTasksFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        });
        assignNewTaskDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignTaskFragment fragment=new AssignTaskFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        });
        DatabaseReference getAllTasks=FirebaseDatabase.getInstance().getReference("AssignedTasks");
        getAllTasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot iterator : dataSnapshot.getChildren()) {
                        try{
                            assignedTasks.add(iterator.getValue(AssignTaskEntity.class));
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Exception in parsing",Toast.LENGTH_LONG).show();
                            completedArcProgress.setProgress(0);
                            pendingArcProgress.setProgress(0);
                        }
                    }
                    int completedCount=0,pendingCount=0;
                    for(AssignTaskEntity entity:assignedTasks){
                        if(entity.getStatus().equals("100")){
                            completedCount++;
                        }
                        else
                            pendingCount++;
                    }
                    int completedCountInPercent=(completedCount*100/(completedCount+pendingCount));
                    int pendingCountInPercent=(pendingCount*100/(completedCount+pendingCount));
                    completedArcProgress.setProgress(completedCountInPercent+1);
                    pendingArcProgress.setProgress(pendingCountInPercent);
                }
                else {
                    completedArcProgress.setProgress(0);
                    pendingArcProgress.setProgress(0);
                    Toast.makeText(getApplicationContext(),"No Assigned Tasks found",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent serviceIntent=new Intent(this, ParadeTimeNotificationService.class);
        startService(serviceIntent);
        final String[] allDates=new String[1000];
        DatabaseReference getOverallAttendanceCoordinatesApi= FirebaseDatabase.getInstance().getReference("PARADE_REPORT");
        getOverallAttendanceCoordinatesApi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    int i=0;
                    for(DataSnapshot iterator:dataSnapshot.getChildren()){
                        try{
                            AttendanceReportEntity entity=iterator.getValue(AttendanceReportEntity.class);
                            String replace = entity.getPercentage_of_present().replace("%", "");
                            double percent=Double.parseDouble(replace);
                            String paradeDate=iterator.getKey();
                            allDates[i]=paradeDate;
                            coordinateMap.put(paradeDate,percent);
                            i++;
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Parse Error in Reading Values",Toast.LENGTH_LONG).show();
                        }
                    }
                    List<Integer> colours=new ArrayList<Integer>();
                    List<BarEntry> coordinates=new ArrayList<BarEntry>();
                    for(int iter=0;iter<coordinateMap.size();iter++){
                        coordinates.add(new BarEntry((float) iter,coordinateMap.get(allDates[iter]).floatValue()));
                        if(coordinateMap.get(allDates[iter]).floatValue()>=80.0f)
                            colours.add(Color.parseColor("#60a844"));
                        else if(coordinateMap.get(allDates[iter]).floatValue()>=40.0f)
                            colours.add(Color.parseColor("#dcdc39"));
                        else
                            colours.add(Color.parseColor("#ff5050"));
                        Log.d("CoordinateTest","x="+iter+",y="+coordinateMap.get(allDates[iter]).floatValue());
                    }
                    BarDataSet set=new BarDataSet(coordinates,"Attendance");
                    set.setColors(colours);
                    BarData data=new BarData(set);
                    data.setBarWidth(0.9f);
                    IAxisValueFormatter formatter=new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return "D+"+(int)value;
                        }
                    };
                    XAxis xAxis=overallAttendanceChart.getXAxis();
                    xAxis.setValueFormatter(formatter);
                    Legend le=overallAttendanceChart.getLegend();
                    le.setEnabled(false);
                    overallAttendanceChart.setFitBars(false);
                    overallAttendanceChart.setData(data);
                    overallAttendanceChart.invalidate();
                    overallAttendanceChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(final Entry e, Highlight h) {
                            Toast.makeText(getApplicationContext(),allDates[(int) (e.getX())],Toast.LENGTH_LONG).show();
                            DatabaseReference getParadeDetailsApi=FirebaseDatabase.getInstance().getReference("PARADE_REPORT/"+allDates[(int)(e.getX())]);
                            getParadeDetailsApi.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChildren()){
                                        try{
                                            AttendanceReportEntity ent=dataSnapshot.getValue(AttendanceReportEntity.class);
                                            int emePresent=Integer.parseInt(ent.getEMEpresentCount());
                                            int engPresent=Integer.parseInt(ent.getENGpresentCount());
                                            int sigPresent=Integer.parseInt(ent.getSIGpresentCount());
                                            float eme=((float) ((float)emePresent/(emePresent+engPresent+sigPresent)))*100;
                                            float eng=((float) ((float)engPresent/(emePresent+engPresent+sigPresent)))*100;
                                            float sig=((float) ((float)sigPresent/(emePresent+engPresent+sigPresent)))*100;
                                            List<PieEntry> pieEntry=new ArrayList<PieEntry>();
                                            pieEntry.add(new PieEntry(eme,"EME"));
                                            pieEntry.add(new PieEntry(eng,"ENGINEERS"));
                                            pieEntry.add(new PieEntry(sig,"SIGNALS"));
                                            PieDataSet s=new PieDataSet(pieEntry,"PRESENT PERCENT");
                                            List<Integer> col=new ArrayList<Integer>();
                                            col.add(Color.parseColor("#60a844"));
                                            col.add(Color.parseColor("#dcdc39"));
                                            col.add(Color.parseColor("#ff0000"));
                                            s.setColors(col);
                                            PieData d=new PieData(s);
                                            induividualAttendance.setData(d);
                                            dbtvAttendance.setText(ent.getPercentage_of_present());
                                            dbtvTotalHC.setText(ent.getOverall_present_count());
                                            dbtvParadeName.setText(allDates[(int)(e.getX())]);
                                            Legend l=induividualAttendance.getLegend();
                                            l.setEnabled(false);
                                            induividualAttendance.invalidate();
                                        }catch (Exception e){
                                            Log.d("EXCEPTION",e.getMessage().toString());
                                        }
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Selected parade details doesnot exist",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected() {
                            //inflate allDates[0] view.
                            loadFirstEntry(allDates);
                        }
                    });
                    loadFirstEntry(allDates);
                    DashboardLoader.setVisibility(View.GONE);
                    rlDashboard.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(getApplicationContext(),"Entry empty",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
            }
        });

        toggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!alteringParade) {
                    toggler.setText("Update");
                    alteringParade = true;
                    map.clear();
                    viewRl.setVisibility(View.GONE);
                    EditRl.setVisibility(View.VISIBLE);
                }else {
                    String dc=dcEdit.getText().toString();
                    String date=dateEdit.getText().toString();
                    String time=timeEdit.getText().toString();
                    String splIns=SpecialInstructionsEdit.getText().toString();
                    if((dc.equals(""))||(date.equals(""))||(time.equals(""))||(splIns.equals(""))){
                        Toast.makeText(getApplicationContext(),"Enter all details to update",Toast.LENGTH_LONG).show();
                    }
                    else {
                        if(markedLocation){
                        alteringParade=false;
                        toggler.setText("Alter");
                        viewRl.setVisibility(View.VISIBLE);
                        EditRl.setVisibility(View.GONE);
                        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference("ParadeDetails");
                        ParadeDeclarationEntity entity=new ParadeDeclarationEntity();
                        entity.setDate(date);
                        entity.setTime(time);
                        entity.setLatitude(lat);
                        entity.setLongitude(lon);
                        entity.setDress_code(dc);
                        entity.setSpecial_instructions(splIns);
                        mDatabase.setValue(entity);
                        Toast.makeText(getApplicationContext(),"Successfully Updated",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Touch map to confirm Location",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date =String.valueOf(year) +"/"+String.valueOf(monthOfYear)
                                +"/"+String.valueOf(dayOfMonth);
                        dateEdit.setText(date);

                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hh = calendar.get(Calendar.HOUR_OF_DAY);
                int mm = calendar.get(MINUTE);
                TimePickerDialog timePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
                        timeEdit.setText(time);
                    }
                },hh,mm,true);
                timePicker.show();
            }
        });
    }

    private void loadFirstEntry(final String [] allDates) {
        DatabaseReference getParadeDetailsApi=FirebaseDatabase.getInstance().getReference("PARADE_REPORT/"+allDates[0]);
        getParadeDetailsApi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    try{
                        AttendanceReportEntity ent=dataSnapshot.getValue(AttendanceReportEntity.class);
                        int emePresent=Integer.parseInt(ent.getEMEpresentCount());
                        int engPresent=Integer.parseInt(ent.getENGpresentCount());
                        int sigPresent=Integer.parseInt(ent.getSIGpresentCount());
                        float eme=((float) ((float)emePresent/(emePresent+engPresent+sigPresent)))*100;
                        float eng=((float) ((float)engPresent/(emePresent+engPresent+sigPresent)))*100;
                        float sig=((float) ((float)sigPresent/(emePresent+engPresent+sigPresent)))*100;
                        List<PieEntry> pieEntry=new ArrayList<PieEntry>();
                        pieEntry.add(new PieEntry(eme,"EME"));
                        pieEntry.add(new PieEntry(eng,"ENGINEERS"));
                        pieEntry.add(new PieEntry(sig,"SIGNALS"));
                        PieDataSet s=new PieDataSet(pieEntry,"PRESENT PERCENT");
                        List<Integer> col=new ArrayList<Integer>();
                        col.add(Color.parseColor("#60a844"));
                        col.add(Color.parseColor("#dcdc39"));
                        col.add(Color.parseColor("#ff0000"));
                        s.setColors(col);
                        PieData d=new PieData(s);
                        induividualAttendance.setData(d);
                        dbtvAttendance.setText(ent.getPercentage_of_present());
                        dbtvTotalHC.setText(ent.getOverall_present_count());
                        dbtvParadeName.setText(allDates[0]);
                        Legend l=induividualAttendance.getLegend();
                        l.setEnabled(false);
                        induividualAttendance.invalidate();
                    }catch (Exception e){
                        Log.d("EXCEPTION",e.getMessage().toString());
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Selected parade details doesnot exist",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            finishAffinity();

        } else {
            drawer.openDrawer(GravityCompat.START);
            Toast.makeText(getApplicationContext(),"Press back oncemore to exit app",Toast.LENGTH_LONG).show();

        }
    }

    private void killSession(String user_name) {
        DatabaseReference killSession=FirebaseDatabase.getInstance().getReference("ACTIVE_SESSION");
        killSession.child(user_name).setValue(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_camera) {
            if(adminEntity.getIsAdmin().equals("1")) {
                CreateNewAdminFragment fragment = new CreateNewAdminFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }

        } else if (id == R.id.nav_gallery) {
            if(adminEntity.getIsAdmin().equals("1")) {
                GrantRevokePermissionFragment fragment = new GrantRevokePermissionFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }
        else if (id == R.id.register_cadet) {
            if(adminEntity.getIsAdmin().equals("1")) {
                RegisterCadetFragment cadetFragment = new RegisterCadetFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, cadetFragment).commit();
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }
        else if (id==R.id.pchange)
        {   if (adminEntity.getIsSuperAdmin().equals("1")) {
            ViewPermissionChangesFragment fr = new ViewPermissionChangesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fr).commit();
        }else {
            UnAuthFragment fragment=new UnAuthFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
        }
        }
        else if (id==R.id.viewCadetsDetail)
        {
            if (adminEntity.getIsSuperAdmin().equals("1")){
            ViewCadetsDetailFragment fragment=new ViewCadetsDetailFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
        }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }

        else if (id==R.id.attendance)
        {   if(adminEntity.getIsAdmin().equals("1")) {
            MarkAttendanceFragment fragment = new MarkAttendanceFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }else {
            UnAuthFragment fragment=new UnAuthFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
        }
        }
        else if (id==R.id.viewAttendanceReport)
        {
            if (adminEntity.getIsSuperAdmin().equals("1")) {
                ViewParadeOverallReportFragment fragment = new ViewParadeOverallReportFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }
        else if (id==R.id.paradeTask){
            if(adminEntity.getIsAdmin().equals("1")){
                ParadeTaskReportFragment fragment=new ParadeTaskReportFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }
        else if (id==R.id.dashboard){
            if(adminEntity.getIsSuperAdmin().equals("1")){
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }else if (id==R.id.chat){
            if(adminEntity.getIsSuperAdmin().equals("1")) {
                AssignTaskFragment fragment = new AssignTaskFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }else if(id==R.id.viewAllTaskHamburger){
            if(adminEntity.getIsSuperAdmin().equals("1")) {
                ViewAllTasksFragment fragment = new ViewAllTasksFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            }else {
                UnAuthFragment fragment=new UnAuthFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
            }
        }else if(id==R.id.viewMyTaskHamburger){
            ViewMyTaskFragment fragment=new ViewMyTaskFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void changeLoginFlag(final String flag)
    {
        SharedPreferences sp=getSharedPreferences("LoginCredentials",MODE_PRIVATE);
        final String user=sp.getString("MyloginID","Error");
        final DatabaseReference myref=FirebaseDatabase.getInstance().getReference("Admins/"+user);
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("tag","Admins/"+user);
                if (dataSnapshot.hasChildren())
                {
                    AdminEntity entity=new AdminEntity();
                    entity=dataSnapshot.getValue(AdminEntity.class);
                    entity.setLogin_flag(flag);
                    myref.setValue(entity);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Connectivity Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (!alteringParade){
         DatabaseReference getParadeDetails=FirebaseDatabase.getInstance().getReference("ParadeDetails");
            getParadeDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        try {
                            ParadeDeclarationEntity entity = dataSnapshot.getValue(ParadeDeclarationEntity.class);
                            map.addMarker(new MarkerOptions().position(new LatLng(entity.getLatitude(),entity.getLongitude())).title("Parade Location"));
                            tvDCTextView.setText(entity.getDress_code());
                            tvdbDateTextView.setText(entity.getDate());
                            tvdbTimeTextView.setText(entity.getTime());
                            dbtvSpecialInstructions.setText(entity.getSpecial_instructions());
                            MoveAndAnimateCamera(new LatLng(entity.getLatitude(),entity.getLongitude()),15);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(alteringParade){
            map.clear();
            map.addMarker(new MarkerOptions().position(latLng));
            markedLocation=true;
            lat=latLng.latitude;
            lon=latLng.longitude;
        }
    }
    public void MoveAndAnimateCamera(LatLng place,int ZoomLevel) {
        if(map!=null){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, ZoomLevel-1));
            map.animateCamera(CameraUpdateFactory.zoomTo(ZoomLevel), 2000, null);
        }
    }
    public static boolean percentValidation(int percent){
       return percent>=0&&percent<=100;
    }
    void signOut()
    {
        changeLoginFlag("0");
        killSession(adminEntity.getUser_name());
        SharedPreferences sp=getSharedPreferences("LoginCredentials",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("MyloginID","NA");
        editor.putString("password","NA");
        editor.commit();

    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
