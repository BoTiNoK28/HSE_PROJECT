import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.myapplication.ButtonGroup
import com.example.myapplication.R

class ButtonAdapter(
    private val context: Context,
    private var buttonGroups: List<ButtonGroup>
) : BaseAdapter() {

    fun updateData(newData: List<ButtonGroup>) {
        this.buttonGroups = emptyList<ButtonGroup>()
        this.buttonGroups = newData
        notifyDataSetChanged()
    }

    override fun getCount(): Int = buttonGroups.size

    override fun getItem(position: Int): ButtonGroup = buttonGroups[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)

        val buttonGroup = getItem(position)

        val tvLink = view.findViewById<TextView>(R.id.text1)
        val tvDate = view.findViewById<TextView>(R.id.text2)
        val tvSummary = view.findViewById<TextView>(R.id.text3)

        tvLink.text = buttonGroup.link
        tvDate.text = buttonGroup.date
        tvSummary.text = buttonGroup.summary

        return view
    }
}