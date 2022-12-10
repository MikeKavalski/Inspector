package com.example.inspector

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inspector.checklists.HeatTreatment
import com.example.inspector.checklists.HotColdForming
import com.example.inspector.checklists.Machining
import com.example.inspector.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    val contentArrayList = arrayListOf<CLContent>()
    lateinit var heading: Array<String>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (this as AppCompatActivity).supportActionBar?.title =
            getString(R.string.choose_check_list) // set title to action bar

        // setTitle()
         dataInitialize()
        //find recyclerview by its id
        // create a vertical layout manager
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        val adapter = CLAdapter(contentArrayList)
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener(object: CLAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                var intent1 = Intent(this@MainActivity, HeatTreatment::class.java)
                var intent2 = Intent(this@MainActivity, HotColdForming::class.java)
                var intent3 = Intent(this@MainActivity, Machining::class.java)


                when (heading[position]) {
                    getString(R.string.heat_treatment) -> startActivity(intent1)
                    getString(R.string.hot_cold_forming) -> startActivity(intent2)
                    getString(R.string.machining) -> startActivity(intent3)

                }
            }
        })
    }

    private fun dataInitialize() {
        heading = arrayOf(
            getString(R.string.heat_treatment),
            getString(R.string.hot_cold_forming),
            getString(R.string.machining),
            getString(R.string.welding_preparation),
            getString(R.string.welding),
            getString(R.string.visual_check),
            getString(R.string.radiographic),
            getString(R.string.magnetic),
            getString(R.string.dye_penetrant),
            getString(R.string.ultrasonic),
            getString(R.string.paintwork),
            getString(R.string.packaging),
            getString(R.string.preservation)
        )
        for (i in heading.indices) {
            val content = CLContent(heading[i])
            contentArrayList.add(content)
        }
    }

    // set centered title to action bar
//    fun setTitle() {
//        val textView = TextView(this)
//        textView.text = getText(R.string.choose_check_list)
//        textView.textSize = 20f
//        textView.setTypeface(null, Typeface.BOLD)
//        textView.layoutParams =
//            LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.FILL_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT)
//        textView.gravity = Gravity.CENTER
//        textView.setTextColor(resources.getColor(R.color.white))
//        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        supportActionBar!!.customView = textView
//    }

    //initialize top tool bar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInfater = menuInflater
        menuInfater.inflate(R.menu.main_actionbar_menu, menu)
        return true
    }

    //Top toolbar actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

          when (item.itemId) {
              //change to Russian
              R.id.russian_lang -> {setLocale("ru")
              recreate()}

              //change to English
              R.id.english_lang-> {setLocale("en")
              recreate()}
        }
        return true
    }

    private fun setLocale(localeCode:String) {
        var resources = resources
        var metrics = resources.displayMetrics
        var configuration = resources.configuration
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(Locale(localeCode.toLowerCase()))
        }else{
            configuration.locale= Locale(localeCode.toLowerCase())
        }
        resources.updateConfiguration(configuration, metrics)
    }

}
