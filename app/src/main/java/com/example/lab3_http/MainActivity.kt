package com.example.lab3_http


import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.android.volley.Request
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import java.net.HttpURLConnection
import java.net.URL


const val URL_STRING = "https://www.forbes.ru/rating/403469-40-samyh-uspeshnyh-zvezd-rossii-do-40-let-reyting-forbes"



fun getPageContent(Url: String): String {
    val url = URL(Url)
    val urlConnection = url.openConnection() as HttpURLConnection
    var text: String
    try {
        text = urlConnection.inputStream.bufferedReader().readText()
        val kk: String

    } finally {
        urlConnection.disconnect()


    }
    return text
}


fun applyRegex(input: String, regex: String): Array<String> {
    val pattern = Regex(regex)
    return pattern.findAll(input).map { it.value }.toList().toTypedArray()
}

fun getCelebritesName(htmlCode: String):ArrayList<String?>{
    val firstLevelStringArray = applyRegex(htmlCode, """\{profile\:\{title\:\"[a-zA-Zа-яА-Я\s\(\)]*\"""")
    val pattern = Regex("""\"[a-zA-Zа-яА-Я\s\(\)]*\"""")
    val res_list:ArrayList<String?> = ArrayList()
    for (firstName in firstLevelStringArray){
        val match = pattern.find(firstName)
        val buff_st = match?.value
        res_list.add(buff_st!!.replace("\"", ""))
    }
    return res_list
}


fun getCelebritesImage(htmlCode: String):ArrayList<String>{
    val firstLevelStringArray = applyRegex(htmlCode, """filename:"[0-9a-zA-Z\.\_\-]*",uri""")
    val pattern = Regex(""""[0-9a-zA-Z\.\_\-]*"""")
    val res_list:ArrayList<String> = ArrayList()
    for (firstName in firstLevelStringArray){
        val buff_st = """https:\\cdn.forbes.ru\files\profile\"""+pattern.find(firstName)!!.value.replace("\"", "")
        res_list.add(buff_st)

    }
    return res_list
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val imageView = findViewById<ImageView>(R.id.imageCellView)
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.google.com"
        val tw = findViewById<TextView>(R.id.debugView)
        val dtl = findViewById<TextView>(R.id.debugLinkTextView)
        val cellNames2 : ArrayList<String?> = ArrayList<String?>()
        val cellPic2 : ArrayList<String?> = ArrayList<String?>()
        var txt: String
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, URL_STRING,
            { response ->
                // Display the first 500 characters of the response string.
                val cellNames = getCelebritesName(response.toString())
                val cellPic = getCelebritesImage(response.toString())
                var st  ="start\n"
                var k =0
                for(item in cellNames){
                    st = item+"\n"
                    cellNames2.add(item)
                    Log.d("myTag", "${k} ${item}")
                    k=k+1

                }
                k=0
                tw.text=st
                var st2 = "link\n"
                for(item in cellPic){
                    st2 = item+"\n"
                    cellPic2.add(item)
                    //Picasso.get().load(item).into(imageView)
                    Log.d("myTag", "${k} ${item}")
                    k=k+1
                }
                dtl.text=st2
            },
            {tw.text  = "That didn't work!" })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
        var index = (0..39).random()
        //Picasso.get().load(cellPic2[index]).into(imageView)
        val bt = findViewById<Button>(R.id.checkGuessButton)
        val guess = findViewById<EditText>(R.id.guessEditText)
        val res = findViewById<TextView>(R.id.resTextView)
        val next = findViewById<Button>(R.id.nextButton)
        bt.setOnClickListener {
            val guessText = guess.text.toString()
            val true_choose = cellNames2[index]
            if(guessText == true_choose){
                res.setText("Правильно")
            }
            else{
                res.setText("Неправильно, Провильный ответ ${true_choose}")
            }
        }
        next.setOnClickListener {
            index = (0..39).random()
            Picasso.get().load(cellPic2[index+1]).into(imageView)
            res.setText("")
        }



}

}


