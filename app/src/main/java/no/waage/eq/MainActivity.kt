package no.waage.eq

import android.app.Activity
import android.os.Bundle
import android.media.audiofx.Equalizer
import android.media.audiofx.BassBoost
import android.widget.*
import android.graphics.Color

class MainActivity : Activity() {
    private var eq: Equalizer? = null
    private var bass: BassBoost? = null
    private lateinit var status: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
            setBackgroundColor(Color.rgb(18, 22, 29))
        }
        val title = TextView(this).apply { text = "Waage EQ · Experimental"; textSize = 23f; setTextColor(Color.WHITE) }
        status = TextView(this).apply { textSize = 13f; setTextColor(Color.LTGRAY) }
        layout.addView(title); layout.addView(status)
        val scroll = ScrollView(this)
        val bands = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(bands); layout.addView(scroll)
        setContentView(layout)
        try {
            @Suppress("DEPRECATION")
            val effect = Equalizer(0, 0)
            eq = effect
            effect.enabled = true
            status.text = "Global mix session 0: experimental; may not affect other apps."
            val count = effect.numberOfBands.toInt()
            val range = effect.bandLevelRange
            val min = range[0].toInt(); val max = range[1].toInt()
            for (i in 0 until count) {
                val band = i.toShort()
                val freq = effect.getCenterFreq(band) / 1000
                val row = TextView(this).apply { text = "$freq Hz"; setTextColor(Color.WHITE) }
                val seek = SeekBar(this).apply { this.max = max - min; progress = effect.getBandLevel(band).toInt() - min }
                seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) {
                        if (user) try { effect.setBandLevel(band, (min + p).toShort()) } catch (_: Exception) {}
                    }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
                bands.addView(row); bands.addView(seek)
            }
            try { bass = BassBoost(0, 0).also { it.enabled = true } } catch (_: Exception) {}
            val bassLabel = TextView(this).apply { text = "Bass Boost"; setTextColor(Color.WHITE) }
            bands.addView(bassLabel)
            bands.addView(SeekBar(this).apply {
                max = 1000
                progress = 0
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) {
                        if (user) try { bass?.setStrength(p.toShort()) } catch (_: Exception) {}
                    }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            })
        } catch (e: Exception) {
            status.text = "Audio effect unavailable: ${e.message}"
        }
    }
    override fun onDestroy() {
        eq?.release(); bass?.release()
        super.onDestroy()
    }
}
