package retrofitnrxjava.com.example.android.retrofitnrxjava

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants

import java.io.File
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream;
import java.util.ArrayList
import java.util.concurrent.Callable

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofitnrxjava.com.example.android.retrofitnrxjava.adapter.WorldPopulationAdapter
import retrofitnrxjava.com.example.android.retrofitnrxjava.contact.Contact
import retrofitnrxjava.com.example.android.retrofitnrxjava.contact.Worldpopulation
import java.io.PrintWriter

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var list: ArrayList<Worldpopulation>? = null
    private val mArrayList = ArrayList<Worldpopulation>()
    internal var contactlist = ArrayList<ContactDetails>()
    internal var observable: Observable<ArrayList<ContactDetails>>? = null;
    private var mRecyclerView: RecyclerView? = null
    private var adapter: WorldPopulationAdapter? = null
    private var buttonContacts: Button? = null
    private var file: File? = null


    val contacts: ArrayList<ContactDetails>
        get() {

            val contactArrayList = ArrayList<ContactDetails>()
            var cursor: Cursor? = null
            val contentResolver = contentResolver


            try {

                cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

            } catch (e: Exception) {

            }

            if (cursor!!.count > 0) {

                while (cursor.moveToNext()) {

                    val contact = ContactDetails()

                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    contact.name = name

                    val phoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))

                    if (phoneNumber > 0) {

                        val phonecursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)

                        while (phonecursor!!.moveToNext()) {

                            val pNumber = phonecursor.getString(phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            contact.number = pNumber
                        }
                        phonecursor.close()
                    }

                    contactArrayList.add(contact)

                }
            }
            writeCsv(contactArrayList)
            zip(file)
            return contactArrayList
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        buttonContacts = findViewById<View>(R.id.buttonFetchContats) as Button
        buttonContacts!!.setOnClickListener(this)
        mRecyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView!!.layoutManager = layoutManager
        loadJSON()
        observable = Observable.fromCallable { contacts }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callObservable()


            } else {
                Toast.makeText(this, "You need to grant permission to fetch contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //function to subscribe to the observable
    private fun callObservable() {

        observable
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<ArrayList<ContactDetails>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(value: ArrayList<ContactDetails>) {

                        contactlist = value

                        //Log.i("MainActivity", "onNext: "+contactlist.size());

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                        //show snackbar on completion
                        val snackbar = Snackbar
                                .make(findViewById<View>(R.id.activity_main) as LinearLayout, "Contacts csv created at path" + file!!.path, Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                })

    }
    //Make API call using retrofit and rxjava2 and load the data into mArrayList
    private fun loadJSON() {

        val requestInterface : RequestInterface = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)
        Log.e("RI", requestInterface.toString())

        val observable = requestInterface.register()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        Log.e("RI", observable.toString())
        observable.subscribe(object : Observer<Contact> {
            override fun onSubscribe(d: Disposable) {

                Log.e("Error", "Yes")

            }

            override fun onNext(value: Contact) {
                Log.e("onNext", "entered")
                Log.e("Value is", value.toString())
                list = ArrayList(value.worldpopulation)
                //val l = if (value.worldpopulation != null) value.worldpopulation?.size else -1

                for (i in 0..value.worldpopulation?.size!!-1) {
                    val nameList = value.worldpopulation;
                    val model = Worldpopulation()
                    model.flag = nameList?.get(i)?.flag
                    model.country = nameList?.get(i)?.country
                    model.rank = nameList?.get(i)?.rank
                    model.population = nameList?.get(i)?.population
                    //Log.e("Url is: ",value.getPhotos().get(i).getUrl());
                    mArrayList.add(model)
                }

            }

            override fun onError(e: Throwable) {
                Log.e("Error ", e.localizedMessage)

            }
            //set the adapter when the data is loaded
            override fun onComplete() {
                adapter = WorldPopulationAdapter(mArrayList)
                mRecyclerView!!.adapter = adapter
            }

        })
    }

    //this function triggers on click of the button to fetch contacts
    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonFetchContats ->
                //request user permission for reading contacts
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
                } else {
                    callObservable()
                }
        }
    }
    //this function creates a csv file and stores the contact details to it
    fun writeCsv(list: ArrayList<ContactDetails>) {

        file = File(this.getExternalFilesDir(null), "contacts.csv")
        val writer = PrintWriter(file)

        //var outputStream: FileOutputStream? = null
        try {
           // outputStream = FileOutputStream(file!!, true)
            var i = 0
            while (i < list.size) {
                writer.append(list[i].name + ",")
                writer.append(list[i].number + "\n")
                //outputStream.write((list[i].name + ",").)
                //outputStream.write((list[i].number + "\n").ByteArray())
                i += 1
            }
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    //this function creates a zip file containing the csv file of contacts.
    fun zip(file: File?) {
        try {

            val zipFile = ZipFile(this.getExternalFilesDir(null)!!.toString() + "/contacts.zip")
            val parameters = ZipParameters()
            parameters.compressionMethod = Zip4jConstants.COMP_DEFLATE
            parameters.compressionLevel = Zip4jConstants.DEFLATE_LEVEL_NORMAL
            zipFile.addFile(file, parameters)
            //Log.i("MainActivity", "zip: zippy");

        } catch (e: net.lingala.zip4j.exception.ZipException) {
            e.printStackTrace()
        }

    }

    companion object {

        val BASE_URL = "http://www.androidbegin.com/tutorial/"
        private val PERMISSIONS_REQUEST_READ_CONTACTS = 1
    }
}
