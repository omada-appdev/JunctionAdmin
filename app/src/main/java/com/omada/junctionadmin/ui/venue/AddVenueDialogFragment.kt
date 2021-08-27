package com.omada.junctionadmin.ui.venue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.omada.junctionadmin.R
import com.omada.junctionadmin.viewmodels.InstituteViewModel

class AddVenueDialogFragment(private var instituteViewModel: InstituteViewModel) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.venue_add_layout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var nameLayout: TextInputLayout = view.findViewById(R.id.name_layout)
        var addressLayout: TextInputLayout = view.findViewById(R.id.address_layout)

        var nameText: TextInputEditText = view.findViewById(R.id.name_input)
        var addressText: TextInputEditText = view.findViewById(R.id.address_input)

        var doneButton: MaterialButton = view.findViewById(R.id.done_button)

        doneButton.setOnClickListener {
            nameLayout.isErrorEnabled = false
            addressLayout.isErrorEnabled = false
            val res = instituteViewModel.addNewVenue(nameText.text?.toString(), addressText.text?.toString())
            if (!res) {
                if (nameText.text == null || nameText.text.toString().length < 8) {
                    nameLayout.error = "Enter at least 8 characters"
                }
                if (addressText.text == null || addressText.text.toString().length < 8) {
                    addressLayout.error = "Enter at least 15 characters"
                }
            } else {
                dismiss()
            }
        }
    }

}