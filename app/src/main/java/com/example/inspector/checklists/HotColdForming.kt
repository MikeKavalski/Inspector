package com.example.inspector.checklists

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.example.inspector.R
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.text.*
import android.view.*
import androidx.appcompat.app.ActionBar
import com.example.inspector.databinding.ActivityHoldColdFormingBinding


class HotColdForming : AppCompatActivity() {
    private lateinit var binding: ActivityHoldColdFormingBinding
    private lateinit var commentFields:Array<EditText>
    private lateinit var acceptedBtns:Array<RadioButton>
    private lateinit var rejectedBtns:Array<RadioButton>
    private lateinit var notApplicableBtns:Array<RadioButton>
    private lateinit var checkCards:Array<CardView>
    private lateinit var checkCardsHeadings:Array<TextView>
    private lateinit var currentPhotoPath: String
    private lateinit var imageURIs: MutableList<Uri>
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PDF_CREATION = 101
    private val PDF_PICK_IMAGE_CODE = 444
    private var prefs: SharedPreferences? = null
    private val commentKeys = arrayOf ("hcfCh1Comment", "hcfCh2Comment", "hcfCh3Comment","hcfCh4Comment",
        "hcfCh5Comment","hcfCh6Comment","hcfCh7Comment","hcfCh8Comment","hcfCh9Comment","hcfCh10Comment",
        "hcfCh11Comment","hcfCh12Comment","hcfCh13Comment","hcfCh14Comment","hcfCh15Comment",
        "hcfCh16Comment","hcfCh17Comment","hcfCh18Comment", "hcfCh19Comment", "hcfChCommonComment")

    private val acceptedBtnsKeys = arrayOf (
        "hcfCh1Accepted", "hcfCh2Accepted", "hcfCh3Accepted","hcfCh4Accepted","hcfCh5Accepted",
        "hcfCh6Accepted","hcfCh7Accepted","hcfCh8Accepted","hcfCh9Accepted","hcfCh10Accepted",
        "hcfCh11Accepted","hcfCh12Accepted","hcfCh13Accepted","hcfCh14Accepted","hcfCh15Accepted",
        "hcfCh16Accepted","hcfCh17Accepted","hcfCh18Accepted","hcfCh19Accepted")

    private val rejectedBtnsKeys = arrayOf (
        "hcfCh1Rejected", "hcfCh2Rejected", "hcfCh3Rejected","hcfCh4Rejected", "hcfCh5Rejected",
        "hcfCh6Rejected","hcfCh7Rejected", "hcfCh8Rejected", "hcfCh9Rejected","hcfCh10Rejected",
        "hcfCh11Rejected", "hcfCh12Rejected","hcfCh13Rejected", "hcfCh14Rejected", "hcfCh15Rejected",
        "hcfCh16Rejected", "hcfCh17Rejected", "hcfCh18Rejected", "hcfCh19Rejected")

    private val notApplicableBtnsKeys= arrayOf (
        "hcfCh1NotApplicable", "hcfCh2NotApplicable", "hcfCh3NotApplicable", "hcfCh4NotApplicable",
        "hcfCh5NotApplicable", "hcfCh6NotApplicable","hcfCh7NotApplicable","hcfCh8NotApplicable",
        "hcfCh9NotApplicable","hcfCh10NotApplicable", "hcfCh11NotApplicable","hcfCh12NotApplicable",
        "hcfCh13NotApplicable","hcfCh14NotApplicable", "hcfCh15NotApplicable","hcfCh16NotApplicable",
        "hcfCh17NotApplicable", "hcfCh18NotApplicable", "hcfCh19NotApplicable")

