package com.example.zov_android.ui.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import asFlow
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.data.signalr.SignalR
import com.example.zov_android.databinding.FragmentChatChannelBinding
import com.example.zov_android.di.qualifiers.Token
import com.example.zov_android.di.qualifiers.User
import com.example.zov_android.ui.adapters.ChatRecyclerViewAdapter
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.viewmodels.AttachmentViewModel
import com.example.zov_android.ui.viewmodels.AttachmentViewState
import com.example.zov_android.ui.viewmodels.BaseViewModel
import com.example.zov_android.ui.viewmodels.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


@AndroidEntryPoint
class ChatChannelFragment(
    private val channelId: Long,
    private val channelName: String,
    private val membersGuild: List<MembersGuildResponse>
) : NavigableFragment(), ChatRecyclerViewAdapter.Listener {
    private var _binding: FragmentChatChannelBinding? = null
    private val binding get() = _binding!!

    private var usersAdapter: ChatRecyclerViewAdapter? = null

    private val messagesViewModel: MessagesViewModel by viewModels()
    private val attachmentViewModel: AttachmentViewModel by activityViewModels()

    private val files: MutableList<File> = mutableListOf()

    private var isHandlingAttachment = false

    @Inject
    @Token
    lateinit var token: String

    @Inject
    @User
    lateinit var user: UserResponse


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(context: Context): View {
        _binding = FragmentChatChannelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupRecyclerView()
        setupSendMessages()

        lifecycleScope.launch {
            messagesViewModel.signalR.newMessageEvent.asFlow().collect { message ->
                messagesViewModel.addNewMessage(message)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                attachmentViewModel.attachmentState.collect { state ->
                    when {
                        state is AttachmentViewState.Success && !isHandlingAttachment -> {
                            isHandlingAttachment = true
                            val mimeType = attachmentViewModel.getLastMimeType()
                            val filename = when {
                                mimeType.startsWith("image/jpg") -> "image_${System.currentTimeMillis()}.jpg"
                                mimeType.startsWith("image/png") -> "image_${System.currentTimeMillis()}.png"
                                mimeType.contains("openxmlformats") -> "document_${System.currentTimeMillis()}.docx"
                                else -> "file_${System.currentTimeMillis()}"
                            }

                            // Запускаем лаунчер и сбрасываем состояние после завершения
                            saveFileLauncher.launch(filename)
                        }

                        state is AttachmentViewState.Error -> {
                            Toast.makeText(requireContext(), "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                            attachmentViewModel.resetState()
                        }
                    }
                }
            }
        }

    }

    private fun setupRecyclerView() = with(binding){
        chatList.layoutManager = LinearLayoutManager(requireContext())
        usersAdapter = ChatRecyclerViewAdapter(this@ChatChannelFragment, user, token, channelId, attachmentViewModel)
        chatList.adapter = usersAdapter
    }

    private fun setupSendMessages(){
        binding.attachButton.setOnClickListener {
            pickFileLauncher.launch("*/*")
        }

        binding.sendButton.setOnClickListener {
            val text = binding.editText.text.toString()
            if (text.isNotBlank() || files.isNotEmpty()){
                messagesViewModel.loadMessages(token, requireContext(), channelId, text, files)
                binding.editText.text.clear()
                files.clear()
            }
            else {
                Toast.makeText(requireContext(), "Введите текст или выберите файл", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            // Получаем файл
            val file = fileUri.getFile(requireContext())
            file?.let {
                files.add(it)
                Log.d("FileAplication", "Добавлен файл: $it")
            }
            if (file == null) {
                Toast.makeText(requireContext(), "Не удалось прочитать файл", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Uri.getFile(context: Context): File? {
        val contentResolver = context.contentResolver
        val displayName = this.getDisplayName(context) ?: "file_${System.currentTimeMillis()}"

        return try {
            contentResolver.openInputStream(this)?.use { inputStream ->
                val cacheDir = context.cacheDir
                val outFile = File(cacheDir, displayName)

                // Копируем InputStream в файл
                FileOutputStream(outFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                }

                outFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Uri.getDisplayName(context: Context): String? {
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor.use {
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    return cursor.getString(nameIndex)
                }
            }
        }
        return null
    }

    private val saveFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        try {
            if (uri != null) {
                val currentState = attachmentViewModel.attachmentState.value
                if (currentState is AttachmentViewState.Success) {
                    requireContext().contentResolver.openOutputStream(uri)?.use { output ->
                        currentState.inputStream.copyTo(output)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Сохранено.", Toast.LENGTH_SHORT).show()
        } finally {
            // Сбрасываем состояние после завершения операции
            isHandlingAttachment = false
            attachmentViewModel.resetState()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun init(){
        messagesViewModel.claimMessages(token,channelId)
        binding.chatChannel.text = "Чат канала $channelName"


        lifecycleScope.launch(Dispatchers.IO) {
            messagesViewModel.messagesListState.collect { state ->
                when (state) {
                    is BaseViewModel.ViewState.Success -> {
                        lifecycleScope.launch(Dispatchers.Main) {
                            val sortedList = state.data.reversed()
                            val adapter = binding.chatList.adapter as? ChatRecyclerViewAdapter
                            adapter?.updateList(sortedList, membersGuild)

                            // Прокрутка к последнему элементу
                            if (state.data.isNotEmpty()) {
                                binding.chatList.scrollToPosition(state.data.size - 1)
                            }
                        }
                    }
                    is BaseViewModel.ViewState.Error,
                    BaseViewModel.ViewState.Loading ->{}
                    else -> {}
                }
            }
        }


    }

    override fun onProfileClick(idChannel: Long) {
    }


    override fun onDestroyView() {
        _binding = null
        usersAdapter = null
        super.onDestroyView()
    }

}