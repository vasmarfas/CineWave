package ru.vasmarfas.cinewave.ui.serviceItems

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vasmarfas.cinewave.App
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.databinding.FragmentCreateNewCollectionDialogBinding
import ru.vasmarfas.cinewave.data.db.entity.NewCollectionsModel
import java.util.Locale
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateNewCollectionDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CreateNewCollectionDialogFragment : DialogFragment() {
    @Inject
    lateinit var dao: UserDao
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
//            val dao = App().getInstance(requireContext()).userDao()

            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
//            val inflater = requireActivity().layoutInflater
//            builder.setView(inflater.inflate(R.layout.fragment_create_new_collection_dialog, null))
            val binding = FragmentCreateNewCollectionDialogBinding.inflate(layoutInflater)
            val readyButton = binding.createNewCollectionDialogButton
            val editText = binding.createNewCollectionDialogEditText


            if (readyButton != null) {
                readyButton.setOnClickListener {
                    val collectionName = editText.text.toString().capitalize(Locale.ROOT)
                    //Log.d("DEBUG CREATE_NEW_COLLECTION", collectionName.toString())
                    //Log.e("DEBUG CREATE_NEW_COLLECTION", editText.text.toString())
                    //Log.d("DEBUG CREATE_NEW_COLLECTION", binding.createNewCollectionDialogEditText.text.toString())

                    lifecycleScope.launch(Dispatchers.IO) {
                        val collections = dao.getCollections()
                        var isExist = false
                        for (col in collections) {
                            if (col.collectionName == collectionName) {
                                isExist = true
                                break
                            }
                        }
                        if (collectionName != "") {
                            if (!isExist) {
                                val newCollection = NewCollectionsModel(
                                    collectionName = collectionName,
                                    filmIds = listOf<Int>()
                                )
                                dao.saveCollection(newCollection)
                                launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(
                                            R.string.collection_with_var_was_successfully_saved,
                                            collectionName
                                        ),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    dialog?.dismiss()
                                }
                            } else {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(
                                            R.string.collection_with_var_already_exists,
                                            collectionName
                                        ),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    dialog?.dismiss()
                                }
                            }
                        } else {
                            launch(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.need_to_enter_collection_name),
                                    Toast.LENGTH_LONG
                                ).show()
//                                dialog?.dismiss()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.button_is_not_exist), Toast.LENGTH_LONG).show()
            }

            // Create the AlertDialog object and return it.
            builder
                .setView(binding.root)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
