package com.mmisoft.mmisweeper.Fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mmisoft.mmisweeper.MainActivity
import com.mmisoft.mmisweeper.R

/**
 * A simple [Fragment] subclass.
 * Use the [OptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OptionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_options, container, false)
        val backBtn = v.findViewById<ImageButton>(R.id.backOptionsButton)
        val themeBtn = v.findViewById<Button>(R.id.themeBtn)
        backBtn.setOnClickListener { (activity as MainActivity?)!!.backBtn() }
        themeBtn.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("Theme", Context.MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            val options = arrayOf("default", "minecraft iron", "minecraft gold", "minecraft diamond")
            var checkedItem = 0
            val selectedOption = sharedPreferences.getString("theme", "default")
            for (i in options.indices) {
                if (options[i] == selectedOption) {
                    checkedItem = i
                }
            }
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Pick A Theme")
            builder.setCancelable(false)
            builder.setSingleChoiceItems(options, checkedItem) { dialogInterface, i -> myEdit.putString("theme", options[i]) }
            builder.setPositiveButton("OK") { dialogInterface, i ->
                myEdit.apply()
                Toast.makeText(context, sharedPreferences.getString("theme", "default") + " theme is selected", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
        return v
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OptionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): OptionsFragment {
            val fragment = OptionsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}