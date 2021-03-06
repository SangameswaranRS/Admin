package com.example.sangameswaran.nccarmy.FragmentsAndAdapters;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sangameswaran.nccarmy.Entities.AttendanceEntity;
import com.example.sangameswaran.nccarmy.Entities.AttendanceReportEntity;
import com.example.sangameswaran.nccarmy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sangameswaran on 12-05-2017.
 */

public class MarkAttendanceFragment extends Fragment{
   TextView date;
    Spinner yearOfJoining;
    Button markAttendance;
    Button generateReport;
    RecyclerView recyclerView;
    RelativeLayout loader2;
    TextView prompt1,prompt2;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ArrayList<AttendanceEntity> arrayList=new ArrayList<>();

    ProgressDialog reportProgress;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.mark_attendance_fragment_layout,container,false);
        date=(TextView) v.findViewById(R.id.etDate);
        prompt1=(TextView)v.findViewById(R.id.prompt1);
        getActivity().setTitle("Mark Attendance");
        prompt2=(TextView)v.findViewById(R.id.prompt2);
        yearOfJoining=(Spinner)v.findViewById(R.id.yearSelectionSpinner);
        loader2=(RelativeLayout) v.findViewById(R.id.loader2);
        generateReport=(Button)v.findViewById(R.id.btnGenerateReport);
        markAttendance=(Button)v.findViewById(R.id.btnViewAttendance);
        reportProgress=new ProgressDialog(getActivity());
        reportProgress.setMessage("Report generating..");
        recyclerView=(RecyclerView)v.findViewById(R.id.attendanceRecyclerView);
        layoutManager=new LinearLayoutManager(getActivity());
        List<String> yoj=new ArrayList<>();
        yoj.add("2015");
        yoj.add("2016");
        yoj.add("2017");
        yoj.add("2018");
        yoj.add("2019");
        yoj.add("2020");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, yoj);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearOfJoining.setAdapter(dataAdapter);
        date.setOnClickListener(new View.OnClickListener() {
            Calendar c=Calendar.getInstance();
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(dayOfMonth+"-"+String.valueOf(month+1)+"-"+year+"-");
                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                datePickerDialog.setMessage("Select parade date");
                datePickerDialog.setCancelable(false);
                datePickerDialog.show();
            }
        });
        markAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s1,s2;
                s1=date.getText().toString();
                s2=String.valueOf(yearOfJoining.getSelectedItem());
                if(s1.equals(""))
                    date.setError("Enter date");
                if(s1.equals("")||s2.equals(""))
                {
                    Toast.makeText(getActivity(),"Select date to continue..",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(ValidateDate(s1))
                    {loader2.setVisibility(View.VISIBLE);
                    DatabaseReference maintainEMECount=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountEMEPresent");
                    maintainEMECount.setValue("0");
                    DatabaseReference maintainENGcount=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountENGPresent");
                    maintainENGcount.setValue("0");
                    DatabaseReference maintainSIGcount=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountSIGPresent");
                    maintainSIGcount.setValue("0");
                    DatabaseReference maintainCount=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountParadePresent");
                    maintainCount.setValue("0");
                    DatabaseReference maintainEMECount1=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountEMEAbsent");
                    maintainEMECount1.setValue("0");
                    DatabaseReference maintainENGcount2=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountENGAbsent");
                    maintainENGcount2.setValue("0");
                    DatabaseReference maintainSIGcount3=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountSIGAbsent");
                    maintainSIGcount3.setValue("0");
                    DatabaseReference maintainCount4=FirebaseDatabase.getInstance().getReference("PARADE/"+s1+s2+"/CountParadeAbsent");
                    maintainCount4.setValue("0");
                    arrayList.clear();
                    DatabaseReference my= FirebaseDatabase.getInstance().getReference("Attendance Data/"+s2);
                    my.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren())
                            {
                                generateReport.setVisibility(View.VISIBLE);
                                markAttendance.setVisibility(View.GONE);
                                date.setVisibility(View.GONE);
                                yearOfJoining.setVisibility(View.GONE);
                                prompt2.setVisibility(View.GONE);
                                prompt1.setVisibility(View.GONE);
                                for(DataSnapshot dsp : dataSnapshot.getChildren())
                                {
                                    AttendanceEntity entity=new AttendanceEntity();
                                    entity=dsp.getValue(AttendanceEntity.class);
                                    arrayList.add(entity);
                                    adapter=new AttendanceRecyclerViewAdapter(arrayList,s1,s2,getContext());
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(layoutManager);
                                    adapter.notifyDataSetChanged();
                                    markAttendance.setClickable(false);
                                    loader2.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Specified year doesnot exist",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                    {
                        Toast.makeText(getActivity(),"Invalid Date Format,Try again",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        adapter=new AttendanceRecyclerViewAdapter(arrayList,date.getText().toString(),String.valueOf(yearOfJoining.getSelectedItem()),getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();

        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportProgress.show();
                final AttendanceReportEntity entity=new AttendanceReportEntity();
                DatabaseReference m1=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountEMEPresent");
                m1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String emePresent=dataSnapshot.getValue(String.class);
                        entity.setEMEpresentCount(emePresent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference m2=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountEMEAbsent");
                m2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String emeAbsent=dataSnapshot.getValue(String.class);
                        entity.setEMEAbsentCount(emeAbsent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference m3=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountENGPresent");
                m3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String engPresent=dataSnapshot.getValue(String.class);
                        entity.setENGpresentCount(engPresent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference m4=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountENGAbsent");
                m4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String engAbsent=dataSnapshot.getValue(String.class);
                        entity.setENGabsentCount(engAbsent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference m5=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountSIGPresent");
                m5.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String sigPresent=dataSnapshot.getValue(String.class);
                        entity.setSIGpresentCount(sigPresent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference m6=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountSIGAbsent");
                m6.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String sigAbsent=dataSnapshot.getValue(String.class);
                        entity.setSIGabsentCount(sigAbsent);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference m7=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountParadeAbsent");
                m7.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ParadeAbsent=dataSnapshot.getValue(String.class);
                        entity.setOverall_absent_count(ParadeAbsent);
                        Log.d("tag",ParadeAbsent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                DatabaseReference m8=FirebaseDatabase.getInstance().getReference("PARADE/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem())+"/CountParadePresent");
                m8.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ParadePresent=dataSnapshot.getValue(String.class);
                        entity.setOverall_present_count(ParadePresent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         try{
                         int a=Integer.parseInt(entity.getOverall_present_count());
                         int b=Integer.parseInt(entity.getOverall_absent_count());
                         int c=a+b;
                         double d=(double)a/c;
                         Log.d("Tag","d="+d+"c="+c+"a="+a);
                         d*=100;
                             entity.setBatch(String.valueOf(yearOfJoining.getSelectedItem()));
                         entity.setPercentage_of_present(""+d+"%");
                         DatabaseReference myReference=FirebaseDatabase.getInstance().getReference("PARADE_REPORT/"+date.getText().toString()+String.valueOf(yearOfJoining.getSelectedItem()));
                         myReference.setValue(entity);
                         reportProgress.dismiss();
                         Toast.makeText(getActivity(),"Report generated",Toast.LENGTH_LONG).show();
                         } catch (Exception e){
                             Toast.makeText(getActivity(),"Server Error, Try Again",Toast.LENGTH_LONG).show();
                         }
                         MarkAttendanceFragment fragment=new MarkAttendanceFragment();
                         FragmentManager fragmentManager = getFragmentManager();
                         FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                         fragmentTransaction.replace(R.id.content_main,fragment).commit();

                     }
                 },5000);


            }

        });
        return v;
    }
    public boolean ValidateDate(String date)
    {
        boolean flag=false;
        if(date.contains("/")||date.contains("."))
            flag= false;

        else
            flag=true;

        return flag;
    }
}
