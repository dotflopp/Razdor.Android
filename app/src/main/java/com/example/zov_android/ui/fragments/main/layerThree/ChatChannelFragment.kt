package com.example.zov_android.ui.fragments.main.layerThree

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import asFlow
import com.example.zov_android.data.models.response.MembersGuildResponse
import com.example.zov_android.data.models.response.UserResponse
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
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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

    private val files = mutableListOf<File>()

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
                            Log.d("MimeTypeFile", mimeType)
                            val filename = when(mimeType) {
                                "image/jpg" -> "image_${System.currentTimeMillis()}.jpg"
                                "image/jpeg" -> "image_${System.currentTimeMillis()}.jpeg"
                                "image/png" -> "image_${System.currentTimeMillis()}.png"
                                "image/pdf" -> "image_${System.currentTimeMillis()}.pdf"
                                "text/plain" -> "document_${System.currentTimeMillis()}.txt"
                                "image/gif" -> "image_${System.currentTimeMillis()}.gif"
                                "application/pdf" -> "document_${System.currentTimeMillis()}.pdf"
                                "application/msword" -> "document_${System.currentTimeMillis()}.doc"
                                "application/vnd.ms-excel" -> "document_${System.currentTimeMillis()}.xls"
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "document_${System.currentTimeMillis()}.xlsx"
                                "application/vnd.ms-powerpoint" -> "document_${System.currentTimeMillis()}.ppt"
                                "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "document_${System.currentTimeMillis()}.pptx"
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "document_${System.currentTimeMillis()}.docx"
                                else -> "file_${System.currentTimeMillis()}.bin"
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
            pickFileLauncher.launch(arrayOf("*/*"))
        }

        binding.sendButton.setOnClickListener {
            val text = binding.editText.text.toString()
            if (text.isNotBlank() || files.isNotEmpty()){
                val appContext = requireContext().applicationContext
                messagesViewModel.loadMessages(token, appContext, channelId, text, files.toList())

                binding.editText.text.clear()
                files.clear()
            }
            else {
                Toast.makeText(requireContext(), "Введите текст или выберите файл", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val file = uri.toFile(requireContext())
            file?.let {
                files.add(it)
                Toast.makeText(requireContext(), "Файл добавлен", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(requireContext(), "Не удалось прочитать файл", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Uri.toFile(context: Context): File? {
        return try {
            val contentResolver = context.contentResolver
            val displayName = getDisplayName(context) ?: "file_${System.currentTimeMillis()}"

            val originalExtension = if (displayName.contains('.')) {
                displayName.substringAfterLast('.', "").takeIf { it.length in 1..5 }?.lowercase()
            } else null

            // 2. Получим MIME-тип
            val mimeType = contentResolver.getType(this)?.lowercase() ?: "application/octet-stream"

            // 3. Определим расширение
            val extension = when {
                originalExtension != null -> originalExtension
                else -> MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                    ?: mimeTypeToExtension(mimeType) // Наш кастомный маппинг
                    ?: "bin"
            }

            // 4. Удалим дублирование расширения в имени файла
            val baseName = if (displayName.contains('.')) {
                displayName.substringBeforeLast('.')
            } else displayName

            val safeName = "$baseName.$extension"
                .replace(Regex("[\\\\/:*?\"<>|]"), "_") // Замена недопустимых символов

            val file = File(context.cacheDir, safeName)
            contentResolver.openInputStream(this)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mimeTypeToExtension(mimeType: String): String? {
        return when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "application/pdf" -> "pdf"
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
            "application/vnd.ms-excel" -> "xls"
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "application/vnd.ms-powerpoint" -> "ppt"
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx"
            "text/plain" -> "txt"
            else -> null
        }
    }

    private fun Uri.getDisplayName(context: Context): String? {
        return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            } else null
        }
    }

    private val saveFileLauncher = registerForActivityResult(CreateDocument("*/*")) { uri ->
        try {
            if (uri != null) {
                val currentState = attachmentViewModel.attachmentState.value
                if (currentState is AttachmentViewState.Success) {
                    // Важно: создаем копию InputStream, так как оригинальный можно использовать только один раз
                    val inputStreamCopy = currentState.inputStream.copy()

                    requireContext().contentResolver.openOutputStream(uri)?.use { output ->
                        inputStreamCopy.use { input ->
                            input.copyTo(output)
                        }
                    }
                    Toast.makeText(requireContext(), "Файл сохранен", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isHandlingAttachment = false
            attachmentViewModel.resetState()
        }
    }

    // Расширение для копирования InputStream
    private fun InputStream.copy(): ByteArrayInputStream {
        val bytes = this.readBytes()
        return ByteArrayInputStream(bytes)
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