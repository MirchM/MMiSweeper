package com.mmisoft.mmisweeper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mmisoft.mmisweeper.MainActivity
import com.mmisoft.mmisweeper.R

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenuFragment : Fragment() {
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
        val v = inflater.inflate(R.layout.fragment_main_menu, container, false)

        //PLAY BUTTON
        val playBtn = v.findViewById<Button>(R.id.playBtn)
        playBtn.setOnClickListener { (activity as MainActivity?)!!.changeMainFragment(GameModeFragment(), "gameMode") }

        //OPTIONS BUTTON
        val optionBtn = v.findViewById<Button>(R.id.optionsBtn)
        optionBtn.setOnClickListener { (activity as MainActivity?)!!.changeMainFragment(OptionsFragment(), "options") }

        //EXIT BUTTON
        val exitBtn = v.findViewById<Button>(R.id.exitBtn)
        exitBtn.setOnClickListener { System.exit(0) }
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
         * @return A new instance of fragment MainMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): MainMenuFragment {
            val fragment = MainMenuFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}