    private var pr: Int = 0  //for Progress Bar
    private val hdlr = Handler()//for Progress Bar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHoldColdFormingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView((view))
        checkCards = arrayOf(
            binding.hcfCv1,binding.hcfCv2,binding.hcfCv3,binding.hcfCv4,binding.hcfCv5,binding.hcfCv6,
            binding.hcfCv7,binding.hcfCv8,binding.hcfCv9,binding.hcfCv10,binding.hcfCv11,binding.hcfCv12,
            binding.hcfCv13,binding.hcfCv14,binding.hcfCv15,binding.hcfCv16,binding.hcfCv17,
            binding.hcfCv18,binding.hcfCv19,binding.hcfCvCommon)
        checkCardsHeadings = arrayOf(
            binding.hcfHeading1,binding.hcfHeading2,binding.hcfHeading3,binding.hcfHeading4,
            binding.hcfHeading5,binding.hcfHeading6,binding.hcfHeading7, binding.hcfHeading8,
            binding.hcfHeading9,binding.hcfHeading10,binding.hcfHeading11,binding.hcfHeading12,
            binding.hcfHeading13,binding.hcfHeading14,binding.hcfHeading15,binding.hcfHeading16,
            binding.hcfHeading17,binding.hcfHeading18,binding.hcfHeading19, binding.hcfHeadingCommon)
        commentFields = arrayOf(
            binding.hcfCh1Comment, binding.hcfCh2Comment, binding.hcfCh3Comment,binding.hcfCh4Comment,
            binding.hcfCh5Comment, binding.hcfCh6Comment,binding.hcfCh7Comment,binding.hcfCh8Comment,
            binding.hcfCh9Comment,binding.hcfCh10Comment, binding.hcfCh11Comment,
            binding.hcfCh12Comment,binding.hcfCh13Comment, binding.hcfCh14Comment,
            binding.hcfCh15Comment, binding.hcfCh16Comment, binding.hcfCh17Comment,
            binding.hcfCh18Comment,binding.hcfCh19Comment, binding.hcfChcommonComment)
        acceptedBtns = arrayOf(binding.hcfCh1Accepted,  binding.hcfCh2Accepted, binding.hcfCh3Accepted,
            binding.hcfCh4Accepted, binding.hcfCh5Accepted, binding.hcfCh6Accepted,
            binding.hcfCh7Accepted, binding.hcfCh8Accepted, binding.hcfCh9Accepted,
            binding.hcfCh10Accepted, binding.hcfCh11Accepted, binding.hcfCh12Accepted,
            binding.hcfCh13Accepted, binding.hcfCh14Accepted, binding.hcfCh15Accepted,
            binding.hcfCh16Accepted, binding.hcfCh17Accepted, binding.hcfCh18Accepted,
            binding.hcfCh19Accepted)
        rejectedBtns = arrayOf(
            binding.hcfCh1Rejected,  binding.hcfCh2Rejected, binding.hcfCh3Rejected,
            binding.hcfCh4Rejected,  binding.hcfCh5Rejected, binding.hcfCh6Rejected,
            binding.hcfCh7Rejected,  binding.hcfCh8Rejected, binding.hcfCh9Rejected,
            binding.hcfCh10Rejected,  binding.hcfCh11Rejected, binding.hcfCh12Rejected,
            binding.hcfCh13Rejected,  binding.hcfCh14Rejected, binding.hcfCh15Rejected,
            binding.hcfCh16Rejected,  binding.hcfCh17Rejected, binding.hcfCh18Rejected,
            binding.hcfCh19Rejected)
        notApplicableBtns = arrayOf(
            binding.hcfCh1NotApplicable,binding.hcfCh2NotApplicable,binding.hcfCh3NotApplicable,
            binding.hcfCh4NotApplicable,binding.hcfCh5NotApplicable,binding.hcfCh6NotApplicable,
            binding.hcfCh7NotApplicable,binding.hcfCh8NotApplicable,binding.hcfCh9NotApplicable,
            binding.hcfCh10NotApplicable,binding.hcfCh11NotApplicable,binding.hcfCh12NotApplicable,
            binding.hcfCh13NotApplicable,binding.hcfCh14NotApplicable,binding.hcfCh15NotApplicable,
            binding.hcfCh16NotApplicable,binding.hcfCh17NotApplicable,binding.hcfCh18NotApplicable,
            binding.hcfCh19NotApplicable)

