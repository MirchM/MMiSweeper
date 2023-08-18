package com.mmisoft.mmisweeper.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.mmisoft.mmisweeper.Fragment.GameFragment.Companion.newInstance
import com.mmisoft.mmisweeper.MainActivity
import com.mmisoft.mmisweeper.R

/**
 * A simple [Fragment] subclass.
 * Use the [GameModeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameModeFragment : Fragment() {
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
        val v = inflater.inflate(R.layout.fragment_game_mode, container, false)
        val backBtn = v.findViewById<ImageButton>(R.id.backOptionsButton)
        backBtn.setOnClickListener { (activity as MainActivity?)!!.backBtn() }
        val easyBtn = v.findViewById<Button>(R.id.easyBtn)
        easyBtn.setOnClickListener { (activity as MainActivity?)!!.changeMainFragment(newInstance(7, 7, 7), "options") }
        val mediumBtn = v.findViewById<Button>(R.id.mediumBtn)
        mediumBtn.setOnClickListener { (activity as MainActivity?)!!.changeMainFragment(newInstance(10, 10, 10), "options") }
        val hardBtn = v.findViewById<Button>(R.id.hardBtn)
        hardBtn.setOnClickListener { (activity as MainActivity?)!!.changeMainFragment(newInstance(15, 15, 15), "options") }
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
         * @return A new instance of fragment GameModeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): GameModeFragment {
            val fragment = GameModeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}