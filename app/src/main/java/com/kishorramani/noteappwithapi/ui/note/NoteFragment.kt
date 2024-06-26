package com.kishorramani.noteappwithapi.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.kishorramani.noteappwithapi.R
import com.kishorramani.noteappwithapi.databinding.FragmentNoteBinding
import com.kishorramani.noteappwithapi.models.NoteRequest
import com.kishorramani.noteappwithapi.models.NoteResponse
import com.kishorramani.noteappwithapi.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel>()
    private var note: NoteResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialData()
        bindHandlers()
        bindObservers()
    }

    private fun setInitialData() {
        val jsonNote = arguments?.getString("note")
        if (jsonNote != null) {
            note = Gson().fromJson<NoteResponse>(jsonNote, NoteResponse::class.java)
            note?.let {
                binding.txtTitle.setText(it.title)
                binding.txtDescription.setText(it.description)
            }
        } else {
            binding.addEditText.text = resources.getString(R.string.add_note)
        }
    }

    private fun bindHandlers() {
        binding.btnDelete.setOnClickListener {
            note?.let { noteViewModel.deleteNote(it._id) }
        }

        binding.apply {
            btnSubmit.setOnClickListener {
                val title = txtTitle.text.toString()
                val description = txtDescription.text.toString()
                val noteRequest = NoteRequest(title, description)
                if (note == null) {
                    noteViewModel.createNote(noteRequest)
                } else {
                    noteViewModel.updateNote(note!!._id, noteRequest)
                }
            }
        }
    }

    private fun bindObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteViewModel.statusFlow.collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            findNavController().popBackStack()
                        }

                        is NetworkResult.Error -> {

                        }

                        is NetworkResult.Loading -> {

                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}