        setTitle() // set custom title to action bar
        loadRadioButtons()  //restore the state of RadioButtons
        loadComments() //restore the state of Comments
        changeColorListener()

// add listener to comment fields to monitor if number of text lines is in the limit
        for (j in 0..commentFields.size-1) {
            commentFields[j].addTextChangedListener(textWatcher)
        }

//floating button action - camera start
        val floatingButton: FloatingActionButton = binding.floatingActionButton
        floatingButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)  //this let to write image to storage out of the App's directory
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, REQUEST_IMAGE_CAPTURE)
                } else {
//                    createImageFile()
                    dispatchTakePictureIntent()
                    galleryAddPic() }
            } else {
//                createImageFile()
                dispatchTakePictureIntent()
                galleryAddPic() }
        }
    }

// set custom title to action bar
    fun setTitle() {
        val textView = TextView(this)
        textView.text = getText(R.string.hot_cold_forming)
        textView.textSize = 20f
        textView.setTypeface(null, Typeface.BOLD)
        textView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        textView.gravity = Gravity.CENTER
        textView.setTextColor(resources.getColor(R.color.white))
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.customView = textView
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //to display Back Home Arrow in the ActionBar

    }
    override fun onDestroy() {
        super.onDestroy()
        saveRadioButtons() //save the state of RadioButtons
        saveComments() //save the state of Comments
    }
    override fun onPause() {
        super.onPause()
        saveRadioButtons() //save the state of RadioButtons
        saveComments() //save the state of Comments
    }
    override fun onStop() {
        super.onStop()
        saveRadioButtons() //save the state of RadioButtons
        saveComments() //save the state of Comments
    }
    override fun onStart() {
        super.onStart()
        loadComments()
        loadRadioButtons()
    }
    override fun onResume() {
        super.onResume()
        loadComments()
        loadRadioButtons()
    }

    //Save the text entered into the Comment EditText fields
    private fun saveComments() {
        val editor = prefs!!.edit()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in 0..commentFields.size-1) {
            editor.putString(commentKeys[i], commentFields[i].getText().toString())
        }
        editor.apply()
    }

    //Restore the text entered into the Comment EditText fields
    private fun loadComments() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in 0..commentFields.size-1) {
            commentFields[i].setText(prefs?.getString(commentKeys[i], ""))
        }
    }

    // Save the state of RadioButtons
    private fun saveRadioButtons() {
        val editor = prefs!!.edit()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        for (i in 0..acceptedBtnsKeys.size-1) {
            editor.putBoolean(acceptedBtnsKeys[i], acceptedBtns[i].isChecked())
            editor.putBoolean(rejectedBtnsKeys[i], rejectedBtns[i].isChecked())
            editor.putBoolean(notApplicableBtnsKeys[i], notApplicableBtns[i].isChecked())
        }
        editor.apply()
    }

    // Restore the state of RadioButtons and card background color
    private fun loadRadioButtons() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        for (i in 0..acceptedBtnsKeys.size-1) {

            prefs?.getBoolean(acceptedBtnsKeys[i], false)?.let {acceptedBtns[i].setChecked(it)}
            prefs?.getBoolean(rejectedBtnsKeys[i], false)?.let {rejectedBtns[i].setChecked(it)}
            prefs?.getBoolean(notApplicableBtnsKeys[i], false)?.let {notApplicableBtns[i].setChecked(it)}

            when {
                acceptedBtns[i].isChecked-> {checkCards[i].setCardBackgroundColor(Color.parseColor("#BBE8A2"))}
                rejectedBtns[i].isChecked -> {checkCards[i].setCardBackgroundColor(Color.parseColor("#F4B6B6"))}
                notApplicableBtns[i].isChecked -> {checkCards[i].setCardBackgroundColor(Color.parseColor("#D6D4D4"))}
            }
        }
    }

    private fun changeColorListener() {
        for (i in 0..acceptedBtns.size-1) {
            acceptedBtns[i].setOnClickListener {
                checkCards[i].setCardBackgroundColor(Color.parseColor("#BBE8A2")) }
        }

        for (i in 0..rejectedBtns.size-1) {
            rejectedBtns[i].setOnClickListener {
                checkCards[i].setCardBackgroundColor(Color.parseColor("#F4B6B6")) }
        }

        for (i in 0..notApplicableBtns.size-1) {
            notApplicableBtns[i].setOnClickListener {
                checkCards[i].setCardBackgroundColor(Color.parseColor("#D6D4D4")) }
        }
    }

    //hide the keyboard when tap the screen out of the keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    //initialize top tool bar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInfater = menuInflater
        menuInfater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    //Top toolbar actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            //reset checklist
            R.id.reset_checklist -> {checkListReset()}
            //save checklist to PDF
            R.id.to_pdf -> {exportToPDF()}
        }
        return true
    }

    //Ask permissions for the PDF formation. Ask additional info for the report.
    private fun exportToPDF() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //get back to vertical orientation to complete the report
        //Get permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, REQUEST_PDF_CREATION)
            } else {
                askImages()
            }
        }
    }
    //Ask if inspector wants to add photos to the report
    private fun askImages () {
        imageURIs =  mutableListOf()
        val mAlertDialogIMG = AlertDialog.Builder(this)
        //Ask if images should be added to the report
        mAlertDialogIMG.setMessage(getString(R.string.ask_imgs_pdf))//set alert dialog message
        mAlertDialogIMG.setPositiveButton(getString(R.string.yes)) { dialog, id ->
//            if (Build.VERSION.SDK_INT < 19) {
//                var intent = Intent()
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photos)),PDF_PICK_IMAGE_CODE)
//            }
//            else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, PDF_PICK_IMAGE_CODE)
        }
        mAlertDialogIMG.setNegativeButton(getString(R.string.no))
        {dialog, id ->
            askMoreInfo()
        }
        mAlertDialogIMG.show()
    }
    // ask more info to complete and sign the report
    private fun askMoreInfo() {
        //Ask to add more information to complete the report
        val view = layoutInflater.inflate(R.layout.alert_data_request_to_pdf, null)
        val mAlertDialog = AlertDialog.Builder(this)
        val manufDataField: EditText = view.findViewById(R.id.manufacturer_pdf)
        val equipName: EditText = view.findViewById(R.id.equipment_name_pdf)
        val equipId: EditText = view.findViewById(R.id.equipment_id_pdf)
        val inspectorName: EditText = view.findViewById(R.id.inspector_name_pdf)

        mAlertDialog.setView(view)
        mAlertDialog.setTitle(getString(R.string.additional_info_for_pdf)) //set alert dialog title
        mAlertDialog.setMessage(getString(R.string.additional_info_for_pdf_msg))//set alert dialog message
        mAlertDialog.setPositiveButton(getString(R.string.ok))
        {dialog, id ->
            val manufacturerName = manufDataField.text.toString()
            val equipmentName = equipName.text.toString()
            val equipmentID = equipId.text.toString()
            val inspectorFullName = inspectorName.text.toString()
            //initiate signature pad
            setContentView(this.layoutInflater.inflate(R.layout.get_signature, null))
            getActionBar()?.hide()  //hide actionbar for SDK more than 10
            getSupportActionBar()?.hide() //hide actionbar for SDK less than 10
            val completeBtn: Button = findViewById(R.id.complete_report_btn)
            val signaturePad: SignaturePad = findViewById(R.id.signature_pad)
            val clearBtn: Button = findViewById(R.id.clear_btn)

            completeBtn.setOnClickListener {
                val bmpSign: Bitmap = signaturePad.getTransparentSignatureBitmap()

                //Run smth like progress bar to fill the screen while PDF is creating
                //need to add to drawPDF() runOnUiThread() in Try and in Catch to move Alert dialog and Toast to the UI thread
                val progressBar:ProgressBar = findViewById(R.id.progress_bar)
                progressBar.visibility = View.VISIBLE
                pr = progressBar.progress
                Thread(Runnable {
                    hdlr.post(Runnable {
                        progressBar.progress = pr})
                    try {
                        drawPDF(manufacturerName, equipmentName, equipmentID, inspectorFullName,bmpSign)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    progressBar.visibility = View.INVISIBLE
                }).start()
            }

            clearBtn.setOnClickListener {
                signaturePad.clear()
            }
        }
        mAlertDialog.show()
    }

    private fun drawPDF (manufacturerName:String, equipmentName:String, equipmentID:String, inspectorFullName:String, bmpSign:Bitmap) {
        val pdfDoc = PdfDocument()
        val myPaint = Paint()
        val mFileName = getText(R.string.report).toString() + " " + SimpleDateFormat(
            "ddMMyyyy_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val mFilePath = Environment.getExternalStorageDirectory()
        val file = File(mFilePath, mFileName + ".pdf")
        val mTextPaint = TextPaint()
        mTextPaint.setColor(R.color.report_text_color)
        val mTextPaintCom = TextPaint()
        mTextPaintCom.setColor(R.color.report_text_color)

// Generate start page 1

        //First Check the current language (Locale) to generate right bitmap
        var insert1 = R.drawable.pdf_title_img_main_rus //Default page 1 initialization to change if another language selected
        var insert2 = R.drawable.pdf_title_img_common_rus //Default page 2 initialization to change if another language selected
        var insert3 = R.drawable.pdf_title_img_signature_rus_4 //Default page 3 initialization to change if another language selected
        val langCheck:String = this.resources.configuration.locales.get(0).toString() //check current locale
        if (langCheck == "en") {
            insert1 = R.drawable.pdf_title_img_main
            insert2 = R.drawable.pdf_title_img_common
            insert3 = R.drawable.pdf_title_img_signature_4
        }
        val bmp1: Bitmap = BitmapFactory.decodeResource(resources,insert1) //report form image
        val scaledBitmap1 =
            Bitmap.createScaledBitmap(bmp1, 750, 1080, false) //adjust the size of the image
        val myPageInfo1 = PdfDocument.PageInfo.Builder(755, 1085, 1).create()
        val page1 = pdfDoc.startPage(myPageInfo1)
        val canvas1 = page1.getCanvas()
        myPaint.setTextSize(18F)
        canvas1.drawBitmap(scaledBitmap1,0F,0F,myPaint) //attach report form image to the canvas

//Draw Process Name
        canvas1.drawText(getString(R.string.process) + " " + getString(R.string.hot_cold_forming), 230F, 45F, mTextPaint)

//Draw ID field of Page 1
        val pdf_id = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        canvas1.drawText(getString(R.string.doc_id) + " " + pdf_id, 230F, 75F, mTextPaint)

//Draw Date field of Page 1
        val pdf_date =
            SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(System.currentTimeMillis())
        canvas1.drawText(getString(R.string.date) + " " + pdf_date, 230F, 105F, mTextPaint)

//Draw Manufacturer name
        canvas1.drawText(getString(R.string.manufacturer1) + " " + manufacturerName, 20F, 162F, mTextPaint)

//Draw Equipment name and ID
        canvas1.save()
        canvas1.translate(20F, 185F)
        StaticLayout(
            getString(R.string.equipment) + " " + equipmentName + " " + "(" + equipmentID + ")", mTextPaint, 750,
            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).draw(canvas1)
        canvas1.restore()

// Draw the checks of Page 1
        var verticalStartPosition1 = 275F
        var verticalCBPosition1 = 330F

        for (i in 0..6) {           //Number of checks that can be located on Page 1
            //Check descriptions
            canvas1.save()
            canvas1.translate(20F, verticalStartPosition1)
            StaticLayout(
                checkCardsHeadings[i].text.toString(), mTextPaint, 210,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).draw(canvas1)
            canvas1.restore()

            //Comments
            canvas1.save()
            canvas1.translate(240F, verticalStartPosition1)
            StaticLayout(
                commentFields[i].text.toString(), mTextPaintCom, 380,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
            ).draw(canvas1)
            canvas1.restore()
            verticalStartPosition1 += 110F

            //CheckBoxes
            if (acceptedBtns[i].isChecked()) {
                canvas1.drawText("X", 640F, verticalCBPosition1, mTextPaint)
            }
            if (rejectedBtns[i].isChecked()) {
                canvas1.drawText("X", 675F, verticalCBPosition1, mTextPaint)
            }
            if (notApplicableBtns[i].isChecked()) {
                canvas1.drawText("X", 710F, verticalCBPosition1, mTextPaint)
            }
            verticalCBPosition1 += 110F
        }

        canvas1.drawText("1/3", 700F, 1050F, mTextPaint)
        pdfDoc.finishPage(page1)

// Generate Page 2
        val bmp2: Bitmap = BitmapFactory.decodeResource(resources,insert2) //report form image (common page)
        val scaledBitmap2 =
            Bitmap.createScaledBitmap(bmp2, 750, 1080, false) //adjust the size of the image
        val myPageInfo2 = PdfDocument.PageInfo.Builder(755, 1085, 2).create()
        val page2 = pdfDoc.startPage(myPageInfo2)
        val canvas2 = page2.getCanvas()
        Bitmap.createScaledBitmap(bmp2, 750, 1080, false) //adjust the size of the image
        canvas2.drawBitmap(scaledBitmap2,0F,0F,myPaint) //attach report form image to the canvas
//Draw ID field of Page 2
        canvas2.drawText(getString(R.string.doc_id) + " " + pdf_id, 520F, 65F, mTextPaint)

//Draw Date field of Page 2
        canvas2.drawText(getString(R.string.date) + " " + pdf_date, 520F, 96F, mTextPaint)

// Draw the checks of Page 2
        var verticalStartPosition2 = 172F
        var verticalCBPosition2 = 220F

        for (i in 7..14) {           //Number of checks that can be located on Page 2
            //Check descriptions
            canvas2.save()
            canvas2.translate(20F, verticalStartPosition2)
            StaticLayout(
                checkCardsHeadings[i].text.toString(), mTextPaint, 210,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).draw(canvas2)
            canvas2.restore()

            //Comments
            canvas2.save()
            canvas2.translate(240F, verticalStartPosition2)
            StaticLayout(
                commentFields[i].text.toString(), mTextPaintCom, 380,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).draw(canvas2)
            canvas2.restore()
            verticalStartPosition2 += 108F

            //CheckBoxes
            if (acceptedBtns[i].isChecked()) {
                canvas2.drawText("X", 640F, verticalCBPosition2, mTextPaint)
            }
            if (rejectedBtns[i].isChecked()) {
                canvas2.drawText("X", 675F, verticalCBPosition2, mTextPaint)
            }
            if (notApplicableBtns[i].isChecked()) {
                canvas2.drawText("X", 710F, verticalCBPosition2, mTextPaint)
            }
            verticalCBPosition2 += 108F
        }
        canvas2.drawText("2/3", 700F, 1050F, mTextPaint)
        pdfDoc.finishPage(page2)

// Generate Page 3
        val bmp3: Bitmap = BitmapFactory.decodeResource(resources,insert3) //report form image (signature page)
        val scaledBitmap3 =
            Bitmap.createScaledBitmap(bmp3, 750, 1080, false) //adjust the size of the image
        val myPageInfo3 = PdfDocument.PageInfo.Builder(755, 1085, 3).create()
        val page3 = pdfDoc.startPage(myPageInfo3)
        val canvas3 = page3.getCanvas()
        val specPaint = Paint()
        specPaint.setTextSize(16F)
        specPaint.setColor(R.color.report_text_color)
        Bitmap.createScaledBitmap(bmp3, 750, 1080, false) //adjust the size of the image
        canvas3.drawBitmap(scaledBitmap3,0F,0F,myPaint) //attach report form image to the canvas
//Draw ID field of Page 3
        canvas3.drawText(getString(R.string.doc_id) + " " + pdf_id, 520F, 65F, mTextPaint)
//Draw Date field of Page 3
        canvas3.drawText(getString(R.string.date) + " " + pdf_date, 520F, 96F, mTextPaint)

// Draw the checks of Page 3
        var verticalStartPosition3 = 172F
        var verticalCBPosition3 = 220F

        for (i in 15..18) {           //Number of checks that can be located on Page 3
            //Check descriptions
            canvas3.save()
            canvas3.translate(20F, verticalStartPosition3)
            StaticLayout(
                checkCardsHeadings[i].text.toString(), mTextPaint, 210,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
            ).draw(canvas3)
            canvas3.restore()

            //Comments
            canvas3.save()
            canvas3.translate(240F, verticalStartPosition3)
            StaticLayout(
                commentFields[i].text.toString(), mTextPaintCom, 380,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
            ).draw(canvas3)
            canvas3.restore()
            verticalStartPosition3 += 110F

            //CheckBoxes
            if (acceptedBtns[i].isChecked()) {
                canvas3.drawText("X", 640F, verticalCBPosition3, mTextPaint)
            }
            if (rejectedBtns[i].isChecked()) {
                canvas3.drawText("X", 675F, verticalCBPosition3, mTextPaint)
            }
            if (notApplicableBtns[i].isChecked()) {
                canvas3.drawText("X", 710F, verticalCBPosition3, mTextPaint)
            }
            verticalCBPosition3 += 108F
        }
//Draw additional comment field
        canvas3.save()
        canvas3.translate(20F, 640F)
        StaticLayout(
            commentFields[19].text.toString(), mTextPaintCom, 700,
            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).draw(canvas3)
        canvas3.restore()

//Draw Inspector title
        canvas3.drawText(getString(R.string.inspector), 20F, 895F, specPaint)
//Draw Signature
        val scaledBmpSign =
            Bitmap.createScaledBitmap(bmpSign, 100, 100, true) //adjust the size
        canvas3.drawBitmap(scaledBmpSign,130F,840F,myPaint)
//Draw Inspector full name
        canvas3.drawText(inspectorFullName,245F, 895F, specPaint)

//Draw number of attachments
       when (imageURIs.size) {
           0 -> canvas3.drawText(getString(R.string.no_imgs_att),20F,1023F,specPaint)
           1 -> canvas3.drawText("1" + " " + getString(R.string.img_att),20F,1023F,specPaint)
           2 -> canvas3.drawText("2" + " " + getString(R.string.img2_att),20F,1023F,specPaint)
           else -> canvas3.drawText(imageURIs.size.toString() + " " + getString(R.string.imgs_att),20F,1023F,specPaint)}
//Draw page number
        canvas3.drawText("3/3", 700F, 1050F, mTextPaint)
        pdfDoc.finishPage(page3)

//Generate additional pages with photos
        for (i in 0..imageURIs.size - 1) {
            val bmpX = MediaStore.Images.Media.getBitmap(contentResolver, imageURIs[i])
            val scaledBitmapX = Bitmap.createScaledBitmap(bmpX, 730, 1080, false)
            val myPageInfoX = PdfDocument.PageInfo.Builder(755, 1085, i).create()
            val pageX = pdfDoc.startPage(myPageInfoX)
            val canvasX = pageX.getCanvas()
            Bitmap.createScaledBitmap(bmpX, 730, 1080, false)
            canvasX.drawBitmap(scaledBitmapX, 10F, 10F, myPaint)
            pdfDoc.finishPage(pageX)
        }

        try {
            pdfDoc.writeTo(FileOutputStream(file))
            this.runOnUiThread(Runnable {
                val mAlertDialogEnd = AlertDialog.Builder(this)
                mAlertDialogEnd.setTitle(getString(R.string.report_completed)) //set alert dialog title
                mAlertDialogEnd.setMessage("$mFileName.pdf\n" + getText(R.string.is_saved_to) + "\n$mFilePath")//set alert dialog message
                mAlertDialogEnd.setPositiveButton(getString(R.string.ok))
                {dialog, id -> dialog.dismiss()}
                mAlertDialogEnd.show()
            })
        } catch (e: Exception) {
            this.runOnUiThread(Runnable {
                Toast.makeText(this, "" + e.toString(), Toast.LENGTH_LONG).show()
            })
        }
        pdfDoc.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PDF_PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            //picked multiple images
            // if multiple images are selected
            if (data?.getClipData() != null) {
                val count = data.clipData?.itemCount  //get number of picked images

                for (i in 0..count!! - 1) {
                    val imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    imageURIs.add(imageUri)
                }
                askMoreInfo()
                Toast.makeText(this, getString(R.string.imgs_added), Toast.LENGTH_LONG).show()
            } else if (data?.getData() != null) {
                // if single image is selected
                val imageUri: Uri = data.data!!
                imageURIs.add(imageUri)
                Toast.makeText(this, getString(R.string.img_added), Toast.LENGTH_LONG).show()
                askMoreInfo()
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, getString(R.string.photo_saved), Toast.LENGTH_LONG).show()
            Log.d("TAG", "Camera yes")}
    }

    // request of permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PDF_CREATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportToPDF()
                } else {
                    Toast.makeText(this, getText(R.string.permission_denied), Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_IMAGE_CAPTURE -> {if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//              createImageFile()
                dispatchTakePictureIntent()
                galleryAddPic()
            } else {
                Toast.makeText(this, getText(R.string.permission_denied), Toast.LENGTH_LONG)
                    .show()
            }}
        }
    }

    //return a unique file name for a new photo using a date-time stamp (https://developer.android.com/training/camera/photobasics)
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
        val storageDir:File? = Environment.getExternalStorageDirectory()

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //create a file for the photo (https://developer.android.com/training/camera/photobasics)
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
    //add the photo to a gallery (https://developer.android.com/training/camera/photobasics)
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
    // checkList reset request actions
    private fun checkListReset() {
        val mAlertDialog = AlertDialog.Builder(this)
        mAlertDialog.setTitle(getString(R.string.reset_alert_title)) //set alert dialog title
        mAlertDialog.setMessage(getString(R.string.reset_question)) //set alert dialog message
        mAlertDialog.setPositiveButton(getString(R.string.yes)) { dialog, id ->

            for (i in 0..acceptedBtns.size-1) {
                acceptedBtns[i].isChecked = false
                rejectedBtns[i].isChecked = false
                notApplicableBtns[i].isChecked = false
            }

            for (j in 0..checkCards.size-1){
                commentFields[j].setText("")
                checkCards[j].setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            }
            Toast.makeText(this, getString(R.string.reseted), Toast.LENGTH_LONG).show()
        }
        mAlertDialog.setNegativeButton(getString(R.string.no)) { dialog, id -> dialog.dismiss() }
        mAlertDialog.show()
    }


    // Set the limit for the number of text lines that can be entered in editText Comment Field
    private val textWatcher = object : TextWatcher
    {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            for (i in 0..commentFields.size-2) {
                if (commentFields[i].layout !=null){
                    if(commentFields[i].layout.lineCount >7) {
                        Toast.makeText(applicationContext, getString(R.string.max_lines_7) + " " + getString(R.string.in_item) + " " + "${i+1}", Toast.LENGTH_SHORT).show()
                    }
                    //separate rules for Additional Comment field
                    if(commentFields[19].layout.lineCount >5) {
                        Toast.makeText(applicationContext, getString(R.string.max_lines_5), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}