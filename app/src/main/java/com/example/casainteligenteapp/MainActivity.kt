package com.example.casainteligenteapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    lateinit var conexion_status: TextView
    lateinit var conexion_status2: TextView

    lateinit var btn_led1: Button
    lateinit var btn_led2: Button
    lateinit var btn_led3: Button
    lateinit var btn_led4: Button
    lateinit var btn_led5: Button

    lateinit var btn_esp32_conexion: Button

    lateinit var btn_puerta_open: Button
    lateinit var btn_puerta_close: Button

    lateinit var btn_cochera_open: Button
    lateinit var btn_cochera_close: Button

    lateinit var intensitybar_led1 : SeekBar
    lateinit var intensitybar_led2 : SeekBar
    lateinit var intensitybar_led3 : SeekBar
    lateinit var intensitybar_led4 : SeekBar
    lateinit var intensitybar_led5 : SeekBar

    lateinit var btn_fan: Button
    var fan : Int = 5000
    var led1 : Int = 1
    var led2 : Int = 3
    var led3 : Int = 5
    var led4 : Int = 7
    var led5 : Int = 9

    var s1 : Int = 2000
    var s2 : Int = 2200


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database = FirebaseDatabase.getInstance()
        conexion_status = findViewById(R.id.conexion_status)
        conexion_status2 = findViewById(R.id.conexion_status2)
        btn_led1 = findViewById(R.id.led1)
        btn_led2 = findViewById(R.id.led2)
        btn_led3 = findViewById(R.id.led3)
        btn_led4 = findViewById(R.id.led4)
        btn_led5 = findViewById(R.id.led5)

        btn_esp32_conexion = findViewById(R.id.btn_esp32conexion)
        btn_fan = findViewById(R.id.fan)
        btn_puerta_open = findViewById(R.id.btnservopuertaopen)
        btn_puerta_close = findViewById(R.id.btnservopuertaclose)
        btn_cochera_open = findViewById(R.id.btnservococheraopen)
        btn_cochera_close = findViewById(R.id.btnservococheraclose)

        intensitybar_led1 = findViewById(R.id.intensitybarLed1);
        intensitybar_led2 = findViewById(R.id.intensitybarLed2);
        intensitybar_led3 = findViewById(R.id.intensitybarLed3);
        intensitybar_led4 = findViewById(R.id.intensitybarLed4);
        intensitybar_led5 = findViewById(R.id.intensitybarLed5);




        btn_led1.setBackgroundColor(Color.RED)
        btn_led2.setBackgroundColor(Color.RED)
        btn_led3.setBackgroundColor(Color.RED)
        btn_led4.setBackgroundColor(Color.RED)
        btn_led5.setBackgroundColor(Color.RED)
        btn_fan.setBackgroundColor(Color.RED)

        intensitybar_led1.setProgress(255);

        intensitybar_led2.setProgress(255);
        intensitybar_led3.setProgress(255);
        intensitybar_led4.setProgress(255);
        intensitybar_led5.setProgress(255);


        val connectedRef = database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    conexion_status2.text = "Conectado a BD"
                    var refConexion = database.getReference("/CONEXION/STATUS")
                    refConexion.setValue(0);
                } else {
                    conexion_status2.text = "Desconectado de BD"
                    var refConexion = database.getReference("/CONEXION/STATUS")
                    refConexion.setValue(0);
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        var myRef = database.getReference("/LEDS/BOTONLED1")
        myRef.setValue(led1)
        myRef = database.getReference("/LEDS/BOTONLED2")
        myRef.setValue(led2)
        myRef = database.getReference("/LEDS/BOTONLED3")
        myRef.setValue(led3)
        myRef = database.getReference("/LEDS/BOTONLED4")
        myRef.setValue(led4)
        myRef = database.getReference("/LEDS/BOTONLED5")
        myRef.setValue(led5)


        //
        btn_esp32_conexion.setOnClickListener {
            var refConexion = database.getReference("/CONEXION/STATUS")
            refConexion.setValue(0);
        }

        //REFERENCIAS Y LISTENERS A SERVOS

        var refS1 = database.getReference("/SERVOS/S1")

        var refS2 = database.getReference("/SERVOS/S2")

        val s1Listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                 var aux = dataSnapshot.getValue().toString()
                s1 = aux.toInt()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        refS1.addListenerForSingleValueEvent(s1Listener)

        val s2Listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var aux = dataSnapshot.getValue().toString()
                s2 = aux.toInt()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        refS2.addListenerForSingleValueEvent(s2Listener)

        //FUNCION DE CONEXION
        var refConexion = database.getReference("/CONEXION/STATUS")
        refConexion.setValue(0);
        val conexionListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var aux = dataSnapshot.getValue().toString()
                conexion_status.setText( aux);
                 if(aux.toInt()==1){
                    conexion_status.text =  "El esp32 está conectado"

                 }else{
                    conexion_status.text =  "El esp32 está desconectado"
                 }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        refConexion.addValueEventListener(conexionListener)

        /*

        myRef = database.getReference("/CONEXION/STATUS ")

                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot){
                        conexion_status.text = "Hay conexion"
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value

                    }
                })
    */
        btn_fan.setOnClickListener {
            val myRef = database.getReference("/VENTILADOR/ESTADO")
            if(fan==5001){
                btn_fan.setBackgroundColor(Color.RED)
                fan=5000
            }else{
                fan=5001
                btn_fan.setBackgroundColor(Color.GREEN)
            }
            myRef.setValue(fan)
        }
        btn_led1.setOnClickListener {
            val myRef = database.getReference("/LEDS/BOTONLED1")
            if(led1==1) {
                led1=2;
            }else{
                led1=1;
            }
            myRef.setValue(led1)
            if(led1==2){
                btn_led1.setBackgroundColor(Color.GREEN)
            }else{
                btn_led1.setBackgroundColor(Color.RED)
            }
        }
        btn_led2.setOnClickListener {
            val myRef = database.getReference("/LEDS/BOTONLED2")
            if(led2==3){
                led2=4;
            }else{
                led2=3;
            }
            myRef.setValue(led2)
            if(led2==4){
                btn_led2.setBackgroundColor(Color.GREEN)
            }else{
                btn_led2.setBackgroundColor(Color.RED)
            }
        }
        btn_led3.setOnClickListener {
            val myRef = database.getReference("/LEDS/BOTONLED3")
            if(led3==5){
                led3=6
            }else{
                led3=5
            }
            myRef.setValue(led3)
            if(led3==6){
                btn_led3.setBackgroundColor(Color.GREEN)
            }else{
                btn_led3.setBackgroundColor(Color.RED)
            }
        }

        btn_led4.setOnClickListener {
            val myRef = database.getReference("/LEDS/BOTONLED4")
            if(led4==7){
                led4=8
            }else{
                led4=7
            }
            myRef.setValue(led4)
            if(led4==8){
                btn_led4.setBackgroundColor(Color.GREEN)
            }else{
                btn_led4.setBackgroundColor(Color.RED)
            }
        }

        btn_led5.setOnClickListener {
            val myRef = database.getReference("/LEDS/BOTONLED5")
            if(led5==9){
                led5=10
            }else{
                led5=9
            }
            myRef.setValue(led5)
            if(led5==10){
                btn_led5.setBackgroundColor(Color.GREEN)
            }else{
                btn_led5.setBackgroundColor(Color.RED)
            }
        }


        btn_puerta_open.setOnClickListener {
            refS1.setValue(3001)
            s1 = 3001
        }

        btn_puerta_close.setOnClickListener {

            refS1.setValue(3000)
            s1 = 3000
        }

        btn_cochera_open.setOnClickListener {
            refS2.setValue(4001)
            s2 = 4001
        }

        btn_cochera_close.setOnClickListener {
                refS2.setValue(4000)
                s2 = 4000
        }



        intensitybar_led1.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val myRef = database.getReference("/LEDS/INTENSIDADLED1")
                myRef.setValue(progress+255)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }


        })

        intensitybar_led2.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val myRef = database.getReference("/LEDS/INTENSIDADLED2")
                myRef.setValue(progress+511)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }


        })

        intensitybar_led3.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val myRef = database.getReference("/LEDS/INTENSIDADLED3")
                myRef.setValue(progress+767)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }


        })

        intensitybar_led4.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val myRef = database.getReference("/LEDS/INTENSIDADLED4")
                myRef.setValue(progress+1022)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }


        })


        intensitybar_led5.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                val myRef = database.getReference("/LEDS/INTENSIDADLED5")
                myRef.setValue(progress+1277)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }


        })


    }
}