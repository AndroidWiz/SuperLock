package com.sk.superlock.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.superlock.databinding.FragmentSetPinBinding

class SetPinFragment : Fragment() {

    private lateinit var binding: FragmentSetPinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetPinBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btn0.setOnClickListener { onNumberButtonClick(binding.btn0.text.toString()) }
        binding.btn1.setOnClickListener { onNumberButtonClick(binding.btn1.text.toString()) }
        binding.btn2.setOnClickListener { onNumberButtonClick(binding.btn2.text.toString()) }
        binding.btn3.setOnClickListener { onNumberButtonClick(binding.btn3.text.toString()) }
        binding.btn4.setOnClickListener { onNumberButtonClick(binding.btn4.text.toString()) }
        binding.btn5.setOnClickListener { onNumberButtonClick(binding.btn5.text.toString()) }
        binding.btn6.setOnClickListener { onNumberButtonClick(binding.btn6.text.toString()) }
        binding.btn7.setOnClickListener { onNumberButtonClick(binding.btn7.text.toString()) }
        binding.btn8.setOnClickListener { onNumberButtonClick(binding.btn8.text.toString()) }
        binding.btn9.setOnClickListener { onNumberButtonClick(binding.btn9.text.toString()) }

        binding.btnBackspace.setOnClickListener {
            val currentText = binding.tvPin.text.toString()
            if (currentText.isNotEmpty()) {
                binding.tvPin.text = currentText.substring(0, currentText.length - 1)
            }
        }


    }

    private fun onNumberButtonClick(number: String) {
        val currentText = binding.tvPin.text.toString()
        if (currentText.length < 4) {
            val newText = currentText + number
            binding.tvPin.text = newText
        }
    }

}

//        var currentText = ""
/* var currentText = binding.tvPin.text.toString()
 binding.btn0.setOnClickListener {
     if (binding.tvPin.text.length < 4) {
         currentText += binding.btn0.text
         binding.tvPin.text = currentText
     }
 }
 binding.btn1.setOnClickListener {
     if (binding.tvPin.text.length < 4) {
         currentText += binding.btn1.text
         binding.tvPin.text = currentText
     }
 }
 binding.btn2.setOnClickListener {
     if (binding.tvPin.text.length < 4) {
         currentText += binding.btn2.text
         binding.tvPin.text = currentText
     }
 }*/
/*binding.btnBackspace.setOnClickListener {
    if (currentText.isNotEmpty()) {
        binding.tvPin.text = currentText.substring(0, currentText.length - 1)
    }
}*/

/*binding.btn2.setOnClickListener {
           binding.tvPin.text = buildString {
               append(currentText)
               append(binding.btn2.text)
           }
       }*/